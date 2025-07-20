package interfacceDAO;

import model.Bacheca;

import java.sql.SQLException;
import java.util.List;

public interface interfacciaBachecaDAO {
    int inserisci(Bacheca bacheca) throws SQLException;
    void elimina(int id) throws SQLException;
    void modifica(Bacheca bacheca) throws SQLException;
    List<Bacheca> getBachecheByUtente(String username) throws SQLException;
    boolean esisteBacheca(String username, String titolo, String descrizione) throws SQLException;
    Bacheca getBachecaById(int id) throws SQLException;
}

