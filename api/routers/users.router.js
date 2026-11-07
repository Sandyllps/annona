import express from "express"
import {
    adicionarItemUsuario,
    alterarConsumoItem,
    alterarDisponibilidadeDoacao,
    atualizarNotificacoesUsuario,
    cadastrarUsuario,
    deletarItemUsuario,
    editarItemUsuario,
    listarHistoricoUsuario,
    listarItensDoacao,
    listarItensUsuario,
    login,
    obterPerfilUsuario
} from "../controllers/users.controller.js"

const usersRouter = express.Router()

// Autenticação e Cadastro (RF01, RF02)
usersRouter.post("/login", login)
usersRouter.post("/cadastro", cadastrarUsuario)

// Despensa CRUD (RF04, RF05, RF06, RF07, RF13)
usersRouter.post("/ItensUsuario", adicionarItemUsuario)
usersRouter.post("/itensUsuario", adicionarItemUsuario)
usersRouter.get("/listarItens/:id_usuario", listarItensUsuario)
usersRouter.put("/editarItem/:id_item_usuario", editarItemUsuario)
usersRouter.put("/itens/:id_item_usuario/consumo", alterarConsumoItem)
usersRouter.delete("/deletarItem/:id_item_usuario", deletarItemUsuario)

// Doação (RF15, RN12, RN13)
usersRouter.get("/:id_usuario/doacao", listarItensDoacao)
usersRouter.put("/itens/:id_item_usuario/doacao", alterarDisponibilidadeDoacao)

// Histórico e Perfil / Notificações (RF16, RF20, RF21)
usersRouter.get("/:id_usuario/historico", listarHistoricoUsuario)
usersRouter.put("/:id_usuario/notificacoes", atualizarNotificacoesUsuario)
usersRouter.get("/:id_usuario/perfil", obterPerfilUsuario)

export { usersRouter }