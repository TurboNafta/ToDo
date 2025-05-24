package manager;

import model.Utente;
import interfaces.InterfacciaUtente;

import java.util.ArrayList;
import java.util.List;

public class UtenteManager implements InterfacciaUtente {
    private List<Utente> utenti;

    public UtenteManager() {
        this.utenti = new ArrayList<>();
    }

    @Override
    public boolean utenteEsiste(String username) {
        for (Utente u : utenti) {
            if (u.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void aggiungiUtente(Utente utente) {
        utenti.add(utente);
    }

    @Override
    public Utente getUtente(String username) {
        for (Utente u : utenti) {
            if (u.getUsername().equals(username)) {
                return u;
            }
        }
        return null;
    }
}

