package com.analyzer.wfmarket.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileService {

    public static List<String> readFile(String path) {
        try {
            // Read all lines from the file at the given path
            return Files.readAllLines(Paths.get(path));
        } catch (IOException e) {
            // Handle exceptions, e.g., file not found, access denied
            e.printStackTrace();
            return null;
        }
    }

    public static void writeFile(String path, String content) {
        try {
            // Write all lines to the file at the given path
            Files.write(Paths.get(path), content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            // Handle exceptions, e.g., file not found, access denied
            e.printStackTrace();
        }
    }
}
