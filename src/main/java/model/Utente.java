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
    private ArrayList<Bacheca> bacheca;

    //costruttore
    public Utente(String username, String password){
        this.username = username;
        this.password = password;
        this.bacheca = new ArrayList<>();
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<Bacheca> getBacheca() {
        return bacheca;
    }

    public void setBacheca(ArrayList<Bacheca> bacheca) {
        this.bacheca = bacheca;
    }

    public String getUsername() {
        return username;
    }

    // INTERFACCIA UTENTE
    public void CreaBacheca(Bacheca b) {
         bacheca.add(b);
         //aggiungo b
    }
    public void eliminaBacheca(Bacheca b){
        bacheca.remove(b);
    }

}
