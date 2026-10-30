package courtculture1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/db_courtculture";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Default XAM^PP biasanya kosong

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}