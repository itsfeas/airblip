package com.example.airblip;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.common.primitives.Bytes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

public class Receiver extends Service {
    private Socket sock;
    private String host = "localhost";
    private PrintWriter out;
    private BufferedReader in;
    private List<Byte> file;

    public Receiver() {
    }

    private Socket getSocket() {
        return this.sock;
    }

    private BufferedReader getInput() {
        return this.in;
    }

    private PrintWriter getOutput() {
        return this.out;
    }

    private void addToFile(byte[] bytes) {
        addToFile(Bytes.asList(bytes));
    }

    private void addToFile(List<Byte> bytes) {
        this.file.addAll(bytes);
    }

    private void initSocket() {
        try {
            this.sock = new Socket(InetAddress.getByName(this.host), 4000);
            this.out = new PrintWriter(sock.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildFile() throws IOException {
        BufferedReader in = getInput();

        byte[] inBytes = in.readLine().getBytes();
        while (inBytes != null) {
            addToFile(inBytes);
            inBytes = in.readLine().getBytes();
        }
    }

    private void buildSequence() {
        BufferedReader in = getInput();
        PrintWriter out = getOutput();

        try {
            String initByte = in.readLine();
            if (initByte != "B") {
                return;
            }
            out.println("A");
            buildFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
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