package controller;

import model.Bacheca;
import model.TitoloBacheca;
import model.ToDo;
import model.Utente;
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
    private ArrayList<Bacheca> bachecaList;

    public Controller() {
        this.bachecaManager = new BachecaManager();
        this.toDoManager = new ToDoManager();
        this.utenteManager = new UtenteManager();
        this.utenteLoggato = null;
        this.bachecaList = new ArrayList<>();
    }


    // --- GESTIONE BACHECHE ---
    //Secondo me c'è il problema qua, non trova le bacheche create
    public ArrayList<Bacheca> getBachecaList(String titolo) {
        /*
        if (utenteLoggato != null) {
            return new ArrayList<>(bachecaManager.getBachecheByUtente(utenteLoggato));
        }*/
        ArrayList<Bacheca> toReturn = new ArrayList<>();
        for(Bacheca b: this.bachecaList){
            if(b.getTitolo().equals(titolo)){
                toReturn.add(b);
            }
        }
        return toReturn;
    }

    public void addBacheca(Bacheca bacheca) {
        if (utenteLoggato != null) {
            bachecaManager.addBacheca(bacheca, utenteLoggato);
        }
        this.bachecaList.add(bacheca);
    }


    //PROVA GENERAZIONE BACHECHE
    public void buildBacheche(){
        int bachecaIndex = 0;
        Random r = new Random();
        while(bachecaIndex<3){
            String name = bachecaNames[r.nextInt(bachecaNames.length)];
            String descr = descrNames[r.nextInt(descrNames.length)];

            if(name.equals("UNIVERSITA")){
                TitoloBacheca tipo = TitoloBacheca.UNIVERSITA;
                Bacheca b = new Bacheca(tipo, descr);
                addBacheca(b);
                bachecaIndex++;
            } else if (name.equals("LAVORO")) {
                TitoloBacheca tipo = TitoloBacheca.LAVORO;
                Bacheca b = new Bacheca(tipo, descr);
                addBacheca(b);
                bachecaIndex++;
            } else if (name.equals("TEMPOLIBERO")) {
                TitoloBacheca tipo = TitoloBacheca.TEMPOLIBERO;
                Bacheca b = new Bacheca(tipo, descr);
                addBacheca(b);
                bachecaIndex++;
            }

        }
    }

    private static final String[] bachecaNames = {
            "UNIVERSITA",
            "LAVORO",
            "TEMPOLIBERO"
    };

    private static final String[] descrNames = {
            "PROVA1",
            "PROVA2",
            "PROVA3"
    };


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


