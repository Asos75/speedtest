var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');


const mongoose = require('mongoose');
const { MongoClient, ServerApiVersion } = require('mongodb');
const uri = "mongodb+srv://david:nice@host.cqupown.mongodb.net/?retryWrites=true&w=majority&appName=SpeedDB";


// Create a MongoClient with a MongoClientOptions object to set the Stable API version
const client = new MongoClient(uri, {
  serverApi: {
    version: ServerApiVersion.v1,
    strict: true,
    deprecationErrors: true,
  }
});

async function run() {
  try {
    // Connect the client to the server	(optional starting in v4.7)
    await client.connect();
    // Send a ping to confirm a successful connection
    await client.db("admin").command({ ping: 1 });
    console.log("Pinged your deployment. You successfully connected to MongoDB!");
  } finally {
    // Ensures that the client will close when you finish/error
    await client.close();
  }
}

mongoose.connect(uri, { useNewUrlParser: true, useUnifiedTopology: true })
  .then(() => console.log('MongoDB connected'))
  .catch(err => console.log(err));

// include routes
var indexRouter = require('./routes/index');
var usersRouter = require('./routes/userRoutes');
var measurementsRouter = require('./routes/measurementRoutes');
var mobileTowerRouter = require('./routes/mobileTowerRoutes');
var eventRouter = require('./routes/eventRoutes');

var app = express()

var cors = require('cors');
var allowedOrigins = ['http://localhost:3000', 'http://localhost:3001'];
app.use(cors({
  credentials: true,
  origin: function(origin, callback){
    // Allow requests with no origin (mobile apps, curl)
    if(!origin) return callback(null, true);
    if(allowedOrigins.indexOf(origin)===-1){
      var msg = "The CORS policy does not allow access from the specified Origin.";
      return callback(new Error(msg), false);
    }
    return callback(null, true);
  }
}));

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

var session = require('express-session');
var MongoStore = require('connect-mongo');
app.use(session({
  secret: 'work hard',
  resave: true,
  saveUninitialized: false,
  store: MongoStore.create({mongoUrl: uri})
}));

app.use(function (req, res, next) {
  res.locals.session = req.session;
  next();
});	


app.use('/', indexRouter);
app.use('/users', usersRouter);
app.use('/measurements', measurementsRouter);
app.use('/mobile', mobileTowerRouter);
app.use('/event', eventRouter)


// catch 404 and forward to error handler
app.use(function(req, res, next) {
  next(createError(404));
});


// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  //res.render('error');
  res.json(err);
});




const User = require('./models/userModel'); 
const Measurement = require('./models/measurementModel');

async function createUser() {
  const newUser = new User({
    username: 'user2',
    password: 'nekaj',
    admin: true
  });

  try {
    const result = await newUser.save();
    console.log('User created:', result);
  } catch (error) {
    console.error('Error creating user:', error);
  }
}

createUser();



async function createMeasure() {
  const newMeasure = new Measurement({
    speed: 10000,
		type: 'data',
		provider: "Telekom",
		time: Date.now(),
		location: {
				type: "Point",
				coordinates: [46.562119, 15.640014]
		}
  });

  try {
    const result = await newMeasure.save();
    console.log('Measure created:', result);
  } catch (error) {
    console.error('Error creating measure:', error);
  }
}

createMeasure();
run().catch(console.dir);
module.exports = app;

