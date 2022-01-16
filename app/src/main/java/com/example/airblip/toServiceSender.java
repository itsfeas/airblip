package com.example.airblip;

import android.os.Build;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.RequiresApi;

public class toServiceSender extends Handler {
    private Sender service;

    public toServiceSender(Sender send) {
        this.service = send;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case 1:
                byte[] bytes = msg.getData().getByteArray("1");
                service.setSendBytes(bytes);
            case 2:
                service.beginSending();
        }
    }
}
