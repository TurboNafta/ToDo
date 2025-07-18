package InterfacceDAO;

import model.StatoAttivita;

import java.sql.SQLException;

public interface InterfacciaAttivitaDAO {
    public void inserisci(int todoId, String titolo, StatoAttivita stato) throws SQLException;
    public void elimina(int todoId) throws SQLException;
}
