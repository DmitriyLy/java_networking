package console_chat.server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientConnection implements Runnable {
    private final Socket socket;
    private final Long number;
    private final Server server;

    public ClientConnection(Socket socket, Long number, Server server) {
        this.socket = socket;
        this.number = number;
        this.server = server;
    }

    @Override
    public void run() {

        try (InputStream inputStream = socket.getInputStream();) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String message = bufferedReader.readLine();

            while (message != null) {
                server.broadcastMessage(message, number);
                message = bufferedReader.readLine();
            }

            closeConnection();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String message) {
        try {
            if (!socket.isClosed()) {
                if (!message.endsWith("\n")) {
                    message = message + "\n";
                }
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(message.getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isClosed() {
        return socket.isClosed();
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    public Long getNumber() {
        return number;
    }

    public void closeConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
