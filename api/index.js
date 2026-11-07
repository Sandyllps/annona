import express from "express"
import "dotenv/config"

import { usersRouter } from "./routers/users.router.js"
import { infoGeralRouter } from "./routers/info_geral.router.js"
import { receitasRouter } from "./routers/receitas.router.js"
import { itensPadraoRouter } from "./routers/itens_padrao.router.js"

const app = express()

app.use(express.json())

// Definição das rotas principais
app.use("/users", usersRouter)
app.use("/infoGeral", infoGeralRouter)
app.use("/info_geral", infoGeralRouter)
app.use("/receitas", receitasRouter)
app.use("/itens_padrao", itensPadraoRouter)

const port = process.env.PORT || 8080

app.listen(port, function () {
    console.log("Servidor Annona rodando com sucesso!")
    console.log("Escutando em: http://localhost:" + port)
})