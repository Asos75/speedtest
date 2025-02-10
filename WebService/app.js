const createError = require('http-errors');
const express = require('express');
const path = require('path');
const cookieParser = require('cookie-parser');
const logger = require('morgan');
const http = require('https');
const socketIo = require('socket.io');
// Cors for cross origin allowance
const cors = require('cors');
const expressFileUpload = require('express-fileupload');


// Changed the path of the .env file for the dotenv package
require('dotenv').config({ path: path.join(__dirname, '../.env') });

const mongoose = require('mongoose');
// Extra strict Squery 
mongoose.set('strictQuery', true);
const { MongoClient, ServerApiVersion } = require('mongodb');

const uri = process.env.MONGO_URL;

const client = new MongoClient(uri, {
  serverApi: {
    version: ServerApiVersion.v1,
    strict: true,
    deprecationErrors: true,
  }
});

async function run() {
  try {
    await client.connect();
    await client.db("admin").command({ ping: 1 });
    console.log("Pinged your deployment. You successfully connected to MongoDB!");
  } finally {
    await client.close();
  }
}


// connect  to mongodb
mongoose.connect(uri, { useNewUrlParser: true, useUnifiedTopology: true })
  .then(() => console.log('MongoDB connected'))
  .catch(err => console.log(err));

// Include routes
const indexRouter = require('./routes/index');
const usersRouter = require('./routes/userRoutes');
const measurementsRouter = require('./routes/measurementRoutes');
const mobileTowerRouter = require('./routes/mobileTowerRoutes');
const eventRouter = require('./routes/eventRoutes');

const app = express();

const server = http.createServer(app);
const io = socketIo(server);

io.on('connection', (socket) => {
  console.log('New client connected');

  // Pošlji sporočilo dobrodošlice samo povezanemu odjemalcu
  io.emit('welcome', 'Welcome to the server!');



  socket.on('disconnect', () => {
    console.log('Client disconnected');
  });
});


app.use(cors({ origin: ['http://localhost:3000', 'http://52.178.15.171:4000', 'https://website-davidrajlic-davids-projects-a2d28f04.vercel.app/']}));
app.use(logger('dev'));
//app.use(cors({ origin: 'http://localhost:3000' }));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));
app.use(expressFileUpload());

const session = require('express-session');
const MongoStore = require('connect-mongo');
app.use(session({
  secret: 'work hard',
  resave: true,
  saveUninitialized: false,
  store: MongoStore.create({ mongoUrl: uri })
}));

app.use(function (req, res, next) {
  res.locals.session = req.session;
  next();
});

app.use('/', indexRouter);
app.use('/users', usersRouter);
app.use('/measurements', measurementsRouter);
app.use('/mobile', mobileTowerRouter);
app.use('/event', eventRouter);

// catch 404 and forward to error handler
app.use(function (req, res, next) {
  next(createError(404));
});

// error handler
app.use(function (err, req, res, next) {
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  res.status(err.status || 500);
  res.json(err);
});

const User = require('./models/userModel');
const Measurement = require('./models/measurementModel');
const measurementModel = require('./models/measurementModel');


run().catch(console.dir);

const PORT = process.env.PORT || 5000;
server.listen(PORT, () => {
  console.log(`Server listening on port ${PORT}`);
});

module.exports = app;
