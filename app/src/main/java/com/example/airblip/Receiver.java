package com.example.airblip;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.google.common.primitives.Bytes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

//                byte[] bytes = msg.getData().getByteArray("2");
//                        service.setPath(bytes.toString());

public class Receiver {
    private Socket sock;
    private final String host = "localhost";
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

    private byte[] getFileBytes() {
        return Bytes.toArray(this.file);
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

    public byte[] beginListening() {
        BufferedReader in = getInput();
        PrintWriter out = getOutput();
        try {
            String initByte = in.readLine();
            if (initByte != "B") {
                return null;
            }
            out.println("A");
            buildFile();
            return getFileBytes();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}