CREATE TABLE usuarios (
    id_usuario INT PRIMARY KEY,
    email VARCHAR(50) NOT NULL UNIQUE,
    nome_usuario VARCHAR(50) NOT NULL,
    senha VARCHAR(50) NOT NULL
) CHARACTER SET utf8mb4;


INSERT INTO usuarios (id_usuario, email, nome_usuario, senha) VALUES
(1, 'sandy@gmail.com', 'Sandy', '123'),
(2, 'ana@gmail.com', 'Ana', '123'),
(3, 'walyson@gmail.com', 'Walyson', '123');








CREATE TABLE categorias (
    id_categoria INT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
) CHARACTER SET utf8mb4;

INSERT INTO categorias (id_categoria, nome) VALUES
(1, 'Bebidas'),
(2, 'Carnes e Peixes'),
(3, 'Enlatados'),
(4, 'Grãos'),
(5, 'Hortifruti'),
(6, 'Pães, Massas e Doces'),
(7, 'Laticínios'),
(8, 'Ovos'),
(9, 'Higiene, Perfumaria e Farmacia'),
(10, 'Limpeza');









CREATE TABLE itens_padrao (
    id_item_padrao INT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    id_categoria INT NOT NULL,
    imagem VARCHAR(150),
    FOREIGN KEY (id_categoria) REFERENCES categorias(id_categoria)
) CHARACTER SET utf8mb4;

INSERT INTO itens_padrao (id_item_padrao, nome, id_categoria, imagem) VALUES
(1, 'Água Mineral', 1, 'bebidas_agua_mineral.png'),
(2, 'Suco', 1, 'bebidas_suco.png'),
(3, 'Frango', 2, 'carnes_e_peixes_frango.png'),
(4, 'Carne Bovina', 2, 'carnes_e_peixes_boi.png'),
(5, 'Carne Vegana', 2, 'carnes_e_peixes_carne_vegana.png'),
(6, 'Peixe', 2, 'carnes_e_peixes_peixe_tilapia.png'),
(7, 'Atum', 3, 'enlatados_atum.png'),
(8, 'Milho em Conserva', 3, 'enlatados_milho_conserva.png'),
(9, 'Ervilha em Conserva', 3, 'enlatados_ervilha.png'),
(10, 'Arroz', 4, 'graos_arroz.png'),
(11, 'Aveia', 4, 'graos_aveia.png'),
(12, 'Café', 4, 'graos_cafe.png'),
(13, 'Feijão', 4, 'graos_feijao.png'),
(14, 'Alface', 5, 'hortifruti_alface.png'),
(15, 'Banana', 5, 'hortifruti_banana.png'),
(16, 'Batatas', 5, 'hortifruti_batatas.png'),
(17, 'Beringela', 5, 'hortifruti_beringela.png'),
(18, 'Brocolis', 5, 'hortifruti_brocolis.png'),
(19, 'Cebola', 5, 'hortifruti_cebola.png'),
(20, 'Cenoura', 5, 'hortifruti_cenoura.png'),
(21, 'Maçã', 5, 'hortifruti_maca.png'),
(22, 'Tomates', 5, 'hortifruti_tomates.png'),
(23, 'Açúcar', 6, 'paes_massas_e_doces_acucar_blocos.png'),
(24, 'Biscoito', 6, 'paes_massas_e_doces_biscoito_recheado.png'),
(25, 'Farinha de Trigo', 6, 'paes_massas_e_doces_farinha_de_trigo.png'),
(26, 'Macarrão', 6, 'pas_massas_e_doces_macarrao.png'),
(27, 'Pão', 6, 'pães_massas_e_doces_pao_frances.png'),
(28, 'Iogurte', 7, 'laticinios_iogurte.png'),
(29, 'Leite', 7, 'laticinios_leite.png'),
(30, 'Manteiga', 7, 'laticinios_manteiga.png'),
(31, 'Queijo', 7, 'laticinios_queijo.png'),
(32, 'Ovos', 8, 'ovos.png'),
(33, 'Dipirona', 9, 'higiene_perfumaria_e_farmacia_dipirona.png'),
(34, 'Papel Higienico', 9, 'higiene_perfumaria_e_farmacia_papel_higienico.png'),
(35, 'Creme Dental', 9, 'higiene_perfumaria_e_farmacia_creme_dental.png'),
(36, 'Xampu/Condicionador', 9, 'higiene_perfumaria_e_farmacia_xampu_condicionador.png'),
(37, 'Perfume', 9, 'higiene_perfumaria_e_farmacia_perfume_hello_kitti.png'),
(38, 'Água Sanitária', 10, 'limpeza_agua_sanitaria.png'),
(39, 'Detergente', 10, 'limpeza_detergente.png'),
(40, 'Sabão em Pó', 10, 'limpeza_sabao_po.png');








