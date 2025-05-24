package controller;

<<<<<<< HEAD
import interfaces.interfaces;
import model.*;
import java.util.*;

public class Controller {
    private ArrayList<Bacheca> bachecaList;
    private ArrayList<Utente> utenti;

    public Controller() {
        this.bachecaList = new ArrayList<>();
        this.utenti = new ArrayList<>();
        // Esempio di utenti e bacheche
        Utente user = new Utente("USER1", "password");
        utenti.add(user);

        bachecaList.add(new Bacheca(TitoloBacheca.UNIVERSITA, "Attività universitarie"));
        bachecaList.add(new Bacheca(TitoloBacheca.LAVORO, "Compiti di lavoro"));
        bachecaList.add(new Bacheca(TitoloBacheca.TEMPOLIBERO, "Hobby e relax"));
        }
        // --------- TODO ---------
        public void addToDo(TitoloBacheca titolo, ToDo todo) {
            Bacheca b = getBachecaByTitolo(titolo);
            if (b != null && todo != null)
                b.getTodo().add(todo);
        }

    public void removeToDo(TitoloBacheca titolo, ToDo todo) {
        Bacheca b = getBachecaByTitolo(titolo);
        if (b != null)
            b.getTodo().remove(todo);
    }

    public void updateToDo(TitoloBacheca titolo, ToDo oldToDo, ToDo newToDo) {
        Bacheca b = getBachecaByTitolo(titolo);
        if (b != null) {
            int idx = b.getTodo().indexOf(oldToDo);
            if (idx != -1)
                b.getTodo().set(idx, newToDo);
        }
    }

    public ArrayList<ToDo> getToDoByBacheca(TitoloBacheca titolo) {
        Bacheca b = getBachecaByTitolo(titolo);
        return b != null ? b.getTodo() : new ArrayList<>();
=======
import model.Bacheca;
import model.TitoloBacheca;
import model.ToDo;

import java.util.*;


public class Controller {
    private ArrayList<Bacheca> bachecaList;
    public Controller() {this.bachecaList= new ArrayList<Bacheca>();}

    public void addBacheca(ToDo t, TitoloBacheca tipo){
        for(Bacheca b : bachecaList){
            if(b.getTitolo() == tipo){
                b.aggiungiToDo(t);
                return;
            }
        }
        System.err.println("Bacheca non trovata");
    }

    public Bacheca getBacheca(int i) {
        return this.bachecaList.get(i);
    }
    public Bacheca removeBacheca(int i) {
        return this.bachecaList.remove(i);
>>>>>>> dfb6e238086326e494a4bb63bfaf66087a9a93f5
    }

    public ArrayList<ToDo> searchToDoByTitle(TitoloBacheca titolo, String search) {
        ArrayList<ToDo> result = new ArrayList<>();
        for (ToDo td : getToDoByBacheca(titolo)) {
            if (td.getTitolo().toLowerCase().contains(search.toLowerCase())) {
                result.add(td);
            }
        }
        return result;
    }

    private Bacheca getBachecaByTitolo(TitoloBacheca titolo) {
        for (Bacheca b : bachecaList) {
            if (b.getTitolo() == titolo) {
                return b;
            }
        }
        return null;
    }
}

