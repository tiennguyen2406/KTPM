const express = require('express');
const mysql = require('mysql2/promise');
const app = express();
const port = 3000;

const dbConfig = {
  host: 'mysql',
  user: 'root',
  password: 'rootpassword',
  database: 'mydb'
};

app.get('/', async (req, res) => {
  try {
    const connection = await mysql.createConnection(dbConfig);
    const [rows] = await connection.execute('SELECT NOW() as now');
    await connection.end();
    res.json({
      message: 'Node.js connected to MySQL successfully!',
      dbTime: rows[0].now,
      timestamp: new Date().toISOString()
    });
  } catch (error) {
    res.status(500).json({
      error: 'Database connection failed',
      message: error.message
    });
  }
});

app.get('/health', (req, res) => {
  res.json({ status: 'OK', service: 'Node.js + MySQL' });
});

app.listen(port, '0.0.0.0', () => {
  console.log(`Server running on port ${port}`);
});
