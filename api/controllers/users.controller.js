import crypto from "crypto";
import { pool } from "../bd.js";

const TOKEN_SECRET = process.env.JWT_SECRET || "annona-secret-key-mobile-ii-2026";

// --- Funções auxiliares de Hash de Senha (RNF08, B07) ---
export function gerarHashSenha(senha) {
    const salt = crypto.randomBytes(8).toString("hex");
    const hash = crypto.scryptSync(senha, salt, 64).toString("hex");
    return `${salt}:${hash}`;
}

export function verificarSenha(senhaDigitada, senhaSalva) {
    if (!senhaSalva) return false;
    // Se já estiver no formato salt:hash
    if (senhaSalva.includes(":")) {
        const [salt, hashOriginal] = senhaSalva.split(":");
        const hashTeste = crypto.scryptSync(senhaDigitada, salt, 64).toString("hex");
        return hashTeste === hashOriginal;
    }
    // Compatibilidade com senhas antigas em texto puro (ex: "123")
    return senhaDigitada === senhaSalva;
}

// --- Funções auxiliares de Token JWT (RNF09, B08) ---
export function gerarToken(payload) {
    const header = Buffer.from(JSON.stringify({ alg: "HS256", typ: "JWT" })).toString("base64url");
    const body = Buffer.from(JSON.stringify({ ...payload, iat: Date.now() })).toString("base64url");
    const signature = crypto
        .createHmac("sha256", TOKEN_SECRET)
        .update(`${header}.${body}`)
        .digest("base64url");
    return `${header}.${body}.${signature}`;
}

export function lerToken(req) {
    const auth = req.headers?.authorization;
    if (!auth || !auth.startsWith("Bearer ")) return null;
    const token = auth.substring(7);
    const partes = token.split(".");
    if (partes.length !== 3) return null;
    const [header, body, signature] = partes;
    const expectedSig = crypto
        .createHmac("sha256", TOKEN_SECRET)
        .update(`${header}.${body}`)
        .digest("base64url");
    if (signature !== expectedSig) return null;
    try {
        return JSON.parse(Buffer.from(body, "base64url").toString("utf8"));
    } catch {
        return null;
    }
}

// --- Cálculo do Semáforo e Prazo no Servidor (RN01, RN02, B03, B14) ---
export function calcularSemaforo(possuiDataValidade, dataValidade) {
    if (!possuiDataValidade || !dataValidade) {
        return {
            id_flag: 4,
            dias_restantes: null,
            prazo: ""
        };
    }

    const hoje = new Date();
    const hojeData = new Date(hoje.getFullYear(), hoje.getMonth(), hoje.getDate());

    let dVal;
    if (dataValidade instanceof Date) {
        dVal = new Date(dataValidade.getFullYear(), dataValidade.getMonth(), dataValidade.getDate());
    } else {
        const str = String(dataValidade).split("T")[0];
        const [ano, mes, dia] = str.split("-").map(Number);
        dVal = new Date(ano, mes - 1, dia);
    }

    const diffMs = dVal.getTime() - hojeData.getTime();
    const dias = Math.round(diffMs / (1000 * 60 * 60 * 24));

    let id_flag = 4;
    let prazo = "";

    if (dias < 0) {
        id_flag = 1; // Vencido (Marrom)
        prazo = "VENCIDO";
    } else if (dias <= 3) {
        id_flag = 2; // Crítico (Vermelho) - 0 a 3 dias
        prazo = dias === 0 ? "HOJE" : (dias === 1 ? "1 DIA" : `${dias} DIAS`);
    } else if (dias <= 7) {
        id_flag = 3; // Atenção (Amarelo) - 4 a 7 dias
        prazo = `${dias} DIAS`;
    } else {
        id_flag = 4; // Seguro (Verde) - 8 dias ou mais
        prazo = `${dias} DIAS`;
    }

    return {
        id_flag,
        dias_restantes: dias,
        prazo
    };
}

// --- Enriquecer e Ordenar Itens por Prioridade (RF12, RN10, B15) ---
export function processarEOrdenarItens(linhas) {
    const processados = linhas.map(item => {
        const { id_flag, dias_restantes, prazo } = calcularSemaforo(
            item.possui_data_validade,
            item.data_validade
        );
        return {
            ...item,
            id_flag,
            dias_restantes,
            prazo,
            fora_doacao: item.fora_doacao ? 1 : 0
        };
    });

    // Ordem de prioridade: 1 (Vencido), 2 (Crítico), 3 (Atenção), 4 (Seguro), e menor número de dias primeiro
    processados.sort((a, b) => {
        if (a.id_flag !== b.id_flag) {
            return a.id_flag - b.id_flag;
        }
        const diasA = a.dias_restantes !== null ? a.dias_restantes : 999999;
        const diasB = b.dias_restantes !== null ? b.dias_restantes : 999999;
        return diasA - diasB;
    });

    return processados;
}

