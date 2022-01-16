package com.example.airblip;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.google.common.primitives.Bytes;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class Sender {
    private boolean fileSetup;
    private String sendStr;
    private AudioTrack initBlip;
    private AudioTrack dataBlip;

    private double transferFreq = 50;                                  // hz
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

    private byte[] getDataBytes() {
        return Bytes.toArray(this.file);
    }

    public void setSendBytes(List<Byte> bytes) {
        this.file = bytes;
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
            AudioTrack dataBlip = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    this.sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    this.initNumBytes,
                    AudioTrack.MODE_STATIC
            );
            byte[] bytes = getDataBytes();
            dataBlip.write(bytes, 0, this.file.size());
            this.dataBlip = dataBlip;
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
            this.initBlip = initBlip;
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