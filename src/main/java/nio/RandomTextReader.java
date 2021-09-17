package nio;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.stream.Stream;

public class RandomTextReader {
    private static final int LINE_LENGTH = 30;
    private static final int LINES_NUMBER = 40;
    private static final String INIT_FILE_NAME = "random_text_container.txt";

    public static void main(String[] args) throws IOException {
        if (Files.notExists(Path.of("files"))) {
            Files.createDirectory(Path.of("files"));
        }

        Files.deleteIfExists(Path.of("files", INIT_FILE_NAME));
        Files.deleteIfExists(Path.of("files", "copy_" + INIT_FILE_NAME));

        generateFileWithRandomText();
        copyFile();

    }

    static void copyFile() throws IOException {
        RandomAccessFile sourceFile = new RandomAccessFile(Path.of("files", INIT_FILE_NAME).toFile(), "r");
        RandomAccessFile destFile = new RandomAccessFile(Path.of("files", "copy_" + INIT_FILE_NAME).toFile(), "rw");

        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        FileChannel sourceChannel = sourceFile.getChannel();
        FileChannel destChannel = destFile.getChannel();

        int byteRead = sourceChannel.read(byteBuffer);

        while (byteRead != -1) {
            byteBuffer.flip();
            destChannel.write(byteBuffer);
            byteBuffer.clear();
            byteRead = sourceChannel.read(byteBuffer);
        }

        sourceChannel.close();
        destChannel.close();
        sourceFile.close();
        destFile.close();
    }

    static void generateFileWithRandomText() {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            Stream.iterate(1, i -> i + 1)
                    .limit(LINES_NUMBER)
                    .forEach(i -> {
                        byteArrayOutputStream.writeBytes(generateRandomString().getBytes(StandardCharsets.UTF_8));
                        byteArrayOutputStream.writeBytes(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
                    });

            Path filePath = Path.of("files", INIT_FILE_NAME);

            RandomAccessFile randomAccessFile = new RandomAccessFile(filePath.toFile(), "rw");
            FileChannel fileChannel = randomAccessFile.getChannel();

            ByteBuffer byteBuffer = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
            fileChannel.write(byteBuffer);

            byteBuffer.clear();
            fileChannel.close();
            randomAccessFile.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static String generateRandomString() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(LINE_LENGTH)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
