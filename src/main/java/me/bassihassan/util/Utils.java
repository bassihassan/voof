package me.bassihassan.util;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
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

    static Object formatCellValue(Field field, Cell cell) {
        DataFormatter dataFormatter = new DataFormatter();
        if (field.getGenericType().getTypeName() == String.class.getTypeName())
            return dataFormatter.formatCellValue(cell);
        else {
            try {
                return cell.getNumericCellValue();
            } catch (RuntimeException e) {
                return dataFormatter.formatCellValue(cell);
            }
        }
    }
}
