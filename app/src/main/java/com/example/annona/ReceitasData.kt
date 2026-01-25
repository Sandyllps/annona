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
                    descricao = "Antes de começar, lave o feijão preto em água corrente," +
                            " coloque 2 xícaras de água e escorra. Reúna os outros ingredientes e inicie o preparo;",
                    imagem = R.drawable.feijoada_preparo_1
                ),
                EtapaPreparo(
                    numero = 2,
                    descricao = "Corte a costelinha de porco em pedaços grandes, um osso por vez. " +
                            "Retire o couro do bacon e corte em cubos grandes. Fatie a linguiça calabresa em rodelas grossas. " +
                            "Corte o pé de porco em pedaços menores." +
                            " Descasque e pique finamente o alho e a cebola em cubos;",
                    imagem = R.drawable.feijoada_preparo_2
                ),
                EtapaPreparo(
                    numero = 3,
                    descricao = "Coloque o feijão, o pé de porco e a costelinha na panela de pressão." +
                            " Cubra com água, coloque as folhas de louro e o couro do bacon." +
                            " Tampe a panela e leve ao fogo alto." +
                            " Quando começar a pegar pressão, cozinhe por 15 minutos;",
                    imagem = R.drawable.feijoada_preparo_3
                ),
                EtapaPreparo(
                    numero = 4,
                    descricao = "Enquanto isso, leve uma panela grande e de fundo grosso ao fogo alto." +
                            " Quando aquecer, adicione o bacon (se necessário, adicione azeite). " +
                            "Frite o bacon até começar a ficar crocante. Adicione a calabresa e frite " +
                            "por cerca de 6 minutos, até que também fique dourada;",
                    imagem = R.drawable.feijoada_preparo_4
                ),
                EtapaPreparo(
                    numero = 5,
                    descricao = "Junte o alho e a cebola na panela, e refogue por 1 minuto." +
                            " Acrescente os tabletes de carne e, com uma colher de pau, esfarele-os." +
                            " Adicione um pouco de água e raspe os queimadinhos do fundo da panela " +
                            "para incorporar os sabores;",
                    imagem = R.drawable.feijoada_preparo_5
                ),
                EtapaPreparo(
                    numero = 6,
                    descricao = "Quando terminar o cozimento da pressão, desligue o fogo e, com cuidado, " +
                            "retire a pressão da panela e abra. Com uma concha, adicione o feijão com as " +
                            "carnes no refogado e misture bem. Prove e adicione sal, se necessário. " +
                            "Abaixe o fogo, tampe a panela e deixe cozinhar por cerca de 10 minutos para apurar os sabores.",
                    imagem = R.drawable.feijoada_preparo_6
                ),
                EtapaPreparo(
                    numero = 7,
                    descricao = "E está pronta! Sirva a feijoada com arroz branco e couve.",
                    imagem = R.drawable.feijoada_preparo_7
                )
            ),
            tag = "PRINCIPAL",
            imagem = R.drawable.feijoada_capa
        ),
    )

    fun buscarPorNome(nome: String): Receita? {
        return receitas.find { it.nome == nome }
    }
}
