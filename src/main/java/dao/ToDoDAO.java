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

    private static final String SQL_SELECT_POSSESSORI = "SELECT utente_username FROM condivisione WHERE todo_id = ?";
    private static final String SQL_SELECT_ATTIVITA = "SELECT id, titolo, stato, checklist_id FROM attivita WHERE checklist_id = ?";



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
            stmt.setInt(1, todoId);
            for (Utente utente : possessori) {
                stmt.setString(2, utente.getUsername());
                stmt.addBatch();
            }
            stmt.executeBatch();
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
            stmtAttivita.setInt(3, checklistId);
            for (Attivita attivita : checklist.getAttivita()) {
                stmtAttivita.setString(1, attivita.getTitolo());
                stmtAttivita.setString(2, attivita.getStato().name()); // usa "COMPLETATO" o "NONCOMPLETATO"
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

                CheckList checklist = new CheckList(todo);
                todo.setChecklist(checklist);

                //Recupera i possessori per il To do corrente
                String sqlSelectPossessori = SQL_SELECT_POSSESSORI;
                try (PreparedStatement pstmt = conn.prepareStatement(sqlSelectPossessori)) {
                    pstmt.setInt(1, todoId);
                    ResultSet rsP = pstmt.executeQuery();
                    while(rsP.next()) {
                        String usernamePossessore = rsP.getString(COL_UTENTE_USERNAME);
                        todo.getUtentiPossessori().add(new Utente(usernamePossessore, ""));
                    }
                }
                //Recupera le attività della checklist per il To do corrente
                String sqlSelectChecklist = SQL_SELECT_ATTIVITA;
                try (PreparedStatement stmtChecklist = conn.prepareStatement(sqlSelectChecklist)) {
                    stmtChecklist.setInt(1, todoId);
                    ResultSet rsChecklist = stmtChecklist.executeQuery();
                    List<Attivita> attivitaList = new ArrayList<>();
                    while (rsChecklist.next()) {
                        int attivitaId = rsChecklist.getInt(COL_ATTIVITA_ID);
                        String attivitaTitolo = rsChecklist.getString(COL_TITOLO);
                        boolean attivitaStato = rsChecklist.getBoolean(COL_ATTIVITA_STATO);
                        int checklistIdFromDb = rsChecklist.getInt(COL_ATTIVITA_CHECKLIST_ID);

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

    public List<Utente> getUtentiCondivisiByToDoId(int todoId) throws SQLException {
        List<Utente> utentiCondivisi = new ArrayList<>();
        String sql = SQL_SELECT_POSSESSORI;

        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, todoId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String usernamePossessore = rs.getString(COL_UTENTE_USERNAME);
                    utentiCondivisi.add(new Utente(usernamePossessore, "")); // Assumi che Utente abbia un costruttore che accetta solo username o un setter per la password
                }
            }
        }
        return utentiCondivisi;
    }

    public void aggiornaBachecaToDo(int todoId, int nuovaBachecaId, String nuovaPosizione) throws SQLException {
        String sql = "UPDATE todo SET bacheca_id = ?, posizione = ? WHERE id = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, nuovaBachecaId);
            stmt.setString(2, nuovaPosizione);
            stmt.setInt(3, todoId);
            int updated = stmt.executeUpdate();
        }
    }

    public List<ToDo> getToDoCondivisiConUtente(String username) throws SQLException {
        String sql = "SELECT t.* FROM todo t " +
                "JOIN condivisione c ON t.id = c.todo_id " +
                "WHERE c.utente_username = ? AND t.autore_username <> ?";
        List<ToDo> condivisi = new ArrayList<>();
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int todoId = rs.getInt("id");
                GregorianCalendar data = new GregorianCalendar();
                if (rs.getDate(COL_DATASCADENZA) != null) {
                    data.setTime(rs.getDate(COL_DATASCADENZA));
                }

                // Costruisci autore (puoi anche recuperare altri campi se vuoi)
                Utente autore = new Utente(rs.getString(COL_AUTORE_USERNAME), "");

                // Istanzia il To Do
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

                String sqlSelectPossessori = SQL_SELECT_POSSESSORI;
                try (PreparedStatement pstmt = conn.prepareStatement(sqlSelectPossessori)) {
                    pstmt.setInt(1, todoId);
                    ResultSet rsP = pstmt.executeQuery();
                    while(rsP.next()) {
                        String usernamePossessore = rsP.getString(COL_UTENTE_USERNAME);
                        todo.getUtentiPossessori().add(new Utente(usernamePossessore, ""));
                    }
                }
                condivisi.add(todo);
            }
        }
        return condivisi;
    }

    @Override
    public List<ToDo> getToDoByBachecaAndUtente(int bachecaId, String username) throws SQLException {
        List<ToDo> result = new ArrayList<>();

        try (Connection conn = ConnessioneDatabase.getConnection()) {
            //To do propri
            String sqlPropri = "SELECT t.* FROM todo t WHERE t.bacheca_id = ? AND t.autore_username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlPropri)) {
                stmt.setInt(1, bachecaId);
                stmt.setString(2, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        ToDo todo = costruisciToDoBase(rs, bachecaId);
                        popolaPossessori(todo, conn);
                        popolaChecklist(todo, conn);
                        result.add(todo);
                    }
                }
            }

            // To do condivisi
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
                        popolaPossessori(todo, conn);
                        popolaChecklist(todo, conn);
                        result.add(todo);
                    }
                }
            }
        }
        return result;
    }

    private ToDo costruisciToDoBase(ResultSet rs, int bachecaId) throws SQLException {
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
        todo.setChecklist(new CheckList(todo));
        return todo;
    }

    private void popolaPossessori(ToDo todo, Connection conn) throws SQLException {
        String sqlSelectPossessori = SQL_SELECT_POSSESSORI;
        try (PreparedStatement pstmt = conn.prepareStatement(sqlSelectPossessori)) {
            pstmt.setInt(1, todo.getTodoId());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String usernamePossessore = rs.getString(COL_UTENTE_USERNAME);
                    todo.getUtentiPossessori().add(new Utente(usernamePossessore, ""));
                }
            }
        }
    }

    private void popolaChecklist(ToDo todo, Connection conn) throws SQLException {
        int checklistId = recuperaChecklistId(todo.getTodoId(), conn);
        CheckList checklist = new CheckList(todo);

        if (checklistId != -1) {
            checklist.setAttivita(recuperaAttivita(checklistId, conn));
            checklist.setId(checklistId);
        }
        todo.setChecklist(checklist);
    }

    private int recuperaChecklistId(int todoId, Connection conn) throws SQLException {
        String sqlGetChecklistId = "SELECT id FROM checklist WHERE todo_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlGetChecklistId)) {
            pstmt.setInt(1, todoId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(COL_TODO_ID);
                }
            }
        }
        return -1;
    }

    private List<Attivita> recuperaAttivita(int checklistId, Connection conn) throws SQLException {
        List<Attivita> attivitaList = new ArrayList<>();
        String sqlSelectChecklist = SQL_SELECT_ATTIVITA;
        try (PreparedStatement stmtChecklist = conn.prepareStatement(sqlSelectChecklist)) {
            stmtChecklist.setInt(1, checklistId);
            try (ResultSet rsChecklist = stmtChecklist.executeQuery()) {
                while (rsChecklist.next()) {
                    int attivitaId = rsChecklist.getInt(COL_ATTIVITA_ID);
                    String attivitaTitolo = rsChecklist.getString(COL_ATTIVITA_TITOLO);
                    String statoStr = rsChecklist.getString(COL_STATO);
                    StatoAttivita statoA = StatoAttivita.valueOf(statoStr);
                    Attivita attivita = new Attivita(attivitaId, checklistId, attivitaTitolo, statoA);
                    attivitaList.add(attivita);
                }
            }
        }
        return attivitaList;
    }

    public void aggiornaChecklistEAttivita(ToDo todo, Connection conn) throws SQLException {
        // Aggiorna o crea la checklist se serve
        if (todo.getChecklist() != null) {
            // Se la checklist non esiste
            String checkSql = "SELECT id FROM checklist WHERE todo_id = ?";
            int checklistId = -1;
            try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
                pstmt.setInt(1, todo.getTodoId());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    checklistId = rs.getInt("id");
                } else {
                    // Non esiste, va creata
                    String insertChecklist = "INSERT INTO checklist (todo_id) VALUES (?) RETURNING id";
                    try (PreparedStatement stmt = conn.prepareStatement(insertChecklist)) {
                        stmt.setInt(1, todo.getTodoId());
                        ResultSet rs2 = stmt.executeQuery();
                        if (rs2.next()) {
                            checklistId = rs2.getInt(1);
                        }
                    }
                }
            }
            // Cancella tutte le attività di questa checklist
            String deleteAttivita = "DELETE FROM attivita WHERE checklist_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteAttivita)) {
                pstmt.setInt(1, checklistId);
                pstmt.executeUpdate();
            }
            // 3. Inserisci tutte le attività nuove
            String insertAttivita = "INSERT INTO attivita (titolo, stato, checklist_id) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertAttivita)) {
                pstmt.setInt(3, checklistId);

                for (Attivita a : todo.getChecklist().getAttivita()) {
                    pstmt.setString(1, a.getTitolo());
                    pstmt.setString(2, a.getStato().name());
                    pstmt.executeUpdate();
                }
            }
        }
    }
}
