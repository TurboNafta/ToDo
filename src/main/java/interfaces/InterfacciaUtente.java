package interfaces;

import model.Utente;

public interface InterfacciaUtente {
    boolean registraUtente(String username, String password);
    Utente login(String username, String password);
    // Eventuale altro metodo (es: logout, recuperaUtenti, ecc.)
}