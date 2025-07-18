package dao;

import InterfacceDAO.InterfacciaCheckListDAO;
import database.ConnessioneDatabase;

import java.sql.*;

public class CheckListDAO implements InterfacciaCheckListDAO {
    @Override
    public int inserisci(int todoId) throws SQLException {
        String sql = "INSERT INTO checklist (todo_id) VALUES (?)";
        int IDgenerato = -1;
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, todoId);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    IDgenerato = rs.getInt(1);
                }
            }
            return IDgenerato;
        }
    }

    @Override
    public void elimina(int todoId) throws SQLException {
        String sql = "DELETE FROM checklist WHERE todo_id = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, todoId);
            stmt.executeUpdate();
        }
    }
}

