package imoveis.excel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import imoveis.utils.ResourceManager;

public class Excel {

    public static void main(String[] args) {
        String fileName = "imoveis.xlsx";
        try {
            InputStream in = ResourceManager.getInstance().getInputStream(fileName);
            try (Workbook workbook = new XSSFWorkbook(in);) {
                Sheet sheet = workbook.getSheetAt(0);
                Row row = sheet.getRow(sheet.getFirstRowNum());
                System.out.println(row.getCell(0).getStringCellValue());
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
