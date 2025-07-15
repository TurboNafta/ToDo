package InterfacceDAO;

import model.StatoToDo;
import model.ToDo;

import java.sql.SQLException;
import java.util.List;

public interface InterfacciaToDoDAO {
    public void inserisci(ToDo todo, String autoreUsername, int bachecaId) throws SQLException;
    public List<ToDo> getToDoByUtente(String username) throws SQLException;
    public void elimina(int id) throws SQLException;
    public void modifica(ToDo todo) throws SQLException;  // Metodo per la modifica nel database
}
