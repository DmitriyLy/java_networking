package console_chat.client;

import console_chat.common.Constants;
import console_chat.common.Utils;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {

    private final AtomicBoolean isStopped = new AtomicBoolean(false);
    private Socket socket;

    public static void main(String[] args) throws InterruptedException {
        Client client = new Client();
    }

    public Client() {

        establishConnection();
        Thread inputMessageListener = initInputMessageListener();

        Thread cliListener = getCliListener();
        cliListener.start();

        while (!isStopped.get()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        inputMessageListener.stop();

        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        cliListener.stop();
    }

    private Thread getCliListener() {
        Runnable runnable = () -> {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            try {
                String line = bufferedReader.readLine();
                while (line != null && !isStopped.get()) {
                    if (Constants.EXIT_COMMAND.equals(line)) {
                        isStopped.set(true);
                        break;
                    } else {
                        sendToServer(line);
                    }
                    line = bufferedReader.readLine();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        return new Thread(runnable, "cli listener");
    }

    private void establishConnection() {
        try {
            socket = new Socket("127.0.0.1", Utils.getPortNumber());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendToServer(String message) {

    }

    private Thread initInputMessageListener() {
        Thread thread = new Thread(() -> {
            try(InputStream inputStream = socket.getInputStream()) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String message = bufferedReader.readLine();

                while (message != null) {
                    System.out.println(message);
                    message = bufferedReader.readLine();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, "input message listener");

        thread.start();

        return thread;
    }
}
