package interfacceDAO;

import model.Attivita;
import model.StatoAttivita;

import java.sql.SQLException;
import java.util.List;

public interface interfacciaAttivitaDAO {
     void inserisci(int checklist_id, String titolo, StatoAttivita stato) throws SQLException;
     void elimina(int todoId) throws SQLException;
     void eliminaAttivitaById(int attivitaId) throws SQLException;
     void eliminaByChecklist(int checklistId) throws SQLException;
     List<Attivita> getAttivitaByChecklistId(int checklistId) throws SQLException;

}
