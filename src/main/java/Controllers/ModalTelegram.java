package Controllers;

import DataBase.DataBase;
import Util.LocaleDataSave;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class ModalTelegram {

    final String pathOfId = "tmp/nameTelegram.txt";

    @FXML
    public VBox root;

    @FXML
    TextField idTelegram;

    @FXML
    public void initialize() {
    }

    @FXML
    public void syncTelegram(ActionEvent ev) {
        if (ev.getEventType() == ActionEvent.ACTION) {
            String tmpId = idTelegram.getText();
            LocaleDataSave saveId = new LocaleDataSave(pathOfId, tmpId);
            saveId.save();

            ((Node) ev.getSource()).getScene().setUserData(tmpId);

            DataBase dataBase = new DataBase();
            try {
                if (dataBase.connection == null) {
                    return;
                }
                PreparedStatement stm = dataBase.connection.prepareStatement("INSERT INTO `inputcashe` VALUES (?, ?, ?)");
                stm.setString(1, tmpId);
                stm.setBlob(2, (Blob) null);
                stm.setBlob(3, (Blob) null);
                stm.execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    } // Сохраняет имя и делает запись в базе данных

}
