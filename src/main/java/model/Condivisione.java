package model;

import java.util.*;

public class Condivisione {
    private String autore;
    private ArrayList<Utente> partecipanti;

    /*
    //gestisco la relazione con utente
    private Utente utente;
    public Condivisione (Utente u) {
        utente = u;
    }

    //gestisco la relazione con todo
    private ToDo todo;
    private Condivisione (ToDo t) {
        todo=t;
    }*/

    //costruttore
    public Condivisione(String autore) {
        this.autore = autore;
        this.partecipanti = new ArrayList<>();
    }

    //funzioni future
    public String getAutore() {
        return autore;
    }

    public void setAutore(String autore) {
        this.autore = autore;
    }

    public ArrayList<Utente> getPartecipanti() {
        return partecipanti;
    }

    public void setPartecipanti(ArrayList<Utente> partecipanti) {
        this.partecipanti = partecipanti;
    }

    public void AggiungiCondivisione(){

    }
    public void EliminaCondivisione(){

    }
}
