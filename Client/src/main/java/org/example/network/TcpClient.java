package org.example.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class TcpClient implements AutoCloseable {
    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;

    public TcpClient(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public String readLine() throws IOException {
        return reader.readLine();
    }

    public void writeLine(String message) throws IOException {
        writer.write(message);
        writer.newLine();
        writer.flush();
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}