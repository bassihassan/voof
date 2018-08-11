package me.bassihassan.util;

import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public interface Utils {
    static InputStream uriToInputStream(String uri) throws IOException {
        return Files.newInputStream(Paths.get(uri));
    }

    static String getExtention(String uri) {
        String extension = FilenameUtils.getExtension(uri);
        if (extension.isEmpty()) throw new RuntimeException();
        return extension;
    }
}
