package dao;

import interfacceDAO.interfacciaUtenteDAO;
import model.Utente;
import database.ConnessioneDatabase;

import java.sql.*;

/**
 * Classe DAO per la gestione degli utenti nel database.
 */
public class UtenteDAO implements interfacciaUtenteDAO {
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";
    private static final String SQL_SELECT_UTENTE = "SELECT * FROM utente WHERE username = ?";
    private static final String SQL_SELECT_USEPASS = "SELECT * FROM utente WHERE username = ? AND password = ?";

    /**
     * Inserisce un nuovo utente nel database.
     * @param utente utente da inserire
     * @throws SQLException se avvengono errori SQL
     */
    @Override
    public void inserisci(Utente utente) throws SQLException {
        String sql = "INSERT INTO utente (username, password) VALUES (?, ?)";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, utente.getUsername());
            stmt.setString(2, utente.getPassword());
            stmt.executeUpdate();
        }
    }

    /**
     * Restituisce l'utente dato l'username.
     * @param username nome utente
     * @return oggetto Utente oppure null se non trovato
     * @throws SQLException se avvengono errori SQL
     */
    @Override
    public Utente getUtente(String username) throws SQLException {
        String sql = SQL_SELECT_UTENTE;
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Utente(rs.getString(COL_USERNAME), rs.getString(COL_PASSWORD));
                }
            }
        }
        return null;
    }

    /**
     * Elimina un utente dal database.
     * @param username nome utente da eliminare
     * @throws SQLException se avvengono errori SQL
     */
    @Override
    public void elimina(String username) throws SQLException {
        String sql = "DELETE FROM utente WHERE username = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        }
    }

    /**
     * Effettua il login dell'utente controllando username e password.
     * @param username nome utente
     * @param password password utente
     * @return l'utente se credenziali corrette, altrimenti null
     * @throws SQLException se avvengono errori SQL
     */
    @Override
    public Utente login (String username, String password) throws SQLException {
        String sql = SQL_SELECT_USEPASS;
        try(Connection conn = ConnessioneDatabase.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, username);
            stmt.setString(2, password);

            try(ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Utente(rs.getString(COL_USERNAME), rs.getString(COL_PASSWORD));
                }
            }
        }
        return null;
    }

    /**
     * Restituisce un utente dato l'username (alias di getUtente).
     * @param username nome utente
     * @return oggetto Utente oppure null se non trovato
     * @throws SQLException se avvengono errori SQL
     */
    @Override
    public Utente getUtenteByUsernameDAO(String username) throws SQLException {
        String sql = SQL_SELECT_UTENTE;
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try(ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Utente(rs.getString(COL_USERNAME), rs.getString(COL_PASSWORD));
                }
            }
        }
        return null;
    }
}
