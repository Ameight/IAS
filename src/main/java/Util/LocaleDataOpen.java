package Util;

import java.io.*;

public class LocaleDataOpen {

    private String _dir;

    private Object _data = null;

    private Object t;

    public LocaleDataOpen(String name){
        _dir = name;
        _open();
    }

    public Object getData(){
        return _data;
    }

    private boolean _check(){
        File file = new File(_dir);
        if(!file.exists()){
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    } // проверка существования файла

    private void _open(){
        if(!_check()) {
            return;
        }
        FileInputStream fis;
        try {
            fis = new FileInputStream(_dir);
            ObjectInputStream ois = new ObjectInputStream(fis);
            t =  ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        _data = t;
    } // Открытие и чтение файла

}
