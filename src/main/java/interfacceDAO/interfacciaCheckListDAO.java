package interfacceDAO;

import model.CheckList;
import model.ToDo;

import java.sql.SQLException;

/**
 * Interfaccia DAO per la gestione delle checklist dei To Do.
 * Definisce le operazioni CRUD e di ricerca sulle checklist e sulle attività associate.
 */
public interface interfacciaCheckListDAO {
    /**
     * Crea una checklist per un To Do.
     * @param todoId id del To Do
     * @throws SQLException se avvengono errori SQL
     */
    void crea(int todoId) throws SQLException;

    /**
     * Elimina una checklist e tutte le attività associate dato il To Do.
     * @param todoId id del To Do
     * @throws SQLException se avvengono errori SQL
     */
    void elimina(int todoId) throws SQLException;

    /**
     * Inserisce una nuova checklist e le sue attività associate.
     * @param todoId id del To Do
     * @param checklist oggetto CheckList da inserire
     * @return id generato per la checklist
     * @throws SQLException se avvengono errori SQL
     */
    int inserisciChecklist(int todoId, CheckList checklist) throws SQLException;

    /**
     * Restituisce l'id della checklist associata a un To Do.
     * @param todoId id del To Do
     * @return id della checklist, oppure -1 se non esiste
     * @throws SQLException se avvengono errori SQL
     */
    int getChecklistIdByToDoId(int todoId) throws SQLException;

    /**
     * Restituisce la checklist e le attività associate a un To Do.
     * @param todoId id del To Do
     * @param todo oggetto To Do di riferimento
     * @return oggetto CheckList
     * @throws SQLException se avvengono errori SQL
     */
    CheckList getChecklistByToDoId(int todoId, ToDo todo) throws SQLException;

    /**
     * Aggiorna la checklist e le sue attività associate a un To Do.
     * @param todo oggetto To Do con checklist aggiornata
     * @throws SQLException se avvengono errori SQL
     */
    void aggiornaChecklistEAttivita(ToDo todo) throws SQLException;
}