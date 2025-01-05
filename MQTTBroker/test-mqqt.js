const mqtt = require('mqtt');

// Define the broker URL and connection options
const brokerUrl = 'mqtt://192.168.1.103:1883';  // Replace with your MQTT broker IP and port
const topic = 'test/topic';  // Topic to subscribe and publish to
const clientId = `mqtt_api_${Math.random().toString(16).substr(2, 8)}`;

// Connect to the MQTT broker
const client = mqtt.connect(brokerUrl, {
  clientId: clientId,
  clean: true,
  connectTimeout: 4000,
  reconnectPeriod: 1000,
});

// When connected, subscribe to the topic and publish a test message
client.on('connect', () => {
  console.log(`Connected to broker at ${brokerUrl}`);

  // Subscribe to the test topic
  client.subscribe(topic, { qos: 1 }, (err) => {
    if (!err) {
      console.log(`Subscribed to topic: ${topic}`);
      // Publish a test message
      client.publish(topic, 'Hello MQTT', { qos: 1, retain: false }, (err) => {
        if (!err) {
          console.log(`Message published to topic: ${topic}`);
        } else {
          console.log('Failed to publish message:', err);
        }
      });
    } else {
      console.log('Failed to subscribe to topic:', err);
    }
  });
});

// Log messages received on the subscribed topic
client.on('message', (topic, message) => {
  console.log(`Received message: Topic = ${topic}, Message = ${message.toString()}`);
});

// Handle connection errors
client.on('error', (err) => {
  console.error('Connection failed:', err);
  client.end();
});

// Handle client disconnection
client.on('close', () => {
  console.log('Connection closed');
});