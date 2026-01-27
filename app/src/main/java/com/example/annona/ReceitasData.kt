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

        Receita(
            nome = "Moqueca de peixe",
            descricao = "Moqueca tem que ter azeite de dendê e leite de coco. Capriche no refogado de tomate, cebola e pimentão, pois ele é a base para saborizar o caldo. Fique de olho no ponto do peixe: queremos ele macio, mas sem se desmanchar na panela.",
            tempoPreparo = "35 MIN",
            porcoes = "4",
            ingredientes = listOf("\n" +
                    "500 gramas de filé de tilápia\n" +
                    "2 colheres de sopa de azeite\n" +
                    "2 dentes de alho amassados\n" +
                    "1 cebola média picada\n" +
                    "4 colheres de sopa de óleo de dendê\n" +
                    "4 tomates italianos picados\n" +
                    "1 pimentão verde picado\n" +
                    "Sal e pimenta-do-reino a gosto\n" +
                    "200 ml de leite de coco\n" +
                    "Salsinha a gosto"),
            modoPreparo = listOf(
                EtapaPreparo(
                    numero = 1,
                    descricao = "Reúna todos os ingredientes da moqueca de peixe",
                    imagem = R.drawable.moqueca_de_peixe_preparo_1
                ),
                EtapaPreparo(
                    numero = 2,
                    descricao = "Em uma panela, aqueça o azeite e refogue o alho e a cebola, até murchar e dourar levemente;",
                    imagem = R.drawable.moqueca_de_peixe_preparo_2
                ),
                EtapaPreparo(
                    numero = 3,
                    descricao = "Adicione 2 colheres de óleo de dendê e os tomates picados. Refogue os tomates até murchar, mexendo às vezes para não queimar;",
                    imagem = R.drawable.moqueca_de_peixe_preparo_3
                ),
                EtapaPreparo(
                    numero = 4,
                    descricao = "Acrescente o pimentão, o restante do óleo de dendê e metade do leite de coco. Tempere com sal e pimenta-do-reino a gosto, misture e cozinhe por mais 2 minutos;",
                    imagem = R.drawable.moqueca_de_peixe_preparo_4
                ),
                EtapaPreparo(
                    numero = 5,
                    descricao = "Acomode os filés de peixe, despeje o restante do leite de coco e misture cuidadosamente para não quebrar os filés. Tampe a panela e deixe cozinhar por cerca de 10 a 15 minutos em fogo médio;",
                    imagem = R.drawable.moqueca_de_peixe_preparo_5
                ),
                EtapaPreparo(
                    numero = 6,
                    descricao = "Prove o molho e, se necessário, acerte o sal e temperos. Verifique se o peixe está completamente cozido, caso ainda não esteja, cozinhe por mais 5 minutos. Finalize com salsinha e misture bem;",
                            imagem = R.drawable.moqueca_de_peixe_preparo_6
                ),
                EtapaPreparo(
                    numero = 7,
                    descricao = "Agora é só servir essa moqueca baiana deliciosa! Você pode servir com arroz branco, farofa de dendê, pirão de peixe e uma salada fresca, fica incrível!",
                    imagem = R.drawable.moqueca_de_peixe_preparo_7
                )
            ),
            tag = "PRINCIPAL",
            imagem = R.drawable.moqueca_de_peixe
        ),

        Receita(
            nome = "Escondidinho de frango",
            descricao = "Esse escondidinho de frango é uma receita prática, cremosa e cheia de sabor. O recheio temperado se mistura ao purê de batatas macio e ganha o toque irresistível do queijo gratinado. Ideal para o almoço de domingo ou para transformar o jantar em um momento especial!",
            tempoPreparo = "70 MIN",
            porcoes = "10",
            ingredientes = listOf("800 gramas de frango cozido e desfiado\n" +
                    "1 colher de sopa de manteiga (20 gramas)\n" +
                    "1 cebola média\n" +
                    "3 dentes de alho\n" +
                    "1 tomate médio\n" +
                    "2 colheres de sopa de extrato de tomate (40 gramas)\n" +
                    "3 colheres de sopa de coentro ou a gosto\n" +
                    "1 colher de chá de sal ou a gosto\n" +
                    "1/2 colher de chá de pimenta-do-reino ou a gosto\n" +
                    "1/2 colher de chá de páprica picante ou a gosto\n" +
                    "1 colher de chá de chimichurri ou a gosto\n" +
                    "1 colher de chá de colorau a gosto\n" +
                    "360 gramas de mussarela para gratinar\n" +
                    "Ingrendientes do purê\n" +
                    "6 batatas grandes (aprox. 950g)\n" +
                    "2 colheres de sopa de manteiga (40 gramas)\n" +
                    "1 caixa de creme de leite (200 gramas)\n" +
                    "100 ml de leite\n" +
                    "1 colher de chá de sal ou a gosto\n" +
                    "1/2 de colher de chá de pimenta-do-reino ou a gosto"),
            modoPreparo = listOf(
                EtapaPreparo(
                    numero = 1,
                    descricao = "Para começar, organize sua bancada e separe todos os ingredientes. Descasque a batata, pique a cebola, o alho, o tomate e o coentro em pedaços pequenos;",
                    imagem = R.drawable.escondidinho_preparo_1
                ),
                EtapaPreparo(
                    numero = 2,
                    descricao = "Em seguida, coloque as batatas e água em uma panela de pressão. Cozinhe por 20 minutos até que fiquem bem macias;",
                    imagem = R.drawable.escondidinho_preparo_2
                ),
                EtapaPreparo(
                    numero = 3,
                    descricao = "Enquanto isso, em uma panela, aqueça a manteiga e refogue a cebola e o alho até dourar levemente. Adicione o frango desfiado e o tomate picado, misturando bem;\n",
                    imagem = R.drawable.escondidinho_preparo_3
                ),
                EtapaPreparo(
                    numero = 4,
                    descricao = "Tempere com sal, pimenta-do-reino, páprica, chimichurri e colorau a gosto. Coloque o extrato de tomate, mexa e deixe cozinhar por alguns minutos. Finalize o recheio com coentro picado, desligue o fogo e reserve;",
                    imagem = R.drawable.escondidinho_preparo_4
                ),
                EtapaPreparo(
                    numero = 5,
                    descricao = "Para o purê, amasse bem as batatas já cozidas até formar uma massa lisa;",
                    imagem = R.drawable.escondidinho_preparo_5
                ),
                EtapaPreparo(
                    numero = 6,
                    descricao = "Acrescente a manteiga, o creme de leite e o leite, misturando até obter consistência cremosa. Tempere com sal e pimenta-do-reino a gosto;",
                    imagem = R.drawable.escondidinho_preparo_6
                ),
                EtapaPreparo(
                    numero = 7,
                    descricao = "Para a montagem, comece com uma camada de purê, adicione o recheio de frango e finalize com outra camada de purê;",
                    imagem = R.drawable.escondidinho_preparo_7
                ),
                EtapaPreparo(
                    numero = 8,
                    descricao = "Polvilhe a mussarela ralada por cima e leve ao forno preaquecido a 200ºC para gratinar, cerca de 20 minutos ou até dourar;",
                    imagem = R.drawable.escondidinho_preparo_8
                ),
                EtapaPreparo(
                    numero = 9,
                    descricao = "Retire do forno e sirva ainda quente. Bom apetite!",
                    imagem = R.drawable.escondidinho_preparo_9
                )
            ),
            tag = "PRINCIPAL",
            imagem = R.drawable.escondidinho_de_frango_com_batata
        ),

        Receita(
            nome = "Farofa de Banana",
            descricao = "A farofa de banana é uma adição encantadora e saborosa para complementar suas refeições. Com poucos ingredientes e um processo simples, esta receita oferece um toque especial e delicioso para acompanhar diversos pratos.",
            tempoPreparo = "20 MIN",
            porcoes = "4",
            ingredientes = listOf("\n" +
                    "2 bananas-prata\n" +
                    "2 colheres de sopa de manteiga\n" +
                    "1/2 cebola cortada em cubos pequenos\n" +
                    "1 dente de alho picado\n" +
                    "Sal a gosto\n" +
                    "Pimenta-do-reino a gosto\n" +
                    "1 e 1/2 xícara de chá de farinha de mandioca torrada"),
            modoPreparo = listOf(
                EtapaPreparo(
                    numero = 1,
                    descricao = "Reúna todos os ingredientes;",
                    imagem = R.drawable.farofa_de_banana_preparo_1
                ),
                EtapaPreparo(
                    numero = 2,
                    descricao = "Descasque as bananas e corte-as em pedaços médios;",
                    imagem = R.drawable.farofa_de_banana_preparo_2
                ),
                EtapaPreparo(
                    numero = 3,
                    descricao = "Em uma panela, sobre o fogo médio, derreta a manteiga e refogue a cebola com uma pitada de sal;",
                    imagem = R.drawable.farofa_de_banana_preparo_3
                ),
                EtapaPreparo(
                    numero = 4,
                    descricao = "Assim que a cebola murchar, coloque o alho e refogue por mais 1 minuto;",
                    imagem = R.drawable.farofa_de_banana_preparo_4
                ),
                EtapaPreparo(
                    numero = 5,
                    descricao = "Acrescente as bananas, sal, pimenta e misture, delicadamente, por mais 1 minuto;",
                    imagem = R.drawable.farofa_de_banana_preparo_5
                ),
                EtapaPreparo(
                    numero = 6,
                    descricao = "Depois, aos poucos, acrescente a farinha de mandioca. Misture todos os ingredientes, com cuidado, até que tudo se incorpore. Deixa cozinhar por cerca de 5 minutos, mexendo ocasionalmente. Na sequência, está pronta para servir.",
                    imagem = R.drawable.farofa_de_banana_preparo_6
                )
            ),
            tag = "ACOMPANHAMENTO",
            imagem = R.drawable.farofa_de_banana_capa
        ),

        Receita(
            nome = "Arroz à Grega",
            descricao = "Todo colorido e muito saboroso, o arroz à grega é um prato festivo, que acompanha muito bem carnes assadas e legumes grelhados. A uva-passa traz um sabor adocicado, que não fica enjoativo com o contraste dos pimentões. Um prato que transforma qualquer refeição em um momento especial.",
            tempoPreparo = "50 MIN",
            porcoes = "9",
            ingredientes = listOf("2 xícaras de chá de arroz agulhinha cru\n" +
                    "1 fio de azeite\n" +
                    "1/2 cebola média picada\n" +
                    "2 dentes de alho picados\n" +
                    "1 cenoura média picada\n" +
                    "1/2 xícara de chá de pimentão vermelho médio picado\n" +
                    "1/2 xícara de chá de pimentão amarelo médio picado\n" +
                    "1/2 xícara de chá de uva-passa\n" +
                    "1 latinha de milho-verde (sem água)\n" +
                    "1 latinha de ervilha (sem água)\n" +
                    "1/2 colher de sopa de sal\n" +
                    "4 xícaras de chá de água\n" +
                    "3 colheres de sopa de cheiro-verde picado"),
            modoPreparo = listOf(
                EtapaPreparo(
                    numero = 1,
                    descricao = "Pegue todos os ingredientes que você precisa para preparar o arroz à grega. Já deixe os legumes lavados e picados. Se quiser, lave o milho e a ervilha para suavizar o sabor da água de conserva;",
                    imagem = R.drawable.arroz_a_grega_preparo_1
                ),
                EtapaPreparo(
                    numero = 2,
                    descricao = "Em uma panela grande, em fogo médio, esquente o azeite e refogue a cebola por 1 minuto, ou até que ela fique transparente;\n",
                    imagem = R.drawable.arroz_a_grega_preparo_2
                ),
                EtapaPreparo(
                    numero = 3,
                    descricao = "Coloque o alho, misture bem e refogue por mais 1 minuto, ou até o alho dourar. Mexa de vez em quando para não queimar;",
                    imagem = R.drawable.arroz_a_grega_preparo_3
                ),
                EtapaPreparo(
                    numero = 4,
                    descricao = "Acrescente a cenoura, os pimentões e a uva-passa. Misture para incorporar e deixe refogar por alguns segundos;",
                    imagem = R.drawable.arroz_a_grega_preparo_4
                ),
                EtapaPreparo(
                    numero = 5,
                    descricao = "Junte o milho-verde, a ervilha e o arroz. Mexa por cerca de 3 minutos para refogar mais um pouco;",
                    imagem = R.drawable.arroz_a_grega_preparo_5
                ),
                EtapaPreparo(
                    numero = 6,
                    descricao = "Coloque a água e o sal. Misture bem e tampe a panela. Deixe o arroz à grega cozinhar em fogo médio por 20 minutos, ou até secar a água;",
                    imagem = R.drawable.arroz_a_grega_preparo_6
                ),
                EtapaPreparo(
                    numero = 7,
                    descricao = "Desligue o fogo e deixe a panela tampada por alguns minutos - o arroz continuará cozinhando com o calor da panela. Finalize com o cheiro-verde;",
                    imagem = R.drawable.arroz_a_grega_preparo_7
                ),
                EtapaPreparo(
                    numero = 8,
                    descricao = "Prato colorido e cheio de textura. Já pode servir e aproveitar uma deliciosa refeição.",
                    imagem = R.drawable.arroz_a_grega_preparo_8
                )
            ),
            tag = "ACOMPANHAMENTO",
            imagem = R.drawable.arroz_a_grega_tradicional_capa
        ),

        Receita(
            nome = "Batata Sauté",
            descricao = "Uma entrada prática, saborosa e muito fácil de preparar! A batata sauté é douradinha por fora e macia por dentro, levando poucos ingredientes e pronta em poucos minutos, graças a um truque simples com o micro-ondas.",
            tempoPreparo = "20 MIN",
            porcoes = "4",
            ingredientes = listOf("\n" +
                    "4 batatas médias\n" +
                    "Sal a gosto\n" +
                    "Pimenta-do-reino a gosto\n" +
                    "2 colheres de sopa de manteiga\n" +
                    "Salsinha a gosto\n" +
                    "1 saquinho para verdura"),
            modoPreparo = listOf(
                EtapaPreparo(
                    numero = 1,
                    descricao = "Reúna todos os ingredientes na bancada! Descasque as batatas e corte-as em pedaços médios. Pique a salsinha finamente e reserve;\n",
                    imagem = R.drawable.batata_saute_preparo_1
                ),
                EtapaPreparo(
                    numero = 2,
                    descricao = "Em um bowl grande, coloque as batatas cortadas e tempere com sal e pimenta-do-reino a gosto;",
                    imagem = R.drawable.batata_saute_preparo_2
                ),
                EtapaPreparo(
                    numero = 3,
                    descricao = "Transfira as batatas para um saquinho de verduras, dê um nó na ponta e faça pequenos furinhos no plástico para permitir a saída do vapor;",
                    imagem = R.drawable.batata_saute_preparo_3
                ),
                EtapaPreparo(
                    numero = 4,
                    descricao = "Leve o saquinho ao micro-ondas por 5 a 6 minutos. Esse processo cozinha as batatas de maneira rápida. Ao retirar o saquinho, tenha cuidado ao abrir, pois o vapor quente pode causar queimaduras;",
                    imagem = R.drawable.batata_saute_preparo_4
                ),
                EtapaPreparo(
                    numero = 5,
                    descricao = "Aqueça uma frigideira grande e derreta nela a manteiga. Adicione as batatas cozidas e mexa delicadamente para não quebrá-las. Deixe dourar bem de todos os lados;",
                    imagem = R.drawable.batata_saute_preparo_5
                ),
                EtapaPreparo(
                    numero = 6,
                    descricao = "Acrescente a salsinha e misture rapidamente. Sirva como acompanhamento para carnes, peixes ou até mesmo como um prato principal!",
                    imagem = R.drawable.batata_saute_preparo_6
                )
            ),
            tag = "ENTRADA",
            imagem = R.drawable.batata_saute_capa
        ),

        Receita(
            nome = "Salada de Lentilha",
            descricao = "Dizem que comer lentilha no Ano Novo atrai fartura e prosperidade. Então, vamos preparar uma salada no capricho, com um toque refrescante de hortelã. Cozinhe a lentilha no ponto al dente para ela não se desmanchar na travessa. Seja para a ceia da virada ou para o dia a dia, uma delícia.",
            tempoPreparo = "20 MIN",
            porcoes = "8",
            ingredientes = listOf("\n" +
                    "1 xícara de chá de lentilha crua\n" +
                    "1 litro de água\n" +
                    "60 ml de azeite de oliva\n" +
                    "1/2 pimentão vermelho picado\n" +
                    "1/2 cebola roxa picada\n" +
                    "1 tomate sem sementes picado\n" +
                    "1 e 1/2 colher de sopa de suco de limão\n" +
                    "Sal a gosto\n" +
                    "Cominho a gosto\n" +
                    "Pimenta síria a gosto (ou pimenta-do-reino)\n" +
                    "Salsinha picada a gosto\n" +
                    "Hortelã picada a gosto"),
            modoPreparo = listOf(
                EtapaPreparo(
                    numero = 1,
                    descricao = "Reúna todos os ingredientes na bancada!",
                    imagem = R.drawable.salada_de_lentilha_preparo_1
                ),
                EtapaPreparo(
                    numero = 2,
                    descricao = "Higienize a lentilha, transfira para uma panela, adicione 1 litro de água, leve para o fogo médio, deixe levantar fervura e cozinhe por 12 a 15 minutos (contados após levantar fervura). Reserve;",
                    imagem = R.drawable.salada_de_lentilha_preparo_2
                ),
                EtapaPreparo(
                    numero = 3,
                    descricao = "Em uma frigideira, esquente metade do azeite e refogue o pimentão e a cebola por 1 minuto;",
                    imagem = R.drawable.salada_de_lentilha_preparo_3
                ),
                EtapaPreparo(
                    numero = 4,
                    descricao = "Desligue o fogo, acrescente a lentilha al dente, o tomate, o suco de limão, o restante do azeite, o sal, o cominho, a pimenta síria e misture bem;",
                    imagem = R.drawable.salada_de_lentilha_preparo_4
                ),
                EtapaPreparo(
                    numero = 5,
                    descricao = "Transfira para um refratário e finalize com a salsinha e a hortelã. Agora é só servir!",
                    imagem = R.drawable.salada_de_lentilha_preparo_5
                )
            ),
            tag = "ENTRADA",
            imagem = R.drawable.salada_de_lentilha_capa
        ),

        Receita(
            nome = "Pavê de Bolacha",
            descricao = "Com camadas de biscoito umedecido, creme suave e cobertura de ganache, esse pavê tradicional é cremoso e irresistível. A combinação entre o doce equilibrado e a textura leve faz dele um sucesso garantido em qualquer ocasião. Para um toque especial, finalize com decoração de chocolate branco e surpreenda na apresentação.",
            tempoPreparo = "40h",
            porcoes = "15",
            ingredientes = listOf("Ingredientes do creme\n" +
                    "2 latas de leite condensado (790 gramas)\n" +
                    "2 e 1/2 xícaras de chá de leite (600 ml)\n" +
                    "2 colheres de sopa de amido de milho (30 gramas)\n" +
                    "2 gemas\n" +
                    "1 caixa de creme de leite (200 gramas)\n" +
                    "1 colher de sopa de extrato de baunilha\n" +
                    "Ingredientes da ganache\n" +
                    "200 gramas de chocolate meio amargo\n" +
                    "50 gramas de chocolate branco para decorar\n" +
                    "1 caixa de creme de leite (200 gramas)\n" +
                    "Ingredientes para a montagem\n" +
                    "3/4 de xícara de chá de leite (200ml)\n" +
                    "350 gramas de biscoito maizena\n"),
            modoPreparo = listOf(
                EtapaPreparo(
                    numero = 1,
                    descricao = "Para preparar essa receita, separe os ingredientes necessários sobre sua bancada;",
                    imagem = R.drawable.pave_de_bolacha_preparo_1
                ),
                EtapaPreparo(
                    numero = 2,
                    descricao = "Dissolva o amido de milho no leite, depois despeje em uma panela média com o leite condensado;",
                    imagem = R.drawable.pave_de_bolacha_preparo_2
                ),
                EtapaPreparo(
                    numero = 3,
                    descricao = "Adicione as gemas peneiradas, o creme de leite e o extrato de baunilha;",
                    imagem = R.drawable.pave_de_bolacha_preparo_3
                ),
                EtapaPreparo(
                    numero = 4,
                    descricao = "Leve ao fogo médio, mexendo sem parar, até levantar fervura. Quando começar a borbulhar, conte 4 minutos e desligue o fogo;",
                    imagem = R.drawable.pave_de_bolacha_preparo_4
                ),
                EtapaPreparo(
                    numero = 5,
                    descricao = "Transfira o creme para um recipiente e deixe esfriar em temperatura ambiente por cerca de 30 minutos;\n",
                    imagem = R.drawable.pave_de_bolacha_preparo_5
                ),
                EtapaPreparo(
                    numero = 6,
                    descricao = "Em outra tigela, coloque os 200 ml de leite para umedecer os biscoitos;",
                    imagem = R.drawable.pave_de_bolacha_preparo_6
                ),
                EtapaPreparo(
                    numero = 7,
                    descricao = "Em uma travessa média (24cm), faça uma camada de creme e cubra com os biscoitos umedecidos no leite;",
                    imagem = R.drawable.pave_de_bolacha_preparo_7
                ),
                EtapaPreparo(
                    numero = 8,
                    descricao = "Repita as camadas até acabar os ingredientes, finalizando com o creme;Para a ganache, leve o chocolate meio amargo e o creme de leite ao micro-ondas por 1 minuto, em potência média, parando a cada 30 segundos para mexer; ",
                    imagem = R.drawable.pave_de_bolacha_preparo_8
                ),
                EtapaPreparo(
                    numero = 9,
                    descricao = "Misture bem até obter um creme brilhante e homogêneo e espalhe sobre o pavê com uma espátula;",
                    imagem = R.drawable.pave_de_bolacha_preparo_9
                ),
                EtapaPreparo(
                    numero = 10,
                    descricao = "Leve à geladeira por no mínimo 4 horas antes de servir. E está pronto!",
                    imagem = R.drawable.pave_de_bolacha_preparo_10
                )
            ),
            tag = "SOBREMESA",
            imagem = R.drawable.pave_de_bolacha_capa
        ),

        Receita(
            nome = "Bolo de Cenoura",
            descricao = "O cheirinho de bolo de cenoura saindo do forno traz alegria para a casa, a sensação de lar. Nessa receita, a massa fica fofinha, cresce que é uma beleza. O sabor equilibrado, doce na medida certa. Na cobertura, uma calda de chocolate cremosa. Já pode passar um cafezinho!",
            tempoPreparo = "60 min",
            porcoes = "10",
            ingredientes = listOf("Ingredientes da massa\n" +
                    "3 cenouras médias (cerca de 360 gramas)\n" +
                    "4 ovos\n" +
                    "1 xícara de chá de óleo de milho (240 ml)\n" +
                    "1 e 1/2 xícara de chá de açúcar (300 gramas)\n" +
                    "2 xícaras de chá de farinha de trigo (280 gramas)\n" +
                    "1 colher de sopa de fermento químico em pó (fermento para bolo) (14 gramas)\n" +
                    "1 pitada de sal (1/4 de colher de chá)\n" +
                    "Manteiga e farinha de trigo para untar\n" +
                    "Ingredientes da calda\n" +
                    "1 caixa de leite codensado (395 gramas)\n" +
                    "1 colher de sopa de manteiga sem sal (14 gramas)\n" +
                    "4 colheres de sopa de chocolate em pó (ou 7 colheres de sopa de achocolatado)\n" +
                    "1/2 caixinha de creme de leite (100 gramas)\n" +
                    "Chocolate granulado para decorar"),
            modoPreparo = listOf(
                EtapaPreparo(
                    numero = 1,
                    descricao = "Higienize, descasque e corte as cenouras em rodelas. É possível substituir o óleo de milho por de girassol ou soja (prefira óleos com sabor mais neutro). Quebre os ovos em um recipiente separado para ver se todos estão bons. Coloque o forno para preaquecer a 180 ºC.",
                    imagem = R.drawable.bolo_de_cenoura_preparo_1
                ),
                EtapaPreparo(
                    numero = 2,
                    descricao = "Em um recipiente, peneire a farinha (para deixar seu bolo mais leve e sem gruminhos). Adicione o sal e o fermento. Misture bem. Reserve;",
                    imagem = R.drawable.bolo_de_cenoura_preparo_2
                ),
                EtapaPreparo(
                    numero = 3,
                    descricao = "No liquidificador, coloque as cenouras, o óleo, os ovos e o açúcar. Bata por cerca de 5 minutos, ou até ficar uma misture bem homogênea. Despeje em um recipiente grande;",
                    imagem = R.drawable.bolo_de_cenoura_preparo_3
                ),
                EtapaPreparo(
                    numero = 4,
                    descricao = "Gradualmente, vá adicionando a mistura de ingredientes secos. Com o auxílio de um fuê (batedor de bolo), mexa a massa cuidadosamente, apenas para incorporar bem. Não bata o bolo com movimentos rápidos, pois isso ativa o glúten do trigo e a massa fica solada;",
                    imagem = R.drawable.bolo_de_cenoura_preparo_4
                ),
                EtapaPreparo(
                    numero = 5,
                    descricao = "Assim que obter uma massa homogênea, despeje-a em uma forma redonda média (22 cm de diâmetro), untada e enfarinha. Leve ao forno preaquecido a 180 ºC por cerca de 45 a 50 minutos - quando você espetar o garfo e ele sair limpinho, o bolo está assado.",
                    imagem = R.drawable.bolo_de_cenoura_preparo_5
                ),
                EtapaPreparo(
                    numero = 6,
                    descricao = "Enquanto o bolo de cenoura esfria um pouquinho, você prepara a calda de chocolate;Em uma panela, coloque o leite condensado, a manteiga e o chocolate. Com uma espátula, misture bem até ficar homogêneo;",
                    imagem = R.drawable.bolo_de_cenoura_preparo_6
                ),
                EtapaPreparo(
                    numero = 7,
                    descricao = "Leve ao fogo médio e mexa sem parar até atingir o ponto de brigadeiro (quando começa a desgrudar do fundo da panela). Desligue o fogo, despeje o creme de leite e misture para incorporar;",
                    imagem = R.drawable.bolo_de_cenoura_preparo_7
                ),
                EtapaPreparo(
                    numero = 8,
                    descricao = "Quando o bolo estiver morno, desenforme e despeje a cobertura por cima; ",
                    imagem = R.drawable.bolo_de_cenoura_preparo_8
                ),
                EtapaPreparo(
                    numero = 9,
                    descricao = "Decore com chocolate granulado. Pode servir quente ou frio.",
                    imagem = R.drawable.bolo_de_cenoura_preparo_9
                )
            ),
            tag = "SOBREMESA",
            imagem = R.drawable.bolo_de_cenoura_capa
        ),
    )

    fun buscarPorNome(nome: String): Receita? {
        return receitas.find { it.nome == nome }
    }
}
