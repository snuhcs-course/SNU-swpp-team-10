const mysql = require('mysql2');
const { Connection } = require('mysql2/typings/mysql/lib/Connection');

const connection = mysql.createConnection({
    host: process.env.DB_HOST,
    user: process.env.DB_USER,
    database: process.env.DB_NAME,
    password: process.env.DB_PASSWORD
});
connection.connect();

module.exports = connection;