// --- LOGIN (RF02, B07, B08) ---
async function login(req, res) {
    try {
        const email = (req.body.email || "").trim();
        const senha = req.body.senha || "";

        if (!email || !senha) {
            return res.status(400).json({ response: "Preencha e-mail e senha" });
        }

        const [linhas] = await pool.execute(
            "SELECT * FROM usuarios WHERE email = ?",
            [email]
        );

        const usuario = linhas[0];
        if (!usuario || !verificarSenha(senha, usuario.senha)) {
            return res.status(401).json({
                response: "E-mail ou senha inválidos"
            });
        }

        // Se a senha no banco ainda estava em texto puro, atualiza automaticamente para hash
        if (!String(usuario.senha).includes(":")) {
            try {
                const novoHash = gerarHashSenha(senha);
                await pool.execute("UPDATE usuarios SET senha = ? WHERE id_usuario = ?", [novoHash, usuario.id_usuario]);
            } catch (e) {
                console.warn("Aviso ao atualizar hash de senha legada:", e.message);
            }
        }

        const token = gerarToken({ id_usuario: usuario.id_usuario, email: usuario.email });

        return res.status(200).json({
            response: "OK",
            id_usuario: usuario.id_usuario,
            nome_usuario: usuario.nome_usuario,
            email: usuario.email,
            notificacoes_ativas: usuario.notificacoes_ativas !== undefined ? Boolean(usuario.notificacoes_ativas) : true,
            token
        });
    } catch (error) {
        console.error(error);
        return res.status(500).json({
            response: "Erro interno no servidor :("
        });
    }
}

// --- CADASTRO DE CONTA (RF01, T3, B07) ---
async function cadastrarUsuario(req, res) {
    try {
        const nome_usuario = (req.body.nome_usuario || req.body.nome || "").trim();
        const email = (req.body.email || "").trim();
        const senha = req.body.senha || "";

        if (!nome_usuario || !email || !senha) {
            return res.status(400).json({
                response: "Preencha nome, e-mail e senha"
            });
        }

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            return res.status(400).json({
                response: "Formato de e-mail inválido"
            });
        }

        if (senha.length < 3) {
            return res.status(400).json({
                response: "A senha deve ter pelo menos 3 caracteres"
            });
        }

        // Verifica se o e-mail já existe
        const [existentes] = await pool.execute(
            "SELECT id_usuario FROM usuarios WHERE email = ?",
            [email]
        );
        if (existentes.length > 0) {
            return res.status(409).json({
                response: "Este e-mail já está cadastrado"
            });
        }

        const senhaHash = gerarHashSenha(senha);

        let insertId;
        try {
            const [resultado] = await pool.execute(
                "INSERT INTO usuarios (nome_usuario, email, senha) VALUES (?, ?, ?)",
                [nome_usuario, email, senhaHash]
            );
            insertId = resultado.insertId;
        } catch (err) {
            // Caso o banco antigo ainda não tenha AUTO_INCREMENT em id_usuario
            const [maxRows] = await pool.execute("SELECT COALESCE(MAX(id_usuario), 0) + 1 AS proxId FROM usuarios");
            const proxId = maxRows[0].proxId;
            await pool.execute(
                "INSERT INTO usuarios (id_usuario, nome_usuario, email, senha) VALUES (?, ?, ?, ?)",
                [proxId, nome_usuario, email, senhaHash]
            );
            insertId = proxId;
        }

        const token = gerarToken({ id_usuario: insertId, email });

        return res.status(201).json({
            response: "Conta criada com sucesso",
            id_usuario: insertId,
            nome_usuario,
            email,
            token
        });
    } catch (error) {
        console.error(error);
        return res.status(500).json({
            response: "Erro interno ao criar conta :("
        });
    }
}

