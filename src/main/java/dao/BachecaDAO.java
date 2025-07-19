package dao;

import InterfacceDAO.InterfacciaBachecaDAO;
import model.Bacheca;
import model.TitoloBacheca;
import model.Utente;
import database.ConnessioneDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BachecaDAO implements InterfacciaBachecaDAO {
    private final UtenteDAO utenteDAO;

    public BachecaDAO() {
        this.utenteDAO = new UtenteDAO();
    }

    @Override
    public int inserisci(Bacheca bacheca) throws SQLException {
        String query = "INSERT INTO bacheca (descrizione, titolo, utente_username) VALUES (?, ?, ?) RETURNING id";

        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, bacheca.getDescrizione());
            stmt.setString(2, bacheca.getTitolo().toString());
            stmt.setString(3, bacheca.getUtente().getUsername());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                bacheca.setId(id); // Imposta l'ID sulla bacheca
                return id;
            }
            throw new SQLException("Impossibile ottenere l'ID della bacheca inserita");
        }
    }


    @Override
    public void modifica(Bacheca bacheca) throws SQLException {
        String query = "UPDATE bacheca SET descrizione = ? WHERE id = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, bacheca.getDescrizione());
            stmt.setInt(2, bacheca.getId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Nessuna bacheca trovata con ID: " + bacheca.getId());
            }
        }
    }

    @Override
    public void elimina(int id) throws SQLException {
        // Prima elimina tutti i To do associati
        String deleteToDosQuery = "DELETE FROM todo WHERE bacheca_id = ?";
        String deleteBachecaQuery = "DELETE FROM bacheca WHERE id = ?";

        Connection conn = null;
        try {
            conn = ConnessioneDatabase.getConnection();
            conn.setAutoCommit(false);  // Inizia la transazione

            // Elimina i To do
            try (PreparedStatement stmt = conn.prepareStatement(deleteToDosQuery)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }

            // Elimina la bacheca
            try (PreparedStatement stmt = conn.prepareStatement(deleteBachecaQuery)) {
                stmt.setInt(1, id);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Nessuna bacheca trovata con ID: " + id);
                }
            }

            conn.commit();  // Completa la transazione
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();  // Annulla la transazione in caso di errore
                } catch (SQLException ex) {
                    throw new SQLException("Errore durante il rollback: " + ex.getMessage(), ex);
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);  // Ripristina l'autocommit
                conn.close();
            }
        }
    }

    @Override
    public List<Bacheca> getBachecheByUtente(String username) throws SQLException {
        List<Bacheca> bacheche = new ArrayList<>();
        String query = "SELECT id, descrizione, titolo FROM bacheca WHERE utente_username = ?";

        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String descrizione = rs.getString("descrizione");
                String titoloStr = rs.getString("titolo");
                TitoloBacheca titolo = TitoloBacheca.valueOf(titoloStr);

                Utente utente = utenteDAO.getUtenteByUsername(username);
                Bacheca bacheca = new Bacheca(id, titolo, descrizione, utente);
                bacheche.add(bacheca);
            }
        }
        return bacheche;
    }


    @Override
    public boolean esisteBacheca(String username, String titolo, String descrizione) throws SQLException {
        String query = "SELECT COUNT(*) FROM bacheca WHERE utente_username = ? AND titolo = ? AND descrizione = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, titolo);
            stmt.setString(3, descrizione);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public Bacheca getBachecaById(int id) throws SQLException {
        String sql = "SELECT id, titolo, descrizione, utente FROM bacheca WHERE id = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                TitoloBacheca titolo = TitoloBacheca.valueOf(rs.getString("titolo"));
                String descrizione = rs.getString("descrizione");
                String username = rs.getString("utente_username");
                Utente utente = new Utente(username, "");
                return new Bacheca(id, titolo, descrizione, utente);
            }
        }
        return null;
    }
}