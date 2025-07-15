package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnessioneDatabase {
    private static final String URL = "jdbc:postgresql://localhost:5432/DBToDo";
    private static final String USER = "GLS";
    private static final String PASSWORD = "1234";
    private static Connection connection;

    /**
     * Costruttore privato per impedire l'istanziazione della classe.
     */
    private ConnessioneDatabase() {
        // Prevent instantiation
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }
}
