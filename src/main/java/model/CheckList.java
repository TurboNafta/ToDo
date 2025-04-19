package model;

import java.util.*;

public class CheckList {
    //gestisco relazioen con todo
    private ToDo todo;
    public CheckList(ToDo t){
        todo = t;
        t.checklist = this;
    }

    //gestisco aggregazione con attivita
    private List<Attivita> attivita;
    public CheckList(ArrayList<Attivita> attivita, ToDo t){
        this.todo = t;
        this.attivita=new ArrayList<>();
    }

    //funzioni
    private void AggiungiAttivita(Attivita attivita){

    }

    private void RimuoviAttivita(Attivita attivita){

    }

    public List<Attivita> getAttivita() {
        return attivita;
    }

    public void setAttivita(List<Attivita> attivita) {
        this.attivita = attivita;
    }

    public ToDo getTodo() {
        return todo;
    }

    public void setTodo(ToDo todo) {
        this.todo = todo;
    }
}
