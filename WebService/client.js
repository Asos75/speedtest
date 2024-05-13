
const io = require('socket.io-client');
const serverUrl = 'http://localhost:3000'; 

const socket = io(serverUrl);

// Dogodek, ki se sproži ob povezavi s strežnikom
socket.on('connect', () => {
  console.log('Connected to server');
  
  // Pošiljanje sporočila na strežnik
  socket.emit('chat message', 'Hello server, this is a test message from client!');
});

// Dogodek, ki se sproži ob prejemu sporočila od strežnika
socket.on('chat message', (msg) => {
  console.log('Message from server:', msg);
});

// Dogodek, ki se sproži ob prekinitvi povezave s strežnikom
socket.on('disconnect', () => {
  console.log('Disconnected from server');
});
