import { response } from "express"
import{pool} from "../bd.js"

//req: Requisição (o que vem do front-end)
//res: Resposta (o que o servidor envia de volta)
async function login(req, res){
    try{
        const email = req.body.email //a requisição de login precisa receber no body email e senha.
        const senha = req.body.senha
        const resposta = await pool.execute('SELECT id_usuario, nome_usuario, email FROM usuarios WHERE email = ? and senha = ?', [email, senha]);
        const linhas = resposta[0]
        if(!linhas[0]){ //se na posição [0] do array não tiver resposta com esse usuario e senha, retorna "usuário inválido"
            return res.status(401).json({
                response:"Usuário inválido"
            }) 
        }
        //se chegar a essa parte, é porque email e senha do usuário coincidiram com os do banco de dados.
        return res.status(200).json({
            response:"OK",
            "id_usuario": linhas[0].id_usuario,
            "nome_usuario": linhas[0].nome_usuario,
            "email": linhas[0].email
        })
    }
    catch(error){
        console.error(error)
        return res.status(500).json({
            response:"Erro interno :("
        })
    }
}


//Função para cadastrar um novo item para o usuário no banco de dados.
async function adicionarItemUsuario(req, res){
    try{
        // Aqui estamos pegando as informações que o front-end enviou no corpo da requisição, o body
        // armazeno em constantes para facilitar o uso na query SQL.
        const id_usuario = req.body.id_usuario
        const nome = req.body.nome
        const id_categoria = req.body.id_categoria
        const imagem = req.body.imagem
        const consumindo = req.body.consumindo
        const possui_data_validade = req.body.possui_data_validade
        const data_validade = req.body.data_validade || null
        const id_local_armazenamento = req.body.id_local_armazenamento
        const quantidade = req.body.quantidade
        const id_medida = req.body.id_medida
        const preco = req.body.preco
        const id_flag = req.body.id_flag
        console.log(req.body)
        

        // Usamos 'await' para o código parar aqui até que o banco confirme o salvamento.
        const resposta = await pool.execute('INSERT INTO itens_usuario (id_usuario, nome, id_categoria, imagem, consumindo, possui_data_validade, data_validade, id_local_armazenamento, quantidade, id_medida, preco, id_flag) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)', [id_usuario, nome, id_categoria, imagem, consumindo, possui_data_validade, data_validade, id_local_armazenamento, quantidade, id_medida, preco, id_flag]);
        const resultado = resposta[0] //No caso do insert, pool.execute() retorna um array onde a posição [0] é um objeto com a informação do insert.

        return res.status(200).json({
            response:"OK",
            "id_item_usuario": resultado.insertId
        })
    }
    catch(error){
        console.error(error)
        return res.status(500).json({
            response:"Erro interno :("
        })
    }
}




async function listarItensUsuario(req, res){
    try{
        const id_usuario = req.params.id_usuario //aqui usei params no lugar de body porque estou lendo o id na rota (no GET não se utiliza o body)
        const resposta = await pool.execute('SELECT * FROM itens_usuario WHERE id_usuario = ? and deletado = FALSE', [id_usuario]);
        const linhas = resposta[0]
        
        return res.status(200).json({
            "listaItens": linhas
        })
    }
    catch(error){
        console.error(error)
        return res.status(500).json({
            response:"Erro interno :("
        })
    }
}




async function editarItemUsuario(req, res){
     try{
        console.log(req.body)
        console.log(req.params)
        const id_item_usuario = req.params.id_item_usuario
        const nome = req.body.nome
        const id_categoria = req.body.id_categoria
        const imagem = req.body.imagem
        const consumindo = req.body.consumindo
        const possui_data_validade = req.body.possui_data_validade
        const data_validade = req.body.data_validade || null
        const id_local_armazenamento = req.body.id_local_armazenamento
        const quantidade = req.body.quantidade
        const id_medida = req.body.id_medida
        const preco = req.body.preco
        const id_flag = req.body.id_flag
        
        const resposta = await pool.execute('UPDATE itens_usuario SET nome = ?, id_categoria = ?, imagem = ? , consumindo = ?, possui_data_validade = ?, data_validade = ?, id_local_armazenamento = ?, quantidade = ?, id_medida = ?, preco = ?, id_flag = ? WHERE id_item_usuario = ?', [nome, id_categoria, imagem, consumindo, possui_data_validade, data_validade, id_local_armazenamento, quantidade, id_medida, preco, id_flag, id_item_usuario]);
        const resultado = resposta[0] //No caso do insert, pool.execute() retorna um array onde a posição [0] é um objeto com a informação do insert.

        return res.status(200).json({
            response:"OK"
            
        })
    }
    catch(error){
        console.error(error)
        return res.status(500).json({
            response:"Erro interno :("
        })
    }
}






async function deletarItemUsuario(req, res){
    try{
        const id_item_usuario = req.params.id_item_usuario //aqui estou pegando id_item_usuario da rota, mas também poderia pegar do body
        const razao_delecao = req.body.razao_delecao //aqui estou pegando razao_delecao do body.
        const resposta = await pool.execute('UPDATE itens_usuario SET deletado = TRUE, razao_delecao = ? WHERE id_item_usuario = ? ', [razao_delecao, id_item_usuario]);
        const resultado = resposta[0]
        
        return res.status(200).json({
            "response": "ok"
        })
    }
    catch(error){
        console.error(error)
        return res.status(500).json({
            response:"Erro interno :("
        })
    }
}


export{login, adicionarItemUsuario, listarItensUsuario, editarItemUsuario, deletarItemUsuario}