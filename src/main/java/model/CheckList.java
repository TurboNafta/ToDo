package model;

import interfaces.InterfacciaCheckList;
import java.util.*;

/**
 * Classe del model CheckList, ha come attributi il to do, e una lista di attività da completare per terminare il to do
 */
public class CheckList implements InterfacciaCheckList {
    //gestisco relazione con to do
    private ToDo todo;

    //gestisco aggregazione con attivita
    private List<Attivita> attivita;

    private int id;

    /**
     * Costruttore con id per DB
     */
    public CheckList(ToDo t, int id){
        this.todo = t;
        this.attivita=new ArrayList<>();
        this.id = id;
    }

    /**
     * Costruttore per creare una nuova CheckList
     */
    public CheckList( ToDo t){
        this.todo = t;
        this.attivita=new ArrayList<>();
    }


    /**
     * Funzioni generali per la gestione degli attributi
     */
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

    /**
     * Funzione che scorre le attività della List e vede se tutte sono completate
     */
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
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
}
