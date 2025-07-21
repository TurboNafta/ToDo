package model;

import interfaces.InterfacciaBacheca;

import java.util.*;

/**
 * Classe del model bacheca, come parametri ha la descrizione, il titolo, un utente, e una lista di to do
 */
public class Bacheca implements InterfacciaBacheca {
    private String descrizione;
    //gestisco enumerazione
    private TitoloBacheca titolo;
    //gestisco to do
    private List <ToDo> todo;
    //gestisco la relazione con utente (1)
    private Utente utente;
    private int id;


    /**
     * Costruttore con id (per oggetti caricati dal DB).
     * @param id id della bacheca
     * @param titolo titolo della bacheca
     * @param descrizione descrizione della bacheca
     * @param utente proprietario della bacheca
     */
    public Bacheca(int id, TitoloBacheca titolo, String descrizione, Utente utente) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.utente = utente;
        this.todo= new ArrayList<>();
        this.id = id;
    }

    /**
     * Costruttore senza id (per nuove bacheche prima di salvarle).
     * @param titolo titolo della bacheca
     * @param descrizione descrizione della bacheca
     * @param utente proprietario della bacheca
     */
    public Bacheca(TitoloBacheca titolo, String descrizione, Utente utente) {
        this(0, titolo, descrizione, utente);
    }

    /**
     * Restituisce la descrizione.
     * @return descrizione
     */
    public String getDescrizione() {
        return descrizione;
    }

    /**
     * Imposta la descrizione.
     * @param descrizione nuova descrizione
     */
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    /**
     * Restituisce il titolo.
     * @return titolo
     */
    public TitoloBacheca getTitolo() {
        return titolo;
    }

    /**
     * Imposta il titolo.
     * @param titolo nuovo titolo
     */
    public void setTitolo(TitoloBacheca titolo) {
        this.titolo = titolo;
    }

    /**
     * Restituisce la lista di ToDo.
     * @return lista di ToDo
     */
    public List<ToDo> getTodo() {
        return todo;
    }

    /**
     * Imposta la lista di ToDo.
     * @param todo nuova lista di ToDo
     */
    public void setTodo(List<ToDo> todo) {
        this.todo = todo;
    }

    /**
     * Restituisce l'utente proprietario.
     * @return utente
     */
    public Utente getUtente() {
        return utente;
    }

    /**
     * Aggiunge un ToDo alla bacheca.
     * @param t ToDo da aggiungere
     */
    public void aggiungiToDo(ToDo t){
        todo.add(t);
    }

    /**
     * Rimuove un ToDo dalla bacheca.
     * @param t ToDo da eliminare
     */
    public void eliminaToDo(ToDo t){
        todo.remove(t);
    }

    /**
     * Restituisce l'id della bacheca.
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Imposta l'id della bacheca.
     * @param id nuovo id
     */
    public void setId(int id) {
        this.id = id;
    }
}