package uk.gov.justice.laa.fee.scheme.mapstruct;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class convertToExcelCrimeLower {

  private static final String[] COLUMN_ORDER = {
      "caseReference",
      "clientForename",
      "clientSurname",
      "crimeLowerSpecific",
      "feeCode",
      "schemeId",
      "claimId",
      "caseEscaped",
      "validationMessages",
      "totalAmount",
      "vatOnClaim",
      "vatRateApplied",
      "vatAmount",
      "boltOnTotalFeeAmount",
      "boltOnHomeOfficeInterviewFee",
      "boltOnAdjournedHearingFee",
      "boltOnCmrhTelephoneFee",
      "boltOnCmrhOralFee",
      "disbursementAmount",
      "disbursementVatAmount",
      "hourlyTotal",
      "fixedFee",
      "profitCosts",
      "costOfCounsel",
      "travelCosts",
      "waitingCosts",
      "detentionAndWaitingCosts",
      "jrFormFilling",
      "travelAndWaitingCosts"
  };

  public static void writeExcelCrimeLower(String filePath, List<SubmissionDataDto> dtoList) throws IOException {
    if (dtoList == null || dtoList.isEmpty()) return;

    try (Workbook workbook = new XSSFWorkbook()) {
      Sheet sheet = workbook.createSheet("Sheet1");

      // Header row
      Row headerRow = sheet.createRow(0);
      for (int i = 0; i < COLUMN_ORDER.length; i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(formatHeader(COLUMN_ORDER[i]));
      }

      // Data rows
      int rowNum = 1;
      for (SubmissionDataDto dto : dtoList) {
        Row row = sheet.createRow(rowNum++);
        for (int i = 0; i < COLUMN_ORDER.length; i++) {
          Object value = getFieldValue(dto, COLUMN_ORDER[i]);
          Cell cell = row.createCell(i);

          if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
          } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
          } else {
            cell.setCellValue(value != null ? value.toString() : ""); // Empty string for missing values
          }
        }
      }

      // Autosize columns
      for (int i = 0; i < COLUMN_ORDER.length; i++) {
        sheet.autoSizeColumn(i);
      }

      try (FileOutputStream fos = new FileOutputStream(filePath)) {
        workbook.write(fos);
      }

    }
  }

  private static Object getFieldValue(SubmissionDataDto dto, String fieldName) {
    try {
      Field field = SubmissionDataDto.class.getDeclaredField(fieldName);
      field.setAccessible(true);
      return field.get(dto);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      return null; // Return null if field doesn't exist in this DTO
    }
  }

  private static String formatHeader(String fieldName) {
    StringBuilder sb = new StringBuilder();
    for (char c : fieldName.toCharArray()) {
      if (Character.isUpperCase(c)) sb.append(' ');
      sb.append(c);
    }
    return sb.substring(0, 1).toUpperCase() + sb.substring(1);
  }
}