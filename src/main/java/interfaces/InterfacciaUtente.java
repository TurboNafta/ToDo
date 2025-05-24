package interfaces;

import model.Utente;

public interface InterfacciaUtente {
    boolean utenteEsiste(String username);
    void aggiungiUtente(Utente u);
    Utente getUtente(String username);
}