package Controllers;

import Main.Main;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

import Models.ScenesEnum;

public class AuthPageController {

    @FXML
    public Button startBut;

    @FXML
    GridPane mainPane;

    Main _parent;

    public void attachMainClass(Main main) {
        _parent = main;
    }

    @FXML
    private void startWork() {
        try {
            _parent.setStage(ScenesEnum.Work);
        } catch (IOException e) {
            e.printStackTrace();
        }
    } // обработка кнопки "начать работу"

    @FXML
    public void syncWithTelegram(ActionEvent event) {
        Stage stage = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(
                    ModalTelegram.class.getResource("/Fxml/ModalTelegram.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene modalScene = new Scene(root);
        stage.setScene(modalScene);
        stage.setTitle("Синхронизировать с Telegram");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(
                ((Node) event.getSource()).getScene().getWindow());
        stage.getIcons().add(new Image("/iconsyn.png"));
        stage.show();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                _parent.setNameTelegram((String) modalScene.getUserData());
            }
        });
    } // функционал синхронизации с телегой
}