// --- ADICIONAR ITEM (RF04, RN04, RN05, B03, B13) ---
async function adicionarItemUsuario(req, res) {
    try {
        const tokenData = lerToken(req);
        const id_usuario = tokenData?.id_usuario || Number(req.body.id_usuario);
        const nome = (req.body.nome || "").trim();
        const id_categoria = Number(req.body.id_categoria);
        const imagem = req.body.imagem || "item_padrao.png";
        const consumindo = Boolean(req.body.consumindo);
        const possui_data_validade = Boolean(req.body.possui_data_validade);
        const data_validade = possui_data_validade && req.body.data_validade ? req.body.data_validade : null;
        const id_local_armazenamento = Number(req.body.id_local_armazenamento);
        const quantidade = req.body.quantidade !== undefined && req.body.quantidade !== null ? Number(req.body.quantidade) : 0;
        const id_medida = Number(req.body.id_medida) || 1;
        const preco = req.body.preco !== undefined && req.body.preco !== null ? Number(req.body.preco) : 0;

        if (!id_usuario || id_usuario <= 0) {
            return res.status(401).json({ response: "Usuário não identificado" });
        }
        if (!nome || !id_categoria || !id_local_armazenamento) {
            return res.status(400).json({ response: "Nome, categoria e local são obrigatórios" });
        }
        if (possui_data_validade && !data_validade) {
            return res.status(400).json({ response: "Informe a data de validade ou desmarque a opção" });
        }

        // RN04: A validade informada manualmente no cadastro não pode ser anterior à data atual
        if (possui_data_validade && data_validade) {
            const { dias_restantes } = calcularSemaforo(true, data_validade);
            if (dias_restantes !== null && dias_restantes < 0 && !req.body.permitir_vencido) {
                return res.status(400).json({ response: "A data de validade não pode ser anterior à data de hoje" });
            }
        }

        const { id_flag } = calcularSemaforo(possui_data_validade, data_validade);

        const [resultado] = await pool.execute(
            "INSERT INTO itens_usuario (id_usuario, nome, id_categoria, imagem, consumindo, possui_data_validade, data_validade, id_local_armazenamento, quantidade, id_medida, preco, id_flag) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            [id_usuario, nome, id_categoria, imagem, consumindo, possui_data_validade, data_validade, id_local_armazenamento, quantidade, id_medida, preco, id_flag]
        );

        return res.status(200).json({
            response: "OK",
            id_item_usuario: resultado.insertId,
            id_flag
        });
    } catch (error) {
        console.error(error);
        return res.status(500).json({
            response: "Erro interno :("
        });
    }
}

// --- LISTAR ITENS DA DESPENSA (RF05, RF09, RF12, RN10, B03, B14, B15) ---
async function listarItensUsuario(req, res) {
    try {
        const id_usuario = Number(req.params.id_usuario);
        if (!id_usuario || id_usuario <= 0) {
            return res.status(400).json({ response: "Usuário inválido" });
        }

        const [linhas] = await pool.execute(
            "SELECT * FROM itens_usuario WHERE id_usuario = ? AND deletado = FALSE",
            [id_usuario]
        );

        const listaOrdenada = processarEOrdenarItens(linhas);

        // Sincroniza id_flag no banco caso tenha mudado com a passagem dos dias
        for (const item of listaOrdenada) {
            const original = linhas.find(l => l.id_item_usuario === item.id_item_usuario);
            if (original && original.id_flag !== item.id_flag) {
                pool.execute(
                    "UPDATE itens_usuario SET id_flag = ? WHERE id_item_usuario = ?",
                    [item.id_flag, item.id_item_usuario]
                ).catch(() => {});
            }
        }

        return res.status(200).json({
            listaItens: listaOrdenada
        });
    } catch (error) {
        console.error(error);
        return res.status(500).json({
            response: "Erro interno :("
        });
    }
}

// --- EDITAR ITEM (RF06, RNF09, B02, B03, B08, B13) ---
async function editarItemUsuario(req, res) {
    try {
        const id_item_usuario = Number(req.params.id_item_usuario);
        const tokenData = lerToken(req);
        const id_usuario = tokenData?.id_usuario || Number(req.body.id_usuario);

        const nome = (req.body.nome || "").trim();
        const id_categoria = Number(req.body.id_categoria);
        const imagem = req.body.imagem || "item_padrao.png";
        const consumindo = Boolean(req.body.consumindo);
        const possui_data_validade = Boolean(req.body.possui_data_validade);
        const data_validade = possui_data_validade && req.body.data_validade ? req.body.data_validade : null;
        const id_local_armazenamento = Number(req.body.id_local_armazenamento);
        const quantidade = req.body.quantidade !== undefined && req.body.quantidade !== null ? Number(req.body.quantidade) : 0;
        const id_medida = Number(req.body.id_medida) || 1;
        const preco = req.body.preco !== undefined && req.body.preco !== null ? Number(req.body.preco) : 0;

        if (!nome || !id_categoria || !id_local_armazenamento) {
            return res.status(400).json({ response: "Nome, categoria e local são obrigatórios" });
        }
        if (possui_data_validade && !data_validade) {
            return res.status(400).json({ response: "Informe a data de validade ou desmarque a opção" });
        }

        const { id_flag } = calcularSemaforo(possui_data_validade, data_validade);

        let query = "UPDATE itens_usuario SET nome = ?, id_categoria = ?, imagem = ?, consumindo = ?, possui_data_validade = ?, data_validade = ?, id_local_armazenamento = ?, quantidade = ?, id_medida = ?, preco = ?, id_flag = ? WHERE id_item_usuario = ?";
        const params = [nome, id_categoria, imagem, consumindo, possui_data_validade, data_validade, id_local_armazenamento, quantidade, id_medida, preco, id_flag, id_item_usuario];

        if (id_usuario && id_usuario > 0) {
            query += " AND id_usuario = ?";
            params.push(id_usuario);
        }

        const [resultado] = await pool.execute(query, params);

        if (resultado.affectedRows === 0) {
            return res.status(404).json({
                response: "Item não encontrado ou não pertence a este usuário"
            });
        }

        return res.status(200).json({
            response: "OK",
            id_flag
        });
    } catch (error) {
        console.error(error);
        return res.status(500).json({
            response: "Erro interno :("
        });
    }
}

