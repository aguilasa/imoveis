package properties.excel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import properties.base.IProperty;
import properties.utils.ResourceManager;

public class Excel {

	private static Excel instance;

	private List<IProperty> imoveis = new LinkedList<>();

	private Excel() {

	}

	public static Excel getInstance() {
		if (instance == null) {
			instance = new Excel();
		}
		return instance;
	}

	public void clear() {
		imoveis.clear();
	}

	public synchronized void addImovel(IProperty imovel) {
		imoveis.add(imovel);
	}

	public synchronized void addTodosImovel(List<IProperty> imovel) {
		imoveis.addAll(imovel);
	}

	public void gerar() {
		String fileName = "imoveis.xlsx";
		try {
			InputStream in = ResourceManager.getInstance().getInputStream(fileName);
			try (Workbook workbook = new XSSFWorkbook(in);) {
				Sheet sheet = workbook.getSheetAt(0);
				processarImoveis(workbook, sheet);
				FileOutputStream outputStream = new FileOutputStream("imoveis2.xlsx");
				workbook.write(outputStream);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Comment createComment(Workbook workbook, Sheet sheet, String texto) {
		CreationHelper creationHelper = workbook.getCreationHelper();
		Drawing drawing = sheet.createDrawingPatriarch();
		ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, 5, 10, 15);
		Comment comment = drawing.createCellComment(anchor);
		RichTextString rtf1 = creationHelper.createRichTextString(texto);
		comment.setString(rtf1);
		return comment;
	}

	private void processarImoveis(Workbook workbook, Sheet sheet) {
		CreationHelper creationHelper = workbook.getCreationHelper();
		CellStyle style = workbook.createCellStyle();
		style.setDataFormat((short) 0x7);
		int linha = 1;
		for (IProperty imovel : imoveis) {
			Row row = sheet.getRow(linha);
			if (row == null) {
				row = sheet.createRow(linha);
			}
			Cell cell = row.createCell(0);
			cell.setCellValue(imovel.getName());
			cell = row.createCell(1);
			cell.setCellValue(imovel.getDistrict());
			cell = row.createCell(2);
			cell.setCellStyle(style);
			cell.setCellValue(imovel.getPrice());
			if (imovel.getPrice() == 0) {
				Comment comment = createComment(workbook, sheet, String.format("Preço: %s", imovel.getPriceStr()));
				cell.setCellComment(comment);
			}
			cell = row.createCell(3);
			cell.setCellValue(imovel.getCondominium());
			cell = row.createCell(4);
			cell.setCellValue(imovel.getRooms());
			cell = row.createCell(5);
			cell.setCellValue(imovel.getParkingSpaces());
			cell = row.createCell(6);
			cell.setCellValue(imovel.getArea());
			cell = row.createCell(7);
			cell.setCellValue(imovel.getSuites());
			cell = row.createCell(8);
			cell.setCellValue(imovel.getAdvertiser());
			cell = row.createCell(9);
			cell.setCellValue("Abrir");
			Hyperlink createHyperlink = creationHelper.createHyperlink(HyperlinkType.URL);
			createHyperlink.setAddress(imovel.getUrl());
			cell.setHyperlink(createHyperlink);
			cell = row.createCell(10);
			cell.setCellValue(imovel.getAddress());
			cell = row.createCell(11);
			cell.setCellValue(imovel.getPropertyType().toString());
			linha++;

		}
	}

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
