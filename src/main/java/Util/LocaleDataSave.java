package Util;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class LocaleDataSave {

    String _path;

    Object _data;

    public LocaleDataSave(String path, Object data) {
        _path = path;
        _data = data;
    }

    private void _prepareDir() {
        File file = new File(_path);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            StandardOpenOption[] options = {StandardOpenOption.TRUNCATE_EXISTING};
            try {
                Files.newBufferedWriter(Paths.get(file.getPath()), options);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    } // Создание директорий и проверка на дурака

    public void saveExcel() {
        _prepareDir();

        XSSFWorkbook book = (XSSFWorkbook) _data;
        FileOutputStream out;
        try {
            out = new FileOutputStream(_path);
            book.write(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    } // получение  разметки и сохранение

    public void save() {
        _prepareDir();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(_path);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(_data);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    } // метод для сохранения
}
