const aedes = require('aedes')();
const net = require('net');

// Define the port the broker will listen on
const PORT = 1883;

// Create the TCP server for the MQTT broker
const server = net.createServer(aedes.handle);

// Start the server
server.listen(PORT, () => {
    console.log(`MQTT broker is running on port ${PORT}`);
});

// Log when a client connects
aedes.on('client', (client) => {
    console.log(`Client connected: ${client.id}`);
});

// Log when a client disconnects
aedes.on('clientDisconnect', (client) => {
    console.log(`Client disconnected: ${client.id}`);
});

// Log when a message is published
aedes.on('publish', (packet, client) => {
    if (client) {
        console.log(`Message from ${client.id}: Topic: ${packet.topic}, Payload: ${packet.payload.toString()}`);
    }
});

// Log when a client subscribes to a topic
aedes.on('subscribe', (subscriptions, client) => {
    subscriptions.forEach((sub) => {
        console.log(`Client ${client.id} subscribed to topic: ${sub.topic}`);
    });
});

// Log when a client unsubscribes from a topic
aedes.on('unsubscribe', (subscriptions, client) => {
    subscriptions.forEach((sub) => {
        console.log(`Client ${client.id} unsubscribed from topic: ${sub}`);
    });
});
