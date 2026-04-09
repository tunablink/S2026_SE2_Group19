const express = require("express");
const mysql = require("mysql2/promise");
const cors = require("cors");
require("dotenv").config();

const app = express();
app.use(cors());

const pool = mysql.createPool({
  host: process.env.DB_HOST || "localhost",
  user: process.env.DB_USER || "root",
  password: process.env.DB_PASSWORD || "Tunablink4305@",
  database: process.env.DB_NAME || "blogweb",
  port: 3306,
});

// GET modules
app.get("/api/modules", async (req, res) => {
  try {
    const [rows] = await pool.query("SELECT * FROM modules");
    res.json(rows);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: err.message });
  }
});

// GET lessons theo moduleId
app.get("/api/lessons/:moduleId", async (req, res) => {
  try {
    const [rows] = await pool.query(
      "SELECT * FROM lessons WHERE module_id = ?",
      [req.params.moduleId],
    );
    res.json(rows);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.listen(3000, () => console.log("Server: http://localhost:3000"));
