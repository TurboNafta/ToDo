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
     * Costruttore con id per oggetti provenienti dal DB.
     * @param t To Do di riferimento
     * @param id identificativo checklist
     */
    public CheckList(ToDo t, int id){
        this.todo = t;
        this.attivita=new ArrayList<>();
        this.id = id;
    }

    /**
     * Costruttore per creare una nuova CheckList.
     * @param t To Do di riferimento
     */
    public CheckList( ToDo t){
        this.todo = t;
        this.attivita=new ArrayList<>();
    }


    /**
     * Restituisce la lista delle attività nella checklist.
     * @return lista attività
     */
    @Override
    public List<Attivita> getAttivita() {
        return attivita;
    }

    /**
     * Imposta la lista delle attività.
     * @param attivita nuova lista attività
     */
    @Override
    public void setAttivita(List<Attivita> attivita) {
        this.attivita = attivita;
    }

    /**
     * Restituisce il To Do associato.
     * @return To Do
     */
    public ToDo getTodo() {
        return todo;
    }

    /**
     * Imposta il ToDo associato.
     * @param todo nuovo ToDo
     */
    public void setTodo(ToDo todo) {
        this.todo = todo;
    }

    /**
     * Verifica se tutte le attività della checklist sono completate.
     * @return true se tutte completate, false altrimenti
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

    /**
     * Restituisce l'id della checklist.
     * @return id checklist
     */
    public int getId() {
        return id;
    }

    /**
     * Imposta l'id della checklist.
     * @param id nuovo id
     */
    public void setId(int id) {
        this.id = id;
    }
}
