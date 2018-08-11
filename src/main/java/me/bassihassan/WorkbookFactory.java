package me.bassihassan;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

public class WorkbookFactory {
    private static HashMap<String, Function<InputStream, Workbook>> workBooks = new HashMap<>();

    static {
        workBooks.put("xls", (inputStream) -> {
            try {
                return new HSSFWorkbook(inputStream);
            } catch (IOException e) {
               throw new RuntimeException();
            }

        });
        workBooks.put("xlsx", (inputStream) -> {
            try {
                return new XSSFWorkbook(inputStream);
            } catch (IOException e) {
                throw new RuntimeException();
            }
        });
    }

    public static Workbook constructWorkbook(final String extension, InputStream stream) {
        Function<InputStream, Workbook> Func = ofNullable(workBooks.get(extension)).orElseThrow(RuntimeException::new);
        Workbook workbook = Func.apply(stream);
        return workbook;
    }
}
