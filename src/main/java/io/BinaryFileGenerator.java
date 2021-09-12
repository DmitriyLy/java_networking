package io;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

public class BinaryFileGenerator {

    public static void main(String[] args) throws IOException {

        if (Files.notExists(Path.of("files"))) {
            Files.createDirectory(Path.of("files"));
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        FileOutputStream fileOutputStream = new FileOutputStream(Path.of("files", "test_binary_file.bin").toFile());

        Stream.iterate(0, i -> i + 1)
                .limit(100_000_000)
                .forEach(i -> {
                    try {
                        dataOutputStream.writeInt(random.nextInt());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });



        fileOutputStream.write(byteArrayOutputStream.toByteArray());

        fileOutputStream.close();
        dataOutputStream.close();
        byteArrayOutputStream.close();

    }

}
