package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe di utility per la gestione delle connessioni al database PostgreSQL.
 * Contiene le costanti di configurazione e fornisce un metodo statico per ottenere la connessione.
 */
public class ConnessioneDatabase {
    private static final String URL = "jdbc:postgresql://localhost:5432/DBToDo";
    private static final String USER = "GLS";
    private static final String PASSWORD = "1234";

    /**
     * Costruttore privato per impedire l'istanziazione della classe.
     */
    private ConnessioneDatabase() {
        // Prevent instantiation
    }

    /**
     * Restituisce una nuova connessione al database PostgreSQL.
     * @return oggetto Connection
     * @throws SQLException se la connessione fallisce
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
