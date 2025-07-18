package InterfacceDAO;

import model.StatoAttivita;

import java.sql.SQLException;

public interface InterfacciaAttivitaDAO {
     void inserisci(int todoId, String titolo, StatoAttivita stato) throws SQLException;
     void elimina(int todoId) throws SQLException;
    void eliminaAttivitaById(int attivitaId) throws SQLException;
}
