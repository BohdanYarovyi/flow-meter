package com.yarovyi.flowmeter.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FileReaderServiceImpl implements FileReaderService {

    @Override
    public String readFile(Path path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("Path is required");
        }

        String content = "";
        try {
            content = Files.readString(path);
        } catch (IOException e) {
            throw new IOException("Failed to read file: " + path, e);
        }

        return content;
    }

}
