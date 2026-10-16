import express from "express"
import { listarItensPadrao } from "../controllers/itens_padrao.controller.js"

// Criamos o roteador específico para este módulo
const itensPadraoRouter = express.Router()

// Definimos que quando chegar um GET na raiz ("/") deste roteador, 
// ele deve executar a função listarItensPadrao
itensPadraoRouter.get("/", listarItensPadrao)

export { itensPadraoRouter }