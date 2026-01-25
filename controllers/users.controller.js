import { response } from "express"
import{pool} from "../bd.js"

async function login(req, res){
    try{
        const email = req.body.email
        const senha = req.body.senha
        const resposta = await pool.execute('SELECT id_usuario, nome_usuario, email FROM usuarios WHERE email = ? and senha = ?', [email, senha]);
        const linhas = resposta[0]
        if(!linhas[0]){
            return res.status(401).json({
                response:"Usuário inválido"
            }) 
        }
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


export{login}