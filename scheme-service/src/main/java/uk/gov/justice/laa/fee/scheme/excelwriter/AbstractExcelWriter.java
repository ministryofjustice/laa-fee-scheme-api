package uk.gov.justice.laa.fee.scheme.excelwriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import uk.gov.justice.laa.fee.scheme.mapstruct.SubmissionDataDto;

public abstract class AbstractExcelWriter {

  abstract String[] getColumnOrder();

  abstract String getFileName();

  public void writeExcel(List<SubmissionDataDto> dtoList)  {
    if (dtoList == null || dtoList.isEmpty()) return;

    try (Workbook workbook = new XSSFWorkbook()) {
      Sheet sheet = workbook.createSheet();
      // Create header row
      createHeaderRow(sheet);
      // Iterate and populate row data
      addRowData(dtoList, sheet);
      // autosize columns
      for (int i = 0; i < getColumnOrder().length; i++) {
        sheet.autoSizeColumn(i);
      }
      // Write Workbook Content to File
      writeWorkBookToFile(getFileName(), workbook);

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void createHeaderRow(Sheet sheet) {
    Row headerRow = sheet.createRow(0);
    String[] columns = getColumnOrder();

    for (int i = 0; i < getColumnOrder().length; i++) {
      Cell cell = headerRow.createCell(i);
      cell.setCellValue(formatHeader(columns[i]));
    }
  }

  private String formatHeader(String columnName) {
    return StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(columnName), ' ');
  }

  private void addRowData(List<SubmissionDataDto> dtoList, Sheet sheet) {
    int rowNum = 1;
    String[] columns = getColumnOrder();
    for (SubmissionDataDto dto : dtoList) {
      Row row = sheet.createRow(rowNum++);
      for (int i = 0; i < columns.length; i++) {
        Object value = getFieldValue(dto, columns[i]);
        Cell cell = row.createCell(i);

        if (value instanceof Number) {
          cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
          cell.setCellValue((Boolean) value);
        } else {
          cell.setCellValue(value != null ? value.toString() : "");
        }
      }
    }
  }

  private Object getFieldValue(SubmissionDataDto dto, String fieldName) {
    try {
      Field field = SubmissionDataDto.class.getDeclaredField(fieldName);
      field.setAccessible(true);
      return field.get(dto);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      return null;
    }
  }

  private void writeWorkBookToFile(String filePath, Workbook workbook) {
    try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
      workbook.write(outputStream);
      workbook.close();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}


