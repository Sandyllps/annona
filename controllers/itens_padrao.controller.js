import { pool } from "../bd.js"

// Função assíncrona que o Router chama quando o Android acessa /itens_padrao
async function listarItensPadrao(req, res) {
    try {
        // [SQL] Busca todos os itens da tabela itens_padrao
        // O 'await' faz o código esperar o banco responder antes de continuar
        const resposta = await pool.execute('SELECT * FROM itens_padrao'); 
        
        // O MySQL retorna um array onde a posição [0] são as linhas da tabela
        const itens = resposta[0]; 
        
        // Retornamos o status 200 (Sucesso) e enviamos os dados em formato JSON
        // O Android vai procurar pela chave "itens" que definimos aqui
        return res.status(200).json({
            itens 
        });
    }
    catch (error) {
        // Se houver erro (banco desligado, por exemplo), logamos no console do PC
        console.error("Erro ao listar itens padrão", error);
        
        // Retornamos 500 (Erro interno) para o Android não ficar esperando para sempre
        return res.status(500).json({
            response: "Erro interno no servidor :("
        });
    }
}

// Exportamos a função para que o Router consiga enxergá-la
export { listarItensPadrao }