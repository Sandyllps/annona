import express from "express"
import 'dotenv/config'
import {usersRouter} from "./routers/users.router.js"

const app = express() //chamando a função express que retorna o objeto do servidor

app.use(express.json()) // indicando pro meu servidor que vou trabalhar com json nas minhas respostas e requisições

app.use("/users", usersRouter)

const port = process.env.PORT //aqui estou dizendo "utilize meu valor que está em PORT"

app.listen(port, function(){
    console.log("Servidor escutando em: http://localhost:"+ port)
})