CREATE TABLE medidas (
    id_medida INT PRIMARY KEY,
    nome_medida VARCHAR(50) NOT NULL
) CHARACTER SET utf8mb4;

INSERT INTO medidas (id_medida, nome_medida) VALUES
(1, 'Unidade'),
(2, 'Pacote'),
(3, 'Caixa'),
(4, 'Grama'),
(5, 'Kg'),
(6, 'Ml'),
(7, 'Litro');








CREATE TABLE local_armazenamento (
    id_local_armazenamento INT PRIMARY KEY,
    nome_local_armazenamento VARCHAR(100) NOT NULL,
    imagem VARCHAR(150) NOT NULL
) CHARACTER SET utf8mb4;


INSERT INTO local_armazenamento 
(id_local_armazenamento, nome_local_armazenamento, imagem) 
VALUES
(1, 'Geladeira', 'local_icone_geladeira.png'),
(2, 'Freezer', 'local_icone_freezer.png'),
(3, 'Fruteira', 'local_icone_fruteira.png'),
(4, 'Armário', 'local_icone_armario.png'),
(5, 'Banheiro', 'local_icone_banheiro.png'),
(6, 'Lavanderia', 'local_icone_lavanderia.png');







CREATE TABLE flags (
    id_flag INT PRIMARY KEY,
    nome_flag VARCHAR(50) NOT NULL
) CHARACTER SET utf8mb4;


INSERT INTO flags (id_flag, nome_flag) VALUES
(1, 'Vencido'),
(2, 'Crítico'),
(3, 'Atenção'),
(4, 'Seguro');









CREATE TABLE itens_usuario (
    id_item_usuario INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    nome VARCHAR(100) NOT NULL,
    id_categoria INT NOT NULL,
    imagem VARCHAR(150),
    consumindo BOOLEAN,
    possui_data_validade BOOLEAN,
    data_validade DATE,
    id_local_armazenamento INT,
    quantidade DECIMAL(10,2),
    id_medida INT,
    preco DECIMAL(10,2),
    id_flag INT,
    deletado BOOLEAN DEFAULT FALSE,
    razao_delecao VARCHAR(100),

	FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_categoria) REFERENCES categorias(id_categoria),
    FOREIGN KEY (id_local_armazenamento) REFERENCES local_armazenamento(id_local_armazenamento),
    FOREIGN KEY (id_medida) REFERENCES medidas(id_medida),
    FOREIGN KEY (id_flag) REFERENCES flags(id_flag)
) CHARACTER SET utf8mb4;













-- RECEITAS
CREATE TABLE `receitas` (
  `ID` INT NOT NULL PRIMARY KEY,
  `img capa` VARCHAR(255),
  `NOME` VARCHAR(255),
  `TEMPO` VARCHAR(50),
  `PORÇÃO` INT,
  `TAG` VARCHAR(100),
  `DESCRIÇÃO` TEXT,
  `INGREDIENTES` TEXT,
  `Img 1` VARCHAR(255),
  `MODO DE PREPARO 1` TEXT,
  `img 2` VARCHAR(255),
  `MODO DE PREPARO 2` TEXT,
  `img 3` VARCHAR(255),
  `MODO DE PREPARO 3` TEXT,
  `img 4` VARCHAR(255),
  `MODO DE PREPARO 4` TEXT,
  `img 5` VARCHAR(255),
  `MODO DE PREPARO 5` TEXT,
  `img 6` VARCHAR(255),
  `MODO DE PREPARO 6` TEXT,
  `img 7` VARCHAR(255),
  `MODO DE PREPARO 7` TEXT
) CHARACTER SET utf8mb4;

