package com.example.tls;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Referência ao layout raiz e ao botão
    View background;

    MqttClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        background = findViewById(R.id.main);

        MqttHelper mqttHelper = new MqttHelper();
        client = mqttHelper.connect();
        startMqtt(mqttHelper);

        mqttHelper.publish("Olá, altere meu background.","main");
    }

    private void startMqtt(MqttHelper mqttHelper){
        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.w("Mqtt", message.toString());
                if (topic.equals("background")){
                    runOnUiThread(() -> changeBackgroundColor(message.toString()));
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void changeBackgroundColor(String color) {
        try {
            switch (color) {
                case "verde":
                    background.setBackgroundColor(Color.argb(255,166,224,148));
                    break;
                case "amarelo":
                    background.setBackgroundColor(Color.argb(255,232,228,144));
                    break;
                case "laranja":
                    background.setBackgroundColor(Color.argb(255,240,115,96));
                    break;
                case "rosa":
                    background.setBackgroundColor(Color.argb(255,191,42,127));
                    break;
                case "roxo":
                    background.setBackgroundColor(Color.argb(255,96,60,92));
                    break;
                default:
                    Log.w("Mqtt", "Unknown color: " + color);
                    break;
            }
        } catch (Exception e) {
            Log.e("Mqtt", "Error changing background color", e);
        }
    }
}