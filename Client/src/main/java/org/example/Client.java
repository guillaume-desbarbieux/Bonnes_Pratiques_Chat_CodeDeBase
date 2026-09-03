package org.example;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import com.google.gson.Gson;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private String serverAddress;
    private int serverPort;
    private Socket socket;
    private ExecutorService executor;
    private BufferedReader bufferedReader;
    private int messageCount = 0;
    private Gson gson = new Gson();

    public Client(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void connect() throws IOException, InterruptedException, ExecutionException {
        socket = new Socket(serverAddress, serverPort);
        executor = Executors.newFixedThreadPool(2);

        Future<?> receiveTask = executor.submit(this::receiveMessages);
        Thread.sleep(100);
        Future<?> sendTask = executor.submit(this::sendMessages);

        receiveTask.get();
        sendTask.get();

        shutdown();
    }

    private void receiveMessages() {
        try {
            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String message;
            while ((message = bufferedReader.readLine()) != null) {
                System.out.println("\r" + message);
                System.out.print("You: ");
            }
        } catch (IOException e) {
            System.out.println("Disconnected");
        }
        // TODO: fermer le reader
    }

    private void sendMessages() {
        try {
            OutputStream outputStream = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(outputStream);
            BufferedWriter w = new BufferedWriter(osw);
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            String input;
            String author = null;
            while ((input = bufferedReader.readLine()) != null) {
                w.write(input);
                w.newLine();
                w.flush();
                if (author == null) {
                    author = input;
                } else {
                    Message msg = new Message(author, input, new DateTime().toString());
                    String json = gson.toJson(msg);
                    logger.info(json);
                }
                System.out.print("You: ");
                messageCount = messageCount + 1;
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void shutdown() throws IOException {
        if (executor != null) {
            executor.shutdown();
        }
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    class Message{
        private String author;
        private String input;
        private String timestamp;

        public Message(String author, String input, String timestamp) {
            this.author = author;
            this.input = input;
            this.timestamp = timestamp;
        }

        public String getAuthor() {
            return author;
        }

        public String getInput() {
            return input;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }
}