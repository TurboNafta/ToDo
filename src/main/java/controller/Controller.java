package controller;

import model.*;
import interfaces.InterfacciaBacheca;
import interfaces.InterfacciaToDo;
import interfaces.InterfacciaUtente;
import manager.BachecaManager;
import manager.ToDoManager;

import java.util.ArrayList;

public class Controller {
    private InterfacciaBacheca bachecaManager;
    private InterfacciaToDo toDoManager;
    private InterfacciaUtente utenteManager;
    private Utente utenteLoggato; // Utente attualmente autenticato
    private ArrayList<Utente> listaUtenti;


    public Controller() {
        this.listaUtenti = new ArrayList<>();
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
    /*
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
    }*/

    // ----- GESTIONE UTENTI -----
    // cerco utente nella lista di utenti
    public Utente getUtente(String username) {
        for (Utente u: listaUtenti) {
            if (u.getUsername().equals(username)) {
                return u;
            }
        }
        return null;// se non va nel if restituisce null
    }


    //Aggiunge un utente alla lista degli utenti contenuti nel controller
    public void addUtente(Utente utente) {
        this.listaUtenti.add(utente);
    }

    //Funzione che verifica l'esistenza dell'utente
    public boolean esisteUtente(String username, String password) {
        for(Utente u: listaUtenti){
            if(password.equals(u.getPassword()) && u.getUsername().equals(username)){
                return true;
            }
        }
        return false;
    }

    //Ci da la lista degli utenti
    public ArrayList<Utente> getListaUtenti() {
        return listaUtenti;
    }

    //Funzione che genera l'admin
    public void buildAdmin(){
        Utente admin = new Utente("admin", "1111");
        this.listaUtenti.add(admin);
    }

    //Funzione che genera bacheche solamente per l'admin
    public void buildBacheche() {
        Utente admin = getUtente("admin");
        //Admin non trovato, è la prima esecuzione del programma, devo crearlo
        if(admin == null){
            admin = new Utente("admin", "1111");
            this.listaUtenti.add(admin);
        }
        //Devo controllare se Admin già possiede delle bacheche
        if( !admin.getBacheca().isEmpty()){
            return;
        }
        TitoloBacheca[] titoli = {TitoloBacheca.LAVORO, TitoloBacheca.TEMPOLIBERO, TitoloBacheca.UNIVERSITA};
        String [] descr = {"PROVA1", "PROVA2", "PROVA3"};
        for(int i=0; i<titoli.length; i++){
            Bacheca b= new Bacheca(titoli[i], descr[i], admin);
            admin.CreaBacheca(b);
        }
    }
}


