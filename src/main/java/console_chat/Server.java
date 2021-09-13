package console_chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class Server {
    private final AtomicBoolean isStopped = new AtomicBoolean(false);
    private final AtomicLong counter = new AtomicLong(0);
    private final Map<Long, Socket> activeSockets = new ConcurrentHashMap<>();
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


        try (ServerSocket serverSocket = new ServerSocket(port, backlogSize)) {

            while (!isStopped.get()) {
                Socket socket = serverSocket.accept();
            }

        } catch (IOException e) {
            isStopped.set(true);
            throw new RuntimeException(e);
        }
    }

    private void initShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            isStopped.set(true);
            System.out.println("\nTerminating server...\n");
        }));
    }
}

class SocketWrapper implements Runnable {

    private final Socket socket;

    SocketWrapper(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        
    }
}
