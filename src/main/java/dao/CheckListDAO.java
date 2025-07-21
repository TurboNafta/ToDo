package dao;

import interfacceDAO.interfacciaCheckListDAO;
import database.ConnessioneDatabase;
import model.Attivita;
import model.CheckList;
import model.StatoAttivita;
import model.ToDo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO per la gestione delle checklist (insieme di attività) dei ToDo.
 */
public class CheckListDAO implements interfacciaCheckListDAO {

    private final AttivitaDAO attivitaDAO = new AttivitaDAO();

    /**
     * Crea una checklist per un To Do.
     * @param todoId id del To Do
     * @throws SQLException se avvengono errori SQL
     */
    @Override
    public void crea(int todoId) throws SQLException {
        String sql = "INSERT INTO checklist (todo_id) VALUES (?)";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, todoId);
            stmt.executeUpdate();
        }
    }

    /**
     * Elimina una checklist e tutte le attività associate dato il To Do.
     * @param todoId id del To Do
     * @throws SQLException se avvengono errori SQL
     */
    @Override
    public void elimina(int todoId) throws SQLException {
        int checklistId = getChecklistIdByToDoId(todoId);
        if (checklistId != -1) {
            attivitaDAO.eliminaByChecklist(checklistId);
        }
        String sql = "DELETE FROM checklist WHERE todo_id = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, todoId);
            stmt.executeUpdate();
        }
    }

    /**
     * Inserisce una nuova checklist e le sue attività associate.
     * @param todoId id del To Do
     * @param checklist oggetto CheckList da inserire
     * @return id generato per la checklist
     * @throws SQLException se avvengono errori SQL
     */
    @Override
    public int inserisciChecklist(int todoId, CheckList checklist) throws SQLException {
        String insertChecklistSql = "INSERT INTO checklist (todo_id) VALUES (?)";
        int checklistId;
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmtChecklist = conn.prepareStatement(insertChecklistSql, Statement.RETURN_GENERATED_KEYS)) {
            stmtChecklist.setInt(1, todoId);
            stmtChecklist.executeUpdate();
            try (ResultSet generatedKeys = stmtChecklist.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    checklistId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Inserimento checklist fallito, nessun ID ottenuto.");
                }
            }
            for(Attivita attivita: checklist.getAttivita()){
                attivitaDAO.inserisci(checklistId, attivita.getTitolo(), attivita.getStato());
            }
        }
        return checklistId;
    }

    /**
     * Restituisce l'id della checklist associata a un To Do.
     * @param todoId id del To Do
     * @return id della checklist, oppure -1 se non esiste
     * @throws SQLException se avvengono errori SQL
     */
    @Override
    public int getChecklistIdByToDoId(int todoId) throws SQLException {
        String sql = "SELECT id FROM checklist WHERE todo_id = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, todoId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return -1;
    }

    /**
     * Restituisce la checklist e le attività associate a un To Do.
     * @param todoId id del To Do
     * @param todo oggetto To Do di riferimento
     * @return oggetto CheckList
     * @throws SQLException se avvengono errori SQL
     */
    @Override
    public CheckList getChecklistByToDoId(int todoId, ToDo todo) throws SQLException {
        int checklistId = getChecklistIdByToDoId(todoId);
        CheckList checklist = new CheckList(todo);
        if (checklistId != -1) {
            List<Attivita> attivitaList = attivitaDAO.getAttivitaByChecklistId(checklistId);
            checklist.setAttivita(attivitaList);
            checklist.setId(checklistId);
        }
        return checklist;
    }

    /**
     * Aggiorna la checklist e le sue attività associate a un To Do.
     * @param todo oggetto To Do con checklist aggiornata
     * @throws SQLException se avvengono errori SQL
     */
    @Override
    public void aggiornaChecklistEAttivita(ToDo todo) throws SQLException {
        int checklistId = getChecklistIdByToDoId(todo.getTodoId());
        if (checklistId == -1) {
            checklistId = inserisciChecklist(todo.getTodoId(), todo.getChecklist());
        } else {
            attivitaDAO.eliminaByChecklist(checklistId);
            for (Attivita a : todo.getChecklist().getAttivita()) {
                attivitaDAO.inserisci(checklistId, a.getTitolo(), a.getStato());
            }
        }
    }
}

