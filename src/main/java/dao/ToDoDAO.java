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
             PreparedStatement istr = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            istr.setString(1, todo.getTitolo());
            istr.setString(2, todo.getDescrizione());
            istr.setString(3, todo.getUrl());
            // Convertiamo la data in formato SQL
            java.sql.Date sqlDate = new java.sql.Date(todo.getDatascadenza().getTime().getTime());
            istr.setDate(4, sqlDate);
            istr.setString(5, todo.getImage());
            istr.setString(6, todo.getPosizione());
            istr.setString(7, todo.getColoresfondo());
            // Imposta lo stato (deve essere COMPELTATO o NONCOMPLETATO, mai null!)
            istr.setString(8, todo.getStato().name()); // Assicurati che getStato() non ritorni null!
            istr.setString(9, username);
            istr.setInt(10, bachecaId);

            istr.executeUpdate();

            // Ottieni l'ID generato
            try (ResultSet rs = istr.getGeneratedKeys()) {
                if (rs.next()) {
                    int todoId = rs.getInt(1);
                    // Inserisci i possessori
                    inserisciPossessori(todoId, todo.getUtentiPossessori(), conn);
                    return todoId;
                }
            }
        }
        throw new SQLException("Creazione ToDo fallita, nessun ID ottenuto.");
    }

    @Override
    public void inserisciPossessori(int todoId, List<Utente> possessori, Connection conn) throws SQLException {
        String query = "INSERT INTO condivisone (todo_id, utente_username) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            for (Utente utente : possessori) {
                stmt.setInt(1, todoId);
                stmt.setString(2, utente.getUsername());
                stmt.executeUpdate();
            }
        }
    }
    private void inserisciChecklist(int todoId, CheckList checklist, Connection conn) throws SQLException {
        String query = "INSERT INTO attivita_checklist (todo_id, descrizione, completata) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            for (Attivita attivita : checklist.getAttivita()) {
                stmt.setInt(1, todoId);
                stmt.setString(2, attivita.getTitolo());
                stmt.setBoolean(3, attivita.getStato() == StatoAttivita.COMPLETATA);
                stmt.executeUpdate();
            }
        }
    }




    @Override
    public void modifica(ToDo todo) throws SQLException {
        String sql = "UPDATE todo SET " +
                "titolo = ?, " +
                "descrizione = ?, " +
                "url = ?, " +
                "datascadenza = ?, " +
                "image = ?, " +
                "posizione = ?, " +
                "coloresfondo = ?, " +
                "stato = ? " +
                "WHERE id = ?";

        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement istr = conn.prepareStatement(sql)) {

            istr.setString(1, todo.getTitolo());
            istr.setString(2, todo.getDescrizione());
            istr.setString(3, todo.getUrl());
            istr.setDate(4, new java.sql.Date(todo.getDatascadenza().getTime().getTime()));
            istr.setString(5, todo.getImage());
            istr.setString(6, todo.getPosizione());
            istr.setString(7, todo.getColoresfondo());
            istr.setString(8, todo.getStato().name()); // Enum: COMPLETATO/NONCOMPLETATO
            istr.setInt(9, todo.getTodoId());

            istr.executeUpdate();
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
        String sql = "SELECT t.*, u.username FROM todo t " +
                "JOIN utente u ON t.autore_username = u.username " +
                "WHERE t.bacheca_id = ?";
        List<ToDo> todos = new ArrayList<>();

        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bachecaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                GregorianCalendar data = new GregorianCalendar();
                if (rs.getDate("datascadenza") != null) {
                    data.setTime(rs.getDate("datascadenza"));
                }

                ToDo todo = new ToDo(
                        rs.getString("titolo"),
                        rs.getString("descrizione"),
                        rs.getString("url"),
                        new SimpleDateFormat("dd/MM/yyyy").format(data.getTime()),
                        rs.getString("image"),
                        rs.getString("posizione"),
                        rs.getString("coloresfondo"),
                        new ArrayList<>(),
                        new Utente(rs.getString("username"), "")
                );
                todo.setTodoId(rs.getInt("id"));
                todos.add(todo);
            }
        }
        return todos;
    }

}
