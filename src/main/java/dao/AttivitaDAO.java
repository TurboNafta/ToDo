package dao;

import InterfacceDAO.InterfacciaAttivitaDAO;
import database.ConnessioneDatabase;
import model.StatoAttivita;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AttivitaDAO implements InterfacciaAttivitaDAO {
    @Override
    public void inserisci(int todoId, String titolo, StatoAttivita stato) throws SQLException {
        String sql = "INSERT INTO attivita (todo_id, titolo, stato) VALUES (?, ?, ?)";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, todoId);
            stmt.setString(2, titolo);
            stmt.setString(3, stato.name());
            stmt.executeUpdate();
        }
    }

    @Override
    public void elimina(int todoId) throws SQLException {
        String sql = "DELETE FROM attivita WHERE todo_id = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, todoId);
            stmt.executeUpdate();
        }
    }
    @Override
    public void eliminaAttivitaById(int attivitaId) throws SQLException {
        String sql = "DELETE FROM attivita WHERE id = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, attivitaId);
            stmt.executeUpdate();
        }
    }
}
