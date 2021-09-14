package console_chat.server;

import console_chat.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class Server {
    private static final String EXIT_COMMAND = "exit()";

    private final AtomicBoolean isStopped = new AtomicBoolean(false);
    private final AtomicLong counter = new AtomicLong(0);
    private final Map<Long, ClientConnection> activeConnections = new ConcurrentHashMap<>();
    private final int port;
    private final int backlogSize;

    public static void main(String[] args) {
        Server.getInstance();

    }

    public static Server getInstance() {
        return new Server();
    }

    private Server() {
        System.out.println("Starting server...");

        initShutdownHook();

        port = Utils.getPortNumber();
        backlogSize = Utils.getServerBacklogSize();

        initPortListen();
        initConnectionCleanUp();
        initCliListener();
    }

    private void initCliListener() {
        new Thread(() -> {
            while (!isStopped.get()) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                try {
                    String command = bufferedReader.readLine();
                    if (EXIT_COMMAND.equals(command)) {
                        isStopped.set(true);
                        fakeConnection();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();
    }

    private void fakeConnection() {
        try {
            new Socket("127.0.0.1", port).close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initPortListen() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port, backlogSize)) {

                while (!isStopped.get()) {
                    var socket = serverSocket.accept();
                    processAcceptedConnection(socket);
                }

            } catch (IOException e) {
                isStopped.set(true);
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void processAcceptedConnection(Socket socket) {
        long number = counter.incrementAndGet();
        var clientConnection = new ClientConnection(socket, number);
        new Thread(clientConnection).start();
        activeConnections.put(number, clientConnection);
        System.out.println(String.format("Connection '%s' accepted.", number));
    }

    private void initConnectionCleanUp() {
        new Thread(() -> {
            while (!isStopped.get()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                Set<Map.Entry<Long, ClientConnection>> entries = activeConnections.entrySet();

                entries.forEach(entry -> {
                    ClientConnection clientConnection = entry.getValue();
                    if (clientConnection.isClosed()) {
                        System.out.println(String.format("Connection '%s' to be removed.", entry.getKey()));
                        activeConnections.remove(entry.getKey());
                    }
                });

            }
        }).start();
    }

    private void initShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            isStopped.set(true);
            System.out.println("\nTerminating server...\n");
        }));
    }
}

