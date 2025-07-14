package model;

/**
 * Classe del model condivisione, questa contiene il to do e l'utente
 */
public class Condivisione {
    private ToDo todo;
    private Utente utente;

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
}
