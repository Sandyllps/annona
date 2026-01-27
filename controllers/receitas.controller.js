import { response } from "express"
import{pool} from "../bd.js"

async function listarReceitas(req, res){
    try{
        const resposta = await pool.execute('SELECT * FROM receitas'); //poll.execute retorna uma array de arrays.
        const receitas = resposta[0] //resposta[0] corresponde às linhas da tabela no banco de dados, resposta[1] corresponde às colunas da tabela. Como eu só preciso das linhas, minha resposta contem a array na posição[0].
        return res.status(200).json({
            receitas //irá retornar somente as linhas da tabela
        })
    }
    catch(error){
        console.error("Erro ao listar receitas", error)
        return res.status(500).json({
            response:"Erro interno :("
        })
    }
}

export {listarReceitas}