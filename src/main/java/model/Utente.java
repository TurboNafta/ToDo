package model;

import interfaces.InterfacciaUtente;

import java.util.*;


/**
 * The type Utente.
 */
public class Utente implements InterfacciaUtente {
    private final String username;
    private String password;


    //serve a gestire la relazione 3..* con Bacheca
    private List<Bacheca> bacheca;

    //costruttore
    public Utente(String username, String password){
        this.username = username;
        this.password = password;
        this.bacheca = new ArrayList<>();
    }


    public String getPassword() {
        return password;
    }

    public List<Bacheca> getBacheca() {
        return bacheca;
    }

    public void setBacheca(List<Bacheca> bacheca) {
        this.bacheca = bacheca;
    }

    public String getUsername() {
        return username;
    }

    // INTERFACCIA UTENTE
    @Override
    public void aggiungiBacheca(Bacheca b) {
         bacheca.add(b);
    }

    @Override
    public void eliminaBacheca(Bacheca b){
        bacheca.remove(b);
    }
}
