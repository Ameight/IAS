package Util;

import java.io.*;

public class LocaleDataOpen {

    private final String _path;

    private Object _data = null;

    private Object t;

    public LocaleDataOpen(String path) {
        _path = path;
        _open();
    }

    public Object getData() {
        return _data;
    }

    private boolean _check() {
        File file = new File(_path);
        if (!file.exists()) {
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

    private void _open() {
        if (!_check()) {
            return;
        }
        FileInputStream fis;
        try {
            fis = new FileInputStream(_path);
            if(fis.getChannel().size() == 0){
                fis.close();
                return;
            }
            ObjectInputStream ois = new ObjectInputStream(fis);
            t = ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        _data = t;
    } // Открытие и чтение файла

}
