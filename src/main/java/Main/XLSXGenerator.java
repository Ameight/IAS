package Main;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.xssf.usermodel.*;

import java.util.List;

public class XLSXGenerator {

    private List<News> _data;

    public XLSXGenerator(List<News> data){
        _data = data;
    }

    public XSSFWorkbook getDocument() {
        XSSFWorkbook myExcelBook = new XSSFWorkbook();
        XSSFSheet sheet = myExcelBook.createSheet("Лист 1"); // создание EXCEL таблицы

        XSSFHyperlink url_link0;
        XSSFHyperlink url_link1;

        for (int i = 0; i < _data.size(); ++i) {
            url_link0 = myExcelBook.getCreationHelper().createHyperlink(HyperlinkType.URL);
            url_link1 = myExcelBook.getCreationHelper().createHyperlink(HyperlinkType.URL);
            url_link0.setAddress(_data.get(i).getUrl_company());
            url_link1.setAddress(_data.get(i).getUrl_article());

            XSSFRow row = sheet.createRow(i);
            XSSFCell cell = row.createCell(0);
            cell.setCellValue(_data.get(i).getDate());

            cell = row.createCell(1);
            cell.setCellValue(_data.get(i).getCompany());
            cell.setHyperlink(url_link0);

            cell = row.createCell(2);
            cell.setCellValue(_data.get(i).getArticle());

            cell.setHyperlink(url_link1);
            //Thread.sleep(100);
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        return myExcelBook;

    } // Создание разметки EXCEL документа
}
