package InterfacceDAO;

import java.sql.SQLException;

public interface InterfacciaCheckListDAO {
    public void crea(int todoId) throws SQLException;
    public void elimina(int todoId) throws SQLException;
}