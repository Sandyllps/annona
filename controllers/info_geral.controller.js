import { pool } from "../bd.js";

async function pegarInfoGeral(req, res){
     try{

        //Nesse caso do SELECT, pool.execute() retorna uma array, onde a primeira posição são as linhas e a segunda são as colunas.
        //poll.execute() retorna uma array de arrays (a posição [0] é uma array, e a posição [1] também é uma array)
        const resposta_medidas = await pool.execute('SELECT id_medida, nome_medida FROM medidas'); 
        const linhas_medidas = resposta_medidas[0]  //aqui estou pegando as linhas, (elas estão na primeira posição da resposta). 

        const resposta_locais = await pool.execute('SELECT id_local_armazenamento, nome_local_armazenamento, imagem FROM local_armazenamento');
        const linhas_locais = resposta_locais[0]

        const resposta_categorias = await pool.execute('SELECT id_categoria, nome FROM categorias');
        const linhas_categorias = resposta_categorias[0]

        const resposta_flags = await pool.execute('SELECT id_flag, nome_flag FROM flags');
        const linhas_flags = resposta_flags[0]


        
        return res.status(200).json({
            "medidas": linhas_medidas,
            "locais": linhas_locais,
            "categorias": linhas_categorias,
            "flags": linhas_flags,
            "itens_padrao": linhas_itens_padrao
        })
    }
    catch(error){
        console.error(error)
        return res.status(500).json({
            response:"Erro interno :("
        })
    }
}

export{pegarInfoGeral}