package model;

import java.util.*;

public class CheckList {
    private boolean completata;

    //gestisco relazione con todo
    private ToDo todo;

    //gestisco aggregazione con attivita
    private ArrayList<Attivita> attivita;
    public CheckList( ToDo t, boolean completata){
        this.todo = t;
        this.attivita=new ArrayList<>();
        this.completata = completata;
    }

    //funzioni
    private void AggiungiAttivita(Attivita a){
        if(!attivita.contains(a)){
            attivita.add(a);
        }
    }

    private void RimuoviAttivita(Attivita a){
        attivita.remove(a);
    }

    public ArrayList<Attivita> getAttivita() {
        return attivita;
    }

    public void setAttivita(ArrayList<Attivita> attivita) {
        this.attivita = attivita;
    }

    public ToDo getTodo() {
        return todo;
    }

    public void setTodo(ToDo todo) {
        this.todo = todo;
    }

    public boolean isCompletata() {
        return completata;
    }
    public void setCompletata(boolean completata) {
        this.completata = completata;
    }
}
