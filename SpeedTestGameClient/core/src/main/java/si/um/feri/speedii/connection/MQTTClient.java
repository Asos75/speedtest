package si.um.feri.speedii.connection;

import com.google.gson.*;
import org.eclipse.paho.client.mqttv3.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import io.github.cdimascio.dotenv.Dotenv;
import si.um.feri.speedii.classes.ExtremeEvent;
import si.um.feri.speedii.classes.Location;
import si.um.feri.speedii.classes.Events;
import si.um.feri.speedii.classes.User;
import si.um.feri.speedii.classes.gsonserializers.LocalDateTimeDeserializer;
import si.um.feri.speedii.classes.gsonserializers.LocalDateTimeSerializer;

import java.util.*;
import java.time.LocalDateTime;

public class MQTTClient {

    private final String mqttBrokerURL;
    private static MqttClient mqttClient;
    private static final String EXTREME_EVENTS_TOPIC_RESPONSE = "measurements/extreme/response";
    private static final String EXTREME_MEASUREMENTS_TOPIC_REQUEST = "measurements/extreme/request";
    private static final String EXTREME_EVENTS_TOPIC = "measurements/extreme";

    private static final String EXTREME_EVENTS_REQUEST_URL = "http://your-api-url/measurements/extreme/request";

    public MQTTClient() {
        Dotenv dotenv = Dotenv.configure().filename(".env").load();
        this.mqttBrokerURL = dotenv.get("MQTT_BROKER_URL");
    }

    public void connectToBroker(ArrayList<ExtremeEvent> extremeEvents) {
        try {
            mqttClient = new MqttClient(mqttBrokerURL, MqttClient.generateClientId());
            mqttClient.connect();
            System.out.println("Connected to MQTT broker");

            mqttClient.subscribe(EXTREME_EVENTS_TOPIC_RESPONSE);
            System.out.println("Subscribed to topic: " + EXTREME_EVENTS_TOPIC_RESPONSE);

            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection lost: " + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    if (topic.equals(EXTREME_EVENTS_TOPIC_RESPONSE)) {
                        mqttClient.unsubscribe(EXTREME_EVENTS_TOPIC_RESPONSE);
                        System.out.println("Unsubscribed from topic: " + EXTREME_EVENTS_TOPIC_RESPONSE);

                        String response = new String(message.getPayload(), StandardCharsets.UTF_8);
                        System.out.println("Received response: " + response);

                        ExtremeEvent[] events = parseEventsResponse(response);
                        extremeEvents.addAll(Arrays.asList(events));
                    }
                    if(topic.equals(EXTREME_EVENTS_TOPIC)) {
                        String response = new String(message.getPayload(), StandardCharsets.UTF_8);
                        System.out.println("Received response: " + response);

                        ExtremeEvent event = parseEventsResponse(response)[0];
                        extremeEvents.add(event);
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                }
            });

        } catch (MqttException e) {
            System.out.println("Error connecting to MQTT broker: " + e.getMessage());
        }
    }

    public boolean isBrokerConnected() {
        return mqttClient.isConnected();
    }

    public void requestExtremeEvents() {
        String requestMessage = "Requesting extreme events";
        try {
            publishEvent(EXTREME_MEASUREMENTS_TOPIC_REQUEST, requestMessage);
            System.out.println("Request message sent: " + requestMessage);
        } catch (Exception e) {
            System.out.println("Error publishing request: " + e.getMessage());
        }
    }

    private static ExtremeEvent[] parseEventsResponse(String response) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());

        Gson gson = gsonBuilder.create();

        return gson.fromJson(response, ExtremeEvent[].class);
    }

    public void publishEvent(String topic, String message) {
        try {
            mqttClient.publish(topic, message.getBytes(), 2, false);
            System.out.println("Published message: " + message);
        } catch (MqttException e) {
            System.out.println("Error publishing message: " + e.getMessage());
        }
    }

    public void disconnectFromBroker() {
        try {
            mqttClient.disconnect();
            System.out.println("Disconnected from MQTT broker");
        } catch (MqttException e) {
            System.out.println("Error disconnecting from MQTT broker: " + e.getMessage());
        }
    }

    public static List<ExtremeEvent> generateFakeEvents() {
        List<ExtremeEvent> fakeEvents = new ArrayList<>();
        Random random = new Random();

        double minLat = 46.5658033949458;
        double minLon = 15.627778352948035;
        double maxLat = 46.53817721717095;
        double maxLon = 15.65561900765952;

        // Generate 5 fake events
        for (int i = 0; i < 5; i++) {
            Events eventType = getRandomEventType(random);
            Location location = new Location("Point", Arrays.asList(minLon + (maxLon - minLon) * random.nextDouble(), minLat + (maxLat - minLat) * random.nextDouble()));
            LocalDateTime time = LocalDateTime.now().minusDays(random.nextInt(7));
            User user = null;

            ExtremeEvent event = new ExtremeEvent(eventType, location, time, user);
            fakeEvents.add(event);
        }

        return fakeEvents;
    }

    private static Events getRandomEventType(Random random) {
        Events[] eventTypes = Events.values();
        return eventTypes[random.nextInt(eventTypes.length)];
    }

    public void publishFakeEvents() {
        List<ExtremeEvent> fakeEvents = generateFakeEvents();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());

        Gson gson = gsonBuilder.create();

        for (ExtremeEvent event : fakeEvents) {
            String jsonEvent = gson.toJson(event);
            publishEvent(EXTREME_EVENTS_TOPIC, jsonEvent);
            System.out.println("Published fake event: " + jsonEvent);
        }
    }
}
