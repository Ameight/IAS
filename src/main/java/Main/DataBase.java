package Main;

import java.sql.*;
//
public class DataBase{

    public Connection connection;

    Statement stmt = null;
    public DataBase(){
        _connect();
    } //

    public Connection getConnection(){
        return this.connection;
    } //

    public ResultSet query(String query){
        ResultSet rs = null;
        try {
            stmt = connection.createStatement();
            rs =  stmt.executeQuery(query);
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return rs;
    } // Выполнение SQL запроса

    public boolean execute(String query){
        boolean rs = false;
        try {
            stmt = connection.createStatement();
            rs =  stmt.execute(query);
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return rs;
    } // Обработка на ошибки

    public void _defaultInit(){
        this.execute("CREATE DATABASE IF NOT EXISTS dataDocs");
        try {
            this.connection.setSchema("dataDocs");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        this.execute("CREATE TABLE if not exists `inputCashe` (" +
                "`idUserTelegram` VARCHAR(100) NOT NULL," +
                "`dataInputName` BLOB," +
                "`dataInputFilter` BLOB," +
                "UNIQUE KEY `keyId` (`idUserTelegram`) USING BTREE" +
                ") ENGINE=MyISAM;");
        this.execute("CREATE TABLE if not exists `documents` (" +
                "`idUserTelegram` VARCHAR(100) NOT NULL," +
                "`nameDocument` TEXT NOT NULL," +
                "`bDocData` BLOB NOT NULL," +
                "`date` DATE NOT NULL" +
                ") ENGINE=MyISAM;");
    } // Создание таблицы

    private void _connect(){
        try {
            connection =
                    DriverManager.getConnection("jdbc:mysql://localhost/dataDocs?" +
                            "user=root&password=qwerty");
            this.connection.setSchema("dataDocs");
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    } // подключение к БД
}
