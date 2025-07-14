package InterfacceDAO;

import model.Utente;

import java.sql.SQLException;

public interface InterfacciaUtenteDAO {
    public void inserisci(Utente utente) throws SQLException;
    public Utente getUtente(String username) throws SQLException;
    public void aggiornaPassword(String username, String nuovaPassword) throws SQLException;
    public void elimina(String username) throws SQLException;
}
