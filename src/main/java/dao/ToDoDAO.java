package dao;

import InterfacceDAO.InterfacciaToDoDAO;
import model.*;
import database.ConnessioneDatabase;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class ToDoDAO implements InterfacciaToDoDAO {
    @Override
    public int inserisci(ToDo todo, String username, int bachecaId) throws SQLException {
        String sql = "INSERT INTO todo (titolo, descrizione, url, datascadenza, image, posizione, coloresfondo, stato, autore_username, bacheca_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, todo.getTitolo());
            stmt.setString(2, todo.getDescrizione());
            stmt.setString(3, todo.getUrl());
            // Convertiamo la data in formato SQL
            java.sql.Date sqlDate = new java.sql.Date(todo.getDatascadenza().getTime().getTime());
            stmt.setDate(4, sqlDate);
            stmt.setString(5, todo.getImage());
            stmt.setString(6, todo.getPosizione());
            stmt.setString(7, todo.getColoresfondo());
            stmt.setString(8, todo.getStato().name());
            stmt.setString(9, username);
            stmt.setInt(10, bachecaId);

            stmt.executeUpdate();

            // Ottieni l'ID generato
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int todoId = rs.getInt(1);
                    //id sull'oggetto to do in memoria
                    todo.setTodoId(todoId);
                    // Inserisci i possessori
                    inserisciPossessori(todoId, todo.getUtentiPossessori(), conn);

                    //inserisci le attività della checklist se presenti
                    if(todo.getChecklist() != null && !todo.getChecklist().getAttivita().isEmpty()){
                        inserisciChecklist(todoId, todo.getChecklist(), conn);
                    }
                    return todoId;
                }
            }
        }
        throw new SQLException("Creazione ToDo fallita, nessun ID ottenuto.");
    }

    @Override
    public void inserisciPossessori(int todoId, List<Utente> possessori, Connection conn) throws SQLException {
        String query = "INSERT INTO condivisione (todo_id, utente_username) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            for (Utente utente : possessori) {
                stmt.setInt(1, todoId);
                stmt.setString(2, utente.getUsername());
                stmt.executeUpdate();
            }
        }
    }

    private void inserisciChecklist(int todoId, CheckList checklist, Connection conn) throws SQLException {
        // 1. Inserisci la checklist
        String insertChecklistSql = "INSERT INTO checklist (todo_id) VALUES (?)";
        int checklistId;

        try (PreparedStatement stmtChecklist = conn.prepareStatement(insertChecklistSql, Statement.RETURN_GENERATED_KEYS)) {
            stmtChecklist.setInt(1, todoId);
            stmtChecklist.executeUpdate();
            try (ResultSet generatedKeys = stmtChecklist.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    checklistId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Inserimento checklist fallito, nessun ID ottenuto.");
                }
            }
        }

        // 2. Inserisci le attività col checklist_id ottenuto
        String insertAttivitaSql = "INSERT INTO attivita (titolo, stato, checklist_id) VALUES (?, ?, ?)";
        try (PreparedStatement stmtAttivita = conn.prepareStatement(insertAttivitaSql)) {
            for (Attivita attivita : checklist.getAttivita()) {
                stmtAttivita.setString(1, attivita.getTitolo());
                stmtAttivita.setString(2, attivita.getStato().name()); // usa "COMPLETATO" o "NONCOMPLETATO"
                stmtAttivita.setInt(3, checklistId);
                stmtAttivita.executeUpdate();
            }
        }
    }


    @Override
    public void modifica(ToDo todo) throws SQLException {
        String sql = "UPDATE todo SET titolo = ?, descrizione = ?, url = ?, datascadenza = ?, " +
                "image = ?, posizione = ?, coloresfondo = ?, stato = ? " +
                "WHERE id = ?";

        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, todo.getTitolo());
            stmt.setString(2, todo.getDescrizione());
            stmt.setString(3, todo.getUrl());
            stmt.setDate(4, new Date(todo.getDatascadenza().getTimeInMillis()));
            stmt.setString(5, todo.getImage());
            stmt.setString(6, todo.getPosizione());
            stmt.setString(7, todo.getColoresfondo());
            stmt.setString(8, todo.getStato().name());
            stmt.setInt(9, todo.getTodoId());

            stmt.executeUpdate();
        }
    }

    @Override
    public void elimina(int id) throws SQLException {
        String sql = "DELETE FROM todo WHERE id = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    @Override
    public List<ToDo> getToDoByBacheca(int bachecaId) throws SQLException {
        String sql = "SELECT t.*, u.username " +
                "FROM todo t " +
                "JOIN utente u ON t.autore_username = u.username " +
                "WHERE t.bacheca_id = ?";

        List<ToDo> todos = new ArrayList<>();

        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bachecaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int todoId = rs.getInt("id");

                GregorianCalendar data = new GregorianCalendar();
                if (rs.getDate("datascadenza") != null) {
                    data.setTime(rs.getDate("datascadenza"));
                }

                Utente autore = new Utente(rs.getString("username"), "");

                ToDo todo = new ToDo(
                        rs.getString("titolo"),
                        rs.getString("descrizione"),
                        rs.getString("url"),
                        new SimpleDateFormat("dd/MM/yyyy").format(data.getTime()),
                        rs.getString("image"),
                        rs.getString("posizione"),
                        rs.getString("coloresfondo"),
                        new ArrayList<>(),
                        autore
                );
                todo.setTodoId(rs.getInt("id"));
                todo.setStato(StatoToDo.valueOf(rs.getString("stato")));
                todo.setBacheca(new Bacheca(bachecaId, null, null, null));

                CheckList checklist = new CheckList(todo);
                todo.setChecklist(checklist);

                //Recupera i possessori per il To do corrente
                String sqlSelectPossessori = "SELECT utente_username FROM condivisione WHERE todo_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlSelectPossessori)) {
                    pstmt.setInt(1, todoId);
                    ResultSet rsP = pstmt.executeQuery();
                    while(rsP.next()) {
                        String usernamePossessore = rsP.getString("utente_username");
                        todo.getUtentiPossessori().add(new Utente(usernamePossessore, ""));
                    }
                }
                //Recupera le attività della checklist per il To do corrente
                String sqlSelectChecklist = "SELECT id, titolo, stato, checklist_id FROM attivita WHERE checklist_id = ?";
                try (PreparedStatement stmtChecklist = conn.prepareStatement(sqlSelectChecklist)) {
                    stmtChecklist.setInt(1, todoId);
                    ResultSet rsChecklist = stmtChecklist.executeQuery();
                    List<Attivita> attivitaList = new ArrayList<>();
                    while (rsChecklist.next()) {
                        int attivitaId = rsChecklist.getInt("id");
                        String attivitaTitolo = rsChecklist.getString("titolo");
                        boolean attivitaStato = rsChecklist.getBoolean("stato");
                        int checklistIdFromDb = rsChecklist.getInt("checklist_id");

                        Attivita attivita = new Attivita(attivitaId, checklistIdFromDb, attivitaTitolo, StatoAttivita.NONCOMPLETATA); // stato iniziale
                        if (attivitaStato) {
                            attivita.setStato(StatoAttivita.COMPLETATA);
                        } else {
                            attivita.setStato(StatoAttivita.NONCOMPLETATA);
                        }
                        attivitaList.add(attivita);
                    }
                    todo.getChecklist().setAttivita(attivitaList);
                }
                todos.add(todo);
            }
        }
        return todos;
    }

}
