package dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class SingletonConnection {

    private static final String url = "jdbc:mysql://localhost:3306/tp1";
    private static final String user = "root";
    private static final String password = "welc0me";


    public static Connection SingletonConnection() throws Exception {
        return DriverManager.getConnection(url, user, password);
    }
}
