package com.example.airblip;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startReceiving(View v){
        v.setEnabled(false);
    }

    public void startSending(View v){

    }

    public void openOverlay(){

    }

    public void changeOverlayText(){

    }
}