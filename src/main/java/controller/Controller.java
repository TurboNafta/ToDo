package controller;

import model.*;


import java.util.*;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

public class Controller {
    private static final Logger LOGGER = Logger.getLogger(Controller.class.getName()); // Dichiarazione del logger
    private Utente utenteLoggato; // Utente attualmente autenticato
    private ArrayList<Utente> listaUtenti;
    private static final String NOME_UTENTE_AMMINISTRATORE = "admin";


    public Controller() {
        this.listaUtenti = new ArrayList<>();
    }

    // GESTIONE BACHECHE
    public List<Bacheca> getBachecaList(String titolo, String username) {
        Utente utente = getUtenteByUsername(username);
        LOGGER.info("Utente trovato: {}"+username);
        ArrayList<Bacheca> bachecheUtente = new ArrayList<>();
        if (utente == null) {
            return bachecheUtente;// da gestire con eccezione pls nn dimenticarti :*
        } else {
            TitoloBacheca titoloBacheca = stringToTitoloBacheca(titolo);
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
        if (titoloStr == null) return null;
        switch (titoloStr.toLowerCase()) {
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


    public void addBacheca(TitoloBacheca titolo, String descrizione, String username) {
        Utente u = getUtenteByUsername(username);
        if (u != null) {
            Bacheca nuova = new Bacheca(titolo, descrizione, u);
            u.CreaBacheca(nuova);
        } else {
            throw new IllegalStateException("Nessun utente loggato");
        }
    }

    public void setUtenteLoggato(Utente utente) {//dovrebbe stare in utente

        this.utenteLoggato = utente;
    }


    // --- GESTIONE TODO ---

    public List<ToDo> getToDoPerBachecaUtente( Bacheca bacheca, String nomeToDo) {
        Bacheca b = bacheca;
        ArrayList<ToDo> filtrati = new ArrayList<>();
        ArrayList<ToDo> tuttiToDo = b.getTodo();

        if (nomeToDo == null || nomeToDo.isEmpty()) {
            return tuttiToDo;
        } else {
            for (ToDo t : tuttiToDo) {
                if (t.getTitolo().equalsIgnoreCase(nomeToDo)) {
                    filtrati.add(t);
                }
            }
        }
        return filtrati;
    }

    public void addToDo(Bacheca bacheca, ToDo todo, String username) {
        Utente u = getUtenteByUsername(username);
        if (u != null) {
            ArrayList<Utente> utenti = todo.getUtentiPossessori();
            if (utenti == null)
                utenti = new ArrayList<>();
            if (!utenti.contains(u)) {
                utenti.add(u);
            }
            todo.setUtentiPossessori(utenti);

        }
        // aggiungo il To Do in bacheca
        bacheca.aggiungiToDo(todo);
    }

    // ToDo in scadenza oggi
    public List<ToDo> getToDoInScadenzaOggi(Bacheca bacheca) {
        ArrayList<ToDo> result = new ArrayList<>();
        ArrayList<ToDo> tutti = getToDoPerBachecaUtente(bacheca, "");
        Calendar oggi = Calendar.getInstance();
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
    public List<ToDo> getToDoInScadenzaEntro( Bacheca bacheca, String dataLimiteStr) {
        ArrayList<ToDo> result = new ArrayList<>();
        ArrayList<ToDo> tutti = getToDoPerBachecaUtente( bacheca, "");
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date dataLimite = sdf.parse(dataLimiteStr);

            for (ToDo t : tutti) {
                if (!t.getDatascadenza().getTime().after(dataLimite)) {
                    result.add(t);
                }
            }
        } catch (Exception _) {
            throw new IllegalArgumentException("Formato data non valido! Usa gg/MM/aaaa.");
        }
        return result;
    }

    // ----- GESTIONE UTENTI -----
    // possono essere lasciati nel Controller se la lista utenti è solo lì, ma se vuoi che ogni utente si gestisca, vanno in Utente/interfaccia.
    // cerco utente nella lista di utenti
    public Utente getUtenteByUsername(String username) {
        for (Utente u : listaUtenti) {
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
        for (Utente u : listaUtenti) {
            if (password.equals(u.getPassword()) && u.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    //Ci da la lista degli utenti
    public List<Utente> getListaUtenti() {
        return listaUtenti;
    }

    //Funzione che genera l'admin
    public void buildAdmin() {
        // Controlla se admin esiste già tra gli utenti
        for (Utente u : listaUtenti) {
            if (u.getUsername().equals(NOME_UTENTE_AMMINISTRATORE)) {
                // Già esiste, non fare nulla
                return;
            }
        }
        // Se non esiste, crea l’admin
        Utente admin = new Utente(NOME_UTENTE_AMMINISTRATORE, "1111");
        listaUtenti.add(admin);
    }

    //Funzione che genera bacheche solamente per l'admin
    public void buildBacheche() {
        Utente admin = getUtenteByUsername(NOME_UTENTE_AMMINISTRATORE);
        if (admin == null) {
            buildAdmin();
            admin = getUtenteByUsername(NOME_UTENTE_AMMINISTRATORE);
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
    public void buildToDoPerBachecaUtente() {
        Utente admin = getUtenteByUsername(NOME_UTENTE_AMMINISTRATORE);
        if (admin == null) {
            admin = new Utente(NOME_UTENTE_AMMINISTRATORE, "1111");
            this.listaUtenti.add(admin);
        }
        //Se admin ha già bacheche coon ToDo non li ricrea
        //Da implementare

        String[] titoli = {"BASKET", "STUDIO", "CUCINARE"};
        String[] descr = {"PROVA4", "PROVA5", "PROVA6"};
        String[] url = {"http/1", "http/2", "http/3"};
        String[] posizione = {"1", "2", "3"};
        String[] colore = {"Rosso", "Giallo", "Blu"};
        String[] data = {"31/5/2025", "1/6/2025", "2/6/2025"};
        String[] image = {"foto1", "foto2", "foto3"};

        //ARRAYLIST OER GESTIRE LA CONDIVISIONE DEI TODO
        ArrayList<Utente> utentiCondivisione = new ArrayList<>();
        utentiCondivisione.add(admin);

        int i = 0;
        for (Bacheca b : admin.getBacheca()) {
            if (!b.getTodo().isEmpty()) {
                return;
            } else {
                b.aggiungiToDo(new ToDo(titoli[i], descr[i], url[i], data[i], image[i], posizione[i], colore[i], utentiCondivisione, admin));
                i++;
            }
        }

        //Stampa nel terminale, giusto per verificare
        for (Bacheca b : admin.getBacheca()) {
            LOGGER.info("Bacheca: " + b.getTitolo());
            for (ToDo t : b.getTodo()) {
                LOGGER.info("   - " + t.getTitolo() + ": " + t.getDescrizione());
            }
        }
    }

    public void eliminaToDo(Bacheca bacheca, ToDo t) {
        // Se l'autore è l'utente loggato, elimina il ToDo da tutte le bacheche condivise
        if (t.getAutore() != null && utenteLoggato != null && t.getAutore().getUsername().equals(utenteLoggato.getUsername())) {
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
        Utente utente = getUtenteByUsername(username);
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

    public void spostaToDoInAltraBacheca(ToDo todo, Bacheca bachecaOrigine, Bacheca bachecaDestinazione) {
        bachecaOrigine.eliminaToDo(todo);
        todo.setPosizione(String.valueOf(bachecaDestinazione.getTodo().size() + 1));
        bachecaDestinazione.aggiungiToDo(todo);
    }

    public Bacheca getBachecaPerUtente(String username, String titoloBacheca) {
        Utente utente = getUtenteByUsername(username);
        if (utente == null) return null;

        TitoloBacheca titolo = stringToTitoloBacheca(titoloBacheca);
        for (Bacheca b : utente.getBacheca()) {
            if (b.getTitolo() == titolo) return b;
        }
        return null;
    }
}