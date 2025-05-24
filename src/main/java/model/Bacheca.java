package model;

import java.util.*;

public class Bacheca {
    private String descrizione;
    //gestisco enumerazione
    private TitoloBacheca titolo;
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

    public void aggiungiToDo(ToDo t){
        todo.add(t);
    }

    public ArrayList<ToDo> getListaToDo(){
        return todo;
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

}