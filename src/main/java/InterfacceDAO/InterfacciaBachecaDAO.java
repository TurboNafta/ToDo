package InterfacceDAO;

import model.Bacheca;

import java.sql.SQLException;
import java.util.List;

public interface InterfacciaBachecaDAO {
    public void inserisci(Bacheca bacheca) throws SQLException;
    public List<Bacheca> getBachecheByUtente(String username) throws SQLException;
    public void elimina(int id) throws SQLException;
}
