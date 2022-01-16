package com.example.airblip;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.common.primitives.Bytes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class Sender extends Service {
    private boolean fileSetup;
    private Path path;
    private AudioTrack initBlip;
    private AudioTrack dataBlip;

    private double transferFreq = 100;                                  // hz
    private double soundFreq = 1000;                                    // hz
    private int sampleRate = 20000;                                     // hz

    private double initBlipLen = 5;                                     // secs
    private int initNumBytes = (int) (initBlipLen * sampleRate);     // number of bytes for conf blip

    List<Byte> file;
    private final Messenger messenger = new Messenger(new toServiceSender(this));

    public Sender() {
        setUpInitBlip();
    }

    public boolean getFileSetup() {
        return this.fileSetup;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setPath(String inputPath) {
        this.path = Paths.get(inputPath);
        this.fileSetup = true;
    }

    public void setFileBytes(List<Byte> file) {
        this.file = file;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean readFileBytes() throws IOException {
        if (!getFileSetup()) { return false; }
        byte[] bytes = Files.readAllBytes(this.path);
        List<Byte> file = Bytes.asList(bytes);
        setFileBytes(file);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setUpDataBlip() {
        try {
            readFileBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setUpInitBlip() {
        List<Byte> bytesGenerics = Collections.nCopies(this.initNumBytes, new Byte((byte) 1));
        byte[] bytes = Bytes.toArray(bytesGenerics);
        try {
            AudioTrack initBlip = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    this.sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    this.initNumBytes,
                    AudioTrack.MODE_STATIC
            );
            initBlip.write(bytes, 0, bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playInitBlip() {
        this.initBlip.play();
    }

    private void playDataBlip() {
        this.dataBlip.play();
    }

    private void sendConf() {
        Bundle bundle = new Bundle();
        bundle.putString("1", "A");
        Message msg = Message.obtain();
        msg.setData(bundle);
        try {
            this.messenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void beginSending() {
        setUpInitBlip();
        playInitBlip();

        setUpDataBlip();
        playDataBlip();
        playInitBlip();

        sendConf();                 //send confirmation of send to activity
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }
}