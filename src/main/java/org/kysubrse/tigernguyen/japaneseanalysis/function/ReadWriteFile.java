package org.kysubrse.tigernguyen.japaneseanalysis.function;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.kysubrse.tigernguyen.japaneseanalysis.data.OutputData;

import javafx.scene.control.TableView;

import javax.swing.JTable;
import javax.swing.table.TableModel;

public class ReadWriteFile {
	public ReadWriteFile() {

	}

	public static List<String> getDictionaryDataList(String FileName) {
		List<String> listData = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(FileName), "UTF8"));) {

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				listData.add(sCurrentLine);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return listData;
	}

	public static String getDataTextFile(String FileName) throws FileNotFoundException {
		String strData = "";
		String encode = "SJIS";
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(FileName), encode));) {
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				strData += sCurrentLine;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return strData;
	}

	public static void writeToExcell(JTable table, String path) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("JapaneseAnalysis");

		XSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle = workbook.createCellStyle();
		XSSFFont xSSFFont = workbook.createFont();
		xSSFFont.setFontName(HSSFFont.FONT_ARIAL);
		cellStyle.setFont(xSSFFont);

		int rowNum = 0;
		System.out.println("Creating excel");

		TableModel model = table.getModel();
		for (int i = 0; i <= model.getRowCount(); i++) {
			Row row = sheet.createRow(rowNum++);
			int colNum = 0;
			for (int j = 0; j < model.getColumnCount(); j++) {
				Cell cell = row.createCell(colNum++);
				cell.setCellStyle(cellStyle);
				if (i == 0) {
					cell.setCellValue(table.getColumnName(j) == null ? "" : table.getColumnName(j));
				} else {
					cell.setCellValue(model.getValueAt(i - 1, j) == null ? "" : model.getValueAt(i - 1, j).toString());
				}
			}
		}

		try {
			FileOutputStream outputStream = new FileOutputStream(path);
			workbook.write(outputStream);
			workbook.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Done");
	}

	public static void writeToExcell(TableView<OutputData> tbvOutputData, String filePath) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("JapaneseAnalysis");

		XSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle = workbook.createCellStyle();
		XSSFFont xSSFFont = workbook.createFont();
		xSSFFont.setFontName(HSSFFont.FONT_ARIAL);
		cellStyle.setFont(xSSFFont);

		int rowNum = 0;
		System.out.println("Creating excel");

		// TableModel model = tbvOutputData.getd();
		for (int i = 0; i <= tbvOutputData.getItems().size(); i++) {
			Row row = sheet.createRow(rowNum++);
			int colNum = 0;
			for (int j = 0; j < tbvOutputData.getColumns().size(); j++) {
				Cell cell = row.createCell(colNum++);
				cell.setCellStyle(cellStyle);
				if (i == 0) {
					cell.setCellValue(tbvOutputData.getColumns().get(j).getText());
				} else {
					if (tbvOutputData.getColumns().get(j) != null
							&& tbvOutputData.getColumns().get(j).getCellData(i - 1) != null) {
						cell.setCellValue(tbvOutputData.getColumns().get(j).getCellData(i - 1).toString());
					} else {
						cell.setCellValue("");
					}
				}
			}
		}

		try {
			FileOutputStream outputStream = new FileOutputStream(filePath);
			workbook.write(outputStream);
			workbook.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Done");

	}
}