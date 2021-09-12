package io;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileCopier {

    public static void main(String[] args) throws IOException {
        if (Files.notExists(Path.of("files"))) {
            throw new RuntimeException("Directory 'files' not found");
        }

        Files.list(Path.of("files")).forEach(path -> {
            if (!path.toFile().getName().equals("test_binary_file.bin")) {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        try (InputStream inputStream = new FileInputStream(Path.of("files", "test_binary_file.bin").toFile());
             OutputStream outputStream = new FileOutputStream(Path.of("files", "test_binary_file.bin.cp").toFile())) {

            byte[] buffer = new byte[4096];

            var read = inputStream.read(buffer);

            while (read != -1) {
                outputStream.write(buffer, 0, read);
                read = inputStream.read(buffer);
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Files.copy(Path.of("files", "test_binary_file.bin"), Path.of("files", "test_binary_file.bin.cp1"));
    }

}
