import 'dotenv/config' // Carrega as configurações do arquivo .env
import mysql from 'mysql2/promise'; // Importa a versão do MySQL que aceita 'await' (promessas)

// Criamos a conexão com o banco de dados usando o objeto 'pool'
// O Pool é mais eficiente que uma conexão única porque gerencia várias conexões ao mesmo tempo
const pool = mysql.createPool({
  // Buscamos os valores do .env. Se não existirem lá, usamos um valor padrão (ex: 'localhost')
  host: process.env.DB_HOST || 'localhost',
  user: process.env.DB_USER || 'root',
  password: process.env.DB_PASS || '',
  database: process.env.DB_NAME || 'test',
  
  // O banco geralmente roda na porta 3306. Convertemos para Número por segurança.
  port: process.env.DB_PORT ? Number(process.env.DB_PORT) : 3306,
  
  waitForConnections: true, // Se todos os "mensageiros" estiverem ocupados, a próxima ordem espera na fila
  connectionLimit: 10,      // Limite máximo de conexões abertas simultaneamente
  queueLimit: 0             // Sem limite de fila de espera
});

// Exportamos o 'pool' para que os controllers possam fazer 'pool.execute()'
export { pool };