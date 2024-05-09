
const { MongoClient, ServerApiVersion } = require('mongodb');
const uri = "mongodb+srv://david:nice@host.cqupown.mongodb.net/?retryWrites=true&w=majority&appName=SpeedDB";

const mongoose = require('mongoose');
const User = require('./models/userModel');  // Uvozite model

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

async function createUser() {
  const newUser = new User({
    username: 'exampleUser',
    password: 'password123',
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
run().catch(console.dir);
