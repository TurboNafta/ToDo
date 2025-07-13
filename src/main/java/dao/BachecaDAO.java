package dao;

import model.Bacheca;
import model.TitoloBacheca;
import model.Utente;
import database.ConnessioneDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BachecaDAO {

    public void inserisci(Bacheca bacheca) throws SQLException {
        String sql = "INSERT INTO bacheca (descrizione, titolo, utente_username) VALUES (?, ?, ?)";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, bacheca.getDescrizione());
            stmt.setString(2, bacheca.getTitolo().name());
            stmt.setString(3, bacheca.getUtente().getUsername());
            stmt.executeUpdate();
        }
    }

    public List<Bacheca> getBachecheByUtente(String username) throws SQLException {
        String sql = "SELECT * FROM bacheca WHERE utente_username = ?";
        List<Bacheca> bacheche = new ArrayList<>();
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                TitoloBacheca titolo = TitoloBacheca.valueOf(rs.getString("titolo"));
                String descrizione = rs.getString("descrizione");
                Utente utente = new Utente(username, ""); // Password non necessaria qui
                bacheche.add(new Bacheca(titolo, descrizione, utente));
            }
        }
        return bacheche;
    }

    public void elimina(int id) throws SQLException {
        String sql = "DELETE FROM bacheca WHERE id = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}