-- Feijoada
INSERT INTO `receitas` (
  `ID`,`img capa`,`NOME`,`TEMPO`,`PORÇÃO`,`TAG`,`DESCRIÇÃO`,`INGREDIENTES`,
  `Img 1`,`MODO DE PREPARO 1`,`img 2`,`MODO DE PREPARO 2`,`img 3`,`MODO DE PREPARO 3`,
  `img 4`,`MODO DE PREPARO 4`,`img 5`,`MODO DE PREPARO 5`,`img 6`,`MODO DE PREPARO 6`,
  `img 7`,`MODO DE PREPARO 7`
) VALUES (
  1,
  'feijoada_cap.png',
  'Feijoada',
  '50 min',
  4,
  'Prato Principal',
  'Essa feijoada é ideal para quem busca o sabor autêntico do prato, sem um longo tempo de preparo. Assim, você pode fazê-la para um almoço delicioso em qualquer dia da semana.',
  '1 kg de feijão preto\n800 gramas de pé de porco\n800 gramas de costelinha de porco\n350 gramas de linguiça calabresa\n300 gramas de bacon\n2 cubos de caldo de carne ou bacon\n1 cebola grande (160 gramas)\n10 dentes de alho\n3 folhas de louro\n1 colher de sopa de sal (ou a gosto)',
  'feijoada_preparo_1.png',
  'Antes de começar, lave o feijão preto em água corrente, coloque 2 xícaras de água e escorra. Reúna os outros ingredientes e inicie o preparo;',
  'feijoada_preparo_2.png',
  'Corte a costelinha de porco em pedaços grandes, um osso por vez. Retire o couro do bacon e corte em cubos grandes. Fatie a linguiça calabresa em rodelas grossas. Corte o pé de porco em pedaços menores. Descasque e pique finamente o alho e a cebola em cubos;',
  'feijoada_preparo_3.png',
  'Coloque o feijão, o pé de porco e a costelinha na panela de pressão. Cubra com água, coloque as folhas de louro e o couro do bacon. Tampe a panela e leve ao fogo alto. Quando começar a pegar pressão, cozinhe por 15 minutos;',
  'feijoada_preparo_4.png',
  'Enquanto isso, leve uma panela grande e de fundo grosso ao fogo alto. Quando aquecer, adicione o bacon (se necessário, adicione azeite). Frite o bacon até começar a ficar crocante. Adicione a calabresa e frite por cerca de 6 minutos, até que também fique dourada;',
  'feijoada_preparo_5.png',
  'Junte o alho e a cebola na panela, e refogue por 1 minuto. Acrescente os tabletes de carne e, com uma colher de pau, esfarele-os. Adicione um pouco de água e raspe os queimadinhos do fundo da panela para incorporar os sabores;',
  'feijoada_preparo_6.png',
  'Quando terminar o cozimento da pressão, desligue o fogo e, com cuidado, retire a pressão da panela e abra. Com uma concha, adicione o feijão com as carnes no refogado e misture bem. Prove e adicione sal, se necessário. Abaixe o fogo, tampe a panela e deixe',
  'feijoada_preparo_7.png',
  'E está pronta! Sirva a feijoada com arroz branco e couve.'
);

-- Moqueca de peixe
INSERT INTO `receitas` (
  `ID`,`img capa`,`NOME`,`TEMPO`,`PORÇÃO`,`TAG`,`DESCRIÇÃO`,`INGREDIENTES`,
  `Img 1`,`MODO DE PREPARO 1`,`img 2`,`MODO DE PREPARO 2`,`img 3`,`MODO DE PREPARO 3`,
  `img 4`,`MODO DE PREPARO 4`,`img 5`,`MODO DE PREPARO 5`,`img 6`,`MODO DE PREPARO 6`,
  `img 7`,`MODO DE PREPARO 7`
) VALUES (
  2,
  'moqueca_de_peixe.png',
  'Moqueca de peixe',
  '35 min',
  4,
  'Prato Principal',
  'Moqueca tem que ter azeite de dendê e leite de coco. Capriche no refogado de tomate, cebola e pimentão, pois ele é a base para saborizar o caldo. Fique de olho no ponto do peixe: queremos ele macio, mas sem se desmanchar na panela.',
  '500 gramas de filé de tilápia\n2 colheres de sopa de azeite\n2 dentes de alho amassados\n1 cebola média picada\n4 colheres de sopa de óleo de dendê\n4 tomates italianos picados\n1 pimentão verde picado\nSal e pimenta-do-reino a gosto\n200 ml de leite de coco\nSalsinha a gosto',
  'moqueca_de_peixe_preparo_1.png',
  'Reúna todos os ingredientes da moqueca de peixe',
  'moqueca_de_peixe_preparo_2.png',
  'Em uma panela, aqueça o azeite e refogue o alho e a cebola, até murchar e dourar levemente;',
  'moqueca_de_peixe_prepararo_3.png',
  'Adicione 2 colheres de óleo de dendê e os tomates picados. Refogue os tomates até murchar, mexendo às vezes para não queimar;',
  'moqueca_de_peixe_prepar o_4.png',
  'Acrescente o pimentão, o restante do óleo de dendê e metade do leite de coco. Tempere com sal e pimenta-do-reino a gosto, misture e cozinhe por mais 2 minutos;',
  'moqueca_de_peixe_prep aro_5.png',
  'Acomode os filés de peixe, despeje o restante do leite de coco e misture cuidadosamente para não quebrar os filés. Tampe a panela e deixe cozinhar por cerca de 10 a 15 minutos em fogo médio;',
  'moqueca_de_peixe_prepar o_6.png',
  'Prove o molho e, se necessário, acerte o sal e temperos. Verifique se o peixe está completamente cozido, caso ainda não esteja, cozinhe por mais 5 minutos. Finalize com salsinha e misture bem;',
  'moqueca_de_peixe_preparo_7.png',
  'Agora é só servir essa moqueca baiana deliciosa! Você pode servir com arroz branco, farofa de dendê, pirão de peixe e uma salada fresca, fica incrível!'
);

