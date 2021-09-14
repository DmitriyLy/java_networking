package nonblocking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class NonblockingClient {
    private static BufferedReader input = null;

    public static void main(String[] args) throws IOException {
        InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("localhost"), 1234);
        Selector selector = Selector.open();

        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(socketAddress);
        socketChannel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE);

        input = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            if (selector.select() > 0) {
                if (processReadySet(selector.selectedKeys())) {
                    break;
                }
            }
        }

    }

    static boolean processReadySet(Set<SelectionKey> selectionKeys) throws IOException {
        SelectionKey selectionKey = null;

        Iterator<SelectionKey> iterator = selectionKeys.iterator();
        while (iterator.hasNext()) {
            selectionKey = iterator.next();
            iterator.remove();
        }

        if (selectionKey.isConnectable()) {
            if (!processConnect(selectionKey)) {
                return true;
            }
        }

        if (selectionKey.isReadable()) {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            socketChannel.read(byteBuffer);

            String result = new String(byteBuffer.array()).trim();
            System.out.println("Message received from Server: " + result + "Message length= " + result.length());
        }

        if (selectionKey.isWritable()) {
            System.out.println("Type a message (type quit to stop): ");
            String msg = input.readLine();
            if (msg.equalsIgnoreCase("quit")) {
                return true;
            }

            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            ByteBuffer byteBuffer = ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8));
            socketChannel.write(byteBuffer);
        }

        return false;
    }

    static boolean processConnect(SelectionKey selectionKey) {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

        while (socketChannel.isConnectionPending()) {
            try {
                socketChannel.finishConnect();
            } catch (IOException e) {
                selectionKey.cancel();
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}
