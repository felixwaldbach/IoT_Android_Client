package com.apps.scofield.iot_android_client;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.Locale;

import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Felix on 24.10.2017.
 */

public class HomeActivity extends AppCompatActivity {

    final int SPEECHINTENT_REQ_CODE = 11;

    ImageButton btn;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);

        String clientId = MqttClient.generateClientId();
        final MqttAndroidClient client = new MqttAndroidClient(this.getApplicationContext(), "tcp://192.168.0.104:1883", clientId);
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT);
                    toast.show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        btn = (ImageButton) findViewById(R.id.speechButton);
        textView = (TextView) findViewById(R.id.textViewSpeech);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("");
                Intent speechRecognitionIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speechRecognitionIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toString());
                speechRecognitionIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, "1000");
                startActivityForResult(speechRecognitionIntent, SPEECHINTENT_REQ_CODE);
            }
        });

        /*btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topic = "test";
                String payload = textView.getText().toString();
                byte[] encodedPayload = new byte[0];
                try {
                    encodedPayload = payload.getBytes("UTF-8");
                    MqttMessage message = new MqttMessage(encodedPayload);
                    client.publish(topic, message);
                } catch (UnsupportedEncodingException | MqttException e) {
                    e.printStackTrace();
                }
            }
        });*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SPEECHINTENT_REQ_CODE && resultCode == RESULT_OK) {
            ArrayList<String> speechResults = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            String finalText;
            if(textView.getText().length() > 0) {
                finalText = textView.getText().toString() + " " + speechResults.get(0);
            } else {
                finalText = speechResults.get(0);
            }
            textView.setText(finalText);
        }
    }

}
