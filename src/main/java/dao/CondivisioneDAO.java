package dao;

import InterfacceDAO.InterfacciaCondivisioneDAO;
import database.ConnessioneDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CondivisioneDAO implements InterfacciaCondivisioneDAO {
    @Override
    public void creaCondivisione(int todoId, int utenteId){
        String sql = "INSERT INTO condivisione (todo_id, utente_id) VALUES (?, ?)";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, todoId);
            stmt.setInt(2, utenteId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminaCondivisione(int todoId, int utenteId){
        String sql = "DELETE FROM condivisione WHERE todo_id = ? AND utente_id = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, todoId);
            stmt.setInt(2, utenteId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
