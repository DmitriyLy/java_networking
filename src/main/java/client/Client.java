package client;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

import static console_chat.common.Constants.EXIT_COMMAND;

public class Client {
    private final AtomicBoolean isStopped = new AtomicBoolean(false);
    private final Queue<String> messageQueue = new LinkedList<>();

    public static void main(String[] args) {
        new Client();
    }

    public Client() {
        initConsoleReading();
        initConnection();
    }

    private void initConnection() {
        new Thread(() -> {
            try {
                SocketChannel socketChannel = SocketChannel.open();
                socketChannel.configureBlocking(false);
                socketChannel.connect(new InetSocketAddress("localhost", 9999));

                while (!socketChannel.finishConnect()) {
                    Thread.sleep(100);
                }

                while (!isStopped.get()) {
                    Thread.sleep(100);

                    synchronized (messageQueue) {
                        while (!messageQueue.isEmpty()) {
                            ByteBuffer byteBuffer = ByteBuffer.wrap(messageQueue.poll().getBytes(StandardCharsets.UTF_8));
                            socketChannel.write(byteBuffer);
                            //byteBuffer.clear();
                        }
                    }

                    ByteBuffer byteBuffer = ByteBuffer.allocate(48);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                    int bytesRead = socketChannel.read(byteBuffer);

                    while (bytesRead > 0) {
                        byteBuffer.flip();
                        byteArrayOutputStream.writeBytes(byteBuffer.array());
                        bytesRead = socketChannel.read(byteBuffer);
                    }

                    if (byteArrayOutputStream.size() > 0) {
                        System.out.println(byteArrayOutputStream.toString(StandardCharsets.UTF_8));
                    }

                    byteArrayOutputStream.close();

                }

                socketChannel.close();

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void initConsoleReading() {
        new Thread(() -> {
            while (!isStopped.get()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                try {
                    if (System.in.available() > 0) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                        String line = bufferedReader.readLine();
                        if (EXIT_COMMAND.equals(line)) {
                            isStopped.set(true);
                            break;
                        } else {
                            putMessageToQueue(line);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private synchronized void putMessageToQueue(String message) {
        messageQueue.add(message);
    }

    private synchronized String getMessageFromQueue() {
        return messageQueue.poll();
    }
}
