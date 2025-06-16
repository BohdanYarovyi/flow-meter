package com.yarovyi.flowmeter.service;

import java.io.IOException;
import java.nio.file.Path;

public interface FileReaderService {

    String readFile(Path path) throws IOException;

}
