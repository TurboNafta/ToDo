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
     * Costruttore per oggetti provenienti dal database.
     * @param todo To Do condiviso
     * @param utente Utente con cui è condiviso
     * @param id identificativo della condivisione
     * @param todoId identificativo del To Do
     * @param utentiId lista degli identificativi degli utenti coinvolti
     */
    public Condivisione(ToDo todo, Utente utente, int id, int todoId, List<Integer> utentiId){
        this.todo = todo;
        this.utente = utente;
        this.todoId = todoId;
        this.utentiId = utentiId;
        this.id = id;
    }

    /**
     * Costruttore per creare una nuova condivisione.
     * @param todo To Do da condividere
     * @param utente Utente con cui condividere
     */
    public Condivisione(ToDo todo, Utente utente){
        this.todo = todo;
        this.utente = utente;
    }

    /**
     * Restituisce il To Do condiviso.
     * @return To Do condiviso
     */
    public ToDo getTodo() {
        return todo;
    }

    /**
     * Imposta il To Do condiviso.
     * @param todo nuovo To Do
     */
    public void setTodo(ToDo todo) {
        this.todo = todo;
    }

    /**
     * Restituisce l'utente con cui è condiviso il To Do.
     * @return utente
     */
    public Utente getUtente() {
        return utente;
    }

    /**
     * Imposta l'utente con cui è condiviso il To Do.
     * @param utente nuovo utente
     */
    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    /**
     * Restituisce l'id del To Do.
     * @return id del To Do
     */
    public int getTodoId() {
        return todoId;
    }

    /**
     * Imposta l'id del To Do.
     * @param todoId nuovo id del To Do
     */
    public void setTodoId(int todoId) {
        this.todoId = todoId;
    }

    /**
     * Restituisce la lista degli id degli utenti coinvolti.
     * @return lista degli id utenti
     */
    public List<Integer> getUtentiId() {
        return utentiId;
    }

    /**
     * Imposta la lista degli id degli utenti coinvolti.
     * @param utentiId nuova lista id utenti
     */
    public void setUtentiId(List<Integer> utentiId) {
        this.utentiId = utentiId;
    }

    /**
     * Restituisce l'id della condivisione.
     * @return id della condivisione
     */
    public int getId() {
        return id;
    }

    /**
     * Imposta l'id della condivisione.
     * @param id nuovo id della condivisione
     */
    public void setId(int id) {
        this.id = id;
    }
}
