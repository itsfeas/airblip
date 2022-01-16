package com.example.airblip;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.google.common.primitives.Bytes;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Sender {
    private boolean fileSetup;
    private String sendStr;
    private byte[] initBlipBytes;
    private byte[] dataBlipBytes;
    private AudioTrack initBlip;
    private AudioTrack dataBlip;

    private final double transferFreq = 100;                                  // hz
    private double dataSoundFreq = 1000;                                    // hz
    private double initSoundFreq = 1500;                                    // hz
    private int sampleRate = 20000;                                     // hz

    private double initBlipLen = 1;                                     // secs
    private int initNumBytes = (int) (initBlipLen * sampleRate);     // number of bytes for conf blip
    private int dataNumBytes;     // number of bytes for conf blip

    List<Boolean> file;
    List<Integer> boolInts;

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
    
    public void setSendBytes() throws UnsupportedEncodingException {
        String str = this.sendStr;
        int len = str.length()*8;

        List<Boolean> bins = new ArrayList<Boolean>(len);
        List<Integer> intBins = new ArrayList<Integer>(len);
        String binStr;
        for (int i = 0; i<str.length(); i++) {
            binStr = Integer.toBinaryString(str.charAt(i) & 255 | 256).substring(1);
            for (int j = 0; j<binStr.length(); j++) {
                if (binStr.charAt(j)=='1') {
                    bins.add(true);
                    intBins.add(1);
                }
                else {
                    bins.add(false);
                    intBins.add(0);
                }
            }
            this.file = bins;
            this.boolInts = intBins;
        }
    }

    public void setSendBytes(List<Boolean> bytes) {
        this.file = bytes;
    }

    private boolean readFileBytes() throws IOException {
        if (!getFileSetup()) { return false; }
//        byte[] bytes = this.sendStr.getBytes(StandardCharsets.UTF_8);
//        List<Byte> file = Bytes.asList(bytes);
        setSendBytes();
        genSendSnd();
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
//            genSendSnd();
            dataBlip.write(this.dataBlipBytes, 0, this.dataBlipBytes.length);
            this.dataBlip = dataBlip;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private void emptyBytes(){
//        // fill out the array
//        for (int i = 0; i < numSamples; ++i) {
//            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone));
//        }
//
//        // convert to 16 bit pcm sound array
//        // assumes the sample buffer is normalised.
//        int idx = 0;
//        for (final double dVal : sample) {
//            // scale to maximum amplitude
//            final short val = (short) ((dVal * 32767));
//            // in 16 bit wav PCM, first byte is the low order byte
//            generatedSnd[idx++] = (byte) (val & 0x00ff);
//            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
//
//        }
//    }

    // sinewave gen based on
    // https://stackoverflow.com/questions/8698633/how-to-generate-a-particular-sound-frequency
    void genTone(){
        // fill out the array
        double sample[] = new double[initNumBytes];
        double replicate[] = new double[initNumBytes];
        for (int i = 0; i < initNumBytes; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/initSoundFreq));
        }
        for (int i = 0; i < initNumBytes; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/initSoundFreq));
        }
        int idx = 0;
        byte generatedSnd[] = new byte[2 * initNumBytes];

        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }
        this.initBlipBytes = generatedSnd;
    }

    void genSendSnd(){
        int nBits = this.file.size();
        List<Integer> bits = this.boolInts;
        double time = (double) nBits/this.transferFreq;
        this.dataNumBytes = (int) (nBits * sampleRate);     // number of bytes for info blip

        // fill out the array
        double sample[] = new double[dataNumBytes];
        for (int i = 0; i < dataNumBytes; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/dataSoundFreq));
        }

        int i = 0;
        byte generatedSnd[] = new byte[2 * dataNumBytes];
        for (final double dVal : sample) {
            // scale to maximum amplitude

            short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[2*i] = (byte) ((val & 0x00ff)*(bits.get(i/dataNumBytes)));
            generatedSnd[2*i+1] = (byte) ((val & 0xff00) >>> 8);
            i++;
        }
        this.dataBlipBytes = generatedSnd;
    }

    private void setUpInitBlip() {
        genTone();
        try {
            AudioTrack initBlip = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    this.sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    this.initNumBytes,
                    AudioTrack.MODE_STATIC
            );
            initBlip.write(this.initBlipBytes, 0, this.initBlipBytes.length);
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
        return;
    }
}