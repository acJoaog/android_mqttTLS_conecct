package com.example.tls;

import android.annotation.SuppressLint;
import android.content.Context;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MqttHelper {

    final private String clientId = MqttClient.generateClientId();
    private final MemoryPersistence persistence = new MemoryPersistence();

    private MqttClient client;

    public MqttHelper() {
    }

    public MqttClient connect(){
        client = null;
        try {
            // substitua com o endereço do seu broker
            String host = "ssl://192.168.0.122:8883";
            client = new MqttClient(host, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();

            // Configurar SSL/TLS ignorando a verificação do nome do host
            connOpts.setSocketFactory(createInsecureSslSocketFactory());

            System.out.println("Connecting to broker: " + host);
            client.connect(connOpts);
            System.out.println("Connected");

            client.subscribe("#");

            // Desconectar
            //client.disconnect();
            //System.out.println("Disconnected");

            return client;
        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return client;
    }

    public void publish(String payload, String topic) {
        byte[] encodedPayload = new byte[0];
        //teste de conexão
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressLint("CustomX509TrustManager")
    private static TrustManager[] getTrustAllCertsManager() {
        return new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    @SuppressLint("TrustAllX509TrustManager")
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }
                    @SuppressLint("TrustAllX509TrustManager")
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };
    }
    private static SSLSocketFactory createInsecureSslSocketFactory() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, getTrustAllCertsManager(), new SecureRandom());
        return sslContext.getSocketFactory();
    }
}

