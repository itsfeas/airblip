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

public class Sender {
    private boolean fileSetup;
    private String sendStr;
    private AudioTrack initBlip;
    private AudioTrack dataBlip;

    private double transferFreq = 100;                                  // hz
    private double soundFreq = 1500;                                    // hz
    private int sampleRate = 20000;                                     // hz

    private double initBlipLen = 1;                                     // secs
    private int initNumBytes = (int) (initBlipLen * sampleRate);     // number of bytes for conf blip

    List<Byte> file;

    public Sender() {
        setUpInitBlip();
    }

    public boolean getFileSetup() {
        return this.fileSetup;
    }

    public void setStr(String str) {
        this.sendStr = str;
        this.fileSetup = true;
    }
    
    public void setSendBytes(byte[] bytes) {
        List<Byte> file = Bytes.asList(bytes);
        setSendBytes(bytes);
    }

    public void setSendBytes(List<Byte> bytes) {
        this.file = file;
    }

    private boolean readFileBytes() throws IOException {
        if (!getFileSetup()) { return false; }
        byte[] bytes = this.sendStr.getBytes();
        List<Byte> file = Bytes.asList(bytes);
        setSendBytes(file);
        return true;
    }

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

    public void beginSending() {
        setUpInitBlip();
        playInitBlip();

        setUpDataBlip();
        playDataBlip();
        playInitBlip();
    }
}