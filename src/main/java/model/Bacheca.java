package model;

import interfaces.InterfacciaBacheca;

import java.util.*;

/**
 * Classe del model bacheca, come parametri ha la descrizione, il titolo, un utente, e una lista di to do
 */
public class Bacheca implements InterfacciaBacheca {
    private String descrizione;
    //gestisco enumerazione
    private TitoloBacheca titolo;
    //gestisco to do
    private List <ToDo> todo;
    //gestisco la relazione con utente (1)
    private Utente utente;


    /**
     * Costruttore per creare una nuova bacheca
     */
    public Bacheca(TitoloBacheca titolo, String descrizione, Utente utente) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.utente = utente;
        this.todo= new ArrayList<>();
    }

    /**
     * Funzioni generali per la gestione degli attributi
     */
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
    public List<ToDo> getTodo() {
        return todo;
    }
    public void setTodo(List<ToDo> todo) {
        this.todo = todo;
    }
    public Utente getUtente() {
        return utente;
    }
    public void aggiungiToDo(ToDo t){
        todo.add(t);
    }
    public void eliminaToDo(ToDo t){
        todo.remove(t);
    }
}