package XLSX;

import Models.News;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.xssf.usermodel.*;

import java.util.List;

public class XLSXGenerator {

    private final List<News> _data;

    public XLSXGenerator(List<News> data) {
        _data = data;
    }

    public XSSFWorkbook getDocument() {
        XSSFWorkbook myExcelBook = new XSSFWorkbook();
        XSSFSheet sheet = myExcelBook.createSheet("Лист 1"); // создание EXCEL таблицы

        XSSFHyperlink url_link0;
        XSSFHyperlink url_link1;

        for (int i = 0; i < _data.size(); ++i) {
            News temp = _data.get(i);

            url_link0 = myExcelBook.getCreationHelper().createHyperlink(HyperlinkType.URL);
            url_link0.setAddress(temp.getUrl_company());

            url_link1 = myExcelBook.getCreationHelper().createHyperlink(HyperlinkType.URL);
            url_link1.setAddress(temp.getUrl_article());

            XSSFRow row = sheet.createRow(i);
            XSSFCell cell = row.createCell(0);
            cell.setCellValue(temp.getDate());

            cell = row.createCell(1);
            cell.setCellValue(temp.getCompany());
            cell.setHyperlink(url_link0);

            cell = row.createCell(2);
            cell.setCellValue(temp.getArticle());
            cell.setHyperlink(url_link1);

            XSSFCellStyle style = myExcelBook.createCellStyle();
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            int tempRating = temp.getRating();

            if (tempRating >= 100) {
                style.setFillForegroundColor(new XSSFColor(new java.awt.Color(153, 187, 150),
                        new DefaultIndexedColorMap()));
            }
            if (tempRating >= 50 && tempRating < 100) {
                style.setFillForegroundColor(new XSSFColor(new java.awt.Color(213, 133, 18, 255),
                        new DefaultIndexedColorMap()));
            }
            if (tempRating < 50) {
                style.setFillForegroundColor(new XSSFColor(new java.awt.Color(169, 68, 66),
                        new DefaultIndexedColorMap()));
            }
            cell.setCellStyle(style);
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        return myExcelBook;
    } // Создание разметки EXCEL документа
}
