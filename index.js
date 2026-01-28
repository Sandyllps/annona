import express from "express"
import 'dotenv/config' // Carrega as variáveis de ambiente do arquivo .env (Segurança)

// Importação dos roteadores: Cada um cuida de uma "área" do sistema
import {usersRouter} from "./routers/users.router.js"
import { infoGeralRouter } from "./routers/info_geral.router.js"
import { receitasRouter } from "./routers/receitas.router.js"
import { itensPadraoRouter } from "./routers/itens_padrao.router.js"

const app = express() // Chamando a função express que retorna o objeto do servidor

// MIDDLEWARE: Indicando pro meu servidor que vou trabalhar com JSON 
// Isso permite que o Node entenda quando o Android envia dados em formato de texto JSON
app.use(express.json()) 

// DEFINIÇÃO DAS ROTAS "PAI" (Prefixos)
// Todas as rotas de usuários começarão com http://ip:porta/users
app.use("/users", usersRouter)

// Todas as rotas de informações gerais (categorias, locais)
app.use("/infoGeral", infoGeralRouter)

// Todas as rotas relacionadas ao livro de receitas
app.use("/receitas", receitasRouter)

// [NOVO] Rota para buscar os itens do sistema (Autocomplete do Android)
app.use("/itens_padrao", itensPadraoRouter)

// CONFIGURAÇÃO DA PORTA
// O processo tenta ler do arquivo .env. Se não achar, usa o valor que estiver lá.
const port = process.env.PORT 

// INICIALIZAÇÃO DO SERVIDOR
// Aqui o servidor começa efetivamente a "ouvir" as chamadas que o Android faz
app.listen(port, function(){
    console.log("Servidor Annona rodando com sucesso!");
    console.log("Escutando em: http://localhost:"+ port);
})