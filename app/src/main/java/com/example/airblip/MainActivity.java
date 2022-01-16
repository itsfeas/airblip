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


public class MainActivity extends AppCompatActivity {

    private Messenger sendMessenger;
    private Messenger receiveMessenger;
    private ServiceConnection senderServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder iBinder) {
            sendMessenger = new Messenger(iBinder);
        }
        public void onServiceDisconnected(ComponentName className) {
        }
    };
    private ServiceConnection receiverServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder iBinder) {
            receiveMessenger= new Messenger(iBinder);
        }
        public void onServiceDisconnected(ComponentName className) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }


    public void startReceiving(View v){
        v.setEnabled(false);
        //Connect messenger to service
        startService(new Intent( this, Receiver.class ));
        bindService(new Intent(this, Receiver.class), senderServiceConnection,
                Context.BIND_AUTO_CREATE);
        Bundle bundle = new Bundle();
        Message message = Message.obtain();
        message.what = 1;
        try {
            sendMessenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public void startSending(View v){
        v.setEnabled(false);
        //Connect messenger to service
        startService(new Intent( this, Sender.class ));
        bindService(new Intent(this, Sender.class), senderServiceConnection,
                Context.BIND_AUTO_CREATE);

        Bundle bundle = new Bundle();
        bundle.putString("1", "testString");
        Message message = Message.obtain();
        message.setData(bundle);
        message.what = 1;
        try {
            sendMessenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public void openOverlay(){

    }

    public void changeOverlayText(String text){
//        TextView text = findViewById(R.id.statusText);
    }
}