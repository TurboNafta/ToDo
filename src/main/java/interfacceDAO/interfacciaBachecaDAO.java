package interfacceDAO;

import model.Bacheca;

import java.sql.SQLException;
import java.util.List;

/**
 * Interfaccia DAO per la gestione delle bacheche.
 * Definisce le operazioni CRUD e di ricerca sulle bacheche.
 */
public interface interfacciaBachecaDAO {
    /**
     * Inserisce una nuova bacheca nel database.
     * @param bacheca bacheca da inserire
     * @return id generato per la bacheca
     * @throws SQLException se avvengono errori SQL
     */
    int inserisci(Bacheca bacheca) throws SQLException;

    /**
     * Elimina una bacheca (e i To Do associati) dal database.
     * @param id id della bacheca da eliminare
     * @throws SQLException se avvengono errori SQL
     */
    void elimina(int id) throws SQLException;

    /**
     * Modifica la descrizione di una bacheca.
     * @param bacheca bacheca da modificare
     * @throws SQLException se avvengono errori SQL
     */
    void modifica(Bacheca bacheca) throws SQLException;

    /**
     * Restituisce tutte le bacheche associate a un utente.
     * @param username nome utente
     * @return lista di bacheche
     * @throws SQLException se avvengono errori SQL
     */
    List<Bacheca> getBachecheByUtente(String username) throws SQLException;

    /**
     * Verifica se esiste già una bacheca con stesso utente, titolo e descrizione.
     * @param username nome utente
     * @param titolo titolo bacheca
     * @param descrizione descrizione bacheca
     * @return true se la bacheca esiste, false altrimenti
     * @throws SQLException se avvengono errori SQL
     */
    boolean esisteBacheca(String username, String titolo, String descrizione) throws SQLException;

    /**
     * Restituisce la bacheca dato il suo id.
     * @param id id della bacheca
     * @return oggetto Bacheca oppure null se non trovata
     * @throws SQLException se avvengono errori SQL
     */
    Bacheca getBachecaById(int id) throws SQLException;
}

