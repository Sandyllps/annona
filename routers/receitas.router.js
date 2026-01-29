//rota não utilizada
import express from "express"
import {listarReceitas} from "../controllers/receitas.controller.js"

const receitasRouter = express.Router() //criando um objeto para chamar os controladores

receitasRouter.get("/", listarReceitas)

export{receitasRouter}