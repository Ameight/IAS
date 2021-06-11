package Controllers;

import Main.Main;
import Models.ArticleTable;
import Models.InputNames;
import Main.DataBase;
import Main.News;
import Main.Parser;
import Main.XLSXGenerator;
import Util.LocaleDataOpen;
import Util.LocaleDataSave;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.sql.rowset.serial.SerialBlob;
import java.io.*;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WorkController {

    Main _parent;
    InputNames names;

    DataBase dataBase;
    boolean usingDataBase = false;

    ArrayList<News> companyToLoadData = new ArrayList<>();
    ArrayList<News> parsedData = new ArrayList<>();

    private final String pathTempInputNames = "tmp/inputNames.txt"; // КЭШ истории
    private final String pathTempDocuments = "Docs/"; // путь к документам

    @FXML
    ComboBox<String> inputNameCompany;

    @FXML
    TableView<ArticleTable> tableArticles;

    @FXML
    Label stateProgram;

    @FXML
    TextField nameDocumentToSave;

    @FXML
    TextField filterText;

    private final ObservableList<ArticleTable> dataTable =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        dataBase = new DataBase();
        if (dataBase.getConnection() != null) {
            dataBase._defaultInit();
            usingDataBase = true;
        } // создание и инициализации БД
    }

    private boolean _checkBdProperties() {
        boolean telegramId = _parent.getNameTelegram() != null;
        return telegramId && usingDataBase;
    } //

    private void _initInputNames() {
        if (!_checkBdProperties()) {
            //System.out.println("Heere !! ! ");
            LocaleDataOpen localeDataOpen = new LocaleDataOpen(pathTempInputNames);
            Object tmp = localeDataOpen.getData();
            if (tmp != null) {
                names = (InputNames) tmp;
            } else {
                names = new InputNames();
            }
        } else {
            try {
                PreparedStatement selectInputNames = dataBase.connection.prepareStatement("SELECT dataInputName FROM inputcashe WHERE idUserTelegram = ?");
                selectInputNames.setString(1, _parent.getNameTelegram());
                ResultSet namesObject = selectInputNames.executeQuery();
                if (namesObject.next()) {
                    Blob temtBlobInputnames = namesObject.getBlob("dataInputName");
                    if (temtBlobInputnames != null) {
                        ObjectInputStream iInputNames = new ObjectInputStream(namesObject.getBlob("dataInputName").getBinaryStream());
                        names = (InputNames) iInputNames.readObject();
                    }
                }
                if (names == null) {
                    names = new InputNames();
                }
            } catch (SQLException | ClassNotFoundException | IOException throwables) {
                throwables.printStackTrace();
            }
        }
        _updateInputNames();
    } // Перезапуск окна work, подугрузка истории запросов

    private void _updateInputNames() {
        inputNameCompany.getItems().clear();
        inputNameCompany.getItems().addAll(names);
    } // Обновление истории запросов

    public void attachMainClass(Main main) {
        _parent = main;
        _initInputNames();

        tableArticles.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("position"));
        tableArticles.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("nameCompany"));
        tableArticles.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("description"));

        tableArticles.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableArticles.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<ArticleTable>() {
            @Override
            public void onChanged(Change<? extends ArticleTable> c) {
                companyToLoadData.clear();
                tableArticles.getSelectionModel().getSelectedItems().forEach(item -> {
                    companyToLoadData.add(parsedData.get(item.getPosition()));
                });
            }
        });

        tableArticles.setRowFactory(new Callback<TableView<ArticleTable>, TableRow<ArticleTable>>() {
            @Override
            public TableRow<ArticleTable> call(TableView<ArticleTable> param) {
                return new TableRow<ArticleTable>(){
                    @Override
                    protected void updateItem(ArticleTable item, boolean empty) {
                        super.updateItem(item, empty);
                        if(!empty){
                            int tempRating = parsedData.get(item.getPosition()).getRating();
                            if(tempRating >= 100){
                                getStyleClass().add("goodCompany");
                                return;
                            }
                            if(tempRating >= 50){
                                getStyleClass().add("averageCompany");
                                return;
                            }
                            getStyleClass().add("badCompany");
                        }
                    }
                };
            }
        });

        stateProgram.setText("Задач нет");
    } // Добавляет класс для того чтоб делать запросы, чтоб менять

    @FXML
    public void submit(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            names.add(inputNameCompany.getEditor().getText());
            if (!_checkBdProperties()) {
                LocaleDataSave save = new LocaleDataSave(pathTempInputNames, names);
                save.save();
            } else {
                PreparedStatement statement = null;
                try {
                    statement = dataBase.connection.prepareStatement("update inputcashe set `dataInputName` = ?");
                    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    final ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(names);
                    statement.setBlob(1, new SerialBlob(baos.toByteArray()));
                    statement.executeUpdate();
                } catch (SQLException | IOException throwables) {
                    throwables.printStackTrace();
                }
            }

            _updateInputNames();
            stateProgram.setText("Выполнение запроса");
            Task<List<News>> running = new Task<List<News>>() {
                @Override
                protected List<News> call() {
                    Parser parser = new Parser();
                    List<News> tempNews = null;
                    try {
                        tempNews = parser.get_news(inputNameCompany.getEditor().getText());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return tempNews;
                }
            };
            running.setOnSucceeded(event1 -> {
                _parseData(running.getValue());
                running.cancel();
            });
            running.setOnFailed(ev -> {
                stateProgram.setText("Ошибка запроса");
                running.cancel();
            });
            new Thread(running).start();
        }
    } // Дейсвие после Enter, в окне имени компании

    private void _parseData(List<News> newData) {
        if (newData == null) {
            stateProgram.setText("Ошибка запроса");
            return;
        }
        dataTable.clear();
        newData.iterator().forEachRemaining(item -> {
            parsedData.add(item);
        });

        ListIterator iter = parsedData.listIterator();
        int tempDataTableSize = dataTable.size();
        while (iter.hasNext()) {
            int index = tempDataTableSize + iter.nextIndex();
            News article = (News) iter.next();
            _addItemToTable(index, article.getCompany(), article.getArticle());
        }
        tableArticles.setItems(dataTable);
        stateProgram.setText("Выполнено успешно");
    } // Парсинг и вывод данных

    private void _addItemToTable(int position, String name, String description) {
        dataTable.add(new ArticleTable(position, name, description));
    } // добавление элемента в массив таблицы

    public void clearData(ActionEvent event) {
        if (event.getEventType() == ActionEvent.ACTION) {
            companyToLoadData.clear();
            dataTable.clear();
        }
    } // Очистка полей

    public void createDocument(ActionEvent event) {
        if (event.getEventType() == ActionEvent.ACTION) {

            String nameDocumentTemp = nameDocumentToSave.getText();
            String dateDoc = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            String distDir = pathTempDocuments + dateDoc + "/";

            new File(distDir).mkdirs();
            int countFiles = new File(distDir).listFiles().length;

            if (nameDocumentTemp.equals("")) {
                nameDocumentTemp = " report " + dateDoc + "-(" + countFiles + ")";
            }

            String nameFile = distDir
                    + nameDocumentTemp
                    + ".xlsx";

            XLSXGenerator genDocument = new XLSXGenerator(companyToLoadData);
            XSSFWorkbook book = genDocument.getDocument();
            LocaleDataSave dataSaveExcel = new LocaleDataSave(nameFile, book);
            dataSaveExcel.saveExcel();

            _fetchDocumentsLoadToDb();
        }
    } // Создание документа отчёта

    private void _fetchDocumentsLoadToDb() {
        File checkDir = new File(pathTempDocuments);

        File[] dirlist = checkDir.listFiles();
        for (File dir : dirlist) {
            if (dir.isDirectory()) {
                ArrayList<File> fileList = new ArrayList<>();
                for (File file : dir.listFiles()) {
                    if (file.isFile()) {
                        fileList.add(file);
                    }
                }
                String[] dirName = dir.getName().split("-");

                Calendar calendar = Calendar.getInstance();
                calendar.clear();
                calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dirName[0]));
                calendar.set(Calendar.MONTH, Integer.parseInt(dirName[1]));
                calendar.set(Calendar.YEAR, Integer.parseInt(dirName[2]));
                Date dateOfDocument = calendar.getTime();
                java.sql.Date dateSql = new java.sql.Date(dateOfDocument.getTime());
                try {
                    PreparedStatement statement = dataBase.connection.prepareStatement("SELECT * from `documents` WHERE idUserTelegram = ?");
                    statement.setString(1, _parent.getNameTelegram());
                    ResultSet existedDocs = statement.executeQuery();
                    ArrayList<File> noSyncFiles = new ArrayList<>();
                    while (existedDocs.next()) {
                        for (File existedFile : fileList) {
                            if (!existedDocs.getDate("date").equals(dateOfDocument) &&
                                    !existedDocs.getString("nameDocument").equals(existedFile.getName())) {
                                noSyncFiles.add(existedFile);
                            }
                        }
                    }
                    for (File existedFile : fileList) {
                        FileInputStream inputBlobDoc = new FileInputStream(existedFile);

                        PreparedStatement addDocs = dataBase.connection.prepareStatement("INSERT INTO `documents` Values (?, ?, ?, ?)");
                        addDocs.setString(1, _parent.getNameTelegram());
                        addDocs.setString(2, existedFile.getName());
                        addDocs.setBlob(3, inputBlobDoc);
                        addDocs.setDate(4, dateSql);
                        addDocs.execute();
                    }
                } catch (SQLException | FileNotFoundException throwables) {
                    throwables.printStackTrace();
                }
                fileList.clear();
            }
        }
    } // Загрузка отчётов в БД, которых там нет


    public void filterData(KeyEvent keyEvent) {
        String text = filterText.getText();
        Pattern pattern = Pattern.compile(text);
        if (keyEvent.getCode() == KeyCode.ENTER) {
            if (dataTable.size() != 0) {
                tableArticles.getSelectionModel().clearSelection();
                tableArticles.getItems().forEach(item -> {
                    Matcher match = pattern.matcher(item.getDescription());
                    if (match.find()) {
                        tableArticles.getSelectionModel().select(item.getPosition());
                    }
                });
            }
        }
    } // Фильтр по ключевым словам
}
