package com.analyzer.wfmarket.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileReaderService {

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

    public static void main(String[] args) {
        // Test the readFile method
        List<String> lines = FileReaderService.readFile("frames.txt");
        List<String> frames = lines.stream().map(frame -> frame.toLowerCase()+"_prime_").toList();
        if (frames != null) {
            for (String frame : frames) {
                System.out.println(frame);
            }
        }
    }
}
