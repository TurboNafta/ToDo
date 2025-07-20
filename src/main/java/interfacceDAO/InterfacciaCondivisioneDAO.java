package interfacceDAO;

import model.Utente;

import java.sql.SQLException;
import java.util.List;

public interface InterfacciaCondivisioneDAO {
    void creaCondivisione(int todoId, String utenteUsername);
    void eliminaCondivisione(int todoId, String utenteUsername);
    void inserisciPossessori(int todoId, List<Utente> possessori) throws SQLException;
    List<Utente> getUtentiCondivisiByToDoId(int todoId) throws SQLException;
}