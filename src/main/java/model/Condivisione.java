package model;

import java.util.List;

/**
 * Classe del model condivisione, questa contiene il to do e l'utente
 */
public class Condivisione {
    private ToDo todo;
    private Utente utente;

    private int todoId;
    private List<Integer> utentiId;
    private int id;

    /**
     * Costruttore per DB
     */
    public Condivisione(ToDo todo, Utente utente, int id, int todoId, List<Integer> utentiId){
        this.todo = todo;
        this.utente = utente;
        this.todoId = todoId;
        this.utentiId = utentiId;
        this.id = id;
    }

    /**
     * Costruttore per creare una nuova condivisione
     */
    public Condivisione(ToDo todo, Utente utente){
        this.todo = todo;
        this.utente = utente;
    }

    /**
     * Funzioni generali per la gestione degli attributi
     */
    public ToDo getTodo() {
        return todo;
    }
    public void setTodo(ToDo todo) {
        this.todo = todo;
    }
    public Utente getUtente() {
        return utente;
    }
    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    public int getTodoId() {
        return todoId;
    }

    public void setTodoId(int todoId) {
        this.todoId = todoId;
    }

    public List<Integer> getUtentiId() {
        return utentiId;
    }

    public void setUtentiId(List<Integer> utentiId) {
        this.utentiId = utentiId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
