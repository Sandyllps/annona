//rota não utilizada
import express from "express"
import {pegarInfoGeral} from "../controllers/info_geral.controller.js"

const infoGeralRouter = express.Router() //criando um objeto para chamar os controladores

infoGeralRouter.get("/", pegarInfoGeral)

export{infoGeralRouter}