import express from "express"
import {adicionarItemUsuario, deletarItemUsuario, editarItemUsuario, listarItensUsuario, login} from "../controllers/users.controller.js"

const usersRouter = express.Router()

usersRouter.post("/login", login)

usersRouter.post("/ItensUsuario", adicionarItemUsuario)

usersRouter.get("/listarItens/:id_usuario", listarItensUsuario) // esse :id_usuario significa que na rota vai ser enviado o valor de id_usuario. Ex.: http://localhost:8080/users/listarItens/2

usersRouter.put("/editarItem/:id_item_usuario", editarItemUsuario)

usersRouter.delete("/deletarItem/:id_item_usuario", deletarItemUsuario)

export{usersRouter}