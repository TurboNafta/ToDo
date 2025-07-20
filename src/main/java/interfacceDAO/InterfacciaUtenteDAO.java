package interfacceDAO;

import model.Utente;

import java.sql.SQLException;

public interface InterfacciaUtenteDAO {
    void inserisci(Utente utente) throws SQLException;
    Utente getUtente(String username) throws SQLException;
    void aggiornaPassword(String username, String nuovaPassword) throws SQLException;
    void elimina(String username) throws SQLException;
    Utente login (String username, String password) throws SQLException;
    Utente getUtenteByUsername(String username) throws SQLException;
}
