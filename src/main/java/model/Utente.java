package model;

import interfaces.InterfacciaUtente;

import java.util.*;


/**
 * Classe del model Utente, serve ad identificare ogni Utente, tramite username e password
 * Un utente può avere più bacheche, gestisco quindi implementando una List di bacheche
 */
public class Utente implements InterfacciaUtente {
    private final String username;
    private String password;


    //serve a gestire la relazione 3..* con Bacheca
    private List<Bacheca> bacheca;

    /**
     * Costruttore per creare un nuovo Utente
     */
    public Utente(String username, String password){
        this.username = username;
        this.password = password;
        this.bacheca = new ArrayList<>();
    }

    /**
     * Funzioni generali per gestione attributi
     */
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
