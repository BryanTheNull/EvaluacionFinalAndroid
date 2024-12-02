package com.example.evaluacionfinalandroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttActivity extends AppCompatActivity {

    // Variables de la conexion a MQTT
    private static  String mqttHost = "tcp://mqttservidoriott.cloud.shiftr.io:1883";
    private static  String IdUsuario = "AppAndroid";

    private static  String Topico = "Mensaje";
    private static  String User = "mqttservidoriott";
    private static  String Pass = "EQnkvvnOpPU4oleh";

    // Variables que utilizara para imprimir los datos del sensor
    private TextView textView;
    private EditText editText;
    private Button botonEnvio;

    // Libreria de MQTT
    private MqttClient mqttClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqtt);

        // Enlace de la variable del id que esta en el activiti main dodne imprimiremos los datos
        textView = findViewById(R.id.txtMensajeRecibido);
        editText = findViewById(R.id.txtMensajeEnviado);
        botonEnvio = findViewById(R.id.botonEnviarMensaje);

        try{
            // Creacion de un cliente MQTT
            mqttClient = new MqttClient(mqttHost, IdUsuario, null);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(User);
            options.setPassword(Pass.toCharArray());

            // Conexion al servidor MQTT
            mqttClient.connect(options);

            // Si se conceta imprimira un mensaje de MQTT
            Toast.makeText(MqttActivity.this, "Aplicacion concetada a servidor MQTT", Toast.LENGTH_SHORT).show();

            // Manejo la entrega de datos y la perdidad de conexion
            mqttClient.setCallback(new MqttCallback() {
                // Metodo en caso de que la conexion se pierda
                @Override
                public void connectionLost(Throwable cause) {
                    Log.d("MQTT", "Conexion perdida");
                }

                // Metodo para enviar el mensaje a MQTT
                @Override
                public void messageArrived(String topic, MqttMessage message){
                    String payload = new String(message.getPayload());
                    runOnUiThread(() -> textView.setText(payload));
                }

                // Metodo para verificar que el envio fue exitoso
                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d("MQTT", "Entrega completa");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        // Al dar click en el boton de enviar el mensaje al topico
        botonEnvio.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // Obtener el mensaje que ingreso el usuario
                String mensaje = editText.getText().toString();
                try {
                    if(mqttClient != null && mqttClient.isConnected()){
                        mqttClient.publish(Topico, mensaje.getBytes(), 0, false);
                        // Mostrar el mensaje enviado en el textView
                        textView.append("\n -" + mensaje);
                        Toast.makeText(MqttActivity.this, "Mensaje enviado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MqttActivity.this, "Error: No se pudo enviar el mensaje. la conexion MQTT no esta activa", Toast.LENGTH_SHORT).show();
                    }
                } catch (MqttException e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void onClickFireBase(View view) {
        Intent intent = new Intent(this, FireBaseActivity.class);
        startActivity(intent);
        finish();
    }
}