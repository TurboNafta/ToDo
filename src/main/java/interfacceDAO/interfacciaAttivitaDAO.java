package interfacceDAO;

import model.Attivita;
import model.StatoAttivita;

import java.sql.SQLException;
import java.util.List;

/**
 * Interfaccia DAO per la gestione delle attività delle checklist.
 * Definisce le operazioni CRUD e di ricerca sulle attività.
 */
public interface interfacciaAttivitaDAO {
     /**
      * Inserisce una nuova attività associata a una checklist.
      * @param checklist_id id della checklist
      * @param titolo titolo dell'attività
      * @param stato stato dell'attività
      * @throws SQLException se avvengono errori SQL
      */
     void inserisci(int checklist_id, String titolo, StatoAttivita stato) throws SQLException;

     /**
      * Elimina tutte le attività associate a un To Do.
      * @param todoId id del To Do
      * @throws SQLException se avvengono errori SQL
      */
     void elimina(int todoId) throws SQLException;

     /**
      * Elimina una singola attività dato il suo id.
      * @param attivitaId id dell'attività
      * @throws SQLException se avvengono errori SQL
      */
     void eliminaAttivitaById(int attivitaId) throws SQLException;

     /**
      * Elimina tutte le attività di una checklist.
      * @param checklistId id della checklist
      * @throws SQLException se avvengono errori SQL
      */
     void eliminaByChecklist(int checklistId) throws SQLException;

     /**
      * Restituisce la lista di attività associate a una checklist.
      * @param checklistId id della checklist
      * @return lista di attività
      * @throws SQLException se avvengono errori SQL
      */
     List<Attivita> getAttivitaByChecklistId(int checklistId) throws SQLException;
}
