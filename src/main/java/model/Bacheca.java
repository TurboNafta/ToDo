package model;

import java.util.*;

public class Bacheca {
    private String descrizione;
    //gestisco enumerazione
    private TitoloBacheca titolo = TitoloBacheca.UNIVERSITA;
    //gestisco todo
    private ArrayList <ToDo> todo;
    //gestisco la relazione con utente (1)
    private ArrayList<Utente> utente;


   /* public Bacheca(Utente u) {
        utente= u;
    }*/

    //costruttore
    public Bacheca(TitoloBacheca titolo, String descrizione){
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.utente = new ArrayList<Utente>();
        this.todo= new ArrayList<ToDo>();
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public TitoloBacheca getTitolo() {
        return titolo;
    }

    public void setTitolo(TitoloBacheca titolo) {
        this.titolo = titolo;
    }
}