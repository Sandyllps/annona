package com.example.annona

import android.content.Context
import java.text.Normalizer

object ReceitasData {

    // Lista que pode ser atualizada com as receitas vindas da API (RF22, B11)
    var receitasAtuais: List<Receita> = emptyList()
        get() = if (field.isNotEmpty()) field else receitas

    // Converte texto tirando acentos para facilitar a busca de ingredientes em comum (RF17, RN14)
    fun normalizar(texto: String): String {
        val temp = Normalizer.normalize(texto, Normalizer.Form.NFD)
        return temp.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            .lowercase()
            .trim()
    }

    // Filtra as receitas e preenche os ingredientes que o usuário tem na despensa (RF17, RN14)
    fun filtrarPorDespensa(listaBase: List<Receita>, itensUsuario: List<Item>): List<Receita> {
        val nomesNaDespensa = itensUsuario.map { it.nome.trim() }.filter { it.isNotEmpty() }
        if (nomesNaDespensa.isEmpty()) return emptyList()

        val resultado = mutableListOf<Receita>()
        for (receita in listaBase) {
            val emComum = encontrarIngredientesEmComum(receita, nomesNaDespensa)
            if (emComum.isNotEmpty()) {
                resultado.add(receita.copy(ingredientesDisponiveis = emComum))
            }
        }
        return resultado
    }

    fun encontrarIngredientesEmComum(receita: Receita, nomesNaDespensa: List<String>): List<String> {
        val encontrados = mutableListOf<String>()
        val textoIngredientes = normalizar(receita.ingredientes.joinToString(" "))
        val padroesRelacionados = receita.itensPadraoRelacionados.map { normalizar(it) }

        for (nomeUser in nomesNaDespensa) {
            val normUser = normalizar(nomeUser)
            if (normUser.isBlank()) continue

            val bateuPadrao = padroesRelacionados.any { padrao ->
                padrao == normUser || padrao.contains(normUser) || normUser.contains(padrao)
            }
            val bateuIngrediente = textoIngredientes.contains(normUser)

            if ((bateuPadrao || bateuIngrediente) && encontrados.none { normalizar(it) == normUser }) {
                encontrados.add(nomeUser)
            }
        }
        return encontrados
    }

    // Converte um DTO vindo da API (/receitas ou /receitas/sugeridas) para o objeto Receita do app (RF22)
    fun converterDaApi(context: Context, dto: ReceitaApiDto): Receita {
        val nomeRec = dto.nome ?: "Receita"
        val fallbackLocal = receitas.find { normalizar(it.nome) == normalizar(nomeRec) }

        val capaName = (dto.imagemCapa ?: "").substringBeforeLast(".").replace("feijoada_cap", "feijoada_capa").trim()
        val capaRes = if (capaName.isNotBlank()) {
            context.resources.getIdentifier(capaName, "drawable", context.packageName).takeIf { it != 0 }
        } else null

        val etapas = if (!dto.modoPreparo.isNullOrEmpty()) {
            dto.modoPreparo.mapIndexed { idx, etapaDto ->
                val imgNome = (etapaDto.imagem_nome ?: "")
                    .replace(" ", "")
                    .replace("prepararo", "preparo")
                    .substringBeforeLast(".")
                    .trim()
                val imgRes = if (imgNome.isNotBlank()) {
                    context.resources.getIdentifier(imgNome, "drawable", context.packageName).takeIf { it != 0 }
                } else null
                val fallbackEtapaImg = fallbackLocal?.modoPreparo?.getOrNull(idx)?.imagem ?: R.drawable.ic_launcher_foreground
                EtapaPreparo(
                    numero = etapaDto.numero,
                    descricao = etapaDto.descricao,
                    imagem = imgRes ?: fallbackEtapaImg
                )
            }
        } else {
            fallbackLocal?.modoPreparo ?: emptyList()
        }

        val listaIngredientes = when {
            !dto.ingredientes.isNullOrEmpty() -> dto.ingredientes
            !dto.ingredientesTextoBruto.isNullOrBlank() -> dto.ingredientesTextoBruto.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
            else -> fallbackLocal?.ingredientes ?: emptyList()
        }

        val tagBruta = (dto.tag ?: fallbackLocal?.tag ?: "PRINCIPAL").uppercase()
        val tagFinal = when {
            tagBruta.contains("PRINCIPAL") -> "PRINCIPAL"
            tagBruta.contains("ACOMPANHAMENTO") -> "ACOMPANHAMENTO"
            tagBruta.contains("ENTRADA") -> "ENTRADA"
            tagBruta.contains("SOBREMESA") -> "SOBREMESA"
            else -> tagBruta
        }

        return Receita(
            nome = nomeRec,
            descricao = dto.descricao ?: fallbackLocal?.descricao ?: "",
            tempoPreparo = dto.tempoPreparo ?: fallbackLocal?.tempoPreparo ?: "",
            porcoes = dto.porcoes ?: fallbackLocal?.porcoes ?: "4",
            ingredientes = listaIngredientes,
            modoPreparo = etapas,
            tag = tagFinal,
            imagem = capaRes ?: fallbackLocal?.imagem ?: R.drawable.feijoada_capa,
            itensPadraoRelacionados = fallbackLocal?.itensPadraoRelacionados ?: emptyList(),
            ingredientesDisponiveis = dto.ingredientesDisponiveis ?: emptyList()
        )
    }

