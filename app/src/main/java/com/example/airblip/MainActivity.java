package com.example.airblip;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.content.Intent;
import android.widget.TextView;

import java.util.concurrent.RunnableFuture;


public class MainActivity extends AppCompatActivity {

    Receiver  receiver = new Receiver();
    Sender sender = new Sender();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    public void startReceiving(View v){
        v.setEnabled(false);
        receiver.beginListening();
    }

    public void startSending(View v){
        v.setEnabled(false);
        sender.setStr("Hello World");
        sender.beginSending();
    }

    public void openOverlay(){

    }

    public void changeOverlayText(String text){
//        TextView text = findViewById(R.id.statusText);
    }
}