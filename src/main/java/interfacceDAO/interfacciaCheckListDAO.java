package interfacceDAO;

import model.CheckList;
import model.ToDo;

import java.sql.SQLException;

public interface interfacciaCheckListDAO {
    void crea(int todoId) throws SQLException;
    void elimina(int todoId) throws SQLException;
    int inserisciChecklist(int todoId, CheckList checklist) throws SQLException;
    int getChecklistIdByToDoId(int todoId) throws SQLException;
    CheckList getChecklistByToDoId(int todoId, ToDo todo) throws SQLException;
     void aggiornaChecklistEAttivita(ToDo todo) throws SQLException;
}