    val receitas = listOf(
        Receita(
            nome = "Feijoada",
            descricao = "Essa feijoada é ideal para quem busca o sabor autêntico do prato, sem um longo tempo de preparo. Assim, você pode fazê-la para um almoço delicioso em qualquer dia da semana.",
            tempoPreparo = "50 min",
            porcoes = "10",
            ingredientes = listOf(
                "1 kg de feijão preto\n", "800 gramas de pé de porco\n",
                "800 gramas de costelinha de porco\n", "350 gramas de linguiça calabresa\n",
                "300 gramas de bacon\n", "2 cubos de caldo de carne ou bacon\n", "1 cebola grande (160 gramas)\n",
                "10 dentes de alho\n", "3 folhas de louro\n", "1 colher de sopa de sal (ou a gosto)"
            ),
            modoPreparo = listOf(
                EtapaPreparo(1, "Antes de começar, lave o feijão preto em água corrente, coloque 2 xícaras de água e escorra. Reúna os outros ingredientes e inicie o preparo;", R.drawable.feijoada_preparo_1),
                EtapaPreparo(2, "Corte a costelinha de porco em pedaços grandes, um osso por vez. Retire o couro do bacon e corte em cubos grandes. Fatie a linguiça calabresa em rodelas grossas. Corte o pé de porco em pedaços menores. Descasque e pique finamente o alho e a cebola em cubos;", R.drawable.feijoada_preparo_2),
                EtapaPreparo(3, "Coloque o feijão, o pé de porco e a costelinha na panela de pressão. Cubra com água, coloque as folhas de louro e o couro do bacon. Tampe a panela e leve ao fogo alto. Quando começar a pegar pressão, cozinhe por 15 minutos;", R.drawable.feijoada_preparo_3),
                EtapaPreparo(4, "Enquanto isso, leve uma panela grande e de fundo grosso ao fogo alto. Quando aquecer, adicione o bacon (se necessário, adicione azeite). Frite o bacon até começar a ficar crocante. Adicione a calabresa e frite por cerca de 6 minutos, até que também fique dourada;", R.drawable.feijoada_preparo_4),
                EtapaPreparo(5, "Junte o alho e a cebola na panela, e refogue por 1 minuto. Acrescente os tabletes de carne e, com uma colher de pau, esfarele-os. Adicione um pouco de água e raspe os queimadinhos do fundo da panela para incorporar os sabores;", R.drawable.feijoada_preparo_5),
                EtapaPreparo(6, "Quando terminar o cozimento da pressão, desligue o fogo e, com cuidado, retire a pressão da panela e abra. Com uma concha, adicione o feijão com as carnes no refogado e misture bem. Prove e adicione sal, se necessário. Abaixe o fogo, tampe a panela e deixe cozinhar por cerca de 10 minutos para apurar os sabores.", R.drawable.feijoada_preparo_6),
                EtapaPreparo(7, "E está pronta! Sirva a feijoada com arroz branco e couve.", R.drawable.feijoada_preparo_7)
            ),
            tag = "PRINCIPAL",
            imagem = R.drawable.feijoada_capa,
            itensPadraoRelacionados = listOf("Feijão", "Carne Bovina", "Cebola", "Água Mineral")
        ),

        Receita(
            nome = "Moqueca de peixe",
            descricao = "Moqueca tem que ter azeite de dendê e leite de coco. Capriche no refogado de tomate, cebola e pimentão, pois ele é a base para saborizar o caldo. Fique de olho no ponto do peixe: queremos ele macio, mas sem se desmanchar na panela.",
            tempoPreparo = "35 min",
            porcoes = "4",
            ingredientes = listOf(
                "500 gramas de filé de tilápia\n" +
                    "2 colheres de sopa de azeite\n" +
                    "2 dentes de alho amassados\n" +
                    "1 cebola média picada\n" +
                    "4 colheres de sopa de óleo de dendê\n" +
                    "4 tomates italianos picados\n" +
                    "1 pimentão verde picado\n" +
                    "Sal e pimenta-do-reino a gosto\n" +
                    "200 ml de leite de coco\n" +
                    "Salsinha a gosto"
            ),
            modoPreparo = listOf(
                EtapaPreparo(1, "Reúna todos os ingredientes da moqueca de peixe", R.drawable.moqueca_de_peixe_preparo_1),
                EtapaPreparo(2, "Em uma panela, aqueça o azeite e refogue o alho e a cebola, até murchar e dourar levemente;", R.drawable.moqueca_de_peixe_preparo_2),
                EtapaPreparo(3, "Adicione 2 colheres de óleo de dendê e os tomates picados. Refogue os tomates até murchar, mexendo às vezes para não queimar;", R.drawable.moqueca_de_peixe_preparo_3),
                EtapaPreparo(4, "Acrescente o pimentão, o restante do óleo de dendê e metade do leite de coco. Tempere com sal e pimenta-do-reino a gosto, misture e cozinhe por mais 2 minutos;", R.drawable.moqueca_de_peixe_preparo_4),
                EtapaPreparo(5, "Acomode os filés de peixe, despeje o restante do leite de coco e misture cuidadosamente para não quebrar os filés. Tampe a panela e deixe cozinhar por cerca de 10 a 15 minutos em fogo médio;", R.drawable.moqueca_de_peixe_preparo_5),
                EtapaPreparo(6, "Prove o molho e, se necessário, acerte o sal e temperos. Verifique se o peixe está completamente cozido, caso ainda não esteja, cozinhe por mais 5 minutos. Finalize com salsinha e misture bem;", R.drawable.moqueca_de_peixe_preparo_6),
                EtapaPreparo(7, "Agora é só servir essa moqueca baiana deliciosa! Você pode servir com arroz branco, farofa de dendê, pirão de peixe e uma salada fresca, fica incrível!", R.drawable.moqueca_de_peixe_preparo_7)
            ),
            tag = "PRINCIPAL",
            imagem = R.drawable.moqueca_de_peixe,
            itensPadraoRelacionados = listOf("Peixe", "Atum", "Tomates", "Cebola")
        ),

        Receita(
            nome = "Escondidinho de frango",
            descricao = "Esse escondidinho de frango é uma receita prática, cremosa e cheia de sabor. O recheio temperado se mistura ao purê de batatas macio e ganha o toque irresistível do queijo gratinado. Ideal para o almoço de domingo ou para transformar o jantar em um momento especial!",
            tempoPreparo = "70 min",
            porcoes = "10",
            ingredientes = listOf(
                "800 gramas de frango cozido e desfiado\n" +
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
                    "Ingredientes do purê\n" +
                    "6 batatas grandes (aprox. 950g)\n" +
                    "2 colheres de sopa de manteiga (40 gramas)\n" +
                    "1 caixa de creme de leite (200 gramas)\n" +
                    "100 ml de leite\n" +
                    "1 colher de chá de sal ou a gosto\n" +
                    "1/2 de colher de chá de pimenta-do-reino ou a gosto"
            ),
            modoPreparo = listOf(
                EtapaPreparo(1, "Para começar, organize sua bancada e separe todos os ingredientes. Descasque a batata, pique a cebola, o alho, o tomate e o coentro em pedaços pequenos;", R.drawable.escondidinho_preparo_1),
                EtapaPreparo(2, "Em seguida, coloque as batatas e água em uma panela de pressão. Cozinhe por 20 minutos até que fiquem bem macias;", R.drawable.escondidinho_preparo_2),
                EtapaPreparo(3, "Enquanto isso, em uma panela, aqueça a manteiga e refogue a cebola e o alho até dourar levemente. Adicione o frango desfiado e o tomate picado, misturando bem;", R.drawable.escondidinho_preparo_3),
                EtapaPreparo(4, "Tempere com sal, pimenta-do-reino, páprica, chimichurri e colorau a gosto. Coloque o extrato de tomate, mexa e deixe cozinhar por alguns minutos. Finalize o recheio com coentro picado, desligue o fogo e reserve;", R.drawable.escondidinho_preparo_4),
                EtapaPreparo(5, "Para o purê, amasse bem as batatas já cozidas até formar uma massa lisa;", R.drawable.escondidinho_preparo_5),
                EtapaPreparo(6, "Acrescente a manteiga, o creme de leite e o leite, misturando até obter consistência cremosa. Tempere com sal e pimenta-do-reino a gosto;", R.drawable.escondidinho_preparo_6),
                EtapaPreparo(7, "Para a montagem, comece com uma camada de purê, adicione o recheio de frango e finalize com outra camada de purê;", R.drawable.escondidinho_preparo_7),
                EtapaPreparo(8, "Polvilhe a mussarela ralada por cima e leve ao forno preaquecido a 200ºC para gratinar, cerca de 20 minutos ou até dourar;", R.drawable.escondidinho_preparo_8),
                EtapaPreparo(9, "Retire do forno e sirva ainda quente. Bom apetite!", R.drawable.escondidinho_preparo_9)
            ),
            tag = "PRINCIPAL",
            imagem = R.drawable.escondidinho_de_frango_com_batata,
            itensPadraoRelacionados = listOf("Frango", "Carne Vegana", "Batatas", "Manteiga", "Queijo", "Leite", "Cebola", "Tomates", "Brocolis")
        ),

        Receita(
            nome = "Farofa de Banana",
            descricao = "A farofa de banana é uma adição encantadora e saborosa para complementar suas refeições. Com poucos ingredientes e um processo simples, esta receita oferece um toque especial e delicioso para acompanhar diversos pratos.",
            tempoPreparo = "20 min",
            porcoes = "4",
            ingredientes = listOf(
                "2 bananas-prata\n" +
                    "2 colheres de sopa de manteiga\n" +
                    "1/2 cebola cortada em cubos pequenos\n" +
                    "1 dente de alho picado\n" +
                    "Sal a gosto\n" +
                    "Pimenta-do-reino a gosto\n" +
                    "1 e 1/2 xícara de chá de farinha de mandioca torrada"
            ),
            modoPreparo = listOf(
                EtapaPreparo(1, "Reúna todos os ingredientes;", R.drawable.farofa_de_banana_preparo_1),
                EtapaPreparo(2, "Descasque as bananas e corte-as em pedaços médios;", R.drawable.farofa_de_banana_preparo_2),
                EtapaPreparo(3, "Em uma panela, sobre o fogo médio, derreta a manteiga e refogue a cebola com uma pitada de sal;", R.drawable.farofa_de_banana_preparo_3),
                EtapaPreparo(4, "Assim que a cebola murchar, coloque o alho e refogue por mais 1 minuto;", R.drawable.farofa_de_banana_preparo_4),
                EtapaPreparo(5, "Acrescente as bananas, sal, pimenta e misture, delicadamente, por mais 1 minuto;", R.drawable.farofa_de_banana_preparo_5),
                EtapaPreparo(6, "Depois, aos poucos, acrescente a farinha de mandioca. Misture todos os ingredientes, com cuidado, até que tudo se incorpore. Deixa cozinhar por cerca de 5 minutos, mexendo ocasionalmente. Na sequência, está pronta para servir.", R.drawable.farofa_de_banana_preparo_6)
            ),
            tag = "ACOMPANHAMENTO",
            imagem = R.drawable.farofa_de_banana_capa,
            itensPadraoRelacionados = listOf("Banana", "Manteiga", "Cebola", "Aveia", "Pão")
        ),

        Receita(
            nome = "Arroz à Grega",
            descricao = "Todo colorido e muito saboroso, o arroz à grega é um prato festivo, que acompanha muito bem carnes assadas e legumes grelhados. A uva-passa traz um sabor adocicado, que não fica enjoativo com o contraste dos pimentões. Um prato que transforma qualquer refeição em um momento especial.",
            tempoPreparo = "50 min",
            porcoes = "9",
            ingredientes = listOf(
                "2 xícaras de chá de arroz agulhinha cru\n" +
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
                    "3 colheres de sopa de cheiro-verde picado"
            ),
            modoPreparo = listOf(
                EtapaPreparo(1, "Pegue todos os ingredientes que você precisa para preparar o arroz à grega. Já deixe os legumes lavados e picados. Se quiser, lave o milho e a ervilha para suavizar o sabor da água de conserva;", R.drawable.arroz_a_grega_preparo_1),
                EtapaPreparo(2, "Em uma panela grande, em fogo médio, esquente o azeite e refogue a cebola por 1 minuto, ou até que ela fique transparente;", R.drawable.arroz_a_grega_preparo_2),
                EtapaPreparo(3, "Coloque o alho, misture bem e refogue por mais 1 minuto, ou até o alho dourar. Mexa de vez em quando para não queimar;", R.drawable.arroz_a_grega_preparo_3),
                EtapaPreparo(4, "Acrescente a cenoura, os pimentões e a uva-passa. Misture para incorporar e deixe refogar por alguns segundos;", R.drawable.arroz_a_grega_preparo_4),
                EtapaPreparo(5, "Junte o milho-verde, a ervilha e o arroz. Mexa por cerca de 3 minutos para refogar mais um pouco;", R.drawable.arroz_a_grega_preparo_5),
                EtapaPreparo(6, "Coloque a água e o sal. Misture bem e tampe a panela. Deixe o arroz à grega cozinhar em fogo médio por 20 minutos, ou até secar a água;", R.drawable.arroz_a_grega_preparo_6),
                EtapaPreparo(7, "Desligue o fogo e deixe a panela tampada por alguns minutos - o arroz continuará cozinhando com o calor da panela. Finalize com o cheiro-verde;", R.drawable.arroz_a_grega_preparo_7),
                EtapaPreparo(8, "Prato colorido e cheio de textura. Já pode servir e aproveitar uma deliciosa refeição.", R.drawable.arroz_a_grega_preparo_8)
            ),
            tag = "ACOMPANHAMENTO",
            imagem = R.drawable.arroz_a_grega_tradicional_capa,
            itensPadraoRelacionados = listOf("Arroz", "Cenoura", "Milho em Conserva", "Ervilha em Conserva", "Cebola", "Beringela", "Maçã", "Água Mineral")
        ),

        Receita(
            nome = "Batata Sauté",
            descricao = "Uma entrada prática, saborosa e muito fácil de preparar! A batata sauté é douradinha por fora e macia por dentro, levando poucos ingredientes e pronta em poucos minutos, graças a um truque simples com o micro-ondas.",
            tempoPreparo = "20 min",
            porcoes = "4",
            ingredientes = listOf(
                "4 batatas médias\n" +
                    "Sal a gosto\n" +
                    "Pimenta-do-reino a gosto\n" +
                    "2 colheres de sopa de manteiga\n" +
                    "Salsinha a gosto\n" +
                    "1 saquinho para verdura"
            ),
            modoPreparo = listOf(
                EtapaPreparo(1, "Reúna todos os ingredientes na bancada! Descasque as batatas e corte-as em pedaços médios. Pique a salsinha finamente e reserve;", R.drawable.batata_saute_preparo_1),
                EtapaPreparo(2, "Em um bowl grande, coloque as batatas cortadas e tempere com sal e pimenta-do-reino a gosto;", R.drawable.batata_saute_preparo_2),
                EtapaPreparo(3, "Transfira as batatas para um saquinho de verduras, dê um nó na ponta e faça pequenos furinhos no plástico para permitir a saída do vapor;", R.drawable.batata_saute_preparo_3),
                EtapaPreparo(4, "Leve o saquinho ao micro-ondas por 5 a 6 minutos. Esse processo cozinha as batatas de maneira rápida. Ao retirar o saquinho, tenha cuidado ao abrir, pois o vapor quente pode causar queimaduras;", R.drawable.batata_saute_preparo_4),
                EtapaPreparo(5, "Aqueça uma frigideira grande e derreta nela a manteiga. Adicione as batatas cozidas e mexa delicadamente para não quebrá-las. Deixe dourar bem de todos os lados;", R.drawable.batata_saute_preparo_5),
                EtapaPreparo(6, "Acrescente a salsinha e misture rapidamente. Sirva como acompanhamento para carnes, peixes ou até mesmo como um prato principal!", R.drawable.batata_saute_preparo_6)
            ),
            tag = "ENTRADA",
            imagem = R.drawable.batata_saute_capa,
            itensPadraoRelacionados = listOf("Batatas", "Manteiga")
        ),

        Receita(
            nome = "Salada de Lentilha",
            descricao = "Dizem que comer lentilha no Ano Novo atrai fartura e prosperidade. Então, vamos preparar uma salada no capricho, com um toque refrescante de hortelã. Cozinhe a lentilha no ponto al dente para ela não se desmanchar na travessa. Seja para a ceia da virada ou para o dia a dia, uma delícia.",
            tempoPreparo = "20 min",
            porcoes = "8",
            ingredientes = listOf(
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
                    "Hortelã picada a gosto"
            ),
            modoPreparo = listOf(
                EtapaPreparo(1, "Reúna todos os ingredientes na bancada!", R.drawable.salada_de_lentilha_preparo_1),
                EtapaPreparo(2, "Higienize a lentilha, transfira para uma panela, adicione 1 litro de água, leve para o fogo médio, deixe levantar fervura e cozinhe por 12 a 15 minutos (contados após levantar fervura). Reserve;", R.drawable.salada_de_lentilha_preparo_2),
                EtapaPreparo(3, "Em uma frigideira, esquente metade do azeite e refogue o pimentão e a cebola por 1 minuto;", R.drawable.salada_de_lentilha_preparo_3),
                EtapaPreparo(4, "Desligue o fogo, acrescente a lentilha al dente, o tomate, o suco de limão, o restante do azeite, o sal, o cominho, a pimenta síria e misture bem;", R.drawable.salada_de_lentilha_preparo_4),
                EtapaPreparo(5, "Transfira para um refratário e finalize com a salsinha e a hortelã. Agora é só servir!", R.drawable.salada_de_lentilha_preparo_5)
            ),
            tag = "ENTRADA",
            imagem = R.drawable.salada_de_lentilha_capa,
            itensPadraoRelacionados = listOf("Feijão", "Alface", "Tomates", "Cebola", "Macarrão", "Suco", "Água Mineral")
        ),

        Receita(
            nome = "Pavê de Bolacha",
            descricao = "Com camadas de biscoito umedecido, creme suave e cobertura de ganache, esse pavê tradicional é cremoso e irresistível. A combinação entre o doce equilibrado e a textura leve faz dele um sucesso garantido em qualquer ocasião. Para um toque especial, finalize com decoração de chocolate branco e surpreenda na apresentação.",
            tempoPreparo = "40 min",
            porcoes = "15",
            ingredientes = listOf(
                "Ingredientes do creme\n" +
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
                    "350 gramas de biscoito maizena\n"
            ),
            modoPreparo = listOf(
                EtapaPreparo(1, "Para preparar essa receita, separe os ingredientes necessários sobre sua bancada;", R.drawable.pave_de_bolacha_preparo_1),
                EtapaPreparo(2, "Dissolva o amido de milho no leite, depois despeje em uma panela média com o leite condensado;", R.drawable.pave_de_bolacha_preparo_2),
                EtapaPreparo(3, "Adicione as gemas peneiradas, o creme de leite e o extrato de baunilha;", R.drawable.pave_de_bolacha_preparo_3),
                EtapaPreparo(4, "Leve ao fogo médio, mexendo sem parar, até levantar fervura. Quando começar a borbulhar, conte 4 minutos e desligue o fogo;", R.drawable.pave_de_bolacha_preparo_4),
                EtapaPreparo(5, "Transfira o creme para um recipiente e deixe esfriar em temperatura ambiente por cerca de 30 minutos;", R.drawable.pave_de_bolacha_preparo_5),
                EtapaPreparo(6, "Em outra tigela, coloque os 200 ml de leite para umedecer os biscoitos;", R.drawable.pave_de_bolacha_preparo_6),
                EtapaPreparo(7, "Em uma travessa média (24cm), faça uma camada de creme e cubra com os biscoitos umedecidos no leite;", R.drawable.pave_de_bolacha_preparo_7),
                EtapaPreparo(8, "Repita as camadas até acabar os ingredientes, finalizando com o creme. Para a ganache, leve o chocolate meio amargo e o creme de leite ao micro-ondas por 1 minuto, em potência média, parando a cada 30 segundos para mexer;", R.drawable.pave_de_bolacha_preparo_8),
                EtapaPreparo(9, "Misture bem até obter um creme brilhante e homogêneo e espalhe sobre o pavê com uma espátula;", R.drawable.pave_de_bolacha_preparo_9),
                EtapaPreparo(10, "Leve à geladeira por no mínimo 4 horas antes de servir. E está pronto!", R.drawable.pave_de_bolacha_preparo_10)
            ),
            tag = "SOBREMESA",
            imagem = R.drawable.pave_de_bolacha_capa,
            itensPadraoRelacionados = listOf("Biscoito", "Leite", "Ovos", "Iogurte", "Açúcar")
        ),

        Receita(
            nome = "Bolo de Cenoura",
            descricao = "O cheirinho de bolo de cenoura saindo do forno traz alegria para a casa, a sensação de lar. Nessa receita, a massa fica fofinha, cresce que é uma beleza. O sabor equilibrado, doce na medida certa. Na cobertura, uma calda de chocolate cremosa. Já pode passar um cafezinho!",
            tempoPreparo = "60 min",
            porcoes = "10",
            ingredientes = listOf(
                "Ingredientes da massa\n" +
                    "3 cenouras médias (cerca de 360 gramas)\n" +
                    "4 ovos\n" +
                    "1 xícara de chá de óleo de milho (240 ml)\n" +
                    "1 e 1/2 xícara de chá de açúcar (300 gramas)\n" +
                    "2 xícaras de chá de farinha de trigo (280 gramas)\n" +
                    "1 colher de sopa de fermento químico em pó (fermento para bolo) (14 gramas)\n" +
                    "1 pitada de sal (1/4 de colher de chá)\n" +
                    "Manteiga e farinha de trigo para untar\n" +
                    "Ingredientes da calda\n" +
                    "1 caixa de leite condensado (395 gramas)\n" +
                    "1 colher de sopa de manteiga sem sal (14 gramas)\n" +
                    "4 colheres de sopa de chocolate em pó (ou 7 colheres de sopa de achocolatado)\n" +
                    "1/2 caixinha de creme de leite (100 gramas)\n" +
                    "Chocolate granulado para decorar"
            ),
            modoPreparo = listOf(
                EtapaPreparo(1, "Higienize, descasque e corte as cenouras em rodelas. É possível substituir o óleo de milho por de girassol ou soja (prefira óleos com sabor mais neutro). Quebre os ovos em um recipiente separado para ver se todos estão bons. Coloque o forno para preaquecer a 180 ºC.", R.drawable.bolo_de_cenoura_preparo_1),
                EtapaPreparo(2, "Em um recipiente, peneire a farinha (para deixar seu bolo mais leve e sem gruminhos). Adicione o sal e o fermento. Misture bem. Reserve;", R.drawable.bolo_de_cenoura_preparo_2),
                EtapaPreparo(3, "No liquidificador, coloque as cenouras, o óleo, os ovos e o açúcar. Bata por cerca de 5 minutos, ou até ficar uma mistura bem homogênea. Despeje em um recipiente grande;", R.drawable.bolo_de_cenoura_preparo_3),
                EtapaPreparo(4, "Gradualmente, vá adicionando a mistura de ingredientes secos. Com o auxílio de um fuê (batedor de bolo), mexa a massa cuidadosamente, apenas para incorporar bem. Não bata o bolo com movimentos rápidos, pois isso ativa o glúten do trigo e a massa fica solada;", R.drawable.bolo_de_cenoura_preparo_4),
                EtapaPreparo(5, "Assim que obter uma massa homogênea, despeje-a em uma forma redonda média (22 cm de diâmetro), untada e enfarinhada. Leve ao forno preaquecido a 180 ºC por cerca de 45 a 50 minutos - quando você espetar o garfo e ele sair limpinho, o bolo está assado.", R.drawable.bolo_de_cenoura_preparo_5),
                EtapaPreparo(6, "Enquanto o bolo de cenoura esfria um pouquinho, você prepara a calda de chocolate; Em uma panela, coloque o leite condensado, a manteiga e o chocolate. Com uma espátula, misture bem até ficar homogêneo;", R.drawable.bolo_de_cenoura_preparo_6),
                EtapaPreparo(7, "Leve ao fogo médio e mexa sem parar até atingir o ponto de brigadeiro (quando começa a desgrudar do fundo da panela). Desligue o fogo, despeje o creme de leite e misture para incorporar;", R.drawable.bolo_de_cenoura_preparo_7),
                EtapaPreparo(8, "Quando o bolo estiver morno, desenforme e despeje a cobertura por cima;", R.drawable.bolo_de_cenoura_preparo_8),
                EtapaPreparo(9, "Decore com chocolate granulado. Pode servir quente ou frio.", R.drawable.bolo_de_cenoura_preparo_9)
            ),
            tag = "SOBREMESA",
            imagem = R.drawable.bolo_de_cenoura_capa,
            itensPadraoRelacionados = listOf("Cenoura", "Ovos", "Açúcar", "Farinha de Trigo", "Manteiga", "Leite", "Café")
        )
    )
}
