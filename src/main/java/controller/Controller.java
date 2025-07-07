package controller;

import model.*;
import interfaces.InterfacciaBacheca;
import interfaces.InterfacciaToDo;
import interfaces.InterfacciaUtente;

import java.sql.Array;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

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

    public void modificaToDo(ToDo todo, String titolo, String descrizione, String dataScadenza, String img, String posizione, String url, String colore, StatoToDo stato) {
        if (todo == null) {
            return;
        };
        if (titolo != null) todo.setTitolo(titolo);
        if (descrizione != null) todo.setDescrizione(descrizione);
        if (url != null) todo.setUrl(url);

        String[] dataSplit = dataScadenza.split("/");
        int anno = Integer.parseInt(dataSplit[2]);
        //Gregorian salva partendo da 0, quindi devo fare così per salvare, quando stampo +1
        int mese = Integer.parseInt(dataSplit[1])-1;
        int gg = Integer.parseInt(dataSplit[0]);
        GregorianCalendar DataScadenza = new GregorianCalendar(anno, mese, gg);
        if (dataScadenza != null) todo.setDatascadenza(DataScadenza);

        if (img != null) todo.setImage(img);
        if (posizione != null) todo.setPosizione(posizione);
        if (colore != null) todo.setColoresfondo(colore);
        if(stato!=null) todo.setStato(stato);
    }

    // ToDo in scadenza oggi
    public ArrayList<ToDo> getToDoInScadenzaOggi(String utente, Bacheca bacheca) {
        ArrayList<ToDo> result = new ArrayList<>();
        ArrayList<ToDo> tutti = getToDoPerBachecaUtente(utente, bacheca, "");
        java.util.Calendar oggi = java.util.Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String oggiStr = sdf.format(oggi.getTime());

        for (ToDo t : tutti) {
            String scadenzaStr = sdf.format(t.getDatascadenza().getTime());
            if (scadenzaStr.equals(oggiStr)) {
                result.add(t);
            }
        }
        return result;
    }

    // ToDo in scadenza entro una certa data
    public ArrayList<ToDo> getToDoInScadenzaEntro(String utente, Bacheca bacheca, String dataLimiteStr) {
        ArrayList<ToDo> result = new ArrayList<>();
        ArrayList<ToDo> tutti = getToDoPerBachecaUtente(utente, bacheca, "");
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date dataLimite = sdf.parse(dataLimiteStr);

            for (ToDo t : tutti) {
                if (!t.getDatascadenza().getTime().after(dataLimite)) {
                    result.add(t);
                }
            }
        } catch (Exception ex) {
          throw new IllegalArgumentException("Formato data non valido! Usa gg/MM/aaaa.");
        }
        return result;
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
    public void buildAdmin() {
        // Controlla se admin esiste già tra gli utenti
        for (Utente u : listaUtenti) {
            if (u.getUsername().equals("admin")) {
                // Già esiste, non fare nulla
                return;
            }
        }
        // Se non esiste, crea l’admin
        Utente admin = new Utente("admin", "1111");
        listaUtenti.add(admin);
    }

    //Funzione che genera bacheche solamente per l'admin
    public void buildBacheche() {
        Utente admin = getUtente("admin");
        if (admin == null) {
            buildAdmin();
            admin = getUtente("admin");
        }
        //SE CI SONO BACHECHE NON FACCIO NIENTE
        if (!admin.getBacheca().isEmpty()) {
            return;
        }
        TitoloBacheca[] titoli = {TitoloBacheca.LAVORO, TitoloBacheca.TEMPOLIBERO, TitoloBacheca.UNIVERSITA};
        String[] descr = {"PROVA1", "PROVA2", "PROVA3"};
        for (int i = 0; i < titoli.length; i++) {
            Bacheca b = new Bacheca(titoli[i], descr[i], admin);
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
                b.aggiungiToDo(new ToDo(titoli[i], descr[i], url[i], data[i], image[i], posizione[i], colore[i], utentiCondivisione, admin));
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

    public void eliminaToDo(Bacheca bacheca, ToDo t) {
        // Se l'autore è l'utente loggato, elimina il ToDo da tutte le bacheche condivise
        if(t.getAutore() != null && utenteLoggato != null && t.getAutore().getUsername().equals(utenteLoggato.getUsername())) {
            ArrayList<Utente> utentiCondivisi = new ArrayList<>(t.getUtentiPossessori());
            for (Utente u : utentiCondivisi) {
                // Cerca la bacheca giusta per ogni utente
                ArrayList<Bacheca> bachecheUtente = getBachecaList(bacheca.getTitolo().toString(), u.getUsername());
                for (Bacheca b : bachecheUtente) {
                    if (b.getDescrizione().equals(bacheca.getDescrizione())) {
                        b.getTodo().remove(t);
                    }
                }
            }
        } else {
            // Altrimenti elimina solo dalla propria bacheca
            bacheca.getTodo().remove(t);
        }
    }

    public Utente[] getTuttiUtenti() {
        return listaUtenti.toArray(new Utente[0]);
    }

    public Bacheca creaBachecaSeManca(TitoloBacheca titolo, String descrizione, String username) {
        Utente utente = getUtente(username);
        if (utente == null) throw new IllegalArgumentException("Utente non trovato: " + username);

        // Controlla se esiste già bacheca con stesso titolo e descrizione
        for (Bacheca b : utente.getBacheca()) {
            if (b.getTitolo() == titolo && b.getDescrizione().equals(descrizione)) {
                return b;
            }
        }
        // Se manca, la crea
        Bacheca nuova = new Bacheca(titolo, descrizione, utente);
        utente.CreaBacheca(nuova);
        return nuova;
    }

    public Bacheca getOrCreateBacheca(TitoloBacheca titolo, String descrizione, String username) {
        ArrayList<Bacheca> bList = getBachecaList(titolo.toString(), username);
        for (Bacheca b : bList) {
            if (b.getDescrizione().equals(descrizione)) {
                return b;
            }
        }
        // Se nessuna bacheca con titolo e descrizione coincidono, la creo
        return creaBachecaSeManca(titolo, descrizione, username);
    }
}


