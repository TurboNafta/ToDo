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
        int IdGenerato=-1;

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

            /*//Ottieni l'ID generato
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
                }*/
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    IdGenerato = rs.getInt(1);
                    todo.setTodoId(IdGenerato); // Imposta l'ID sull'oggetto ToDo
                }
            }
        }
        return IdGenerato;
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

                //Carica la checklist associata
                String sqlSelectChecklistId = "SELECT id FROM checklist WHERE todo_id = ?";
                int checklistId = -1;
                try (PreparedStatement stmtChecklistId = conn.prepareStatement(sqlSelectChecklistId)) {
                    stmtChecklistId.setInt(1, todoId);
                    ResultSet rsChecklistId = stmtChecklistId.executeQuery();
                    if (rsChecklistId.next()) {
                        checklistId = rsChecklistId.getInt("id");
                    }
                }

                if (checklistId != -1) {
                    CheckList checklist = new CheckList(todo, checklistId);
                    todo.setChecklist(checklist);

                    // Recupera le attività della checklist
                    String sqlSelectAttivita = "SELECT id, titolo, stato FROM attivita WHERE checklist_id = ?";
                    try (PreparedStatement stmtAttivita = conn.prepareStatement(sqlSelectAttivita)) {
                        stmtAttivita.setInt(1, checklistId);
                        ResultSet rsAttivita = stmtAttivita.executeQuery();
                        List<Attivita> attivitaList = new ArrayList<>();
                        while (rsAttivita.next()) {
                            int attivitaId = rsAttivita.getInt("id");
                            String attivitaTitolo = rsAttivita.getString("titolo");
                            StatoAttivita attivitaStato = StatoAttivita.valueOf(rsAttivita.getString("stato"));
                            attivitaList.add(new Attivita(attivitaId, checklistId, attivitaTitolo, attivitaStato));
                        }
                        todo.getChecklist().setAttivita(attivitaList);
                    }
                }

                todos.add(todo);
            }
        }
        return todos;
    }
    @Override
    public List<Utente> getUtentiCondivisiByToDoId(int todoId) throws SQLException {
        List<Utente> utentiCondivisi = new ArrayList<>();
        String sql = "SELECT utente_username FROM condivisione WHERE todo_id = ?";

        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, todoId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String usernamePossessore = rs.getString("utente_username");
                    utentiCondivisi.add(new Utente(usernamePossessore, "")); // Assumi che Utente abbia un costruttore che accetta solo username o un setter per la password
                }
            }
        }
        return utentiCondivisi;
    };
    @Override
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
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int todoId = rs.getInt("id");
                GregorianCalendar data = new GregorianCalendar();
                if (rs.getDate("datascadenza") != null) {
                    data.setTime(rs.getDate("datascadenza"));
                }

                // Costruisci autore (puoi anche recuperare altri campi se vuoi)
                Utente autore = new Utente(rs.getString("autore_username"), "");

                // Istanzia il To Do
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
                todo.setTodoId(todoId);
                todo.setStato(StatoToDo.valueOf(rs.getString("stato")));
                todo.setBacheca(new Bacheca(rs.getInt("bacheca_id"), null, null, null));

                String sqlSelectPossessori = "SELECT utente_username FROM condivisione WHERE todo_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlSelectPossessori)) {
                    pstmt.setInt(1, todoId);
                    ResultSet rsP = pstmt.executeQuery();
                    while(rsP.next()) {
                        String usernamePossessore = rsP.getString("utente_username");
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

        // 1. To Do propri dell'utente in questa bacheca
        String sqlPropri = "SELECT t.* FROM todo t WHERE t.bacheca_id = ? AND t.autore_username = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlPropri)) {
            stmt.setInt(1, bachecaId);
            stmt.setString(2, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int todoId = rs.getInt("id");

                GregorianCalendar data = new GregorianCalendar();
                if (rs.getDate("datascadenza") != null) {
                    data.setTime(rs.getDate("datascadenza"));
                }

                Utente autore = new Utente(rs.getString("autore_username"), "");

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
                todo.setTodoId(todoId);
                todo.setStato(StatoToDo.valueOf(rs.getString("stato")));
                todo.setBacheca(new Bacheca(bachecaId, null, null, null));
                //Carica la checklist associata
                String sqlSelectChecklistId = "SELECT id FROM checklist WHERE todo_id = ?";
                int checklistId = -1;
                try (PreparedStatement stmtChecklistId = conn.prepareStatement(sqlSelectChecklistId)) {
                    stmtChecklistId.setInt(1, todoId);
                    ResultSet rsChecklistId = stmtChecklistId.executeQuery();
                    if (rsChecklistId.next()) {
                        checklistId = rsChecklistId.getInt("id");
                    }
                }

                if (checklistId != -1) {
                    CheckList checklist = new CheckList(todo, checklistId);
                    todo.setChecklist(checklist);

                    // Recupera le attività della checklist
                    String sqlSelectAttivita = "SELECT id, titolo, stato FROM attivita WHERE checklist_id = ?";
                    try (PreparedStatement stmtAttivita = conn.prepareStatement(sqlSelectAttivita)) {
                        stmtAttivita.setInt(1, checklistId);
                        ResultSet rsAttivita = stmtAttivita.executeQuery();
                        List<Attivita> attivitaList = new ArrayList<>();
                        while (rsAttivita.next()) {
                            int attivitaId = rsAttivita.getInt("id");
                            String attivitaTitolo = rsAttivita.getString("titolo");
                            StatoAttivita attivitaStato = StatoAttivita.valueOf(rsAttivita.getString("stato"));
                            attivitaList.add(new Attivita(attivitaId, checklistId, attivitaTitolo, attivitaStato));
                        }
                        todo.getChecklist().setAttivita(attivitaList);
                    }
                }

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
                result.add(todo);
            }
        }

        // 2. To Do condivisi (tramite tabella condivisione)
        String sqlCondivisi = "SELECT t.* FROM todo t " +
                "JOIN condivisione c ON t.id = c.todo_id " +
                "WHERE t.bacheca_id = ? AND c.utente_username = ? AND t.autore_username <> ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlCondivisi)) {
            stmt.setInt(1, bachecaId);
            stmt.setString(2, username);
            stmt.setString(3, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int todoId = rs.getInt("id");

                GregorianCalendar data = new GregorianCalendar();
                if (rs.getDate("datascadenza") != null) {
                    data.setTime(rs.getDate("datascadenza"));
                }

                Utente autore = new Utente(rs.getString("autore_username"), "");

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
                todo.setTodoId(todoId);
                todo.setStato(StatoToDo.valueOf(rs.getString("stato")));
                todo.setBacheca(new Bacheca(bachecaId, null, null, null));

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
                // Carica la checklist associata (se esiste)
                String sqlSelectChecklistId = "SELECT id FROM checklist WHERE todo_id = ?";
                int checklistId = -1;
                try (PreparedStatement stmtChecklistId = conn.prepareStatement(sqlSelectChecklistId)) {
                    stmtChecklistId.setInt(1, todoId);
                    ResultSet rsChecklistId = stmtChecklistId.executeQuery();
                    if (rsChecklistId.next()) {
                        checklistId = rsChecklistId.getInt("id");
                    }
                }

                if (checklistId != -1) {
                    CheckList checklist = new CheckList(todo, checklistId);
                    todo.setChecklist(checklist);

                    // Recupera le attività della checklist
                    String sqlSelectAttivita = "SELECT id, titolo, stato FROM attivita WHERE checklist_id = ?";
                    try (PreparedStatement stmtAttivita = conn.prepareStatement(sqlSelectAttivita)) {
                        stmtAttivita.setInt(1, checklistId);
                        ResultSet rsAttivita = stmtAttivita.executeQuery();
                        List<Attivita> attivitaList = new ArrayList<>();
                        while (rsAttivita.next()) {
                            int attivitaId = rsAttivita.getInt("id");
                            String attivitaTitolo = rsAttivita.getString("titolo");
                            StatoAttivita attivitaStato = StatoAttivita.valueOf(rsAttivita.getString("stato"));
                            attivitaList.add(new Attivita(attivitaId, checklistId, attivitaTitolo, attivitaStato));
                        }
                        todo.getChecklist().setAttivita(attivitaList);
                    }
                }
                result.add(todo);
            }
        }
        return result;
    }
    @Override
    public ToDo getById(int todoId) throws SQLException {
        String sql = "SELECT t.*, u.username FROM todo t JOIN utente u ON t.autore_username = u.username WHERE t.id = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, todoId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    GregorianCalendar data = new GregorianCalendar();
                    if (rs.getDate("datascadenza") != null) {
                        data.setTime(rs.getDate("datascadenza"));
                    }
                    Utente autore = new Utente(rs.getString("autore_username"), "");
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
                    todo.setBacheca(new Bacheca(rs.getInt("bacheca_id"), null, null, null));

                    // Carica i possessori
                    todo.setUtentiPossessori(getUtentiCondivisiByToDoId(todoId));

                    // Carica la checklist e le attività
                    String sqlSelectChecklistId = "SELECT id FROM checklist WHERE todo_id = ?";
                    int checklistId = -1;
                    try (PreparedStatement stmtChecklistId = conn.prepareStatement(sqlSelectChecklistId)) {
                        stmtChecklistId.setInt(1, todoId);
                        ResultSet rsChecklistId = stmtChecklistId.executeQuery();
                        if (rsChecklistId.next()) {
                            checklistId = rsChecklistId.getInt("id");
                        }
                    }

                    if (checklistId != -1) {
                        CheckList checklist = new CheckList(todo, checklistId);
                        todo.setChecklist(checklist);

                        String sqlSelectAttivita = "SELECT id, titolo, stato FROM attivita WHERE checklist_id = ?";
                        try (PreparedStatement stmtAttivita = conn.prepareStatement(sqlSelectAttivita)) {
                            stmtAttivita.setInt(1, checklistId);
                            ResultSet rsAttivita = stmtAttivita.executeQuery();
                            List<Attivita> attivitaList = new ArrayList<>();
                            while (rsAttivita.next()) {
                                int attivitaId = rsAttivita.getInt("id");
                                String attivitaTitolo = rsAttivita.getString("titolo");
                                StatoAttivita attivitaStato = StatoAttivita.valueOf(rsAttivita.getString("stato"));
                                attivitaList.add(new Attivita(attivitaId, checklistId, attivitaTitolo, attivitaStato));
                            }
                            todo.getChecklist().setAttivita(attivitaList);
                        }
                    }
                    return todo;
                }
            }
        }
        return null;
    }

}
