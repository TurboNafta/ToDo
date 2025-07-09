package model;

import java.util.*;

public class CheckList {
    //gestisco relazioen con todo
    private ToDo todo;

    //gestisco aggregazione con attivita
    private ArrayList<Attivita> attivita;
    public CheckList( ToDo t){
        this.todo = t;
        this.attivita=new ArrayList<>();
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
}
