package com.example.airblip;

import android.os.Build;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.RequiresApi;

public class toServiceReceiver extends Handler {
    private Receiver service;

    public toServiceReceiver(Receiver receive) {
        this.service = receive;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case 1:
                service.listenSequence();
        }
    }
}
