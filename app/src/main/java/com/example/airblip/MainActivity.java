package com.example.airblip;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;


import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;


public class MainActivity extends AppCompatActivity {

    Sender sender = new Sender();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        closeOverlay();

    }

    public void promptString(View V) {
        changeOverlayText("Enter a message");
        openOverlay();
    }


    public void startSending(View v) {
        v.setEnabled(false);
        changeOverlayText("Sending...");
        hideClose();
        try {
            EditText message = (EditText) findViewById(R.id.messageInput);
            sender.setStr(message.getText().toString());
            sender.beginSending();
            changeOverlayText("Message Sent!");
            revealClose();
            v.setEnabled(true);
        } catch (Exception ex) {
            changeOverlayText("Send failed");
            revealClose();
            v.setEnabled(true);

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


    public void changeOverlayText(String text) {
        TextView statusText = findViewById(R.id.status);
        statusText.setText(text);
    }
}