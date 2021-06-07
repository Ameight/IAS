package Util;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class LocaleDataSave {

    String _dir;

    Object _data;

    public LocaleDataSave(String name, Object data){
        _dir = name;
        _data = data;
    }

    private void _prepareDir(){
        File file = new File(_dir);
        if(!file.exists()){
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            StandardOpenOption[] options = {StandardOpenOption.TRUNCATE_EXISTING};
            try {
                Files.newBufferedWriter(Paths.get(file.getPath()), options);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    } // Создание директорий и проверка на дурака

    public void saveExcel(){
        XSSFWorkbook book = (XSSFWorkbook) _data;
        _prepareDir();
        FileOutputStream out;
        try {
            out = new FileOutputStream(_dir);
            book.write(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    } // получение  разметки и сохрание

    public void save(){
        _prepareDir();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(_dir);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(_data);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    } // метод для сохранения
}
