package dao;

import InterfacceDAO.InterfacciaCondivisioneDAO;
import database.ConnessioneDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CondivisioneDAO implements InterfacciaCondivisioneDAO {
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
}
