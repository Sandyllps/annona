import { pool } from "../bd.js";

// Devolve medidas, locais, categorias, flags e itens_padrao para o app (RF23, B04, B11)
async function pegarInfoGeral(req, res) {
    try {
        const [linhas_medidas] = await pool.execute("SELECT id_medida, nome_medida FROM medidas ORDER BY id_medida");
        const [linhas_locais] = await pool.execute("SELECT id_local_armazenamento, nome_local_armazenamento, imagem FROM local_armazenamento ORDER BY id_local_armazenamento");
        const [linhas_categorias] = await pool.execute("SELECT id_categoria, nome FROM categorias ORDER BY id_categoria");
        const [linhas_flags] = await pool.execute("SELECT id_flag, nome_flag FROM flags ORDER BY id_flag");
        const [linhas_itens_padrao] = await pool.execute("SELECT * FROM itens_padrao ORDER BY id_item_padrao");

        return res.status(200).json({
            medidas: linhas_medidas,
            locais: linhas_locais,
            categorias: linhas_categorias,
            flags: linhas_flags,
            itens_padrao: linhas_itens_padrao
        });
    } catch (error) {
        console.error(error);
        return res.status(500).json({
            response: "Erro interno :("
        });
    }
}

export { pegarInfoGeral };