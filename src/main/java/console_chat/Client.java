package console_chat;

import java.io.IOException;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws InterruptedException {

        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                try {
                    Socket socket = new Socket("127.0.0.1", Utils.getPortNumber());
                    Thread.sleep(10000);
                    socket.close();
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }

    }
}
