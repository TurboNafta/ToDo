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
}

    public ArrayList<Bacheca> removeBacheca(int index) {
        if(index >= 0 && index < bachecaList.size()) {
            bachecaList.remove(index);
        }
    }

    public void addToDoToBacheca(ToDo t, TitoloBacheca tipo){
        for(Bacheca b : bachecaList){
            if(b.getTitolo().equals(tipo)){
                b.aggiungiToDo(t);
                return;
            }
        }
        utenti.add(new Utente(username, password));
        return true;
    }

    public boolean verificaAccesso(String username, String)

}

