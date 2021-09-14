package nonblocking;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NonblockingServer {
    public static void main(String[] args) throws IOException {
        InetAddress address = InetAddress.getByName("localhost");
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(address, 1234));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        SelectionKey selectionKey = null;

        while (true) {
            if (selector.select() <= 0) {
                continue;
            }

            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                selectionKey = iterator.next();
                iterator.remove();

                if (selectionKey.isAcceptable()) {
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    System.out.println("Connection Accepted: " + socketChannel.getLocalAddress() + "\n");
                }

                if (selectionKey.isReadable()) {
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    socketChannel.read(byteBuffer);
                    String result = new String(byteBuffer.array()).trim();
                    System.out.println("Message received: " + result + "\nMessage length= " + result.length());
                    if (result.length() <= 0) {
                        socketChannel.close();
                        System.out.println("Connection closed....");
                        System.out.println("Server is running, new connection can be established");
                    }
                }
            }
        }
    }
}
