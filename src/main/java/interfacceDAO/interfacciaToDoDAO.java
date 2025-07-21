package interfacceDAO;

import model.ToDo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Interfaccia DAO per la gestione dei To Do.
 * Definisce le operazioni CRUD e di ricerca sui To Do nel database.
 */
public interface interfacciaToDoDAO {
    /**
     * Inserisce un nuovo To Do nel database.
     * @param todo l'oggetto To Do da inserire
     * @param username username dell'autore
     * @param bachecaId identificativo della bacheca di appartenenza
     * @return id generato per il To Do
     * @throws SQLException se avvengono errori SQL
     */
    int inserisci(ToDo todo, String username, int bachecaId) throws SQLException;

    /**
     * Modifica le informazioni di un To Do già esistente nel database.
     * @param todo To Do da modificare
     * @throws SQLException se avvengono errori SQL
     */
    void modifica(ToDo todo) throws SQLException;

    /**
     * Elimina un To Do dal database.
     * @param id identificativo del To Do da eliminare
     * @throws SQLException se avvengono errori SQL
     */
    void elimina(int id) throws SQLException;

    /**
     * Restituisce la lista dei To Do associati a una bacheca.
     * @param bachecaId id della bacheca
     * @return lista di To Do
     * @throws SQLException se avvengono errori SQL
     */
    List<ToDo> getToDoByBacheca(int bachecaId) throws SQLException;

    /**
     * Aggiorna la bacheca e la posizione di un To Do.
     * @param todoId id del To Do
     * @param nuovaBachecaId nuovo id bacheca
     * @param nuovaPosizione nuova posizione
     * @throws SQLException se avvengono errori SQL
     */
    void aggiornaBachecaToDo(int todoId, int nuovaBachecaId, String nuovaPosizione) throws SQLException;

    /**
     * Restituisce tutti i To Do condivisi con un determinato utente (esclusi quelli di cui è autore).
     * @param username username utente
     * @return lista di To Do condivisi
     * @throws SQLException se avvengono errori SQL
     */
    List<ToDo> getToDoCondivisiConUtente(String username) throws SQLException;

    /**
     * Costruisce un oggetto To Do a partire da un ResultSet.
     * @param rs ResultSet con i dati del To Do
     * @param bachecaId id della bacheca
     * @return oggetto To Do
     * @throws SQLException se avvengono errori SQL
     */
    ToDo costruisciToDoBase(ResultSet rs, int bachecaId) throws SQLException;

    /**
     * Restituisce i To Do di una bacheca sia propri che condivisi con l'utente.
     * @param bachecaId id della bacheca
     * @param username username dell'utente
     * @return lista di To Do
     * @throws SQLException se avvengono errori SQL
     */
    List<ToDo> getToDoByBachecaAndUtente(int bachecaId, String username) throws SQLException;
}