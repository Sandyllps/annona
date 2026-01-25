package com.example.annona

object ReceitasData {

    val receitas = listOf(
        Receita(
            nome = "Feijoada",
            descricao = "Essa feijoada é ideal para quem busca o sabor autêntico do prato, sem um longo tempo de preparo. Assim, você pode fazê-la para um almoço delicioso em qualquer dia da semana.",
            tempoPreparo = "50 MIN",
            porcoes = "10",
            ingredientes = listOf("1 kg de feijão preto\n", "800 gramas de pé de porco\n",
                "800 gramas de costelinha de porco\n", "350 gramas de linguiça calabresa\n",
                "300 gramas de bacon\n", "2 cubos de caldo de carne ou bacon\n", "1 cebola grande (160 gramas)\n",
                "10 dentes de alho\n", "3 folhas de louro\n", "1 colher de sopa de sal (ou a gosto)"),
            modoPreparo = listOf(
                EtapaPreparo(
                    numero = 1,
                    descricao = "",
                    imagem = R.drawable.ic_launcher_foreground
                ),
                EtapaPreparo(
                    numero = 1,
                    descricao = "",
                    imagem = R.drawable.ic_launcher_foreground
                ),
                EtapaPreparo(
                    numero = 1,
                    descricao = "",
                    imagem = R.drawable.ic_launcher_foreground
                ),
                EtapaPreparo(
                    numero = 1,
                    descricao = "",
                    imagem = R.drawable.ic_launcher_foreground
                ),
                EtapaPreparo(
                    numero = 1,
                    descricao = "",
                    imagem = R.drawable.ic_launcher_foreground
                )
            ),
            tag = "PRINCIPAL",
            imagem = R.drawable.ic_launcher_foreground
        ),
    )

    fun buscarPorNome(nome: String): Receita? {
        return receitas.find { it.nome == nome }
    }
}
