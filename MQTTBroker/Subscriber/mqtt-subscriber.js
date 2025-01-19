const mqtt = require('mqtt');
const axios = require('axios'); 

// Set up the MQTT client to subscribe to a topic
const mqttClient = mqtt.connect(process.env.MQTT_BROKER_URL)
const topics = ['measurements/speed', 'measurements/extreme', 'tower/add'];  // Add more topics as needed


// API endpoint to store data
const apiEndpoint = process.env.API_ENDPOINT

mqttClient.on('connect', () => {
    console.log('Connected to MQTT broker');
    mqttClient.subscribe(topics, (err) => {
        if (!err) {
            console.log(`Subscribed to ${topics}`);
        }
    });
});


mqttClient.on('message', async (topic, message) => {
    console.log('Raw message:', message.toString());
    if (topic === 'measurements/speed') {
        let payload;
        try {
            payload = JSON.parse(message.toString());
        } catch (error) {
            console.error('Failed to parse MQTT message as JSON:', error);
            return;
        }

        const measuredBy = payload.user || null; 
        const data = {
            speed: payload.speed,
            type: payload.type,
            provider: payload.provider,
            time: new Date().toISOString(),  
            location: {
                type: payload.location?.type || "Point",  
                coordinates: payload.location?.coordinates || [0, 0]  
            },
            measuredBy: measuredBy  
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
    } else if (topic === 'measurements/extreme') {

    } else if (topic === 'tower/add' ){
        let payload;
        try {
            payload = JSON.parse(message.toString());
        } catch (error) {
            console.error('Failed to parse MQTT message as JSON:', error);
            return;
        }

        const { tower, jwtToken } = payload;

        if (!jwtToken) {
            console.error('JWT token is missing in the payload');
            return;
        }

        const location = {
            type: tower.location?.type || 'Point',
            coordinates: tower.location?.coordinates || [0, 0]
        };

        const towerData = {
            location: location,
            operator: tower.operator,
            type: tower.type,
            confirmed: tower.confirmed || false,
            locator: tower.locator || null
        };

        console.log('Data being sent to create tower:', towerData);

        try {
            // Forward the JWT token as part of the Authorization header
            const response = await axios.post(apiEndpoint + '/mobile/', towerData, {
                headers: { Authorization: `Bearer ${jwtToken}` }
            });
            console.log('Tower saved to database:', response.data);
        } catch (error) {
            if (error.response) {
                console.error('Server error:', error.response.data);
            } else if (error.request) {
                console.error('No response received:', error.request);
            } else {
                console.error('Error setting up the request:', error.message);
            }
        }
    }
});
