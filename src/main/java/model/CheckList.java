package model;

import interfaces.InterfacciaCheckList;

import java.util.*;

public class CheckList implements InterfacciaCheckList {

    //gestisco relazione con to do
    private ToDo todo;

    //gestisco aggregazione con attivita
    private List<Attivita> attivita;
    public CheckList( ToDo t){
        this.todo = t;
        this.attivita=new ArrayList<>();
    }

    //funzioni
    @Override
    public List<Attivita> getAttivita() {
        return attivita;
    }

    @Override
    public void setAttivita(List<Attivita> attivita) {
        this.attivita = attivita;
    }

    public ToDo getTodo() {
        return todo;
    }

    public void setTodo(ToDo todo) {
        this.todo = todo;
    }

    //VERIFICA SE TUTTE LE ATTIVITA' SONO COMPLETATE
    public boolean tutteCompletate(){
        if(attivita.isEmpty()){
            return false;
        }
        for(Attivita a : attivita){
            if(a.getStato() != StatoAttivita.COMPLETATA){
                return false;
            }
        }
        return true;
    }
}
