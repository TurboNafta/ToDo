package controller;

import model.*;
import interfaces.InterfacciaBacheca;
import interfaces.InterfacciaToDo;
import interfaces.InterfacciaUtente;
import manager.BachecaManager;
import manager.ToDoManager;
import manager.UtenteManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Controller {
    private InterfacciaBacheca bachecaManager;
    private InterfacciaToDo toDoManager;
    private InterfacciaUtente utenteManager;
    private Utente utenteLoggato; // Utente attualmente autenticato
    private ArrayList<Utente> listaUtenti;


    public Controller() {
        this.bachecaManager = new BachecaManager();
        this.toDoManager = new ToDoManager();
        this.utenteManager = new UtenteManager();
        this.utenteLoggato = null;
    }
    //GESTIONE UTENTE
    // cerco utente nella lista di utenti
    public Utente getUtente(String username) {
        for (Utente u: listaUtenti) {
            if (u.getUsername().equals(username)) {
                return u;
            }
        }
        return null;// se non va nel if restituisce null
    }
    // GESTIONE BACHECHE

    public ArrayList<Bacheca> getBachecaList(String username, String titolo) {
        Utente utente = getUtente(username);
        ArrayList<Bacheca> bachecheUtente=new ArrayList<>();
        if (utente == null){
          return bachecheUtente;// da gestire con eccezione pls nn dimenticarti :*
        } else{
           TitoloBacheca titoloBacheca=stringToTitoloBacheca(titolo);
            for (Bacheca b: utente.getBacheca()){
                if(b.getTitolo().equals(titolo)){
                    bachecheUtente.add(b);
                }else if(titolo==null){
                    return utente.getBacheca();
                }

            }
        }
        return bachecheUtente;

    }
    private TitoloBacheca stringToTitoloBacheca(String titoloStr) {
        if(titoloStr == null) return null;
        switch(titoloStr.toLowerCase()) {
            case "università":
            case "universita":
                return TitoloBacheca.UNIVERSITA;
            case "lavoro":
                return TitoloBacheca.LAVORO;
            case "tempo libero":
                return TitoloBacheca.TEMPOLIBERO;
            default:
                return null;
        }
    }



    // --- GESTIONE TODO ---

    public ArrayList<ToDo> getToDoByBacheca(Bacheca bacheca) {
        return new ArrayList<>(toDoManager.getToDoByBacheca(bacheca));
    }

    public void addToDo(ToDo todo, Bacheca bacheca) {
        toDoManager.addToDo(todo, bacheca);
    }

    public void removeToDo(ToDo todo, Bacheca bacheca) {
        toDoManager.removeToDo(todo, bacheca);
    }

    public void updateToDo(ToDo todo, Bacheca bacheca) {
        toDoManager.updateToDo(todo, bacheca);
    }

    public void addABacheca(ToDo todo, TitoloBacheca titolo){
        String titoloBacheca = titolo.toString();
        List<Bacheca> bacheca = getBachecaList(titoloBacheca);
        for(Bacheca b: bacheca){
            if(b.getTitolo() == titolo){
                addToDo(todo, b);
                return;
            }
        }
        Bacheca nuova = new Bacheca(titolo, utenteLoggato.getUsername());
        addBacheca(nuova);
        addToDo(todo, nuova);
    }

    // ----- GESTIONE UTENTI -----

    public boolean registraUtente(String username, String password){
            if (utenteManager.utenteEsiste(username)) {
                return false; // Utente già esistente
            }
            utenteManager.aggiungiUtente(new Utente(username, password));
            return true;
        }

    public boolean verificaAccesso(String username, String password) {
        Utente utente = utenteManager.getUtente(username);
        if (utente != null && utente.getPassword().equals(password)) {
            this.utenteLoggato = utente; //memorizza chi ha effettuato l'accesso
            return true;
        }
        return false;
    }

    public boolean utenteEsiste(String username) {
        return utenteManager.utenteEsiste(username);
    }

    public Utente getUtenteLoggato() {
        return utenteLoggato;
    }
}


