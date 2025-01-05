const mqtt = require('mqtt');
const axios = require('axios');  // For making HTTP requests

// Set up the MQTT client to subscribe to a topic
const mqttClient = mqtt.connect('mqtt://localhost:1883');
const topic_measurement = 'measurements/speed';

// API endpoint to store data
const apiEndpoint = 'http://20.160.43.196:3000';

mqttClient.on('connect', () => {
    console.log('Connected to MQTT broker');
    mqttClient.subscribe(topic_measurement, (err) => {
        if (!err) {
            console.log(`Subscribed to ${topic_measurement}`);
        }
    });
});


mqttClient.on('message', async (topic_measurement, message) => {
    console.log('Raw message:', message.toString());

    let payload;
    try {
        payload = JSON.parse(message.toString());
    } catch (error) {
        console.error('Failed to parse MQTT message as JSON:', error);
        return;
    }

    // Check if user exists and has an id, otherwise set measuredBy to null
    const measuredBy = payload.user || null;  // Assuming id is already a string (from Kotlin)

    const data = {
        speed: payload.speed,
        type: payload.type,
        provider: payload.provider,
        time: new Date().toISOString(),  // Use current time as ISO string
        location: {
            type: payload.location?.type || "Point",  // Default to "Point" if not present
            coordinates: payload.location?.coordinates || [0, 0]  // Default to [0,0] if not present
        },
        measuredBy: measuredBy  // Send the counter value or null
    };
    

    console.log('Data being sent:', data);

    try {
        const response = await axios.post(apiEndpoint + "/measurements/", data);
        console.log('Data saved to database:', response.data);
    } catch (error) {
        if (error.response) {
            console.error('Server error:', error.response.data);
        } else if (error.request) {
            console.error('No response received:', error.request);
        } else {
            console.error('Error setting up the request:', error.message);
        }
    }
});
