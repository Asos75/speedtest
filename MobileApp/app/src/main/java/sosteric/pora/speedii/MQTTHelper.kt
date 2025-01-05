package sosteric.pora.speedii

import android.content.Context
import android.util.Log
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.MqttGlobalPublishFilter
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish
import java.nio.charset.StandardCharsets

class MqttHelper(private val context: Context) {

    private val clientId = "AndroidClient_" + System.currentTimeMillis()
    private val username = "yourUsername"
    private val password = "yourPassword"

    private val mqttClient: Mqtt3AsyncClient = MqttClient.builder()
        .useMqttVersion3()
        .serverHost("10.0.2.2")
        .serverPort(1883)
        .identifier(clientId)
        .buildAsync()

    init {
        connect()
    }

    fun connect() {
        mqttClient.connectWith()
            .simpleAuth()
            .username(username)
            .password(password.toByteArray())
            .applySimpleAuth()
            .send()
            .whenComplete { connAck: Mqtt3ConnAck?, throwable: Throwable? ->
                if (throwable != null) {
                    Log.e("MqttHelper", "Failed to connect to MQTT broker. Exception: ${throwable.message}")
                } else {
                    Log.d("MqttHelper", "Connected to MQTT broker successfully.")
                    subscribeToTopic("test/topic")
                }
            }

        mqttClient.publishes(MqttGlobalPublishFilter.ALL) { publish: Mqtt3Publish ->
            Log.d("MqttHelper", "Message arrived: ${publish.payloadAsBytes.toString(StandardCharsets.UTF_8)}")
        }
    }

    fun isConnected(): Boolean {
        return mqttClient.state.isConnected
    }

    fun subscribeToTopic(topic: String) {
        mqttClient.subscribeWith()
            .topicFilter(topic)
            .qos(MqttQos.AT_LEAST_ONCE)
            .send()
            .whenComplete { subAck, throwable ->
                if (throwable != null) {
                    Log.e("MqttHelper", "Failed to subscribe to $topic. Exception: ${throwable.message}")
                } else {
                    Log.d("MqttHelper", "Subscribed to $topic successfully.")
                }
            }
    }

    fun publishMessage(topic: String, message: String) {
        mqttClient.publishWith()
            .topic(topic)
            .payload(message.toByteArray())
            .qos(MqttQos.AT_LEAST_ONCE)
            .send()
            .whenComplete { pubAck, throwable ->
                if (throwable != null) {
                    Log.e("MqttHelper", "Failed to publish message to $topic. Exception: ${throwable.message}")
                } else {
                    Log.d("MqttHelper", "Published message to $topic successfully.")
                }
            }
    }

    fun disconnect() {
        mqttClient.disconnect()
            .whenComplete { _, throwable ->
                if (throwable != null) {
                    Log.e("MqttHelper", "Failed to disconnect from MQTT broker. Exception: ${throwable.message}")
                } else {
                    Log.d("MqttHelper", "Disconnected from MQTT broker successfully.")
                }
            }
    }
}