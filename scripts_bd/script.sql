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