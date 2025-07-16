package com.yarovyi.flowmeter.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class FileReaderServiceImplTest {
    private FileReaderServiceImpl fileReaderService;
    private Path tempFile;

    @BeforeEach
    void setUp() throws IOException {
        fileReaderService = new FileReaderServiceImpl();
        tempFile = createTemporalFile();
    }

    @AfterEach
    void tearDown() throws IOException {
        deleteTemporalFile(tempFile);
    }

    
/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    readFile(Path path)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
    @Test
    void readFile_whenIsOk_returnString() throws IOException {
        // given
        Path filePath = tempFile;

        // when
        String content = fileReaderService.readFile(filePath);

        // then
        assertNotNull(content);
        assertFalse(content.isEmpty());
    }

    @Test
    void readFile_whenPathNotCorrect_throwException() throws IOException {
        // given
        Path filePath = tempFile.resolve("doPathNotCorrect");

        // when
        assertThrows(IOException.class, () -> fileReaderService.readFile(filePath));
    }

    @Test
    void readFile_whenParameterPathIsNull_throwException() {
        // given
        Path filePath = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> fileReaderService.readFile(filePath));
    }

    Path createTemporalFile() throws IOException {
        String fileName = "file";
        String extension = ".html";
        String content = """
                    <p>Hello,</p>
                    <p>Thank you for registering in <strong>Flowmeter</strong>!</p>
                """;

        Path tempFile = Files.createTempFile(fileName, extension);
        Files.writeString(tempFile, content);

        log.info("File was created: {}", tempFile);
        return tempFile;
    }

    private void deleteTemporalFile(Path tempFile) throws IOException {
        boolean wasDeleted = Files.deleteIfExists(tempFile);

        if (wasDeleted) {
            log.info("File was deleted: {} ", tempFile);
        }
    }

}