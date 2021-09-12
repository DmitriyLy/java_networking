package nio;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class TextWriter {
    public static void main(String[] args) throws IOException {
        if (Files.notExists(Path.of("files"))) {
            Files.createDirectory(Path.of("files"));
        }

        RandomAccessFile randomAccessFile = new RandomAccessFile(Path.of("files", "some_txt.txt").toFile(), "rw");

        FileChannel channel = randomAccessFile.getChannel();

        byte[] value = LocalDateTime.now().toString().getBytes(StandardCharsets.UTF_8);
        ByteBuffer byteBuffer = ByteBuffer.allocate(value.length);
        byteBuffer.put(value);
        byteBuffer.flip();
        channel.write(byteBuffer);
        randomAccessFile.close();
        channel.close();

        InputStream bufferedReader = Files.newInputStream(Path.of("files", "some_txt.txt"));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[4096];

        var read = bufferedReader.read(buffer);

        while (read != -1) {
            byteArrayOutputStream.write(buffer, 0, read);
            read = bufferedReader.read(buffer);
        }

        bufferedReader.close();
        System.out.println(new String(byteArrayOutputStream.toByteArray()));

    }
}
