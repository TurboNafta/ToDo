package interfacceDAO;

import model.Utente;

import java.sql.SQLException;
import java.util.List;

/**
 * Interfaccia DAO per la gestione della tabella di condivisione dei To Do tra utenti.
 */
public interface interfacciaCondivisioneDAO {
    /**
     * Crea una nuova condivisione tra un To Do e un utente.
     * @param todoId id del To Do
     * @param utenteUsername username dell'utente
     */
    void creaCondivisione(int todoId, String utenteUsername);

    /**
     * Elimina la condivisione di un To Do con un utente.
     * @param todoId id del To Do
     * @param utenteUsername username dell'utente
     */
    void eliminaCondivisione(int todoId, String utenteUsername);

    /**
     * Inserisce i possessori di un To Do nella tabella di condivisione.
     * @param todoId id del To Do
     * @param possessori lista di utenti possessori
     * @throws SQLException se avvengono errori SQL
     */
    void inserisciPossessori(int todoId, List<Utente> possessori) throws SQLException;

    /**
     * Restituisce la lista di utenti con cui un To Do è condiviso.
     * @param todoId id del To Do
     * @return lista di utenti
     * @throws SQLException se avvengono errori SQL
     */
    List<Utente> getUtentiCondivisiByToDoId(int todoId) throws SQLException;
}