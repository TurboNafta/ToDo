package dao;

import InterfacceDAO.InterfacciaAttivitaDAO;
import database.ConnessioneDatabase;
import model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.sql.ResultSet;

public class AttivitaDAO implements InterfacciaAttivitaDAO {
    @Override
    public void inserisci(int checklist_id, String titolo, StatoAttivita stato) throws SQLException {
        String sql = "INSERT INTO attivita (titolo, stato,checklist_id) VALUES (?, ?, ?)";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, titolo);
            stmt.setString(2, stato.name());
            stmt.setInt(3, checklist_id );
            stmt.executeUpdate();
        }
    }

    @Override
    public void elimina(int todoId) throws SQLException {
        String sql = "DELETE FROM attivita WHERE checklist_id IN " +
                "(SELECT id FROM checklist WHERE todo_id = ?)";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, todoId);
            stmt.executeUpdate();
        }
    }
    @Override
    public List<Attivita> getAttivitaByChecklistId(int checklistId) throws SQLException {
        List<Attivita> attivitaList = new ArrayList<>();
        String sql = "SELECT id, titolo, stato FROM attivita WHERE checklist_id = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, checklistId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String titolo = rs.getString("titolo");
                    StatoAttivita stato = StatoAttivita.valueOf(rs.getString("stato"));
                    attivitaList.add(new Attivita(id, checklistId, titolo, stato));
                }
            }
        }
        return attivitaList;
    }
    @Override
    public void aggiornaStatoAttivita(int attivitaId, StatoAttivita nuovoStato) throws SQLException {
        String sql = "UPDATE attivita SET stato = ? WHERE id = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nuovoStato.name());
            stmt.setInt(2, attivitaId);
            stmt.executeUpdate();
        }
    }
}
