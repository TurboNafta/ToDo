package InterfacceDAO;

import model.Attivita;
import model.StatoAttivita;

import java.sql.SQLException;
import java.util.List;

public interface InterfacciaAttivitaDAO {
    void inserisci(int todoId, String titolo, StatoAttivita stato) throws SQLException;
    void elimina(int todoId) throws SQLException;
    void aggiornaStatoAttivita(int attivitaId, StatoAttivita nuovoStato) throws SQLException;
    List<Attivita> getAttivitaByChecklistId(int checklistId) throws SQLException;
}
