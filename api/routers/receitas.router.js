import express from "express"
import { listarReceitas, listarReceitasSugeridas } from "../controllers/receitas.controller.js"

const receitasRouter = express.Router()

receitasRouter.get("/", listarReceitas)
receitasRouter.get("/sugeridas/:id_usuario", listarReceitasSugeridas)

export { receitasRouter }