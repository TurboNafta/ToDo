package model;

import java.util.*;

public class Bacheca {
    private String descrizione;
    //gestisco enumerazione
    private TitoloBacheca titolo;
    //gestisco todo
    private ArrayList <ToDo> todo;
    //gestisco la relazione con utente (1)
    private Utente utente;


   /* public Bacheca(Utente u) {
        utente= u;
    }*/

    //costruttore
    public Bacheca(TitoloBacheca titolo, String descrizione, Utente utente) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.utente = utente;
        this.todo= new ArrayList<ToDo>();
    }

    public void aggiungiToDo(ToDo t){
        todo.add(t);
    }

    public void eliminaToDo(ToDo t){ todo.remove(t); }

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
    public void CreaToDo(ToDo todo){

    }
    public void ModificaToDo(ToDo todo){

    }
    public void SpostaToDo(ToDo todo){

    }
    public void OrdinaTodo(ToDo todo){

    }
    public void ToDoInScadenza(ToDo todo){

    }
    public void RicercaToDo(ToDo todo){

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
}