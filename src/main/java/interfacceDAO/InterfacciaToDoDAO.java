package interfacceDAO;

import model.ToDo;
import model.Utente;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.List;


public interface InterfacciaToDoDAO {
    int inserisci(ToDo todo, String username, int bachecaId) throws SQLException;
    void modifica(ToDo todo) throws SQLException;
    void elimina(int id) throws SQLException;
    List<ToDo> getToDoByBacheca(int bachecaId) throws SQLException;
    void aggiornaBachecaToDo(int todoId, int nuovaBachecaId, String nuovaPosizione) throws SQLException;
    List<ToDo> getToDoCondivisiConUtente(String username) throws SQLException;
    ToDo costruisciToDoBase(ResultSet rs, int bachecaId) throws SQLException;
    List<ToDo> getToDoByBachecaAndUtente(int bachecaId, String username) throws SQLException;
}