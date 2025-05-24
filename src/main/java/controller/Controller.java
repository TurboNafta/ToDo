package controller;

import model.Bacheca;
import model.ToDo;
import model.Utente;
import interfaces.InterfacciaBacheca;
import interfaces.InterfacciaToDo;
import interfaces.InterfacciaUtente;
import manager.BachecaManager;
import manager.ToDoManager;
import manager.UtenteManager;

import java.util.List;

public class Controller {
    private InterfacciaBacheca bachecaManager;
    private InterfacciaToDo toDoManager;
    private InterfacciaUtente utenteManager;
    private Utente utenteLoggato; // Utente attualmente autenticato

    public Controller() {
        this.bachecaManager = new BachecaManager();
        this.toDoManager = new ToDoManager();
        this.utenteManager = new UtenteManager();
        this.utenteLoggato = null;
    }


    // --- GESTIONE BACHECHE ---

    public List<Bacheca> getBachecaList() {
        if (utenteLoggato != null) {
            return bachecaManager.getBachecheByUtente(utenteLoggato);
        }
        return List.of();
    }

    public void addBacheca(Bacheca bacheca) {
        if (utenteLoggato != null) {
            bachecaManager.addBacheca(bacheca, utenteLoggato);
        }
    }

    // --- GESTIONE TODO ---

    public List<ToDo> getToDoByBacheca(Bacheca bacheca) {
        return toDoManager.getToDoByBacheca(bacheca);
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


