package model;

import java.lang.reflect.Array;
import java.util.*;

/**
 * The type Utente.
 */
public class Utente {
    private final String login;
    private String password;


    //serve a gestire la relazione 3..* con Bacheca
    private ArrayList<Bacheca> bacheca;


    //in questo modo gestisco la relazione * con Condivisione
    private ArrayList<Condivisione> condivisione;

    //costruttore
    public Utente(String login, String password){
        this.login = login;
        this.password = password;
        this.condivisione = new ArrayList<>();
        this.bacheca = new ArrayList<>();
    }


    //funzioni future
    public void CreaBacheca(Bacheca bacheca) {
    }

    public void ModificaBacheca(Bacheca bacheca) {
    }

    public void EliminaBacheca(Bacheca bacheca) {

    }

    public void LeggereUtenti(ToDo todo){

    }

    public void ModificaSfondo(ToDo todo){

    }

    public void RicercaToDo(ToDo todo){

    }
    public void CreaToDo(ToDo todo){

    }
    public void ModificaToDo(ToDo todo){

    }
    public void SpostaToDo(ToDo todo){

    }
    public void OrdinaTodo(ToDo todo){

    }
    public void ToDoInScadenza(ToDo todo){

    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<Condivisione> getCondivisione() {
        return condivisione;
    }

    public void setCondivisione(ArrayList<Condivisione> condivisione) {
        this.condivisione = condivisione;
    }

    public ArrayList<Bacheca> getBacheca() {
        return bacheca;
    }

    public void setBacheca(ArrayList<Bacheca> bacheca) {
        this.bacheca = bacheca;
    }


    public String getLogin() {
        return login;
    }
}
