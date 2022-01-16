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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.concurrent.RunnableFuture;


public class MainActivity extends AppCompatActivity {

    Receiver receiver = new Receiver();
    Sender sender = new Sender();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        closeOverlay();

    }


    public void startReceiving(View v) {
        v.setEnabled(false);
        changeOverlayText("Receiving...");
        openOverlay();
        try {
            receiver.beginListening();
            changeOverlayText("Your message:");
        } catch (Exception ex) {
            changeOverlayText("Listen failed.");
            revealClose();
        }

    }

    public void promptString(View V) {
        changeOverlayText("Enter a message");
        openOverlay();
    }


    public void startSending(View v) {
        v.setEnabled(false);
        sender.setStr("Hello World");
        sender.beginSending();
//        PythonEnv.run();
    }

        }

    }

    private void revealClose() {
        TextView text = findViewById(R.id.close);
        text.setVisibility(View.VISIBLE);
    }

    private void hideClose() {
        TextView text = findViewById(R.id.close);
        text.setVisibility(View.INVISIBLE);

    }

    public void openOverlay(View v) {

        openOverlay();
    }

    public void openOverlay() {
        Button v = findViewById(R.id.send);
        v.setEnabled(false);

        RelativeLayout layout = findViewById(R.id.overlay);
        layout.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FadeOut).duration(0).playOn(layout);
        YoYo.with(Techniques.FadeIn).duration(400).playOn(layout);

        TextView status = findViewById(R.id.status);
        YoYo.with(Techniques.FadeOut).duration(0).playOn(status);
        YoYo.with(Techniques.FadeIn).duration(1000).playOn(status);

        EditText form = findViewById(R.id.messageInput);
        YoYo.with(Techniques.FadeOut).duration(0).playOn(form);
        YoYo.with(Techniques.FadeIn).duration(1600).playOn(form);

        Button sendButton = findViewById(R.id.sendButton);
        sendButton.setEnabled(true);

        YoYo.with(Techniques.FadeOut).duration(0).playOn(sendButton);
        YoYo.with(Techniques.FadeIn).duration(2200).playOn(sendButton);
        revealClose();
    }

    public void closeOverlay(View v) {
        closeOverlay();

    }

    public void closeOverlay() {
        RelativeLayout layout = findViewById(R.id.overlay);
        YoYo.with(Techniques.FadeOut).duration(400).playOn(layout);
        layout.setVisibility(View.INVISIBLE);
        Button v = findViewById(R.id.send);
        v.setEnabled(true);
        hideClose();
    }

//    private void showMessage(){
//        RelativeLayout layout =findViewById(R.id.resultBox);
//        layout.setVisibility(View.VISIBLE);
//    }
//
//    private void hideMessage(){
//        RelativeLayout layout =findViewById(R.id.resultBox);
//        layout.setVisibility(View.GONE);
//    }

    public void changeOverlayText(String text) {
        TextView statusText = findViewById(R.id.status);
        statusText.setText(text);
    }
}