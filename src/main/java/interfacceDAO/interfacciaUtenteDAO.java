package interfacceDAO;

import model.Utente;

import java.sql.SQLException;

/**
 * Interfaccia DAO per la gestione degli utenti.
 * Definisce le operazioni CRUD e di autenticazione per gli utenti.
 */
public interface interfacciaUtenteDAO {
    /**
     * Inserisce un nuovo utente nel database.
     * @param utente utente da inserire
     * @throws SQLException se avvengono errori SQL
     */
    void inserisci(Utente utente) throws SQLException;

    /**
     * Restituisce l'utente dato l'username.
     * @param username nome utente
     * @return oggetto Utente oppure null se non trovato
     * @throws SQLException se avvengono errori SQL
     */
    Utente getUtente(String username) throws SQLException;

    /**
     * Elimina un utente dal database.
     * @param username nome utente da eliminare
     * @throws SQLException se avvengono errori SQL
     */
    void elimina(String username) throws SQLException;

    /**
     * Effettua il login dell'utente controllando username e password.
     * @param username nome utente
     * @param password password utente
     * @return l'utente se credenziali corrette, altrimenti null
     * @throws SQLException se avvengono errori SQL
     */
    Utente login (String username, String password) throws SQLException;

    /**
     * Restituisce un utente dato l'username (alias di getUtente).
     * @param username nome utente
     * @return oggetto Utente oppure null se non trovato
     * @throws SQLException se avvengono errori SQL
     */
    Utente getUtenteByUsernameDAO(String username) throws SQLException;
}
