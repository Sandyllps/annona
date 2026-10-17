CREATE TABLE usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(50) NOT NULL UNIQUE,
    nome_usuario VARCHAR(50) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    notificacoes_ativas BOOLEAN DEFAULT TRUE
) CHARACTER SET utf8mb4;


-- Senhas de exemplo com hash (correspondem a '123'; o servidor também aceita '123' legado e atualiza automaticamente para hash)
INSERT INTO usuarios (id_usuario, email, nome_usuario, senha, notificacoes_ativas) VALUES
(1, 'sandy@gmail.com', 'Sandy', '1618033988749894:bf878ded5b4f4e3d2d4d19637277907d9cac74c8d93083ab2890516b52a9e22ee84327730ffe1f3a0bed366e5661e5760b33aa910dcd54a3352d1fe3350005e1', TRUE),
(2, 'ana@gmail.com', 'Ana', '1618033988749894:bf878ded5b4f4e3d2d4d19637277907d9cac74c8d93083ab2890516b52a9e22ee84327730ffe1f3a0bed366e5661e5760b33aa910dcd54a3352d1fe3350005e1', TRUE),
(3, 'walyson@gmail.com', 'Walyson', '1618033988749894:bf878ded5b4f4e3d2d4d19637277907d9cac74c8d93083ab2890516b52a9e22ee84327730ffe1f3a0bed366e5661e5760b33aa910dcd54a3352d1fe3350005e1', TRUE);




CREATE TABLE categorias (
    id_categoria INT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    dica VARCHAR(255)
) CHARACTER SET utf8mb4;

INSERT INTO categorias (id_categoria, nome, dica) VALUES
(1, 'Bebidas', 'Mantenha em local fresco e longe da luz direta.'),
(2, 'Carnes e Peixes', 'Mantenha no congelador se não for consumir em 24h.'),
(3, 'Enlatados', 'Após aberto, guarde na geladeira.'),
(4, 'Grãos', 'Guarde em potes bem fechados, em local seco e longe da luz.'),
(5, 'Hortifruti', 'Lave bem antes de guardar.'),
(6, 'Pães, Massas e Doces', 'Mantenha em local fresco e longe da umidade.'),
(7, 'Laticínios', 'Guarde na geladeira, nas prateleiras superiores onde é mais frio.'),
(8, 'Ovos', 'Guarde na geladeira.'),
(9, 'Higiene, Perfumaria e Farmacia', 'Mantenha longe do calor.'),
(10, 'Limpeza', 'Mantenha em local arejado.');




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
(23, 'Açúcar', 6, 'paes_massas_e_doces_acucar_cubos.png'),
(24, 'Biscoito', 6, 'paes_massas_e_doces_biscoito_recheado.png'),
(25, 'Farinha de Trigo', 6, 'paes_massas_e_doces_farinha_de_trigo.png'),
(26, 'Macarrão', 6, 'paes_massas_e_doces_macarrao.png'),
(27, 'Pão', 6, 'paes_massas_e_doces_pao_frances.png'),
(28, 'Iogurte', 7, 'laticinios_iogurte.png'),
(29, 'Leite', 7, 'laticinios_leite.png'),
(30, 'Manteiga', 7, 'laticinios_manteiga.png'),
(31, 'Queijo', 7, 'laticinios_queijo.png'),
(32, 'Ovos', 8, 'ovos.png'),
(33, 'Dipirona', 9, 'higiene_perfumaria_e_farmacia_dipirona.png'),
(34, 'Papel Higienico', 9, 'higiene_perfumaria_e_farmacia_papel_higienico.png'),
(35, 'Creme Dental', 9, 'higiene_perfumaria_e_farmacia_creme_dental.png'),
(36, 'Xampu/Condicionador', 9, 'higiene_perfumaria_e_farmacia_xampu_condicionador.png'),
(37, 'Perfume', 9, 'higiene_perfumaria_e_farmacia_hello_kitti.png'),
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
    consumindo BOOLEAN DEFAULT FALSE,
    possui_data_validade BOOLEAN DEFAULT FALSE,
    data_validade DATE,
    id_local_armazenamento INT,
    quantidade DECIMAL(10,2) DEFAULT 0,
    id_medida INT,
    preco DECIMAL(10,2) DEFAULT 0,
    id_flag INT DEFAULT 4,
    fora_doacao BOOLEAN DEFAULT FALSE,
    deletado BOOLEAN DEFAULT FALSE,
    razao_delecao VARCHAR(100),
    data_delecao DATETIME DEFAULT NULL,

    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_categoria) REFERENCES categorias(id_categoria),
    FOREIGN KEY (id_local_armazenamento) REFERENCES local_armazenamento(id_local_armazenamento),
    FOREIGN KEY (id_medida) REFERENCES medidas(id_medida),
    FOREIGN KEY (id_flag) REFERENCES flags(id_flag)
) CHARACTER SET utf8mb4;

