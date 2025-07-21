package dao;

import database.ConnessioneDatabase;
import interfacceDAO.interfacciaAttivitaDAO;
import model.Attivita;
import model.StatoAttivita;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO per la gestione delle attività nella checklist nel database.
 */
public class AttivitaDAO implements interfacciaAttivitaDAO {

    /**
     * Inserisce una nuova attività associata a una checklist.
     * @param checklist_id id della checklist
     * @param titolo titolo dell'attività
     * @param stato stato dell'attività
     * @throws SQLException se avvengono errori SQL
     */
    @Override
    public void inserisci(int checklist_id, String titolo, StatoAttivita stato) throws SQLException {
        String sql = "INSERT INTO attivita (checklist_id, titolo, stato) VALUES (?, ?, ?)";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, checklist_id);
            stmt.setString(2, titolo);
            stmt.setString(3, stato.name());
            stmt.executeUpdate();
        }
    }

    /**
     * Elimina tutte le attività associate a un determinato To Do.
     * @param todoId id del To Do
     * @throws SQLException se avvengono errori SQL
     */
    @Override
    public void elimina(int todoId) throws SQLException {
        String sql = "DELETE FROM attivita WHERE todo_id = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, todoId);
            stmt.executeUpdate();
        }
    }

    /**
     * Elimina una singola attività dato il suo id.
     * @param attivitaId id dell'attività
     * @throws SQLException se avvengono errori SQL
     */
    @Override
    public void eliminaAttivitaById(int attivitaId) throws SQLException {
        String sql = "DELETE FROM attivita WHERE id = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, attivitaId);
            stmt.executeUpdate();
        }
    }

    /**
     * Elimina tutte le attività di una checklist.
     * @param checklistId id della checklist
     * @throws SQLException se avvengono errori SQL
     */
    @Override
    public void eliminaByChecklist(int checklistId) throws SQLException {
        String sql = "DELETE FROM attivita WHERE checklist_id = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, checklistId);
            stmt.executeUpdate();
        }
    }

    /**
     * Restituisce la lista di attività associate a una checklist.
     * @param checklistId id della checklist
     * @return lista di attività
     * @throws SQLException se avvengono errori SQL
     */
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
}
