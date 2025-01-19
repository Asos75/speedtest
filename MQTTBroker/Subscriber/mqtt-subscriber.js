require('dotenv').config();  
const mqtt = require('mqtt');
const axios = require('axios'); 
const moment = require('moment');
const fs = require('fs');

const mqttBrokerURL = process.env.MQTT_BROKER_URL;
console.log(`Trying to connect to ${mqttBrokerURL}`);
const mqttClient = mqtt.connect(mqttBrokerURL);
const topics = ['measurements/speed', 'measurements/extreme', 'tower/add', 'measurements/extreme/request'];  

const eventsFilePath = './extremeEvents.json';


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

const getExtremeEventsFromFile = () => {
    try {
        const data = fs.readFileSync(eventsFilePath);
        return JSON.parse(data);
    } catch (err) {
        return []; // If the file doesn't exist or there's an error, return an empty array
    }
};

const saveExtremeEventsToFile = (events) => {
    fs.writeFileSync(eventsFilePath, JSON.stringify(events, null, 2), 'utf8');
};

const removeOldEvents = (events) => {
    const oneWeekAgo = moment().subtract(7, 'days');
    return events.filter(event => moment(event.time).isAfter(oneWeekAgo));
};



mqttClient.on('message', async (topic, message) => {
    console.log('Raw message:', message.toString());
    if (topic === '/measurements/speed') {
        let payload;
        try {
            payload = JSON.parse(message.toString());
        } catch (error) {
            console.error('Failed to parse MQTT message as JSON:', error);
            return;
        }

        const { type, location, time, user } = payload;

        const newEvent = {
            type: type,
            location: location,
            time: time, 
            user: user
        };

        console.log('New extreme event:', newEvent);

        const events = getExtremeEventsFromFile();
        
        events.push(newEvent);

        const updatedEvents = removeOldEvents(events);

        saveExtremeEventsToFile(updatedEvents);
    } else if (topic === 'measurements/extreme') {
        let payload;
        try {
            payload = JSON.parse(message.toString());
        } catch (error) {
            console.error('Failed to parse MQTT message as JSON:', error);
            return;
        }

        const { type, location, time, user } = payload;

        const newEvent = {
            type: type,
            location: location,
            time: time, // Make sure the time is in ISO string format or a format you can parse easily
            user: user
        };

        console.log('New extreme event:', newEvent);

        const events = getExtremeEventsFromFile();

        events.push(newEvent);

        const filteredEvents = removeOldEvents(events);

        saveExtremeEventsToFile(filteredEvents);

        console.log('Extreme events list updated:', filteredEvents);
    } else if (topic === '/measurements/speed') {
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
    } else if(topic === 'measurements/extreme/request') {
    
            let events = getExtremeEventsFromFile();
            events = removeOldEvents(events); 
            
            mqttClient.publish('measurements/extreme/response', JSON.stringify(events), (err) => {
                if (err) {
                    console.error('Error publishing MQTT message:', err);
                } else {
                    console.log('Extreme events published successfully.');
                }
            });
        
    } else if (topic === '/tower/add' ){
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