-- Farofa de banana
INSERT INTO `receitas` (
  `ID`,`img capa`,`NOME`,`TEMPO`,`PORÇÃO`,`TAG`,`DESCRIÇÃO`,`INGREDIENTES`,
  `Img 1`,`MODO DE PREPARO 1`,`img 2`,`MODO DE PREPARO 2`,`img 3`,`MODO DE PREPARO 3`,
  `img 4`,`MODO DE PREPARO 4`,`img 5`,`MODO DE PREPARO 5`,`img 6`,`MODO DE PREPARO 6`,
  `img 7`,`MODO DE PREPARO 7`
) VALUES (
  5,
  'farofa_de_banana_capa.png',
  'Farofa de banana',
  '20 min',
  4,
  'Acompanhamento',
  'A farofa de banana é uma adição encantadora e saborosa para complementar suas refeições. Com poucos ingredientes e um processo simples, esta receita oferece um toque especial e delicioso para acompanhar diversos pratos.',
  '2 bananas-prata\n2 colheres de sopa de manteiga\n1/2 cebola cortada em cubos pequenos\n1 dente de alho picado\nSal a gosto\nPimenta-do-reino a gosto\n1 e 1/2 xícara de chá de farinha de mandioca torrada',
  'farofa_de_banana_preparo_1.png',
  'Reúna todos os ingredientes;',
  'farofa_de_banana_preparo_2.png',
  'Descasque as bananas e corte-as em pedaços médios;',
  'farofa_de_banana_preparo_3.png',
  'Em uma panela, sobre o fogo médio, derreta a manteiga e refogue a cebola com uma pitada de sal;',
  'farofa_de_banana_prepar o_4.png',
  'Assim que a cebola murchar, coloque o alho e refogue por mais 1 minuto;',
  'farofa_de_banana_preparo_5.png',
  'Acrescente as bananas, sal, pimenta e misture, delicadamente, por mais 1 minuto;',
  'farofa_de_banana_preparo_6.png',
  'Depois, aos poucos, acrescente a farinha de mandioca. Misture todos os ingredientes, com cuidado, até que tudo se incorpore. Deixa cozinhar por cerca de 5 minutos, mexendo ocasionalmente. Na sequência, está pronta para servir.',
  NULL, NULL
);

-- Batata sauté
INSERT INTO `receitas` (
  `ID`,`img capa`,`NOME`,`TEMPO`,`PORÇÃO`,`TAG`,`DESCRIÇÃO`,`INGREDIENTES`,
  `Img 1`,`MODO DE PREPARO 1`,`img 2`,`MODO DE PREPARO 2`,`img 3`,`MODO DE PREPARO 3`,
  `img 4`,`MODO DE PREPARO 4`,`img 5`,`MODO DE PREPARO 5`,`img 6`,`MODO DE PREPARO 6`,
  `img 7`,`MODO DE PREPARO 7`
) VALUES (
  6,
  'batata_saute_capa.png',
  'Batata sauté',
  '20 min',
  4,
  'Entrada',
  'Uma entrada prática, saborosa e muito fácil de preparar! A batata sauté é douradinha por fora e macia por dentro, levando poucos ingredientes e pronta em poucos minutos, graças a um truque simples com o micro-ondas.',
  '4 batatas médias\nSal a gosto\nPimenta-do-reino a gosto\n2 colheres de sopa de manteiga\nSalsinha a gosto\n1 saquinho para verdura',
  'batata_saute_preparo_1.png',
  'Reúna todos os ingredientes na bancada! Descasque as batatas e corte-as em pedaços médios. Pique a salsinha finamente e reserve;',
  'batata_saute_preparo_2.png',
  'Em um bowl grande, coloque as batatas cortadas e tempere com sal e pimenta-do-reino a gosto;',
  'batata_saute_preparo_3.png',
  'Transfira as batatas para um saquinho de verduras, dê um nó na ponta e faça pequenos furinhos no plástico para permitir a saída do vapor;',
  'batata_saute_preparo_4.png',
  'Leve o saquinho ao micro-ondas por 5 a 6 minutos. Esse processo cozinha as batatas de maneira rápida. Ao retirar o saquinho, tenha cuidado ao abrir, pois o vapor quente pode causar queimaduras;',
  'batata_saute_preparo_5.png',
  'Aqueça uma frigideira grande e derreta nela a manteiga. Adicione as batatas cozidas e mexa delicadamente para não quebrá-las. Deixe dourar bem de todos os lados;',
  'batata_saute_preparo_6.png',
  'Acrescente a salsinha e misture rapidamente. Sirva como acompanhamento para carnes, peixes ou até mesmo como um prato principal!',
  NULL, NULL
);