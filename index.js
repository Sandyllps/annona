//Esse import do framework express é importante porque esse framework facilita a criação da API, simplificando o tratamento 
//de rquisições, rotas, e facilitando a troca de infor. entre o app kotlin e o servidor, já que lida bem com JSON.
import express from "express" 

import 'dotenv/config' // esse import carrega as variáveis do arquivo .env (foi nesse arquivo que coloquei os dados do banco de dados)

// aqui fazemos o import das rotas
import {usersRouter} from "./routers/users.router.js"
import { infoGeralRouter } from "./routers/info_geral.router.js"
import { receitasRouter } from "./routers/receitas.router.js"
import { itensPadraoRouter } from "./routers/itens_padrao.router.js"

const app = express() // Inicializando o servidor Express, chamando a função express() que retorna o objeto do servidor.

//Indicando pro meu servidor que vou trabalhar com JSON 
// Isso permite que o Node entenda quando o Android envia dados em formato de texto JSON
app.use(express.json()) 

// Definição das rotas pai
// Todas as rotas de usuários começarão com http://localhost:8080/users... seguindo a mesma lógica pras demais.
app.use("/users", usersRouter)

//rota não utilizada
app.use("/infoGeral", infoGeralRouter)

//rota não utilizada
app.use("/receitas", receitasRouter)

//Rota para buscar os itens padrão do sistema
app.use("/itens_padrao", itensPadraoRouter)

//Busca a porta definida no arquivo .env
const port = process.env.PORT 

// INICIALIZAÇÃO DO SERVIDOR
// Aqui o servidor começa efetivamente a "ouvir" as chamadas que o Android faz na porta definida.
app.listen(port, function(){
    console.log("Servidor Annona rodando com sucesso!");
    console.log("Escutando em: http://localhost:"+ port);
})