-- ITENS DE EXEMPLO NA DESPENSA, DOAÇÃO, RECEITAS E HISTÓRICO (Para todos os 3 usuários: 1=Sandy, 2=Ana, 3=Walyson)
-- Inclui todas as tags: Vencido (1), Crítico (2), Atenção (3), Seguro (4), Consumindo, Por consumir, Doação e Histórico
INSERT INTO itens_usuario (
    id_item_usuario, id_usuario, nome, id_categoria, imagem, consumindo,
    possui_data_validade, data_validade, id_local_armazenamento, quantidade,
    id_medida, preco, id_flag, fora_doacao, deletado, razao_delecao, data_delecao
) VALUES
-- ==================== USUÁRIO 1 (Sandy) ====================
-- Vencido (id_flag = 1)
(1, 1, 'Iogurte', 7, 'laticinios_iogurte.png', TRUE, TRUE, DATE_ADD(CURDATE(), INTERVAL -3 DAY), 1, 2, 1, 4.50, 1, FALSE, FALSE, NULL, NULL),
(2, 1, 'Pão', 6, 'paes_massas_e_doces_pao_frances.png', TRUE, TRUE, DATE_ADD(CURDATE(), INTERVAL -2 DAY), 4, 1, 2, 7.90, 1, FALSE, FALSE, NULL, NULL),
-- Crítico (id_flag = 2 | 0 a 3 dias -> Por consumir aparecem em DOAÇÃO)
(3, 1, 'Leite', 7, 'laticinios_leite.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 1, 3, 3, 5.49, 2, FALSE, FALSE, NULL, NULL),
(4, 1, 'Peixe', 2, 'carnes_e_peixes_peixe_tilapia.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 2 DAY), 1, 1, 5, 32.90, 2, FALSE, FALSE, NULL, NULL),
(5, 1, 'Frango', 2, 'carnes_e_peixes_frango.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 3 DAY), 2, 1, 5, 21.90, 2, FALSE, FALSE, NULL, NULL),
(6, 1, 'Tomates', 5, 'hortifruti_tomates.png', TRUE, TRUE, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 1, 500, 4, 6.50, 2, FALSE, FALSE, NULL, NULL),
-- Atenção (id_flag = 3 | 4 a 7 dias -> Por consumir aparecem em DOAÇÃO)
(7, 1, 'Queijo', 7, 'laticinios_queijo.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 5 DAY), 1, 400, 4, 18.90, 3, FALSE, FALSE, NULL, NULL),
(8, 1, 'Biscoito', 6, 'paes_massas_e_doces_biscoito_recheado.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 6 DAY), 4, 3, 2, 4.20, 3, FALSE, FALSE, NULL, NULL),
(9, 1, 'Cenoura', 5, 'hortifruti_cenoura.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 7 DAY), 3, 4, 1, 4.80, 3, FALSE, FALSE, NULL, NULL),
(10, 1, 'Ovos', 8, 'ovos.png', TRUE, TRUE, DATE_ADD(CURDATE(), INTERVAL 5 DAY), 1, 12, 1, 11.90, 3, FALSE, FALSE, NULL, NULL),
-- Seguro (id_flag = 4 | 8+ dias ou sem validade -> Ingredientes para todas as receitas)
(11, 1, 'Arroz', 4, 'graos_arroz.png', TRUE, TRUE, DATE_ADD(CURDATE(), INTERVAL 45 DAY), 4, 5, 5, 26.90, 4, FALSE, FALSE, NULL, NULL),
(12, 1, 'Feijão', 4, 'graos_feijao.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 60 DAY), 4, 2, 5, 16.50, 4, FALSE, FALSE, NULL, NULL),
(13, 1, 'Macarrão', 6, 'paes_massas_e_doces_macarrao.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 90 DAY), 4, 2, 2, 6.90, 4, FALSE, FALSE, NULL, NULL),
(14, 1, 'Açúcar', 6, 'paes_massas_e_doces_acucar_cubos.png', TRUE, TRUE, DATE_ADD(CURDATE(), INTERVAL 120 DAY), 4, 1, 5, 5.20, 4, FALSE, FALSE, NULL, NULL),
(15, 1, 'Farinha de Trigo', 6, 'paes_massas_e_doces_farinha_de_trigo.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 75 DAY), 4, 1, 5, 6.40, 4, FALSE, FALSE, NULL, NULL),
(16, 1, 'Batatas', 5, 'hortifruti_batatas.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 12 DAY), 3, 1, 5, 8.90, 4, FALSE, FALSE, NULL, NULL),
(17, 1, 'Manteiga', 7, 'laticinios_manteiga.png', TRUE, TRUE, DATE_ADD(CURDATE(), INTERVAL 30 DAY), 1, 200, 4, 12.50, 4, FALSE, FALSE, NULL, NULL),
(18, 1, 'Cebola', 5, 'hortifruti_cebola.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 15 DAY), 3, 6, 1, 5.60, 4, FALSE, FALSE, NULL, NULL),
(19, 1, 'Milho em Conserva', 3, 'enlatados_milho_conserva.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 180 DAY), 4, 2, 1, 4.30, 4, FALSE, FALSE, NULL, NULL),
(20, 1, 'Sabão em Pó', 10, 'limpeza_sabao_po.png', TRUE, FALSE, NULL, 6, 1, 3, 14.90, 4, FALSE, FALSE, NULL, NULL),
-- Histórico (deletado = TRUE)
(21, 1, 'Café', 4, 'graos_cafe.png', TRUE, TRUE, DATE_ADD(CURDATE(), INTERVAL 20 DAY), 4, 1, 2, 18.90, 4, FALSE, TRUE, 'Item consumido', NOW()),
(22, 1, 'Banana', 5, 'hortifruti_banana.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL -4 DAY), 3, 6, 1, 5.90, 1, FALSE, TRUE, 'Item vencido / estragado', NOW()),
(23, 1, 'Suco', 1, 'bebidas_suco.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 3 DAY), 1, 2, 7, 9.80, 2, FALSE, TRUE, 'Item doado', NOW()),

-- ==================== USUÁRIO 2 (Ana) ====================
-- Vencido (id_flag = 1)
(24, 2, 'Iogurte', 7, 'laticinios_iogurte.png', TRUE, TRUE, DATE_ADD(CURDATE(), INTERVAL -3 DAY), 1, 2, 1, 4.50, 1, FALSE, FALSE, NULL, NULL),
(25, 2, 'Pão', 6, 'paes_massas_e_doces_pao_frances.png', TRUE, TRUE, DATE_ADD(CURDATE(), INTERVAL -2 DAY), 4, 1, 2, 7.90, 1, FALSE, FALSE, NULL, NULL),
-- Crítico (id_flag = 2 | 0 a 3 dias -> Por consumir aparecem em DOAÇÃO)
(26, 2, 'Leite', 7, 'laticinios_leite.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 1, 3, 3, 5.49, 2, FALSE, FALSE, NULL, NULL),
(27, 2, 'Peixe', 2, 'carnes_e_peixes_peixe_tilapia.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 2 DAY), 1, 1, 5, 32.90, 2, FALSE, FALSE, NULL, NULL),
(28, 2, 'Frango', 2, 'carnes_e_peixes_frango.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 3 DAY), 2, 1, 5, 21.90, 2, FALSE, FALSE, NULL, NULL),
(29, 2, 'Tomates', 5, 'hortifruti_tomates.png', TRUE, TRUE, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 1, 500, 4, 6.50, 2, FALSE, FALSE, NULL, NULL),
-- Atenção (id_flag = 3 | 4 a 7 dias -> Por consumir aparecem em DOAÇÃO)
(30, 2, 'Queijo', 7, 'laticinios_queijo.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 5 DAY), 1, 400, 4, 18.90, 3, FALSE, FALSE, NULL, NULL),
(31, 2, 'Biscoito', 6, 'paes_massas_e_doces_biscoito_recheado.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 6 DAY), 4, 3, 2, 4.20, 3, FALSE, FALSE, NULL, NULL),
(32, 2, 'Cenoura', 5, 'hortifruti_cenoura.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 7 DAY), 3, 4, 1, 4.80, 3, FALSE, FALSE, NULL, NULL),
(33, 2, 'Ovos', 8, 'ovos.png', TRUE, TRUE, DATE_ADD(CURDATE(), INTERVAL 5 DAY), 1, 12, 1, 11.90, 3, FALSE, FALSE, NULL, NULL),
-- Seguro (id_flag = 4 | 8+ dias ou sem validade -> Ingredientes para todas as receitas)
(34, 2, 'Arroz', 4, 'graos_arroz.png', TRUE, TRUE, DATE_ADD(CURDATE(), INTERVAL 45 DAY), 4, 5, 5, 26.90, 4, FALSE, FALSE, NULL, NULL),
(35, 2, 'Feijão', 4, 'graos_feijao.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 60 DAY), 4, 2, 5, 16.50, 4, FALSE, FALSE, NULL, NULL),
(36, 2, 'Macarrão', 6, 'paes_massas_e_doces_macarrao.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 90 DAY), 4, 2, 2, 6.90, 4, FALSE, FALSE, NULL, NULL),
(37, 2, 'Açúcar', 6, 'paes_massas_e_doces_acucar_cubos.png', TRUE, TRUE, DATE_ADD(CURDATE(), INTERVAL 120 DAY), 4, 1, 5, 5.20, 4, FALSE, FALSE, NULL, NULL),
(38, 2, 'Farinha de Trigo', 6, 'paes_massas_e_doces_farinha_de_trigo.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 75 DAY), 4, 1, 5, 6.40, 4, FALSE, FALSE, NULL, NULL),
(39, 2, 'Batatas', 5, 'hortifruti_batatas.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 12 DAY), 3, 1, 5, 8.90, 4, FALSE, FALSE, NULL, NULL),
(40, 2, 'Manteiga', 7, 'laticinios_manteiga.png', TRUE, TRUE, DATE_ADD(CURDATE(), INTERVAL 30 DAY), 1, 200, 4, 12.50, 4, FALSE, FALSE, NULL, NULL),
(41, 2, 'Cebola', 5, 'hortifruti_cebola.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 15 DAY), 3, 6, 1, 5.60, 4, FALSE, FALSE, NULL, NULL),
(42, 2, 'Milho em Conserva', 3, 'enlatados_milho_conserva.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 180 DAY), 4, 2, 1, 4.30, 4, FALSE, FALSE, NULL, NULL),
(43, 2, 'Sabão em Pó', 10, 'limpeza_sabao_po.png', TRUE, FALSE, NULL, 6, 1, 3, 14.90, 4, FALSE, FALSE, NULL, NULL),
-- Histórico (deletado = TRUE)
(44, 2, 'Café', 4, 'graos_cafe.png', TRUE, TRUE, DATE_ADD(CURDATE(), INTERVAL 20 DAY), 4, 1, 2, 18.90, 4, FALSE, TRUE, 'Item consumido', NOW()),
(45, 2, 'Banana', 5, 'hortifruti_banana.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL -4 DAY), 3, 6, 1, 5.90, 1, FALSE, TRUE, 'Item vencido / estragado', NOW()),
(46, 2, 'Suco', 1, 'bebidas_suco.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 3 DAY), 1, 2, 7, 9.80, 2, FALSE, TRUE, 'Item doado', NOW()),

-- ==================== USUÁRIO 3 (Walyson) ====================
-- Vencido (id_flag = 1)
(47, 3, 'Iogurte', 7, 'laticinios_iogurte.png', TRUE, TRUE, DATE_ADD(CURDATE(), INTERVAL -3 DAY), 1, 2, 1, 4.50, 1, FALSE, FALSE, NULL, NULL),
(48, 3, 'Pão', 6, 'paes_massas_e_doces_pao_frances.png', TRUE, TRUE, DATE_ADD(CURDATE(), INTERVAL -2 DAY), 4, 1, 2, 7.90, 1, FALSE, FALSE, NULL, NULL),
-- Crítico (id_flag = 2 | 0 a 3 dias -> Por consumir aparecem em DOAÇÃO)
(49, 3, 'Leite', 7, 'laticinios_leite.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 1, 3, 3, 5.49, 2, FALSE, FALSE, NULL, NULL),
(50, 3, 'Peixe', 2, 'carnes_e_peixes_peixe_tilapia.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 2 DAY), 1, 1, 5, 32.90, 2, FALSE, FALSE, NULL, NULL),
(51, 3, 'Frango', 2, 'carnes_e_peixes_frango.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 3 DAY), 2, 1, 5, 21.90, 2, FALSE, FALSE, NULL, NULL),
(52, 3, 'Tomates', 5, 'hortifruti_tomates.png', TRUE, TRUE, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 1, 500, 4, 6.50, 2, FALSE, FALSE, NULL, NULL),
-- Atenção (id_flag = 3 | 4 a 7 dias -> Por consumir aparecem em DOAÇÃO)
(53, 3, 'Queijo', 7, 'laticinios_queijo.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 5 DAY), 1, 400, 4, 18.90, 3, FALSE, FALSE, NULL, NULL),
(54, 3, 'Biscoito', 6, 'paes_massas_e_doces_biscoito_recheado.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 6 DAY), 4, 3, 2, 4.20, 3, FALSE, FALSE, NULL, NULL),
(55, 3, 'Cenoura', 5, 'hortifruti_cenoura.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 7 DAY), 3, 4, 1, 4.80, 3, FALSE, FALSE, NULL, NULL),
(56, 3, 'Ovos', 8, 'ovos.png', TRUE, TRUE, DATE_ADD(CURDATE(), INTERVAL 5 DAY), 1, 12, 1, 11.90, 3, FALSE, FALSE, NULL, NULL),
-- Seguro (id_flag = 4 | 8+ dias ou sem validade -> Ingredientes para todas as receitas)
(57, 3, 'Arroz', 4, 'graos_arroz.png', TRUE, TRUE, DATE_ADD(CURDATE(), INTERVAL 45 DAY), 4, 5, 5, 26.90, 4, FALSE, FALSE, NULL, NULL),
(58, 3, 'Feijão', 4, 'graos_feijao.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 60 DAY), 4, 2, 5, 16.50, 4, FALSE, FALSE, NULL, NULL),
(59, 3, 'Macarrão', 6, 'paes_massas_e_doces_macarrao.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 90 DAY), 4, 2, 2, 6.90, 4, FALSE, FALSE, NULL, NULL),
(60, 3, 'Açúcar', 6, 'paes_massas_e_doces_acucar_cubos.png', TRUE, TRUE, DATE_ADD(CURDATE(), INTERVAL 120 DAY), 4, 1, 5, 5.20, 4, FALSE, FALSE, NULL, NULL),
(61, 3, 'Farinha de Trigo', 6, 'paes_massas_e_doces_farinha_de_trigo.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 75 DAY), 4, 1, 5, 6.40, 4, FALSE, FALSE, NULL, NULL),
(62, 3, 'Batatas', 5, 'hortifruti_batatas.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 12 DAY), 3, 1, 5, 8.90, 4, FALSE, FALSE, NULL, NULL),
(63, 3, 'Manteiga', 7, 'laticinios_manteiga.png', TRUE, TRUE, DATE_ADD(CURDATE(), INTERVAL 30 DAY), 1, 200, 4, 12.50, 4, FALSE, FALSE, NULL, NULL),
(64, 3, 'Cebola', 5, 'hortifruti_cebola.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 15 DAY), 3, 6, 1, 5.60, 4, FALSE, FALSE, NULL, NULL),
(65, 3, 'Milho em Conserva', 3, 'enlatados_milho_conserva.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 180 DAY), 4, 2, 1, 4.30, 4, FALSE, FALSE, NULL, NULL),
(66, 3, 'Sabão em Pó', 10, 'limpeza_sabao_po.png', TRUE, FALSE, NULL, 6, 1, 3, 14.90, 4, FALSE, FALSE, NULL, NULL),
-- Histórico (deletado = TRUE)
(67, 3, 'Café', 4, 'graos_cafe.png', TRUE, TRUE, DATE_ADD(CURDATE(), INTERVAL 20 DAY), 4, 1, 2, 18.90, 4, FALSE, TRUE, 'Item consumido', NOW()),
(68, 3, 'Banana', 5, 'hortifruti_banana.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL -4 DAY), 3, 6, 1, 5.90, 1, FALSE, TRUE, 'Item vencido / estragado', NOW()),
(69, 3, 'Suco', 1, 'bebidas_suco.png', FALSE, TRUE, DATE_ADD(CURDATE(), INTERVAL 3 DAY), 1, 2, 7, 9.80, 2, FALSE, TRUE, 'Item doado', NOW());


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
  `MODO DE PREPARO 7` TEXT,
  `img 8` VARCHAR(255),
  `MODO DE PREPARO 8` TEXT,
  `img 9` VARCHAR(255),
  `MODO DE PREPARO 9` TEXT,
  `img 10` VARCHAR(255),
  `MODO DE PREPARO 10` TEXT
) CHARACTER SET utf8mb4;

-- 1: Feijoada
INSERT INTO `receitas` (
  `ID`,`img capa`,`NOME`,`TEMPO`,`PORÇÃO`,`TAG`,`DESCRIÇÃO`,`INGREDIENTES`,
  `Img 1`,`MODO DE PREPARO 1`,`img 2`,`MODO DE PREPARO 2`,`img 3`,`MODO DE PREPARO 3`,
  `img 4`,`MODO DE PREPARO 4`,`img 5`,`MODO DE PREPARO 5`,`img 6`,`MODO DE PREPARO 6`,
  `img 7`,`MODO DE PREPARO 7`
) VALUES (
  1,
  'feijoada_capa.png',
  'Feijoada',
  '50 min',
  10,
  'PRINCIPAL',
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
  'Quando terminar o cozimento da pressão, desligue o fogo e, com cuidado, retire a pressão da panela e abra. Com uma concha, adicione o feijão com as carnes no refogado e misture bem. Prove e adicione sal, se necessário. Abaixe o fogo, tampe a panela e deixe cozinhar por cerca de 10 minutos para apurar os sabores.',
  'feijoada_preparo_7.png',
  'E está pronta! Sirva a feijoada com arroz branco e couve.'
);

-- 2: Moqueca de peixe
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
  'PRINCIPAL',
  'Moqueca tem que ter azeite de dendê e leite de coco. Capriche no refogado de tomate, cebola e pimentão, pois ele é a base para saborizar o caldo. Fique de olho no ponto do peixe: queremos ele macio, mas sem se desmanchar na panela.',
  '500 gramas de filé de tilápia\n2 colheres de sopa de azeite\n2 dentes de alho amassados\n1 cebola média picada\n4 colheres de sopa de óleo de dendê\n4 tomates italianos picados\n1 pimentão verde picado\nSal e pimenta-do-reino a gosto\n200 ml de leite de coco\nSalsinha a gosto',
  'moqueca_de_peixe_preparo_1.png',
  'Reúna todos os ingredientes da moqueca de peixe',
  'moqueca_de_peixe_preparo_2.png',
  'Em uma panela, aqueça o azeite e refogue o alho e a cebola, até murchar e dourar levemente;',
  'moqueca_de_peixe_preparo_3.png',
  'Adicione 2 colheres de óleo de dendê e os tomates picados. Refogue os tomates até murchar, mexendo às vezes para não queimar;',
  'moqueca_de_peixe_preparo_4.png',
  'Acrescente o pimentão, o restante do óleo de dendê e metade do leite de coco. Tempere com sal e pimenta-do-reino a gosto, misture e cozinhe por mais 2 minutos;',
  'moqueca_de_peixe_preparo_5.png',
  'Acomode os filés de peixe, despeje o restante do leite de coco e misture cuidadosamente para não quebrar os filés. Tampe a panela e deixe cozinhar por cerca de 10 a 15 minutos em fogo médio;',
  'moqueca_de_peixe_preparo_6.png',
  'Prove o molho e, se necessário, acerte o sal e temperos. Verifique se o peixe está completamente cozido, caso ainda não esteja, cozinhe por mais 5 minutos. Finalize com salsinha e misture bem;',
  'moqueca_de_peixe_preparo_7.png',
  'Agora é só servir essa moqueca baiana deliciosa! Você pode servir com arroz branco, farofa de dendê, pirão de peixe e uma salada fresca, fica incrível!'
);

-- 3: Escondidinho de frango
INSERT INTO `receitas` (
  `ID`,`img capa`,`NOME`,`TEMPO`,`PORÇÃO`,`TAG`,`DESCRIÇÃO`,`INGREDIENTES`,
  `Img 1`,`MODO DE PREPARO 1`,`img 2`,`MODO DE PREPARO 2`,`img 3`,`MODO DE PREPARO 3`,
  `img 4`,`MODO DE PREPARO 4`,`img 5`,`MODO DE PREPARO 5`,`img 6`,`MODO DE PREPARO 6`,
  `img 7`,`MODO DE PREPARO 7`,`img 8`,`MODO DE PREPARO 8`,`img 9`,`MODO DE PREPARO 9`
) VALUES (
  3,
  'escondidinho_de_frango_com_batata.png',
  'Escondidinho de frango',
  '70 min',
  10,
  'PRINCIPAL',
  'Esse escondidinho de frango é uma receita prática, cremosa e cheia de sabor. O recheio temperado se mistura ao purê de batatas macio e ganha o toque irresistível do queijo gratinado. Ideal para o almoço de domingo ou para transformar o jantar em um momento especial!',
  '800 gramas de frango cozido e desfiado\n1 colher de sopa de manteiga (20 gramas)\n1 cebola média\n3 dentes de alho\n1 tomate médio\n2 colheres de sopa de extrato de tomate (40 gramas)\n3 colheres de sopa de coentro ou a gosto\n1 colher de chá de sal ou a gosto\n1/2 colher de chá de pimenta-do-reino ou a gosto\n1/2 colher de chá de páprica picante ou a gosto\n1 colher de chá de chimichurri ou a gosto\n1 colher de chá de colorau a gosto\n360 gramas de mussarela para gratinar\nIngredientes do purê:\n6 batatas grandes (aprox. 950g)\n2 colheres de sopa de manteiga (40 gramas)\n1 caixa de creme de leite (200 gramas)\n100 ml de leite\n1 colher de chá de sal ou a gosto\n1/2 de colher de chá de pimenta-do-reino ou a gosto',
  'escondidinho_preparo_1.png',
  'Para começar, organize sua bancada e separe todos os ingredientes. Descasque a batata, pique a cebola, o alho, o tomate e o coentro em pedaços pequenos;',
  'escondidinho_preparo_2.png',
  'Em seguida, coloque as batatas e água em uma panela de pressão. Cozinhe por 20 minutos até que fiquem bem macias;',
  'escondidinho_preparo_3.png',
  'Enquanto isso, em uma panela, aqueça a manteiga e refogue a cebola e o alho até dourar levemente. Adicione o frango desfiado e o tomate picado, misturando bem;',
  'escondidinho_preparo_4.png',
  'Tempere com sal, pimenta-do-reino, páprica, chimichurri e colorau a gosto. Coloque o extrato de tomate, mexa e deixe cozinhar por alguns minutos. Finalize o recheio com coentro picado, desligue o fogo e reserve;',
  'escondidinho_preparo_5.png',
  'Para o purê, amasse bem as batatas já cozidas até formar uma massa lisa;',
  'escondidinho_preparo_6.png',
  'Acrescente a manteiga, o creme de leite e o leite, misturando até obter consistência cremosa. Tempere com sal e pimenta-do-reino a gosto;',
  'escondidinho_preparo_7.png',
  'Para a montagem, comece com uma camada de purê, adicione o recheio de frango e finalize com outra camada de purê;',
  'escondidinho_preparo_8.png',
  'Polvilhe a mussarela ralada por cima e leve ao forno preaquecido a 200ºC para gratinar, cerca de 20 minutos ou até dourar;',
  'escondidinho_preparo_9.png',
  'Retire do forno e sirva ainda quente. Bom apetite!'
);

-- 4: Arroz à Grega
INSERT INTO `receitas` (
  `ID`,`img capa`,`NOME`,`TEMPO`,`PORÇÃO`,`TAG`,`DESCRIÇÃO`,`INGREDIENTES`,
  `Img 1`,`MODO DE PREPARO 1`,`img 2`,`MODO DE PREPARO 2`,`img 3`,`MODO DE PREPARO 3`,
  `img 4`,`MODO DE PREPARO 4`,`img 5`,`MODO DE PREPARO 5`,`img 6`,`MODO DE PREPARO 6`,
  `img 7`,`MODO DE PREPARO 7`,`img 8`,`MODO DE PREPARO 8`
) VALUES (
  4,
  'arroz_a_grega_tradicional_capa.png',
  'Arroz à Grega',
  '50 min',
  9,
  'ACOMPANHAMENTO',
  'Todo colorido e muito saboroso, o arroz à grega é um prato festivo, que acompanha muito bem carnes assadas e legumes grelhados. A uva-passa traz um sabor adocicado, que não fica enjoativo com o contraste dos pimentões. Um prato que transforma qualquer refeição em um momento especial.',
  '2 xícaras de chá de arroz agulhinha cru\n1 fio de azeite\n1/2 cebola média picada\n2 dentes de alho picados\n1 cenoura média picada\n1/2 xícara de chá de pimentão vermelho médio picado\n1/2 xícara de chá de pimentão amarelo médio picado\n1/2 xícara de chá de uva-passa\n1 latinha de milho-verde (sem água)\n1 latinha de ervilha (sem água)\n1/2 colher de sopa de sal\n4 xícaras de chá de água\n3 colheres de sopa de cheiro-verde picado',
  'arroz_a_grega_preparo_1.png',
  'Pegue todos os ingredientes que você precisa para preparar o arroz à grega. Já deixe os legumes lavados e picados. Se quiser, lave o milho e a ervilha para suavizar o sabor da água de conserva;',
  'arroz_a_grega_preparo_2.png',
  'Em uma panela grande, em fogo médio, esquente o azeite e refogue a cebola por 1 minuto, ou até que ela fique transparente;',
  'arroz_a_grega_preparo_3.png',
  'Coloque o alho, misture bem e refogue por mais 1 minuto, ou até o alho dourar. Mexa de vez em quando para não queimar;',
  'arroz_a_grega_preparo_4.png',
  'Acrescente a cenoura, os pimentões e a uva-passa. Misture para incorporar e deixe refogar por alguns segundos;',
  'arroz_a_grega_preparo_5.png',
  'Junte o milho-verde, a ervilha e o arroz. Mexa por cerca de 3 minutos para refogar mais um pouco;',
  'arroz_a_grega_preparo_6.png',
  'Coloque a água e o sal. Misture bem e tampe a panela. Deixe o arroz à grega cozinhar em fogo médio por 20 minutos, ou até secar a água;',
  'arroz_a_grega_preparo_7.png',
  'Desligue o fogo e deixe a panela tampada por alguns minutos - o arroz continuará cozinhando com o calor da panela. Finalize com o cheiro-verde;',
  'arroz_a_grega_preparo_8.png',
  'Prato colorido e cheio de textura. Já pode servir e aproveitar uma deliciosa refeição.'
);

-- 5: Farofa de Banana
INSERT INTO `receitas` (
  `ID`,`img capa`,`NOME`,`TEMPO`,`PORÇÃO`,`TAG`,`DESCRIÇÃO`,`INGREDIENTES`,
  `Img 1`,`MODO DE PREPARO 1`,`img 2`,`MODO DE PREPARO 2`,`img 3`,`MODO DE PREPARO 3`,
  `img 4`,`MODO DE PREPARO 4`,`img 5`,`MODO DE PREPARO 5`,`img 6`,`MODO DE PREPARO 6`,
  `img 7`,`MODO DE PREPARO 7`
) VALUES (
  5,
  'farofa_de_banana_capa.png',
  'Farofa de Banana',
  '20 min',
  4,
  'ACOMPANHAMENTO',
  'A farofa de banana é uma adição encantadora e saborosa para complementar suas refeições. Com poucos ingredientes e um processo simples, esta receita oferece um toque especial e delicioso para acompanhar diversos pratos.',
  '2 bananas-prata\n2 colheres de sopa de manteiga\n1/2 cebola cortada em cubos pequenos\n1 dente de alho picado\nSal a gosto\nPimenta-do-reino a gosto\n1 e 1/2 xícara de chá de farinha de mandioca torrada',
  'farofa_de_banana_preparo_1.png',
  'Reúna todos os ingredientes;',
  'farofa_de_banana_preparo_2.png',
  'Descasque as bananas e corte-as em pedaços médios;',
  'farofa_de_banana_preparo_3.png',
  'Em uma panela, sobre o fogo médio, derreta a manteiga e refogue a cebola com uma pitada de sal;',
  'farofa_de_banana_preparo_4.png',
  'Assim que a cebola murchar, coloque o alho e refogue por mais 1 minuto;',
  'farofa_de_banana_preparo_5.png',
  'Acrescente as bananas, sal, pimenta e misture, delicadamente, por mais 1 minuto;',
  'farofa_de_banana_preparo_6.png',
  'Depois, aos poucos, acrescente a farinha de mandioca. Misture todos os ingredientes, com cuidado, até que tudo se incorpore. Deixa cozinhar por cerca de 5 minutos, mexendo ocasionalmente. Na sequência, está pronta para servir.',
  NULL, NULL
);

-- 6: Batata Sauté
INSERT INTO `receitas` (
  `ID`,`img capa`,`NOME`,`TEMPO`,`PORÇÃO`,`TAG`,`DESCRIÇÃO`,`INGREDIENTES`,
  `Img 1`,`MODO DE PREPARO 1`,`img 2`,`MODO DE PREPARO 2`,`img 3`,`MODO DE PREPARO 3`,
  `img 4`,`MODO DE PREPARO 4`,`img 5`,`MODO DE PREPARO 5`,`img 6`,`MODO DE PREPARO 6`,
  `img 7`,`MODO DE PREPARO 7`
) VALUES (
  6,
  'batata_saute_capa.png',
  'Batata Sauté',
  '20 min',
  4,
  'ENTRADA',
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

-- 7: Salada de Lentilha
INSERT INTO `receitas` (
  `ID`,`img capa`,`NOME`,`TEMPO`,`PORÇÃO`,`TAG`,`DESCRIÇÃO`,`INGREDIENTES`,
  `Img 1`,`MODO DE PREPARO 1`,`img 2`,`MODO DE PREPARO 2`,`img 3`,`MODO DE PREPARO 3`,
  `img 4`,`MODO DE PREPARO 4`,`img 5`,`MODO DE PREPARO 5`
) VALUES (
  7,
  'salada_de_lentilha_capa.png',
  'Salada de Lentilha',
  '20 min',
  8,
  'ENTRADA',
  'Dizem que comer lentilha no Ano Novo atrai fartura e prosperidade. Então, vamos preparar uma salada no capricho, com um toque refrescante de hortelã. Cozinhe a lentilha no ponto al dente para ela não se desmanchar na travessa. Seja para a ceia da virada ou para o dia a dia, uma delícia.',
  '1 xícara de chá de lentilha crua\n1 litro de água\n60 ml de azeite de oliva\n1/2 pimentão vermelho picado\n1/2 cebola roxa picada\n1 tomate sem sementes picado\n1 e 1/2 colher de sopa de suco de limão\nSal a gosto\nCominho a gosto\nPimenta síria a gosto (ou pimenta-do-reino)\nSalsinha picada a gosto\nHortelã picada a gosto',
  'salada_de_lentilha_preparo_1.png',
  'Reúna todos os ingredientes na bancada!',
  'salada_de_lentilha_preparo_2.png',
  'Higienize a lentilha, transfira para uma panela, adicione 1 litro de água, leve para o fogo médio, deixe levantar fervura e cozinhe por 12 a 15 minutos (contados após levantar fervura). Reserve;',
  'salada_de_lentilha_preparo_3.png',
  'Em uma frigideira, esquente metade do azeite e refogue o pimentão e a cebola por 1 minuto;',
  'salada_de_lentilha_preparo_4.png',
  'Desligue o fogo, acrescente a lentilha al dente, o tomate, o suco de limão, o restante do azeite, o sal, o cominho, a pimenta síria e misture bem;',
  'salada_de_lentilha_preparo_5.png',
  'Transfira para um refratário e finalize com a salsinha e a hortelã. Agora é só servir!'
);

-- 8: Pavê de Bolacha
INSERT INTO `receitas` (
  `ID`,`img capa`,`NOME`,`TEMPO`,`PORÇÃO`,`TAG`,`DESCRIÇÃO`,`INGREDIENTES`,
  `Img 1`,`MODO DE PREPARO 1`,`img 2`,`MODO DE PREPARO 2`,`img 3`,`MODO DE PREPARO 3`,
  `img 4`,`MODO DE PREPARO 4`,`img 5`,`MODO DE PREPARO 5`,`img 6`,`MODO DE PREPARO 6`,
  `img 7`,`MODO DE PREPARO 7`,`img 8`,`MODO DE PREPARO 8`,`img 9`,`MODO DE PREPARO 9`,
  `img 10`,`MODO DE PREPARO 10`
) VALUES (
  8,
  'pave_de_bolacha_capa.png',
  'Pavê de Bolacha',
  '40 min',
  15,
  'SOBREMESA',
  'Com camadas de biscoito umedecido, creme suave e cobertura de ganache, esse pavê tradicional é cremoso e irresistível. A combinação entre o doce equilibrado e a textura leve faz dele um sucesso garantido em qualquer ocasião. Para um toque especial, finalize com decoração de chocolate branco e surpreenda na apresentação.',
  'Ingredientes do creme:\n2 latas de leite condensado (790 gramas)\n2 e 1/2 xícaras de chá de leite (600 ml)\n2 colheres de sopa de amido de milho (30 gramas)\n2 gemas\n1 caixa de creme de leite (200 gramas)\n1 colher de sopa de extrato de baunilha\nIngredientes da ganache:\n200 gramas de chocolate meio amargo\n50 gramas de chocolate branco para decorar\n1 caixa de creme de leite (200 gramas)\nIngredientes para a montagem:\n3/4 de xícara de chá de leite (200ml)\n350 gramas de biscoito maizena',
  'pave_de_bolacha_preparo_1.png',
  'Para preparar essa receita, separe os ingredientes necessários sobre sua bancada;',
  'pave_de_bolacha_preparo_2.png',
  'Dissolva o amido de milho no leite, depois despeje em uma panela média com o leite condensado;',
  'pave_de_bolacha_preparo_3.png',
  'Adicione as gemas peneiradas, o creme de leite e o extrato de baunilha;',
  'pave_de_bolacha_preparo_4.png',
  'Leve ao fogo médio, mexendo sem parar, até levantar fervura. Quando começar a borbulhar, conte 4 minutos e desligue o fogo;',
  'pave_de_bolacha_preparo_5.png',
  'Transfira o creme para um recipiente e deixe esfriar em temperatura ambiente por cerca de 30 minutos;',
  'pave_de_bolacha_preparo_6.png',
  'Em outra tigela, coloque os 200 ml de leite para umedecer os biscoitos;',
  'pave_de_bolacha_preparo_7.png',
  'Em uma travessa média (24cm), faça uma camada de creme e cubra com os biscoitos umedecidos no leite;',
  'pave_de_bolacha_preparo_8.png',
  'Repita as camadas até acabar os ingredientes, finalizando com o creme. Para a ganache, leve o chocolate meio amargo e o creme de leite ao micro-ondas por 1 minuto, em potência média, parando a cada 30 segundos para mexer;',
  'pave_de_bolacha_preparo_9.png',
  'Misture bem até obter um creme brilhante e homogêneo e espalhe sobre o pavê com uma espátula;',
  'pave_de_bolacha_preparo_10.png',
  'Leve à geladeira por no mínimo 4 horas antes de servir. E está pronto!'
);

-- 9: Bolo de Cenoura
INSERT INTO `receitas` (
  `ID`,`img capa`,`NOME`,`TEMPO`,`PORÇÃO`,`TAG`,`DESCRIÇÃO`,`INGREDIENTES`,
  `Img 1`,`MODO DE PREPARO 1`,`img 2`,`MODO DE PREPARO 2`,`img 3`,`MODO DE PREPARO 3`,
  `img 4`,`MODO DE PREPARO 4`,`img 5`,`MODO DE PREPARO 5`,`img 6`,`MODO DE PREPARO 6`,
  `img 7`,`MODO DE PREPARO 7`,`img 8`,`MODO DE PREPARO 8`,`img 9`,`MODO DE PREPARO 9`
) VALUES (
  9,
  'bolo_de_cenoura_capa.png',
  'Bolo de Cenoura',
  '60 min',
  10,
  'SOBREMESA',
  'O cheirinho de bolo de cenoura saindo do forno traz alegria para a casa, a sensação de lar. Nessa receita, a massa fica fofinha, cresce que é uma beleza. O sabor equilibrado, doce na medida certa. Na cobertura, uma calda de chocolate cremosa. Já pode passar um cafezinho!',
  'Ingredientes da massa:\n3 cenouras médias (cerca de 360 gramas)\n4 ovos\n1 xícara de chá de óleo de milho (240 ml)\n1 e 1/2 xícara de chá de açúcar (300 gramas)\n2 xícaras de chá de farinha de trigo (280 gramas)\n1 colher de sopa de fermento químico em pó (fermento para bolo) (14 gramas)\n1 pitada de sal (1/4 de colher de chá)\nManteiga e farinha de trigo para untar\nIngredientes da calda:\n1 caixa de leite condensado (395 gramas)\n1 colher de sopa de manteiga sem sal (14 gramas)\n4 colheres de sopa de chocolate em pó (ou 7 colheres de sopa de achocolatado)\n1/2 caixinha de creme de leite (100 gramas)\nChocolate granulado para decorar',
  'bolo_de_cenoura_preparo_1.png',
  'Higienize, descasque e corte as cenouras em rodelas. É possível substituir o óleo de milho por de girassol ou soja (prefira óleos com sabor mais neutro). Quebre os ovos em um recipiente separado para ver se todos estão bons. Coloque o forno para preaquecer a 180 ºC.',
  'bolo_de_cenoura_preparo_2.png',
  'Em um recipiente, peneire a farinha (para deixar seu bolo mais leve e sem gruminhos). Adicione o sal e o fermento. Misture bem. Reserve;',
  'bolo_de_cenoura_preparo_3.png',
  'No liquidificador, coloque as cenouras, o óleo, os ovos e o açúcar. Bata por cerca de 5 minutos, ou até ficar uma mistura bem homogênea. Despeje em um recipiente grande;',
  'bolo_de_cenoura_preparo_4.png',
  'Gradualmente, vá adicionando a mistura de ingredientes secos. Com o auxílio de um fuê (batedor de bolo), mexa a massa cuidadosamente, apenas para incorporar bem. Não bata o bolo com movimentos rápidos, pois isso ativa o glúten do trigo e a massa fica solada;',
  'bolo_de_cenoura_preparo_5.png',
  'Assim que obter uma massa homogênea, despeje-a em uma forma redonda média (22 cm de diâmetro), untada e enfarinhada. Leve ao forno preaquecido a 180 ºC por cerca de 45 a 50 minutos - quando você espetar o garfo e ele sair limpinho, o bolo está assado.',
  'bolo_de_cenoura_preparo_6.png',
  'Enquanto o bolo de cenoura esfria um pouquinho, você prepara a calda de chocolate. Em uma panela, coloque o leite condensado, a manteiga e o chocolate. Com uma espátula, misture bem até ficar homogêneo;',
  'bolo_de_cenoura_preparo_7.png',
  'Leve ao fogo médio e mexa sem parar até atingir o ponto de brigadeiro (quando começa a desgrudar do fundo da panela). Desligue o fogo, despeje o creme de leite e misture para incorporar;',
  'bolo_de_cenoura_preparo_8.png',
  'Quando o bolo estiver morno, desenforme e despeje a cobertura por cima;',
  'bolo_de_cenoura_preparo_9.png',
  'Decore com chocolate granulado. Pode servir quente ou frio.'
);


-- Ligação entre Receitas e Itens Padrão (RF17 e RN14)
CREATE TABLE receita_itens_padrao (
    id_receita INT NOT NULL,
    id_item_padrao INT NOT NULL,
    PRIMARY KEY (id_receita, id_item_padrao),
    FOREIGN KEY (id_receita) REFERENCES receitas(`ID`),
    FOREIGN KEY (id_item_padrao) REFERENCES itens_padrao(id_item_padrao)
) CHARACTER SET utf8mb4;

-- Todos os 32 itens de alimentos e bebidas (ids 1 a 32) ligados a pelo menos uma receita
INSERT INTO receita_itens_padrao (id_receita, id_item_padrao) VALUES
-- 1: Feijoada (Feijão, Carne Bovina, Cebola, Água Mineral)
(1, 13), (1, 4), (1, 19), (1, 1),
-- 2: Moqueca de peixe (Peixe, Atum, Tomates, Cebola)
(2, 6), (2, 7), (2, 22), (2, 19),
-- 3: Escondidinho de frango (Frango, Carne Vegana, Batatas, Manteiga, Queijo, Leite, Cebola, Tomates, Brocolis)
(3, 3), (3, 5), (3, 16), (3, 30), (3, 31), (3, 29), (3, 19), (3, 22), (3, 18),
-- 4: Arroz à Grega (Arroz, Cenoura, Milho em Conserva, Ervilha em Conserva, Cebola, Beringela, Maçã, Água Mineral)
(4, 10), (4, 20), (4, 8), (4, 9), (4, 19), (4, 17), (4, 21), (4, 1),
-- 5: Farofa de Banana (Banana, Manteiga, Cebola, Aveia, Pão)
(5, 15), (5, 30), (5, 19), (5, 11), (5, 27),
-- 6: Batata Sauté (Batatas, Manteiga)
(6, 16), (6, 30),
-- 7: Salada de Lentilha (Feijão, Alface, Tomates, Cebola, Macarrão, Suco, Água Mineral)
(7, 13), (7, 14), (7, 22), (7, 19), (7, 26), (7, 2), (7, 1),
-- 8: Pavê de Bolacha (Biscoito, Leite, Ovos, Iogurte, Açúcar)
(8, 24), (8, 29), (8, 32), (8, 28), (8, 23),
-- 9: Bolo de Cenoura (Cenoura, Ovos, Açúcar, Farinha de Trigo, Manteiga, Leite, Café)
(9, 20), (9, 32), (9, 23), (9, 25), (9, 30), (9, 29), (9, 12);