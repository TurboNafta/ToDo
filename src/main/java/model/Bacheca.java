package model;

import interfaces.InterfacciaBacheca;

import java.util.*;

public class Bacheca implements InterfacciaBacheca {
    private String descrizione;
    //gestisco enumerazione
    private TitoloBacheca titolo;
    //gestisco todo
    private ArrayList <ToDo> todo;
    //gestisco la relazione con utente (1)
    private Utente utente;


    //costruttore
    public Bacheca(TitoloBacheca titolo, String descrizione, Utente utente) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.utente = utente;
        this.todo= new ArrayList<ToDo>();
    }

    //GETTER E SETTER
    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public void modificaDescrizione(String nuovaDescrizione){
        this.descrizione = nuovaDescrizione;
    }

    public TitoloBacheca getTitolo() {
        return titolo;
    }

    public void setTitolo(TitoloBacheca titolo) {
        this.titolo = titolo;
    }


    public ArrayList<ToDo> getTodo() {
        return todo;
    }

    public void setTodo(ArrayList<ToDo> todo) {
        this.todo = todo;
    }

    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    //INTERFACCIA BACHECA
    public void aggiungiToDo(ToDo t){
        todo.add(t);
    }

    //public void modificaDescrizione(Bacheca b, String nuovaDescrizione){
    //b.modificaDescrizione(nuovaDescrizione);
    //}

    public void eliminaToDo(ToDo t){ todo.remove(t); }


}