import express from "express"
import {login} from "../controllers/users.controller.js"

const usersRouter = express.Router()

usersRouter.post("/login", login)

export{usersRouter}