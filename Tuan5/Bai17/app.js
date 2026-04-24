const express = require('express');
const mongoose = require('mongoose');
const app = express();
const port = 3000;

app.use(express.json());

// Connect to MongoDB
mongoose.connect('mongodb://mongo:27017/myapp', {
  useNewUrlParser: true,
  useUnifiedTopology: true
})
.then(() => console.log('Connected to MongoDB'))
.catch(err => console.error('MongoDB connection error:', err));

// Item schema
const itemSchema = new mongoose.Schema({
  name: String,
  createdAt: { type: Date, default: Date.now }
});

const Item = mongoose.model('Item', itemSchema);

// Routes
app.get('/', (req, res) => {
  res.json({ message: 'Node.js + MongoDB API', status: 'running' });
});

app.get('/health', (req, res) => {
  const dbState = mongoose.connection.readyState;
  res.json({
    status: dbState === 1 ? 'healthy' : 'unhealthy',
    database: dbState === 1 ? 'connected' : 'disconnected',
    timestamp: new Date().toISOString()
  });
});

app.get('/items', async (req, res) => {
  try {
    const items = await Item.find().sort({ createdAt: -1 });
    res.json(items);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

app.post('/items', async (req, res) => {
  try {
    const item = new Item({ name: req.body.name });
    await item.save();
    res.status(201).json(item);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

app.listen(port, '0.0.0.0', () => {
  console.log(`Server running on port ${port}`);
});
