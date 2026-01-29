import 'dotenv/config'
import mysql from 'mysql2/promise';

// Criamos a conexão com o banco de dados usando o objeto 'pool'
// O Pool é mais eficiente que uma conexão única porque gerencia várias conexões ao mesmo tempo
const pool = mysql.createPool({
  host: process.env.DB_HOST,
  user: process.env.DB_USER,
  password: process.env.DB_PASS,
  database: process.env.DB_NAME,
  
  // O banco roda na porta 3306.
  port: process.env.DB_PORT ? Number(process.env.DB_PORT) : 3306,
  
  waitForConnections: true, 
  connectionLimit: 10,
  queueLimit: 0
});

// Exportamos o pool para que os controllers possam fazer pool.execute()
export { pool };