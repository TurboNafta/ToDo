package InterfacceDAO;

import model.ToDo;
import model.Utente;

import java.sql.SQLException;
import java.sql.Connection;
import java.util.List;


public interface InterfacciaToDoDAO {
    /**
     * Inserisce un nuovo To Do nel database
     * @param todo Il to do da inserire
     * @param username Username dell'utente proprietario
     * @param bachecaId ID della bacheca a cui appartiene il To Do
     * @return ID del To Do inserito
     * @throws SQLException in caso di errori di accesso al database
     */
    int inserisci(ToDo todo, String username, int bachecaId) throws SQLException;

    /**
     * Modifica un To Do esistente
     * @param todo Il to do da modificare
     * @throws SQLException in caso di errori di accesso al database
     */
    void modifica(ToDo todo) throws SQLException;

    /**
     * Elimina un To Do dal database
     * @param id ID del To Do da eliminare
     * @throws SQLException in caso di errori di accesso al database
     */
    void elimina(int id) throws SQLException;

    /**
     * Recupera tutti i To Do associati a una bacheca
     * @param bachecaId L'ID della bacheca
     * @return Lista dei To Do appartenenti alla bacheca
     * @throws SQLException in caso di errori di accesso al database
     */
    List<ToDo> getToDoByBacheca(int bachecaId) throws SQLException;

    void inserisciPossessori(int todoId, List<Utente> possessori, Connection conn) throws SQLException;
    List<ToDo> getToDoByBachecaAndUtente(int bachecaId, String username) throws SQLException;
}