package console_chat.server;

import java.io.*;
import java.net.Socket;

public class ClientConnection implements Runnable {
    private final Socket socket;
    private final Long number;

    public ClientConnection(Socket socket, Long number) {
        this.socket = socket;
        this.number = number;
    }

    @Override
    public void run() {

        try (InputStream inputStream = socket.getInputStream();) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String message = bufferedReader.readLine();

            while (message != null) {

            }

            closeConnection();

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
