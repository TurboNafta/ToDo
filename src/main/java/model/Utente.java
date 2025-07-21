package model;

import interfaces.InterfacciaUtente;

import java.util.*;


/**
 * Classe del model Utente, serve a identificare ogni Utente, tramite username e password
 * Un utente può avere più bacheche, gestisco quindi implementando una List di bacheche
 */
public class Utente implements InterfacciaUtente {
    private final String username;
    private final String password;

    //serve a gestire la relazione ..* con Bacheca
    private List<Bacheca> bacheca;
    private int id;

    /**
     * Costruttore per DB
     * @param username nome utente
     * @param password password utente
     * @param id identificativo utente
     */
    public Utente(String username, String password, int id){
        this.username = username;
        this.password = password;
        this.bacheca = new ArrayList<>();
        this.id = id;
    }

    /**
     * Costruttore per creare un nuovo Utente
     * @param username nome utente
     * @param password password utente
     */
    public Utente(String username, String password){
        this.username = username;
        this.password = password;
        this.bacheca = new ArrayList<>();
    }

    /**
     * Restituisce la password dell'utente
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Restituisce la lista delle bacheche associate all'utente.
     * @return lista di bacheche
     */
    public List<Bacheca> getBacheca() {
        return bacheca;
    }

    /**
     * Imposta la lista delle bacheche dell'utente.
     * @param bacheca lista di bacheche
     */
    public void setBacheca(List<Bacheca> bacheca) {
        this.bacheca = bacheca;
    }

    /**
     * Restituisce lo username dell'utente.
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Aggiunge una bacheca alla lista dell'utente.
     * @param b bacheca da aggiungere
     */
    @Override
    public void aggiungiBacheca(Bacheca b) {
         bacheca.add(b);
    }

    /**
     * Aggiunge una bacheca alla lista dell'utente.
     * @param b Bacheca da aggiungere
     */
    @Override
    public void eliminaBacheca(Bacheca b){
        bacheca.remove(b);
    }

    /**
     * Restituisce l'id dell'utente.
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Restituisce l'id dell'utente.
     * @return id
     */
    public void setId(int id) {
        this.id = id;
    }
}
