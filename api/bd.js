import "dotenv/config"
import fs from "fs"
import path from "path"
import { fileURLToPath } from "url"
import mysql from "mysql2/promise"
import { DatabaseSync } from "node:sqlite"

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)

const scriptSqlPath = path.join(__dirname, "scripts_bd", "script.sql")
const sqliteFilePath = path.join(__dirname, "scripts_bd", "annona_local.sqlite")

// Divide o script.sql em comandos individuais respeitando ponto e vírgula dentro de strings '...'
function carregarComandosDoScriptSql() {
    if (!fs.existsSync(scriptSqlPath)) return []
    const conteudo = fs.readFileSync(scriptSqlPath, "utf8")

    // Remove comentários de linha (-- ...)
    const semComentarios = conteudo
        .split(/\r?\n/)
        .filter(linha => !linha.trim().startsWith("--"))
        .join("\n")

    const comandos = []
    let atual = ""
    let dentroDeAspas = false

    for (let i = 0; i < semComentarios.length; i++) {
        const ch = semComentarios[i]
        const prox = semComentarios[i + 1]

        if (ch === "'") {
            if (dentroDeAspas && prox === "'") {
                atual += "''"
                i++
                continue
            }
            dentroDeAspas = !dentroDeAspas
            atual += ch
        } else if (ch === ";" && !dentroDeAspas) {
            if (atual.trim().length > 0) {
                comandos.push(atual.trim())
            }
            atual = ""
        } else {
            atual += ch
        }
    }

    if (atual.trim().length > 0) {
        comandos.push(atual.trim())
    }

    return comandos
}

