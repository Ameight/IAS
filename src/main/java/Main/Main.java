package Main;

import Analysis.CheckAge;
import Controllers.AuthPageController;
import Controllers.AuthPageScene;
import Controllers.WorkController;
import Controllers.WorkScene;
import Util.LocaleDataOpen;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.io.*;
import java.util.Date;


public class Main extends Application {
    Stage _primaryStage; //������� ������� � javafx
// djn
    static String nameTelegram;

    public String getNameTelegram(){
        return nameTelegram;
    }

    public void setNameTelegram(String name){
        nameTelegram = name;
    }

    public static void main(String[] args){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // ������������ MYSQL ������� � ������ ������� ��� IDE
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }



        LocaleDataOpen load = new LocaleDataOpen("tmp/nameTelegram.txt"); // ��������� ���������� ������
        String loadedTelegram = (String) load.getData();
        nameTelegram = loadedTelegram;
        System.out.println("TelegramID: " + nameTelegram);

        launch(args); // ������ ������ start javaFx
    }

    private FXMLLoader _loadScene(String name){
        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource(name));
        return loader;
    }

    public void setStage(ScenesEnum scenesEnum) throws IOException {
        FXMLLoader loader;
        switch (scenesEnum){
            case MAIN:{
                //_primaryStage.getIcons().add(new Image("/icon.png"));
                loader = _loadScene("/Fxml/AuthPage.fxml");
                _primaryStage.setTitle("�������������� ������������� ������� ");
                VBox root = loader.load();
                AuthPageController controller = loader.getController();
                controller.attachMainClass(this);
                _primaryStage.setScene(new AuthPageScene(root, _primaryStage));
                _primaryStage.show();
                break;
            } // ������� �����
            case Work:{
                loader = _loadScene("/Fxml/Work.fxml");
                _primaryStage.setTitle("����� ��������");
                VBox root = loader.load();
                WorkController controller = loader.getController();
                controller.attachMainClass(this);
                _primaryStage.setScene(new WorkScene(root, _primaryStage));
                _primaryStage.show();
                break;
            } // ����� ������ ������������
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        _primaryStage = primaryStage;
        setStage(ScenesEnum.MAIN);

        _primaryStage.getIcons().add(new Image("/icon.png"));
    } // ������� start
}
