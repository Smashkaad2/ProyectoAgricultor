const express = require('express');
const session = require('express-session');
const Keycloak = require('keycloak-connect');
const { Pool } = require('pg');
const { v4: uuidv4 } = require('uuid');
const axios = require('axios');

const app = express();

// Configuración de sesión (usando MemoryStore, solo para desarrollo)
const memoryStore = new session.MemoryStore(); // ¡Así se crea correctamente!
const productsServiceUrl = 'http://localhost:5206';

app.use(session({
  secret: 'secret123',
  resave: false,
  saveUninitialized: true,
  store: memoryStore, // ¡Pasamos el store aquí!
}));

// Configuración de Keycloak
const keycloakConfig = {
  realm: 'mi-realm',
  serverUrl: 'http://localhost:8085',
  clientId: 'mi-api',
  bearerOnly: true,
};

const keycloak = new Keycloak({ store: memoryStore }, keycloakConfig); // ¡Usamos el mismo store!

app.use(keycloak.middleware());

// Configuración de PostgreSQL
const pool = new Pool({
  user: 'products_user',
  host: 'localhost',
  database: 'products_db',
  password: 'products_password',
  port: 5434,
});

// Prueba la conexión
pool.connect((err, client, release) => {
  if (err) {
    return console.error('Error al conectar a PostgreSQL:', err.stack);
  }
  console.log('Conexión exitosa a PostgreSQL');
  release();
});

// Middleware para parsear JSON
app.use(express.json());

// Ruta para guardar un producto en la lista del usuario
app.post('/api/products', keycloak.protect(), async (req, res) => {
  const userId = req.kauth.grant.access_token.content.sub; // ID del usuario desde Keycloak
  const { name, description, price } = req.body;
  
  try {
    const result = await pool.query(
      'INSERT INTO user_products (id, user_id, name, description, price) VALUES ($1, $2, $3, $4, $5) RETURNING *',
      [uuidv4(), userId, name, description, price]
    );
    res.json(result.rows[0]);
  } catch (err) {
    res.status(500).json({ error: 'Error al guardar el producto' });
  }
});

// Ruta para obtener la lista de productos del usuario
app.get('/api/products', keycloak.protect(), async (req, res) => {
  const userId = req.kauth.grant.access_token.content.sub;
  
  try {
    const result = await pool.query(
      'SELECT * FROM user_products WHERE user_id = $1',
      [userId]
    );
    res.json(result.rows);
  } catch (err) {
    res.status(500).json({ error: 'Error al obtener los productos' });
  }
});


// Ruta pública
app.get('/', (req, res) => {
  res.send('¡API REST funcionando!');
});

// Ruta protegida
app.get('/ruta-protegida', keycloak.protect(), (req, res) => {
  res.json({ mensaje: '¡Solo visible con token JWT válido!' });
});

app.listen(3000, () => {
  console.log('API escuchando en http://localhost:3000');
});

// Ruta para guardar un producto del catálogo en la lista del usuario
app.post('/api/user-products/:productId', keycloak.protect(), async (req, res) => {
  const userId = req.kauth.grant.access_token.content.sub;
  const { productId } = req.params;

  try {
    // 1. Obtener el producto del servicio externo
    const productResponse = await axios.get(`${productsServiceUrl}/api/Products/${productId}`);
    const product = productResponse.data;

    // 2. Guardar en la lista del usuario
    const result = await pool.query(
      'INSERT INTO user_products (id, user_id, name, description, price, product_id) VALUES ($1, $2, $3, $4, $5, $6) RETURNING *',
      [uuidv4(), userId, product.name, product.description, product.price, product.id]
    );

    res.json(result.rows[0]);
  } catch (err) {
    console.error('Error:', err);
    res.status(500).json({ error: 'Error al guardar el producto en la lista del usuario' });
  }
});

// Ruta para obtener productos del catálogo
app.get('/api/catalog', keycloak.protect(), async (req, res) => {
  try {
    const response = await axios.get(`${productsServiceUrl}/api/Products`);
    res.json(response.data);
  } catch (err) {
    res.status(500).json({ error: 'Error al obtener el catálogo de productos' });
  }
});