// --- ALTERNAR CONSUMO: CONSUMINDO / POR CONSUMIR (RF13, B12) ---
async function alterarConsumoItem(req, res) {
    try {
        const id_item_usuario = Number(req.params.id_item_usuario);
        const tokenData = lerToken(req);
        const id_usuario = tokenData?.id_usuario || Number(req.body.id_usuario);
        const consumindo = Boolean(req.body.consumindo);

        let query = "UPDATE itens_usuario SET consumindo = ? WHERE id_item_usuario = ?";
        const params = [consumindo, id_item_usuario];

        if (id_usuario && id_usuario > 0) {
            query += " AND id_usuario = ?";
            params.push(id_usuario);
        }

        const [resultado] = await pool.execute(query, params);

        if (resultado.affectedRows === 0) {
            return res.status(404).json({
                response: "Item não encontrado ou não pertence a este usuário"
            });
        }

        return res.status(200).json({
            response: "OK",
            consumindo: consumindo ? 1 : 0
        });
    } catch (error) {
        console.error(error);
        return res.status(500).json({
            response: "Erro interno :("
        });
    }
}

// --- DELETAR ITEM COM EXCLUSÃO LÓGICA (RF07, RN09, RNF09, B08) ---
async function deletarItemUsuario(req, res) {
    try {
        const id_item_usuario = Number(req.params.id_item_usuario);
        const tokenData = lerToken(req);
        const id_usuario = tokenData?.id_usuario || Number(req.body?.id_usuario);
        const razao_delecao = req.body?.razao_delecao || "Item consumido";

        let query = "UPDATE itens_usuario SET deletado = TRUE, razao_delecao = ? WHERE id_item_usuario = ?";
        const params = [razao_delecao, id_item_usuario];

        if (id_usuario && id_usuario > 0) {
            query += " AND id_usuario = ?";
            params.push(id_usuario);
        }

        const [resultado] = await pool.execute(query, params);

        if (resultado.affectedRows === 0) {
            return res.status(404).json({
                response: "Item não encontrado ou não pertence a este usuário"
            });
        }

        return res.status(200).json({
            response: "ok"
        });
    } catch (error) {
        console.error(error);
        return res.status(500).json({
            response: "Erro interno :("
        });
    }
}

// --- LISTAR DOAÇÃO (RF15, RN12, RN13, B16) ---
async function listarItensDoacao(req, res) {
    try {
        const id_usuario = Number(req.params.id_usuario);
        const [linhas] = await pool.execute(
            "SELECT * FROM itens_usuario WHERE id_usuario = ? AND deletado = FALSE",
            [id_usuario]
        );

        const todosProcessados = processarEOrdenarItens(linhas);
        const listaDoacao = todosProcessados.filter(
            item => (item.id_flag === 2 || item.id_flag === 3) && !item.consumindo && !item.fora_doacao
        );

        return res.status(200).json({
            listaItens: listaDoacao
        });
    } catch (error) {
        console.error(error);
        return res.status(500).json({
            response: "Erro interno :("
        });
    }
}

