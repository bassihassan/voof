package me.bassihassan.core;

import me.bassihassan.annotation.RowCell;
import me.bassihassan.exception.VoofException;
import me.bassihassan.option.Options;
import me.bassihassan.annotation.RowIndex;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.apache.commons.collections4.PredicateUtils.notNullPredicate;

public class Unmarshaller {
    private Logger logger = Logger.getLogger(Unmarshaller.class);
    private Workbook workbook;
    private Options options;
    private Map<String, Integer> titles = new HashMap<>();
    private DataFormatter dataFormatter = new DataFormatter();


    public Unmarshaller(Workbook workbook, Options options) {
        this.workbook = workbook;
        this.options = options;
    }

    public <T> void unmarshaller(Class<T> type, Callback<T> callback) {
        StreamSupport.stream(rows(), false).forEach((currentRow) -> {
            if (!skipRow(currentRow, options.getRowOffset()) && !isRowEmpty(currentRow)) {
                Tuple2<T, List<CellError>> tuple = deserialize(currentRow, type);
                T t = tuple._1;
                List<CellError> errors = tuple._2;
                CollectionUtils.filter(errors, notNullPredicate());
                callback.apply(t, errors);
            }
        });
    }

    public <T> List<T> unmarshaller(Class<T> type) {
        return StreamSupport.stream(rows(), false)
                .filter((row) -> !skipRow(row, options.getRowOffset()) && !isRowEmpty(row))
                .map((currentRow) -> deserialize(currentRow, type)._1)
                .collect(Collectors.toList());
    }

    private Spliterator<Row> rows() {
        Sheet sheet = workbook.getSheetAt(options.getSheetIndex());

        int rowOffset = options.getRowOffset();
        int maxPhysicalNumberOfRows = sheet.getPhysicalNumberOfRows() + 1 - rowOffset;

        loadColumnTitles(sheet, maxPhysicalNumberOfRows);
        return sheet.spliterator();
    }

    private void loadColumnTitles(Sheet sheet, int maxPhysicalNumberOfRows) {
        if (maxPhysicalNumberOfRows > 0) {
            Row firstRow = sheet.getRow(0);
            for (Cell cell : firstRow) {
                titles.put(cell.getStringCellValue(), cell.getColumnIndex());
            }
        }
    }

    private <T> Tuple2<T, List<CellError>> deserialize(Row currentRow, Class<T> type) {
        T instance;
        List<CellError> cellErrors = new ArrayList<>();
        CollectionUtils.addIgnoreNull(cellErrors, null);
        try {
            instance = type.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            throw new VoofException("Cannot create a new instance of " + type.getName());
        }

        return Tuple.of(mapCellValuesToFields(currentRow, type, instance, cellErrors), cellErrors);
    }

    private <T> T tailmapCellValuesToFields(Row currentRow, Class<? super T> type, T instance, List<CellError> cellErrors) {
        for (Field field : type.getDeclaredFields()) {
            RowIndex excelRow = field.getAnnotation(RowIndex.class);
            if (Objects.nonNull(excelRow)) {
                setFieldData(instance, field, currentRow.getRowNum());
            } else {
                RowCell index = field.getAnnotation(RowCell.class);
                String columnName = index.label();
                Integer columnIndex = index.index();
                if (columnIndex != -1) {
                    cellErrors.add(setCellToField(currentRow, instance, field, columnIndex));
                } else {
                    if (columnName != null && !columnName.isEmpty()) {
                        Integer colIndex = titles.get(columnName);
                        if (colIndex != null) {
                            cellErrors.add(setCellToField(currentRow, instance, field, colIndex));
                        }
                    }
                }
            }

        }
        return instance;
    }


    private <T> CellError setCellToField(Row currentRow, T instance, Field field, int column) {
        Cell cell = currentRow.getCell(column);
        Object convertValue = new Object();
        Object value = new Object();
        if (cell != null) {
            value = dataFormatter.formatCellValue(cell);
            field.setAccessible(true);
            convertValue = ConvertUtils.convert(value, field.getType());
            try {
                field.set(instance, convertValue);
            } catch (IllegalAccessException e) {
                throw new VoofException(e);
            }
        }


        return !String.valueOf(convertValue).equals(value) ? new CellError(String.valueOf(column), String.valueOf(value), "Value should be a ".concat(field.getType().getSimpleName())) : null;
    }

    private <T> void setFieldData(T instance, Field field, Object o) {
        field.setAccessible(true);
        try {
            field.set(instance, o);
        } catch (IllegalAccessException e) {
            throw new VoofException(e);
        }
    }

    private <T> T mapCellValuesToFields(Row currentRow, Class<? super T> subclass, T instance, List<CellError> cellErrors) {
        return subclass == null
                ? instance
                : tailmapCellValuesToFields(currentRow, subclass, mapCellValuesToFields(currentRow, subclass.getSuperclass(), instance, cellErrors), cellErrors);
    }

    private boolean skipRow(final Row currentRow, int rowOffset) {
        return currentRow.getRowNum() + 1 <= rowOffset;
    }

    private boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            if (cell != null && cell.getCellTypeEnum() != CellType.BLANK)
                return false;
        }
        return true;
    }
}
