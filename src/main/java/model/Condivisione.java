package model;

public class Condivisione {
    private ToDo todo;
    private Utente utente;

    public Condivisione(ToDo todo, Utente utente){
        this.todo = todo;
        this.utente = utente;
    }

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
