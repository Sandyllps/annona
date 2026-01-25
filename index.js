import express from "express"

const app = express() //chamando a função express que retorna o objeto do servidor

app.use(express.json()) // indicando pro meu servidor que vou trabalhar com json nas minhas respostas e requisições

const port = process.env.PORT //aqui estou dizendo "utilize meu valor que está em PORT"

app.listen(port, function(){
    console.log("Servidor escutando na porta: ", port)
})
