package model;

import java.util.*;

public class Condivisione {
    private String autore;
    private List<Utente> partecipanti;

    //gestisco la relazione con utente
    private Utente utente;
    public Condivisione (Utente u) {
        utente = u;
    }

    //gestisco la relazione con todo
    private ToDo todo;
    private Condivisione (ToDo t) {
        todo=t;
    }

    //costruttore
    public Condivisione(String autore, ArrayList<Utente> partecipanti, Utente utente, ToDo todo) {
        this.autore = autore;
        this.partecipanti = new ArrayList<>();
        this.todo = todo;
    }

    //funzioni future
    public String getAutore() {
        return autore;
    }

    public void setAutore(String autore) {
        this.autore = autore;
    }

    public List<Utente> getPartecipanti() {
        return partecipanti;
    }

    public void setPartecipanti(List<Utente> partecipanti) {
        this.partecipanti = partecipanti;
    }

    public void AggiungiCondivisione(){

    }
    public void EliminaCondivisione(){

    }
}
