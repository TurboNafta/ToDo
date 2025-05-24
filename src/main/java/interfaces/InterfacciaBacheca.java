
package interfaces;

import model.Bacheca;
import model.Utente;
import java.util.List;

public interface InterfacciaBacheca {
    List<Bacheca> getAllBacheche();
    List<Bacheca> getBachecheByUtente(Utente utente);
    void addBacheca(Bacheca bacheca, Utente utente);
    // Eventuali metodi per update/remove se servono
}