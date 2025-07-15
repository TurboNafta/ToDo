package dao;

import InterfacceDAO.InterfacciaUtenteDAO;
import model.Utente;
import database.ConnessioneDatabase;

import java.sql.*;
import java.util.*;

public class UtenteDAO implements InterfacciaUtenteDAO {
    @Override
    public void inserisci(Utente utente) throws SQLException {
        String sql = "INSERT INTO utente (username, password) VALUES (?, ?)";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, utente.getUsername());
            stmt.setString(2, utente.getPassword());
            stmt.executeUpdate();
        }
    }

    @Override
    public Utente getUtente(String username) throws SQLException {
        String sql = "SELECT * FROM utente WHERE username = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Utente(rs.getString("username"), rs.getString("password"));
            }
        }
        return null;
    }

    @Override
    public void aggiornaPassword(String username, String nuovaPassword) throws SQLException {
        String sql = "UPDATE utente SET password = ? WHERE username = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nuovaPassword);
            stmt.setString(2, username);
            stmt.executeUpdate();
        }
    }

    @Override
    public void elimina(String username) throws SQLException {
        String sql = "DELETE FROM utente WHERE username = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        }
    }

    public Utente login (String username, String password) throws SQLException {
        String sql = "SELECT * FROM utente WHERE username = ? AND password = ?";
        try(Connection conn = ConnessioneDatabase.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return new Utente(rs.getString("username"), rs.getString("password"));
            }
        }
        return null;
    }

    public Utente getUtenteByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM utente WHERE username = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // crea e ritorna l’oggetto Utente con i dati dal result set
                return new Utente(rs.getString("username"), rs.getString("password"));
            }
            return null; // utente non trovato
        }
    }
}
