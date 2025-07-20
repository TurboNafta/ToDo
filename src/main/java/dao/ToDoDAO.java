package dao;

import interfacceDAO.InterfacciaToDoDAO;
import model.*;
import database.ConnessioneDatabase;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class ToDoDAO implements InterfacciaToDoDAO {
    private static final String COL_TITOLO = "titolo";
    private static final String COL_DESCRIZIONE = "descrizione";
    private static final String COL_URL = "url";
    private static final String COL_DATASCADENZA = "datascadenza";
    private static final String COL_IMAGE = "image";
    private static final String COL_POSISIONE = "posizione";
    private static final String COL_COLORESFONDO = "coloresfondo";
    private static final String COL_STATO = "stato";
    private static final String COL_AUTORE_USERNAME = "autore_username";
    private static final String COL_UTENTE_USERNAME = "utente_username";
    private static final String COL_ATTIVITA_ID = "id";
    private static final String COL_ATTIVITA_TITOLO = "titolo";
    private static final String COL_ATTIVITA_STATO = "stato";
    private static final String COL_ATTIVITA_CHECKLIST_ID = "checklist_id";
    private static final String COL_TODO_ID = "id";
    private static final String COL_BACHECA_ID = "bacheca_id";
    private static final String DATA_FORMAT = "dd/MM/yyyy";
    private static final String USE = "username";

    private final CondivisioneDAO condivisioneDAO = new CondivisioneDAO();
    private final CheckListDAO checkListDAO = new CheckListDAO();

    @Override
    public int inserisci(ToDo todo, String username, int bachecaId) throws SQLException {
        String sql = "INSERT INTO todo (titolo, descrizione, url, datascadenza, image, posizione, coloresfondo, stato, autore_username, bacheca_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, todo.getTitolo());
            stmt.setString(2, todo.getDescrizione());
            stmt.setString(3, todo.getUrl());
            java.sql.Date sqlDate = new java.sql.Date(todo.getDatascadenza().getTime().getTime());
            stmt.setDate(4, sqlDate);
            stmt.setString(5, todo.getImage());
            stmt.setString(6, todo.getPosizione());
            stmt.setString(7, todo.getColoresfondo());
            stmt.setString(8, todo.getStato().name());
            stmt.setString(9, username);
            stmt.setInt(10, bachecaId);

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int todoId = rs.getInt(1);
                    todo.setTodoId(todoId);

                    // Inserisci i possessori
                    if(todo.getUtentiPossessori() != null && !todo.getUtentiPossessori().isEmpty()) {
                        condivisioneDAO.inserisciPossessori(todoId, todo.getUtentiPossessori());
                    }
                    //inserisci le attività della checklist se presenti
                    if(todo.getChecklist() != null && !todo.getChecklist().getAttivita().isEmpty()){
                        checkListDAO.inserisciChecklist(todoId, todo.getChecklist());
                    }
                    return todoId;
                }
            }
        }
        throw new SQLException("Creazione ToDo fallita, nessun ID ottenuto.");
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
        int checklistId = -1;
        String checklistQuery = "SELECT id FROM checklist WHERE todo_id = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(checklistQuery)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    checklistId = rs.getInt("id");
                }
            }
        }

        if (checklistId != -1) {
            String deleteAttivitaSql = "DELETE FROM attivita WHERE checklist_id = ?";
            try (Connection conn = ConnessioneDatabase.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(deleteAttivitaSql)) {
                stmt.setInt(1, checklistId);
                stmt.executeUpdate();
            }

            String deleteChecklistSql = "DELETE FROM checklist WHERE id = ?";
            try (Connection conn = ConnessioneDatabase.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(deleteChecklistSql)) {
                stmt.setInt(1, checklistId);
                stmt.executeUpdate();
            }
        }

        String deleteCondivisioniSql = "DELETE FROM condivisione WHERE todo_id = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteCondivisioniSql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }

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

            try(ResultSet rs = stmt.executeQuery()){
                while (rs.next()) {
                    int todoId = rs.getInt(COL_TODO_ID);


                    GregorianCalendar data = new GregorianCalendar();
                    if (rs.getDate(COL_DATASCADENZA) != null) {
                        data.setTime(rs.getDate(COL_DATASCADENZA));
                    }

                    Utente autore = new Utente(rs.getString(USE), "");

                    ToDo todo = new ToDo(
                            rs.getString(COL_TITOLO),
                            rs.getString(COL_DESCRIZIONE),
                            rs.getString(COL_URL),
                            new SimpleDateFormat(DATA_FORMAT).format(data.getTime()),
                            rs.getString(COL_IMAGE),
                            rs.getString(COL_POSISIONE),
                            rs.getString(COL_COLORESFONDO),
                            new ArrayList<>(),
                            autore
                    );
                    todo.setTodoId(rs.getInt(COL_TODO_ID));
                    todo.setStato(StatoToDo.valueOf(rs.getString(COL_STATO)));
                    todo.setBacheca(new Bacheca(bachecaId, null, null, null));

                    todo.setUtentiPossessori(condivisioneDAO.getUtentiCondivisiByToDoId(todoId));

                    todo.setChecklist(checkListDAO.getChecklistByToDoId(todoId, todo));

                    todos.add(todo);
                }
            }
        }
        return todos;
    }

    @Override
    public void aggiornaBachecaToDo(int todoId, int nuovaBachecaId, String nuovaPosizione) throws SQLException {
        String sql = "UPDATE todo SET bacheca_id = ?, posizione = ? WHERE id = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, nuovaBachecaId);
            stmt.setString(2, nuovaPosizione);
            stmt.setInt(3, todoId);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<ToDo> getToDoCondivisiConUtente(String username) throws SQLException {
        String sql = "SELECT t.* FROM todo t " +
                "JOIN condivisione c ON t.id = c.todo_id " +
                "WHERE c.utente_username = ? AND t.autore_username <> ?";
        List<ToDo> condivisi = new ArrayList<>();
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, username);

            try(ResultSet rs = stmt.executeQuery()){
                while (rs.next()) {
                    int todoId = rs.getInt("id");
                    GregorianCalendar data = new GregorianCalendar();
                    if (rs.getDate(COL_DATASCADENZA) != null) {
                        data.setTime(rs.getDate(COL_DATASCADENZA));
                    }

                    Utente autore = new Utente(rs.getString(COL_AUTORE_USERNAME), "");

                    ToDo todo = new ToDo(
                            rs.getString(COL_TITOLO),
                            rs.getString(COL_DESCRIZIONE),
                            rs.getString(COL_URL),
                            new SimpleDateFormat(DATA_FORMAT).format(data.getTime()),
                            rs.getString(COL_IMAGE),
                            rs.getString(COL_POSISIONE),
                            rs.getString(COL_COLORESFONDO),
                            new ArrayList<>(),
                            autore
                    );
                    todo.setTodoId(todoId);
                    todo.setStato(StatoToDo.valueOf(rs.getString(COL_STATO)));
                    todo.setBacheca(new Bacheca(rs.getInt(COL_BACHECA_ID), null, null, null));

                    todo.setTodoId(todoId);
                    todo.setStato(StatoToDo.valueOf(rs.getString(COL_STATO)));
                    todo.setBacheca(new Bacheca(rs.getInt(COL_BACHECA_ID), null, null, null));
                    condivisi.add(todo);
                }
            }
        }
        return condivisi;
    }

    @Override
    public List<ToDo> getToDoByBachecaAndUtente(int bachecaId, String username) throws SQLException {
        List<ToDo> result = new ArrayList<>();

        try (Connection conn = ConnessioneDatabase.getConnection()) {
            String sqlPropri = "SELECT t.* FROM todo t WHERE t.bacheca_id = ? AND t.autore_username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlPropri)) {
                stmt.setInt(1, bachecaId);
                stmt.setString(2, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        ToDo todo = costruisciToDoBase(rs, bachecaId);
                        todo.setUtentiPossessori(condivisioneDAO.getUtentiCondivisiByToDoId(todo.getTodoId()));
                        todo.setChecklist(checkListDAO.getChecklistByToDoId(todo.getTodoId(), todo));
                        result.add(todo);
                    }
                }
            }

            String sqlCondivisi =
                    "SELECT t.* FROM todo t " +
                            "JOIN condivisione c ON t.id = c.todo_id " +
                            "WHERE t.bacheca_id = ? AND c.utente_username = ? AND t.autore_username <> ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlCondivisi)) {
                stmt.setInt(1, bachecaId);
                stmt.setString(2, username);
                stmt.setString(3, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        ToDo todo = costruisciToDoBase(rs, bachecaId);
                        todo.setUtentiPossessori(condivisioneDAO.getUtentiCondivisiByToDoId(todo.getTodoId()));
                        todo.setChecklist(checkListDAO.getChecklistByToDoId(todo.getTodoId(), todo));
                        result.add(todo);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public ToDo costruisciToDoBase(ResultSet rs, int bachecaId) throws SQLException {
        GregorianCalendar data = new GregorianCalendar();
        Date sqlDate = rs.getDate(COL_DATASCADENZA);
        if (sqlDate != null) {
            data.setTime(sqlDate);
        }
        Utente autore = new Utente(rs.getString(COL_AUTORE_USERNAME), "");
        ToDo todo = new ToDo(
                rs.getString(COL_TITOLO),
                rs.getString(COL_DESCRIZIONE),
                rs.getString(COL_URL),
                new SimpleDateFormat(DATA_FORMAT).format(data.getTime()),
                rs.getString(COL_IMAGE),
                rs.getString(COL_POSISIONE),
                rs.getString(COL_COLORESFONDO),
                new ArrayList<>(),
                autore
        );
        todo.setTodoId(rs.getInt(COL_TODO_ID));
        todo.setStato(StatoToDo.valueOf(rs.getString(COL_STATO)));
        todo.setBacheca(new Bacheca(bachecaId, null, null, null));
        return todo;
    }
}
