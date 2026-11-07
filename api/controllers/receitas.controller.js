import { pool } from "../bd.js";

// Remove acentos e deixa minúsculo para comparar nomes de itens
function normalizarTexto(str) {
    return String(str || "")
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .toLowerCase()
        .trim();
}

// Mapa de ligação entre receitas e itens padrão (usado como fallback caso a tabela receita_itens_padrao não exista)
const MAPA_RECEITA_ITENS = {
    "feijoada": ["Feijão", "Carne Bovina", "Cebola", "Água Mineral"],
    "moqueca de peixe": ["Peixe", "Atum", "Tomates", "Cebola"],
    "escondidinho de frango": ["Frango", "Carne Vegana", "Batatas", "Manteiga", "Queijo", "Leite", "Cebola", "Tomates", "Brocolis"],
    "arroz a grega": ["Arroz", "Cenoura", "Milho em Conserva", "Ervilha em Conserva", "Cebola", "Beringela", "Maçã", "Água Mineral"],
    "farofa de banana": ["Banana", "Manteiga", "Cebola", "Aveia", "Pão"],
    "batata saute": ["Batatas", "Manteiga"],
    "salada de lentilha": ["Feijão", "Alface", "Tomates", "Cebola", "Macarrão", "Suco", "Água Mineral"],
    "pave de bolacha": ["Biscoito", "Leite", "Ovos", "Iogurte", "Açúcar"],
    "bolo de cenoura": ["Cenoura", "Ovos", "Açúcar", "Farinha de Trigo", "Manteiga", "Leite", "Café"]
};

// Converte uma linha da tabela receitas em formato estruturado para o app
function formatarReceitaBanco(row, ingredientesNaDespensa = []) {
    const etapas = [];
    for (let i = 1; i <= 10; i++) {
        const imgKey = i === 1 ? (row["Img 1"] !== undefined ? "Img 1" : "img 1") : `img ${i}`;
        const descKey = `MODO DE PREPARO ${i}`;
        const desc = row[descKey];
        const img = row[imgKey];
        if (desc && String(desc).trim() !== "") {
            etapas.push({
                numero: etapas.length + 1,
                descricao: String(desc).trim(),
                imagem_nome: img ? String(img).replace(/\s+/g, "").trim() : ""
            });
        }
    }

    const tagOriginal = String(row["TAG"] || "PRINCIPAL").toUpperCase();
    let tagPadronizada = tagOriginal;
    if (tagOriginal.includes("PRINCIPAL")) tagPadronizada = "PRINCIPAL";
    else if (tagOriginal.includes("ACOMPANHAMENTO")) tagPadronizada = "ACOMPANHAMENTO";
    else if (tagOriginal.includes("ENTRADA")) tagPadronizada = "ENTRADA";
    else if (tagOriginal.includes("SOBREMESA")) tagPadronizada = "SOBREMESA";

    return {
        id: row["ID"],
        nome: row["NOME"],
        descricao: row["DESCRIÇÃO"] || "",
        tempoPreparo: row["TEMPO"] || "",
        porcoes: String(row["PORÇÃO"] || "4"),
        tag: tagPadronizada,
        imagem_capa: row["img capa"] ? String(row["img capa"]).replace("feijoada_cap.png", "feijoada_capa.png").trim() : "",
        ingredientes: row["INGREDIENTES"]
            ? String(row["INGREDIENTES"]).split("\n").map(s => s.trim()).filter(Boolean)
            : [],
        modoPreparo: etapas,
        ingredientes_disponiveis: ingredientesNaDespensa
    };
}

// GET /receitas - Lista todas as receitas cadastradas no banco (RF22)
async function listarReceitas(req, res) {
    try {
        const [linhas] = await pool.execute("SELECT * FROM receitas ORDER BY ID");
        const receitasFormatadas = linhas.map(r => formatarReceitaBanco(r, []));
        return res.status(200).json({
            receitas: receitasFormatadas
        });
    } catch (error) {
        console.error("Erro ao listar receitas", error);
        return res.status(500).json({
            response: "Erro interno :("
        });
    }
}

// GET /receitas/sugeridas/:id_usuario - Sugere receitas com base nos itens da despensa do usuário (RF17, RN14)
async function listarReceitasSugeridas(req, res) {
    try {
        const id_usuario = Number(req.params.id_usuario);
        const [receitasRows] = await pool.execute("SELECT * FROM receitas ORDER BY ID");
        const [itensUsuario] = await pool.execute(
            "SELECT nome FROM itens_usuario WHERE id_usuario = ? AND deletado = FALSE",
            [id_usuario]
        );

        const nomesUsuario = itensUsuario.map(i => i.nome);

        // Tenta buscar vínculos da tabela receita_itens_padrao
        let vinculosPorReceita = {};
        try {
            const [vinculos] = await pool.execute(
                `SELECT rip.id_receita, ip.nome 
                 FROM receita_itens_padrao rip 
                 JOIN itens_padrao ip ON rip.id_item_padrao = ip.id_item_padrao`
            );
            for (const v of vinculos) {
                if (!vinculosPorReceita[v.id_receita]) vinculosPorReceita[v.id_receita] = [];
                vinculosPorReceita[v.id_receita].push(v.nome);
            }
        } catch {
            // Se a tabela receita_itens_padrao ainda não existir no banco, usa o MAPA_RECEITA_ITENS
        }

        const sugeridas = [];
        const todas = [];

        for (const row of receitasRows) {
            const chaveNome = normalizarTexto(row["NOME"]);
            const itensPadraoDaReceita = vinculosPorReceita[row["ID"]] || MAPA_RECEITA_ITENS[chaveNome] || [];
            const textoIngredientes = normalizarTexto(row["INGREDIENTES"]);

            const emComum = [];
            for (const nomeItemUser of nomesUsuario) {
                const normUser = normalizarTexto(nomeItemUser);
                if (!normUser) continue;

                const bateuPadrao = itensPadraoDaReceita.some(ip => {
                    const normIp = normalizarTexto(ip);
                    return normIp === normUser || normIp.includes(normUser) || normUser.includes(normIp);
                });

                const bateuTexto = textoIngredientes.includes(normUser);

                if ((bateuPadrao || bateuTexto) && !emComum.some(e => normalizarTexto(e) === normUser)) {
                    emComum.push(nomeItemUser);
                }
            }

            const formatada = formatarReceitaBanco(row, emComum);
            todas.push(formatada);
            if (emComum.length > 0) {
                sugeridas.push(formatada);
            }
        }

        return res.status(200).json({
            receitas: sugeridas,
            todas_receitas: todas
        });
    } catch (error) {
        console.error("Erro ao listar receitas sugeridas", error);
        return res.status(500).json({
            response: "Erro interno :("
        });
    }
}

export { listarReceitas, listarReceitasSugeridas };