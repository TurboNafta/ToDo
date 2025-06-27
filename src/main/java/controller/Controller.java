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
    public ArrayList<Bacheca> getBachecaList(String titolo, String username) {
        Utente utente = getUtente(username);
        System.out.println("Utente trovato: " + (utente != null ? utente.getUsername() : "null"));
        ArrayList<Bacheca> bachecheUtente=new ArrayList<>();
        if (utente == null){
          return bachecheUtente;// da gestire con eccezione pls nn dimenticarti :*
        } else{
           TitoloBacheca titoloBacheca=stringToTitoloBacheca(titolo);
            if (titolo == null || titolo.isEmpty()) {
                return utente.getBacheca();
            }
            for (Bacheca b : utente.getBacheca()) {
                if (b.getTitolo().equals(titoloBacheca)) {
                    bachecheUtente.add(b);
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

    public void login(String username, String password) {
        for (Utente u : listaUtenti) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                utenteLoggato = u;
                return;
            }
        }
        throw new IllegalArgumentException("Credenziali errate");
    }


    public void addBacheca(TitoloBacheca titolo, String descrizione, String username) {
        Utente u = getUtente(username);
        if(u != null){
            Bacheca nuova = new Bacheca(titolo, descrizione, u);
            u.CreaBacheca(nuova);
        } else {
            throw new IllegalStateException("Nessun utente loggato");
        }
    }

    public void setUtenteLoggato(Utente utente){
        this.utenteLoggato = utente;
    }

    public void eliminaBacheca(Bacheca b){
        if(utenteLoggato != null){
            utenteLoggato.eliminaBacheca(b);
        } else {
            throw new IllegalStateException("Nessun utente loggato");
        }
    }

    public void modificaDescrizione(Bacheca b, String nuovaDescrizione){
        b.modificaDescrizione(nuovaDescrizione);
    }

    // --- GESTIONE TODO ---

    public ArrayList<ToDo> getToDoPerBachecaUtente(String utenteLoggato, Bacheca bacheca, String nomeToDo) {
        Utente utente = getUtente(utenteLoggato);
        Bacheca b = bacheca;
        ArrayList<ToDo> filtrati = new ArrayList<>();
        ArrayList<ToDo> tuttiToDo = b.getTodo();

        if(nomeToDo == null || nomeToDo.isEmpty()){
            return tuttiToDo;
        }else{
            for(ToDo t : tuttiToDo){
                if(t.getTitolo().equalsIgnoreCase(nomeToDo)){
                    filtrati.add(t);
                }
            }
        }
        return filtrati;
    }
    public void addToDo(Bacheca bacheca, ToDo todo, String username) {
        Utente u=getUtente(username);
        if(u != null){
           ArrayList<Utente> utenti=todo.getUtentiPossessori();
           if(utenti==null)
               utenti= new ArrayList<Utente>();
           if(!utenti.contains(u)){
               utenti.add(u);
           }
           todo.setUtentiPossessori(utenti);

        }
        // aggiungo il To Do in bacheca
        bacheca.aggiungiToDo(todo);
    }
    /*
    public ArrayList<ToDo> getToDoByBacheca(Bacheca bacheca) {
        return new ArrayList<>(toDoManager.getToDoByBacheca(bacheca));
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
            System.out.println(b.getTitolo()+" ");
            System.out.println(b.getDescrizione()+"\n");
            admin.CreaBacheca(b);
        }
    }

    //Funzione che genera i todo solamente per l'admin
    public void buildToDoPerBachecaUtente(){
        Utente admin = getUtente("admin");
        if (admin == null) {
            admin = new Utente("admin", "1111");
            this.listaUtenti.add(admin);
        }
        //Se admin ha già bacheche coon ToDo non li ricrea
        //Da implementare

        String[] titoli = {"BASKET", "STUDIO", "CUCINARE"};
        String[] descr = {"PROVA4", "PROVA5", "PROVA6"};
        String[] url = {"http/1", "http/2", "http/3"};
        String[] posizione = {"Desktop", "Download", "Documenti"};
        String[] colore = {"Rosso", "Giallo", "Blu"};
        String[] data = {"31/5/2025", "1/6/2025", "2/6/2025"};
        String[] image = {"foto1", "foto2", "foto3"};

        //ARRAYLIST OER GESTIRE LA CONDIVISIONE DEI TODO
        ArrayList<Utente> utentiCondivisione = new ArrayList<>();
        utentiCondivisione.add(admin);

        int i=0;
        for (Bacheca b : admin.getBacheca()) {
            if (!b.getTodo().isEmpty()) {
                return;
            }else {
                b.aggiungiToDo(new ToDo(titoli[i], descr[i], url[i], data[i], image[i], posizione[i], colore[i], utentiCondivisione));
                i++;
            }
        }

        //Stampa nel terminale, giusto per verificare
        for (Bacheca b : admin.getBacheca()) {
            System.out.println("Bacheca: " + b.getTitolo());
            for (ToDo t : b.getTodo()) {
                System.out.println("   - " + t.getTitolo() + ": " + t.getDescrizione());
            }
        }
    }
}


