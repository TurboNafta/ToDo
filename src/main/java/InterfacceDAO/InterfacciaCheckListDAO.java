package InterfacceDAO;

import java.sql.SQLException;

public interface InterfacciaCheckListDAO {
    int inserisci(int todoId) throws SQLException;
    void elimina(int todoId) throws SQLException;
}