// Adapta comandos do script.sql (MySQL) para funcionar tanto no MySQL quanto no SQLite local
function adaptarSqlParaSqlite(sql) {
    return sql
        .replace(/CHARACTER\s+SET\s+utf8mb4/gi, "")
        .replace(/INT\s+AUTO_INCREMENT\s+PRIMARY\s+KEY/gi, "INTEGER PRIMARY KEY AUTOINCREMENT")
        .replace(/CREATE\s+TABLE\s+`/gi, "CREATE TABLE IF NOT EXISTS `")
        .replace(/CREATE\s+TABLE\s+(?!IF\s+NOT\s+EXISTS)/gi, "CREATE TABLE IF NOT EXISTS ")
        .replace(/INSERT\s+INTO\s+itens_usuario/gi, "INSERT OR REPLACE INTO itens_usuario")
        .replace(/INSERT\s+INTO/gi, "INSERT OR IGNORE INTO")
        .replace(/DATE_ADD\s*\(\s*CURDATE\s*\(\s*\)\s*,\s*INTERVAL\s+(-?\d+)\s+DAY\s*\)/gi, (_, dias) => {
            const n = Number(dias)
            const sinal = n >= 0 ? `+${n}` : `${n}`
            return `date('now', 'localtime', '${sinal} days')`
        })
        .replace(/\bCURDATE\s*\(\s*\)/gi, "date('now', 'localtime')")
        .replace(/\bNOW\s*\(\s*\)/gi, "datetime('now', 'localtime')")
        .replace(/\\n/g, "\n")
}

function adaptarSqlParaMysqlAutoSetup(sql) {
    return sql
        .replace(/CREATE\s+TABLE\s+`/gi, "CREATE TABLE IF NOT EXISTS `")
        .replace(/CREATE\s+TABLE\s+(?!IF\s+NOT\s+EXISTS)/gi, "CREATE TABLE IF NOT EXISTS ")
        .replace(/INSERT\s+INTO/gi, "INSERT IGNORE INTO")
}

// =========================================================================
// MOTOR 1: BANCO LOCAL AUTOMÁTICO (SQLite embutido + script.sql)
// Roda 100% local a partir do script.sql, sem precisar de MySQL ou ngrok!
// =========================================================================
function criarPoolSqliteLocal() {
    const db = new DatabaseSync(sqliteFilePath)
    db.exec("PRAGMA foreign_keys = OFF;")

    const comandos = carregarComandosDoScriptSql()
    for (const cmd of comandos) {
        const sqlAdaptado = adaptarSqlParaSqlite(cmd)
        try {
            db.exec(sqlAdaptado)
        } catch (err) {
            console.warn("Aviso ao executar comando inicial no SQLite:", err.message)
        }
    }

    // Garante que alimentos cadastrados sem data de validade assumam por padrão hoje + 7 dias
    // e mantém as datas dos itens de exemplo (1..69) sempre fidedignas em relação à data atual
    try {
        db.exec("UPDATE usuarios SET email = 'walysson@gmail.com', nome_usuario = 'Walysson' WHERE id_usuario = 3;")
        db.exec("UPDATE itens_usuario SET possui_data_validade = 1, data_validade = date('now', 'localtime', '+7 day'), id_flag = 3 WHERE (possui_data_validade = 0 OR data_validade IS NULL) AND id_categoria <= 8;")
        db.exec("UPDATE itens_usuario SET id_flag = 4 WHERE possui_data_validade = 0 OR data_validade IS NULL;")
        const mapaDatasFidedignas = [
            ["Pão", "-1 day", 1],
            ["Iogurte", "-2 day", 1],
            ["Peixe", "+1 day", 2],
            ["Tomates", "+1 day", 2],
            ["Leite", "+2 day", 2],
            ["Frango", "+3 day", 2],
            ["Queijo", "+4 day", 3],
            ["Cenoura", "+5 day", 3],
            ["Ovos", "+6 day", 3],
            ["Biscoito", "+7 day", 3],
            ["Batatas", "+10 day", 4],
            ["Cebola", "+12 day", 4],
            ["Café", "+15 day", 4],
            ["Manteiga", "+18 day", 4],
            ["Farinha de Trigo", "+22 day", 4],
            ["Arroz", "+25 day", 4],
            ["Feijão", "+28 day", 4],
            ["Macarrão", "+30 day", 4],
            ["Açúcar", "+35 day", 4],
            ["Milho em Conserva", "+40 day", 4],
            ["Banana", "-2 day", 1],
            ["Suco", "+2 day", 2]
        ]
        const stmtUpdate = db.prepare(
            "UPDATE itens_usuario SET data_validade = date('now', 'localtime', ?), id_flag = ? WHERE id_item_usuario <= 92 AND nome = ?"
        )
        for (const [nome, offset, flag] of mapaDatasFidedignas) {
            stmtUpdate.run(offset, flag, nome)
        }
    } catch {
    }

    console.log("📦 Banco de dados local (SQLite) inicializado automaticamente a partir de scripts_bd/script.sql!")
    console.log(`📁 Arquivo local salvo em: ${sqliteFilePath}`)

    return {
        async execute(sql, params = []) {
            const sqlNormalizado = sql
                .replace(/\bdeletado\s*=\s*FALSE\b/gi, "deletado = 0")
                .replace(/\bdeletado\s*=\s*TRUE\b/gi, "deletado = 1")
                .replace(/\bfora_doacao\s*=\s*FALSE\b/gi, "fora_doacao = 0")
                .replace(/\bfora_doacao\s*=\s*TRUE\b/gi, "fora_doacao = 1")
                .replace(/\bDEFAULT\s+FALSE\b/gi, "DEFAULT 0")
                .replace(/\bDEFAULT\s+TRUE\b/gi, "DEFAULT 1")

            const paramsNormalizados = params.map(p => {
                if (typeof p === "boolean") return p ? 1 : 0
                if (p === undefined) return null
                return p
            })

            const stmt = db.prepare(sqlNormalizado)
            const ehSelect = /^\s*SELECT\b/i.test(sqlNormalizado)

            if (ehSelect) {
                const rows = stmt.all(...paramsNormalizados)
                return [rows, []]
            } else {
                const info = stmt.run(...paramsNormalizados)
                return [
                    {
                        insertId: Number(info.lastInsertRowid || 0),
                        affectedRows: Number(info.changes || 0)
                    },
                    []
                ]
            }
        }
    }
}

// =========================================================================
// MOTOR 2: MYSQL LOCAL OU REMOTO (Caso o colega configure DB_MODE=mysql)
// Também cria o banco e executa o script.sql sozinho se estiver vazio!
// =========================================================================
async function tentarConectarEInicializarMysql() {
    const modo = (process.env.DB_MODE || "local").toLowerCase().trim()
    if (modo !== "mysql") {
        return null
    }

    const host = process.env.DB_HOST || "localhost"
    const user = process.env.DB_USER || "root"
    const password = process.env.DB_PASS ?? ""
    const database = process.env.DB_NAME || "annona"
    const port = process.env.DB_PORT ? Number(process.env.DB_PORT) : 3306

    try {
        const conexaoInicial = await mysql.createConnection({ host, user, password, port, connectTimeout: 3000 })
        await conexaoInicial.query(`CREATE DATABASE IF NOT EXISTS \`${database}\` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci`)
        await conexaoInicial.end()

        const mysqlPool = mysql.createPool({
            host,
            user,
            password,
            database,
            port,
            waitForConnections: true,
            connectionLimit: 10,
            queueLimit: 0
        })

        const comandos = carregarComandosDoScriptSql()
        for (const cmd of comandos) {
            const sqlMysql = adaptarSqlParaMysqlAutoSetup(cmd)
            try {
                await mysqlPool.query(sqlMysql)
            } catch {
                // Ignora caso já exista
            }
        }

        console.log(`🐬 Conectado ao MySQL (${host}:${port}/${database}) e tabelas sincronizadas com script.sql!`)
        return mysqlPool
    } catch (err) {
        console.log(`⚠️ MySQL não disponível (${err.message}). Ativando banco local automático (SQLite)!`)
        return null
    }
}

let poolAtivo = null

async function obterPool() {
    if (poolAtivo) return poolAtivo
    const mysqlPool = await tentarConectarEInicializarMysql()
    poolAtivo = mysqlPool || criarPoolSqliteLocal()
    return poolAtivo
}

obterPool()

const pool = {
    async execute(sql, params = []) {
        const p = await obterPool()
        return p.execute(sql, params)
    }
}

export { pool }