// --- RETIRAR OU DEVOLVER ITEM DA LISTA DE DOAÇÃO (RF15, RN13, B16) ---
async function alterarDisponibilidadeDoacao(req, res) {
    try {
        const id_item_usuario = Number(req.params.id_item_usuario);
        const tokenData = lerToken(req);
        const id_usuario = tokenData?.id_usuario || Number(req.body.id_usuario);
        const fora_doacao = req.body.fora_doacao !== undefined ? Boolean(req.body.fora_doacao) : true;

        try {
            let query = "UPDATE itens_usuario SET fora_doacao = ? WHERE id_item_usuario = ?";
            const params = [fora_doacao, id_item_usuario];
            if (id_usuario && id_usuario > 0) {
                query += " AND id_usuario = ?";
                params.push(id_usuario);
            }
            await pool.execute(query, params);
        } catch (err) {
            // Caso a coluna fora_doacao ainda não exista no banco, cria automaticamente
            if (err.code === "ER_BAD_FIELD_ERROR") {
                await pool.execute("ALTER TABLE itens_usuario ADD COLUMN fora_doacao BOOLEAN DEFAULT FALSE");
                await pool.execute("UPDATE itens_usuario SET fora_doacao = ? WHERE id_item_usuario = ?", [fora_doacao, id_item_usuario]);
            } else {
                throw err;
            }
        }

        return res.status(200).json({
            response: "OK",
            fora_doacao
        });
    } catch (error) {
        console.error(error);
        return res.status(500).json({
            response: "Erro interno :("
        });
    }
}

// --- HISTÓRICO DE ITENS CONSUMIDOS, VENCIDOS E DOADOS (RF20, RF21) ---
async function listarHistoricoUsuario(req, res) {
    try {
        const id_usuario = Number(req.params.id_usuario);
        const [linhas] = await pool.execute(
            "SELECT * FROM itens_usuario WHERE id_usuario = ? AND deletado = TRUE ORDER BY id_item_usuario DESC",
            [id_usuario]
        );

        const historico = processarEOrdenarItens(linhas);
        const resumo = {
            consumidos: linhas.filter(i => (i.razao_delecao || "").toLowerCase().includes("consumido")).length,
            desperdicados: linhas.filter(i => (i.razao_delecao || "").toLowerCase().includes("vencido") || (i.razao_delecao || "").toLowerCase().includes("estragado")).length,
            doados: linhas.filter(i => (i.razao_delecao || "").toLowerCase().includes("doado") || (i.razao_delecao || "").toLowerCase().includes("doação")).length,
            outros: linhas.filter(i => {
                const r = (i.razao_delecao || "").toLowerCase();
                return !r.includes("consumido") && !r.includes("vencido") && !r.includes("estragado") && !r.includes("doado") && !r.includes("doação");
            }).length
        };

        return res.status(200).json({
            historico,
            resumo
        });
    } catch (error) {
        console.error(error);
        return res.status(500).json({
            response: "Erro interno :("
        });
    }
}

// --- PREFERÊNCIA DE NOTIFICAÇÕES E PERFIL (RF16, RF20, RN15) ---
async function atualizarNotificacoesUsuario(req, res) {
    try {
        const id_usuario = Number(req.params.id_usuario);
        const notificacoes_ativas = Boolean(req.body.notificacoes_ativas);

        try {
            await pool.execute(
                "UPDATE usuarios SET notificacoes_ativas = ? WHERE id_usuario = ?",
                [notificacoes_ativas, id_usuario]
            );
        } catch (err) {
            if (err.code === "ER_BAD_FIELD_ERROR") {
                await pool.execute("ALTER TABLE usuarios ADD COLUMN notificacoes_ativas BOOLEAN DEFAULT TRUE");
                await pool.execute("UPDATE usuarios SET notificacoes_ativas = ? WHERE id_usuario = ?", [notificacoes_ativas, id_usuario]);
            }
        }

        return res.status(200).json({
            response: "OK",
            notificacoes_ativas
        });
    } catch (error) {
        console.error(error);
        return res.status(500).json({
            response: "Erro interno :("
        });
    }
}

async function obterPerfilUsuario(req, res) {
    try {
        const id_usuario = Number(req.params.id_usuario);
        const [linhas] = await pool.execute(
            "SELECT id_usuario, nome_usuario, email FROM usuarios WHERE id_usuario = ?",
            [id_usuario]
        );
        if (!linhas[0]) {
            return res.status(404).json({ response: "Usuário não encontrado" });
        }
        return res.status(200).json({
            ...linhas[0]
        });
    } catch (error) {
        console.error(error);
        return res.status(500).json({ response: "Erro interno :(" });
    }
}

export {
    login,
    cadastrarUsuario,
    adicionarItemUsuario,
    listarItensUsuario,
    editarItemUsuario,
    alterarConsumoItem,
    deletarItemUsuario,
    listarItensDoacao,
    alterarDisponibilidadeDoacao,
    listarHistoricoUsuario,
    atualizarNotificacoesUsuario,
    obterPerfilUsuario
};