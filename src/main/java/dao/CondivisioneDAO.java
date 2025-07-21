package dao;

import interfacceDAO.interfacciaCondivisioneDAO;
import database.ConnessioneDatabase;
import model.Utente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO per la gestione della tabella di condivisione dei ToDo tra utenti.
 */
public class CondivisioneDAO implements interfacciaCondivisioneDAO {

    /**
     * Crea una nuova condivisione tra un To Do e un utente.
     * @param todoId id del To Do
     * @param utenteUsername username dell'utente
     */
    @Override
    public void creaCondivisione(int todoId, String utenteUsername){
        String sql = "INSERT INTO condivisione (todo_id, utente_username) VALUES (?, ?)";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, todoId);
            stmt.setString(2, utenteUsername);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Elimina la condivisione di un To Do con un utente.
     * @param todoId id del To Do
     * @param utenteUsername username dell'utente
     */
    @Override
    public void eliminaCondivisione(int todoId, String utenteUsername){
        String sql = "DELETE FROM condivisione WHERE todo_id = ? AND utente_username = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, todoId);
            stmt.setString(2, utenteUsername);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inserisce i possessori di un To Do nella tabella di condivisione.
     * @param todoId id del To Do
     * @param possessori lista di utenti possessori
     * @throws SQLException se avvengono errori SQL
     */
    @Override
    public void inserisciPossessori(int todoId, List<Utente> possessori) throws SQLException {
        String query = "INSERT INTO condivisione (todo_id, utente_username) VALUES (?, ?)";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, todoId);
            for (Utente utente : possessori) {
                stmt.setString(2, utente.getUsername());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    /**
     * Restituisce la lista di utenti con cui un To Do è condiviso.
     * @param todoId id del To Do
     * @return lista di utenti
     * @throws SQLException se avvengono errori SQL
     */
    @Override
    public List<Utente> getUtentiCondivisiByToDoId(int todoId) throws SQLException {
        List<Utente> utentiCondivisi = new ArrayList<>();
        String sql = "SELECT utente_username FROM condivisione WHERE todo_id = ?";

        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, todoId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String usernamePossessore = rs.getString("utente_username");
                    utentiCondivisi.add(new Utente(usernamePossessore, ""));
                }
            }
        }
        return utentiCondivisi;
    }
}
