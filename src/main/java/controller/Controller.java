package controller;

import dao.*;
import database.ConnessioneDatabase;
import model.*;

import java.sql.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

/**
 * Classe controller con cui si gestiscono varie operazioni come la ricerca delle bacheche per un utente, la creazione di una nuova
 * bacheca, ecc.
 */
public class Controller {
    private static final Logger LOGGER = Logger.getLogger(Controller.class.getName()); // Dichiarazione del logger
    private Utente utenteLoggato; // Utente attualmente autenticato
    private final List<Utente> listaUtenti;
    private static final String NOME_UTENTE_AMMINISTRATORE = "admin";
    private final UtenteDAO utenteDAO = new UtenteDAO();
    private final BachecaDAO bachecaDAO = new BachecaDAO();
    private final ToDoDAO toDoDAO = new ToDoDAO();

    /**
     * Costruttore del Controller
     */
    public Controller() {
        this.listaUtenti = new ArrayList<>();
    }

    // GESTIONE BACHECHE

    /**
     * Metodo che ci restituisce le bacheche a seconda del titolo e dell'utente loggato
     */
    public List<Bacheca> getBachecaList(String titolo, String username) {
        try {
            // Recupera l'utente
            Utente utente = getUtenteByUsername(username);
            if (utente == null) {
                throw new IllegalArgumentException("Utente non trovato: " + username);
            }

            // Recupera tutte le bacheche dal database
            List<Bacheca> bachecheDalDB = bachecaDAO.getBachecheByUtente(username);

            // Aggiorna la lista delle bacheche dell'utente in memoria
            utente.setBacheca(bachecheDalDB);

            // Se non è specificato un titolo, restituisce tutte le bacheche
            if (titolo == null || titolo.isEmpty()) {
                return bachecheDalDB;
            }

            // Filtra per titolo se specificato
            List<Bacheca> bachecheFiltered = new ArrayList<>();
            TitoloBacheca titoloBacheca = stringToTitoloBacheca(titolo);
            if (titoloBacheca != null) {
                for (Bacheca b : bachecheDalDB) {
                    if (b.getTitolo().equals(titoloBacheca)) {
                        bachecheFiltered.add(b);
                    }
                }
            }
            return bachecheFiltered;

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero delle bacheche: " + e.getMessage(), e);
        }
    }


    /**
     * Metodo che ci permette di convertire il titolo da string all'enumeration
     */
    private TitoloBacheca stringToTitoloBacheca(String titoloStr) {
        if (titoloStr == null) return null;
        return switch (titoloStr.toLowerCase()) {
            case "università" -> TitoloBacheca.UNIVERSITÀ;
            case "lavoro" -> TitoloBacheca.LAVORO;
            case "tempo libero" -> TitoloBacheca.TEMPOLIBERO;
            default -> null;
        };
    }

    /**
     * Metodo che ci aggiunge la bacheca alla lista delle bacheche dell'utente
     */
    public void addBacheca(TitoloBacheca titolo, String descrizione, String username) {
        try {
            Utente u = getUtenteByUsername(username);
            if (u == null) {
                throw new IllegalArgumentException("Utente non trovato");
            }

            // Verifica se esiste già una bacheca con lo stesso titolo e descrizione
            if (bachecaDAO.esisteBacheca(username, titolo.toString(), descrizione)) {
                throw new IllegalArgumentException("Esiste già una bacheca con questo titolo e descrizione");
            }

            // Crea la nuova bacheca
            Bacheca nuova = new Bacheca(titolo, descrizione, u);

            // Salva nel database e ottiene l'id generato
            int idBacheca = bachecaDAO.inserisci(nuova);

            // Debug
            System.out.println("Nuova bacheca creata - ID: " + idBacheca);

            // Aggiorna la struttura in memoria
            nuova.setId(idBacheca);
            u.aggiungiBacheca(nuova);

            // Ricarica le bacheche
            List<Bacheca> bachecheDalDB = bachecaDAO.getBachecheByUtente(username);
            u.setBacheca(bachecheDalDB);

        } catch (SQLException e) {
            throw new RuntimeException("Errore inserimento bacheca: " + e.getMessage(), e);
        }
    }




    /**
     * Metodo che ci setta l'utente loggato
     */
    public void setUtenteLoggato(Utente utente) {
        this.utenteLoggato = utente;
    }


    // --- GESTIONE TO dO ---

    /**
     * Metodo che ci recupera i to do contenuti in quella bacheca dell'utente
     */
    public List<ToDo> getToDoPerBachecaUtente( Bacheca bacheca, String nomeToDo) {
        List<ToDo> filtrati = new ArrayList<>();
        List<ToDo> tuttiToDo = bacheca.getTodo();

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

    /**
     * Metodo che ci aggiunge un to do alla bacheca di quell'utente
     */
    public void addToDo(Bacheca bacheca, ToDo todo, String username) {
        try {
            // Prima salva il To Do nel database e ottieni l'ID generato
            int todoId = toDoDAO.inserisci(todo, username, bacheca.getId());
            todo.setTodoId(todoId); // Importante: imposta l'ID del to do

            // Imposta la bacheca nel to do
            todo.setBacheca(bacheca);

            // Aggiorna la struttura in memoria
            bacheca.aggiungiToDo(todo);

            // Gestisci i possessori
            if (todo.getUtentiPossessori() == null) {
                todo.setUtentiPossessori(new ArrayList<>());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore inserimento ToDo: " + e.getMessage(), e);
        }
    }


    /**
     * Metodo che ci da i to do in scadenza oggi
     */
    public List<ToDo> getToDoInScadenzaOggi(Bacheca bacheca) {
        List<ToDo> result = new ArrayList<>();
        List<ToDo> tutti = getToDoPerBachecaUtente(bacheca, "");
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

    /**
     * Metodo che ci da i to do in scadenza entro la data presa in input dall'utente
     */
    public List<ToDo> getToDoInScadenzaEntro( Bacheca bacheca, String dataLimiteStr) {
        List<ToDo> result = new ArrayList<>();
        List<ToDo> tutti = getToDoPerBachecaUtente( bacheca, "");
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
    /**
     * Metodo che ci prende l'utente in base all'username
     */
    public Utente getUtenteByUsername(String username) {
        try {
            return utenteDAO.getUtenteByUsername(username);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Metodo che aggiunge l'utente alla lista di utenti registrati
     */
    public void addUtente(Utente utente){
        try {
            utenteDAO.inserisci(utente);
        } catch (SQLException e) {
            throw new RuntimeException("Errore inserimento utente: " + e.getMessage());
        }
    }

    /**
     * Metodo che verifica l'esistenza di un utente
     */
    public boolean esisteUtente(String username, String password) {
        try {
           Utente u = utenteDAO.login(username, password);
           return u != null;
        } catch (SQLException e) {
            throw new RuntimeException("Errore recupero utente: " + e.getMessage(), e);
        }
    }

    /**
     * Metodo che ci restituisce la lista degli utenti registrati
     */
    public List<Utente> getListaUtenti() {
        return listaUtenti;
    }

    /**
     * Metodo che ci genera l'utente admin
     */
    public void buildAdmin() {
        try {
            // Controlla se admin esiste già nel database
            Utente adminEsistente = utenteDAO.getUtenteByUsername(NOME_UTENTE_AMMINISTRATORE);
            if (adminEsistente != null) {
                // Admin già esiste, aggiungi alla lista in memoria se non presente
                if (!listaUtenti.contains(adminEsistente)) {
                    listaUtenti.add(adminEsistente);
                }
                return;
            }
            // Se non esiste, crea l'admin
            Utente admin = new Utente(NOME_UTENTE_AMMINISTRATORE, "1111");
            utenteDAO.inserisci(admin); // Salva nel database
            listaUtenti.add(admin);
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la creazione dell'admin: " + e.getMessage(), e);
        }
    }


    /**
     * Metodo che genera delle bacheche di prova solo per admin
     */
    public void buildBacheche() {
        Utente admin = getUtenteByUsername(NOME_UTENTE_AMMINISTRATORE);
        if (admin == null) {
            throw new IllegalArgumentException("Utente admin non trovato");
        }
        //SE CI SONO BACHECHE NON FACCIO NIENTE
        if (!admin.getBacheca().isEmpty()) {
            return;
        }
        TitoloBacheca[] titoli = {TitoloBacheca.LAVORO, TitoloBacheca.TEMPOLIBERO, TitoloBacheca.UNIVERSITÀ};
        String[] descr = {"PROVA1", "PROVA2", "PROVA3"};
        for (int i = 0; i < titoli.length; i++) {
            Bacheca b = new Bacheca(titoli[i], descr[i], admin);
            admin.aggiungiBacheca(b);
        }
    }

    /**
     * Metodo che genera per l'admin dei to do per le bacheche precedentemente create
     */
    public void buildToDoPerBachecaUtente() {
        Utente admin = getUtenteByUsername(NOME_UTENTE_AMMINISTRATORE);
        if (admin == null) {
            admin = new Utente(NOME_UTENTE_AMMINISTRATORE, "1111");
            this.listaUtenti.add(admin);
        }
        //Se admin ha già bacheche coon To do non li ricrea
        //Da implementare

        String[] titoli = {"BASKET", "STUDIO", "CUCINARE"};
        String[] descr = {"PROVA4", "PROVA5", "PROVA6"};
        String[] url = {"http/1", "http/2", "http/3"};
        String[] posizione = {"1", "2", "3"};
        String[] colore = {"Rosso", "Giallo", "Blu"};
        String[] data = {"31/5/2025", "1/6/2025", "2/6/2025"};
        String[] image = {"foto1", "foto2", "foto3"};

        //ARRAYLIST OER GESTIRE LA CONDIVISIONE DEI To do
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

    /**
     * Metodo che ci elimina il to do dalla bacheca dell'utente
     */
    public void eliminaToDo(Bacheca bacheca, ToDo t) {
        try {
            bacheca.getTodo().remove(t);
            toDoDAO.elimina(t.getTodoId());
        } catch (SQLException ex) {
            throw new RuntimeException("Errore durante l'eliminazione del ToDo del database: " + ex.getMessage(), ex);
        }
    }

    /**
     * Metodo che ci restituisce tutti gli utenti
     * @return
     */
    public Utente[] getTuttiUtenti() {
        return listaUtenti.toArray(new Utente[0]);
    }

    /**
     * Metodo che prende la bacheca con titolo e descrizione inseriti dall'utente, e se manca, la crea
     */
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
        utente.aggiungiBacheca(nuova);
        return nuova;
    }

    /**
     * Metodo che in coppia con creaBachecaSeManca
     */
    public Bacheca getOrCreateBacheca(TitoloBacheca titolo, String descrizione, String username) {
        List<Bacheca> bList = getBachecaList(titolo.toString(), username);
        for (Bacheca b : bList) {
            if (b.getDescrizione().equals(descrizione)) {
                return b;
            }
        }
        // Se nessuna bacheca con titolo e descrizione coincidono, la creo
        return creaBachecaSeManca(titolo, descrizione, username);
    }

    /**
     * Metodo che ci serve a spostare quel to do in un'altra bacheca scelta dall'utente
     */
    public void spostaToDoInAltraBacheca(ToDo todo, Bacheca bachecaOrigine, Bacheca bachecaDestinazione) {
        bachecaOrigine.eliminaToDo(todo);
        todo.setPosizione(String.valueOf(bachecaDestinazione.getTodo().size() + 1));
        bachecaDestinazione.aggiungiToDo(todo);
    }

    /**
     * Metodo che ci restituisce la bacheca secondo titolo e descrizione
     */
    public Bacheca getBachecaPerTitoloEDescrizione(String utenteLoggato, String titolo, String descrizione) {
        if (utenteLoggato == null || titolo == null || descrizione == null) {
            return null;
        }

        Utente utente = getUtenteByUsername(utenteLoggato);
        if (utente == null) {
            return null;
        }

        for (Bacheca bacheca : utente.getBacheca()) {
            if (bacheca.getTitolo().toString().equalsIgnoreCase(titolo) &&
                    bacheca.getDescrizione().equalsIgnoreCase(descrizione)) {
                return bacheca;
            }
        }

        return null;
    }

    /**
     * Metodo che ci verifica se la data inserita è nel formato corretto
     */
    public boolean isValidDate(String dateStr) {
        if (!dateStr.matches("\\d{2}/\\d{2}/\\d{4}")) {
            return false;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            sdf.parse(dateStr);
            return true;
        } catch (Exception _) {
            return false;
        }
    }

    /**
     * Metodo che ci verifica se la posizione inserita è un numero positivo
     */
    public boolean isValidPosition(String posizioneStr) {
        try {
            int posizione = Integer.parseInt(posizioneStr);
            return posizione > 0;
        } catch (NumberFormatException _) {
            return false;
        }
    }

    /**
     * Verifica se il colore specificato è valido
     */
    public boolean isValidColor(String colore) {
        if (colore == null || colore.trim().isEmpty()) {
            return false;
        }

        String[] coloriValidi = {
                "rosso", "giallo", "blu", "verde", "arancione",
                "rosa", "viola", "celeste", "marrone"
        };

        return Arrays.asList(coloriValidi).contains(colore.toLowerCase().trim());
    }
    /**
     * Metodo per modificare la descrizione di una bacheca
     */
    public void modificaBacheca(Bacheca bacheca, String nuovaDescrizione) {
        try {
            // Aggiorna l'oggetto in memoria
            bacheca.setDescrizione(nuovaDescrizione);

            // Aggiorna il database
            bachecaDAO.modifica(bacheca);

            // Ricarica le bacheche per l'utente per mantenere la sincronizzazione
            List<Bacheca> bachecheDalDB = bachecaDAO.getBachecheByUtente(bacheca.getUtente().getUsername());
            bacheca.getUtente().setBacheca(bachecheDalDB);
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la modifica della bacheca: " + e.getMessage(), e);
        }
    }


    /**
     * Metodo per eliminare una bacheca
     */
    public void eliminaBacheca(Bacheca bacheca) {
        try {
            // Elimina dal database
            bachecaDAO.elimina(bacheca.getId());

            // Elimina dalla memoria
            Utente utente = bacheca.getUtente();
            utente.eliminaBacheca(bacheca);

            // Ricarica le bacheche per l'utente per mantenere la sincronizzazione
            List<Bacheca> bachecheDalDB = bachecaDAO.getBachecheByUtente(utente.getUsername());
            utente.setBacheca(bachecheDalDB);
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'eliminazione della bacheca: " + e.getMessage(), e);
        }
    }
    /**
     * Recupera tutti i ToDo associati a una bacheca dal database
     * @param bachecaId L'ID della bacheca
     * @return Lista dei ToDo appartenenti alla bacheca
     * @throws SQLException in caso di errori di accesso al database
     */
    public List<ToDo> getToDoByBacheca(int bachecaId) throws SQLException {
        return toDoDAO.getToDoByBacheca(bachecaId);
    }

    public List<Utente> getTuttiUtentiFromDB(){
        List<Utente> utenti = new ArrayList<>();
        String query = "SELECT username, password FROM utente";

        try(Connection conn = ConnessioneDatabase.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery()){

            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                utenti.add(new Utente(username, password));
            }

        }catch(SQLException e){
            System.err.println("Errore durante il recupero degli utenti dal database: " + e.getMessage());
        }
        return utenti;
    }

    public void inserisciToDoECondividiNelDB(Bacheca bacheca, ToDo nuovoToDo, String utenteCreatore)throws SQLException{
        String queryToDo = "INSERT INTO todo (titolo, descrizione, url, datascadenza, image, posizione, coloresfondo, stato, autore_username, bacheca_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String queryCond = "INSERT INTO condivisione (todo_id, utente_username) VALUES (?, ?)";

        Connection conn = null;
        PreparedStatement pstmtToDo = null;
        PreparedStatement pstmtCondivisione = null;
        ResultSet rs = null;

        try {
            conn = ConnessioneDatabase.getConnection();
            conn.setAutoCommit(false);

            // 1. Inserisci il ToDo nella tabella 'todo'
            pstmtToDo = conn.prepareStatement(queryToDo, Statement.RETURN_GENERATED_KEYS);
            pstmtToDo.setString(1, nuovoToDo.getTitolo());
            pstmtToDo.setString(2, nuovoToDo.getDescrizione());
            pstmtToDo.setString(3, nuovoToDo.getUrl());

            java.util.GregorianCalendar gc = nuovoToDo.getDatascadenza();
            java.util.Date utilDate = gc.getTime();
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            pstmtToDo.setDate(4, sqlDate);

            pstmtToDo.setString(5, nuovoToDo.getImage());
            pstmtToDo.setString(6, nuovoToDo.getPosizione());
            pstmtToDo.setString(7, nuovoToDo.getColoresfondo());
            pstmtToDo.setString(8, nuovoToDo.getStato().toString());
            pstmtToDo.setString(9, utenteCreatore);
            pstmtToDo.setInt(10, bacheca.getId());

            int affectedRows = pstmtToDo.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La creazione del ToDo nel database è fallita, nessuna riga interessata.");
            }

            // Recupera l'ID generato per il nuovo ToDo
            int todoId = -1;
            rs = pstmtToDo.getGeneratedKeys();
            if (rs.next()) {
                todoId = rs.getInt(1);
            } else {
                throw new SQLException("La creazione del ToDo nel database è fallita, nessun ID ottenuto.");
            }

            nuovoToDo.setTodoId(todoId);


            pstmtCondivisione = conn.prepareStatement(queryCond);
            List<Utente> utentiCondivisione = nuovoToDo.getUtentiPossessori(); // Ottieni la lista degli utenti possessori

            for (Utente u : utentiCondivisione) {
                pstmtCondivisione.setInt(1, todoId);
                pstmtCondivisione.setString(2, u.getUsername());
                pstmtCondivisione.addBatch();
            }
            pstmtCondivisione.executeBatch();

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Errore durante il rollback: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.err.println("Errore " + e.getMessage());
                }
            }
            if (pstmtToDo != null) {
                try {
                    pstmtToDo.close();
                } catch (SQLException e) {
                    System.err.println("Errore " + e.getMessage());
                }
            }
            if (pstmtCondivisione != null) {
                try {
                    pstmtCondivisione.close();
                } catch (SQLException e) {
                    System.err.println("Errore " + e.getMessage());
                }
            }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Errore " + e.getMessage());
                }
            }
        }
    }

    public void updateToDoAndSharesInDB(Bacheca bacheca, ToDo toDoModificato, String utenteCreatoreUsername) throws SQLException {
        String updateToDoSQL = "UPDATE todo SET titolo = ?, descrizione = ?, url = ?, datascadenza = ?, image = ?, posizione = ?, coloresfondo = ?, stato = ?, autore_username = ?, bacheca_id = ? WHERE id = ?";
        String deleteSharesSQL = "DELETE FROM condivisione WHERE todo_id = ?";
        String insertShareSQL = "INSERT INTO condivisione (todo_id, utente_username) VALUES (?, ?)";

        Connection conn = null;
        PreparedStatement pstmtUpdateToDo = null;
        PreparedStatement pstmtDeleteShares = null;
        PreparedStatement pstmtInsertShare = null;

        try {
            conn = ConnessioneDatabase.getConnection();
            conn.setAutoCommit(false);

            // Validazione dell'ID del ToDo
            if (toDoModificato.getTodoId() == 0) {
                throw new SQLException("Impossibile aggiornare un ToDo con ID 0. Assicurarsi che il ToDo esista nel DB.");
            }

            // 1. Aggiorna i dati del ToDo nella tabella 'todo'
            pstmtUpdateToDo = conn.prepareStatement(updateToDoSQL);
            pstmtUpdateToDo.setString(1, toDoModificato.getTitolo());
            pstmtUpdateToDo.setString(2, toDoModificato.getDescrizione());
            pstmtUpdateToDo.setString(3, toDoModificato.getUrl());

            java.util.GregorianCalendar gc = toDoModificato.getDatascadenza();
            java.util.Date utilDate = gc.getTime();
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            pstmtUpdateToDo.setDate(4, sqlDate);

            pstmtUpdateToDo.setString(5, toDoModificato.getImage());
            pstmtUpdateToDo.setString(6, toDoModificato.getPosizione());
            pstmtUpdateToDo.setString(7, toDoModificato.getColoresfondo());
            pstmtUpdateToDo.setString(8, toDoModificato.getStato().toString());
            pstmtUpdateToDo.setString(9, utenteCreatoreUsername); // L'autore è l'utente originale
            pstmtUpdateToDo.setInt(10, bacheca.getId());
            pstmtUpdateToDo.setInt(11, toDoModificato.getTodoId()); // Condizione WHERE: ID del ToDo da aggiornare

            int affectedRows = pstmtUpdateToDo.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Aggiornamento del ToDo nel database fallito, nessuna riga interessata. ID ToDo: " + toDoModificato.getTodoId());
            }

            // 2. Elimina tutte le condivisioni esistenti per questo ToDo
            pstmtDeleteShares = conn.prepareStatement(deleteSharesSQL);
            pstmtDeleteShares.setInt(1, toDoModificato.getTodoId());
            pstmtDeleteShares.executeUpdate();

            pstmtInsertShare = conn.prepareStatement(insertShareSQL);
            List<Utente> nuoviUtentiCondivisi = toDoModificato.getUtentiPossessori();

            for (Utente u : nuoviUtentiCondivisi) {
                pstmtInsertShare.setInt(1, toDoModificato.getTodoId());
                pstmtInsertShare.setString(2, u.getUsername());
                pstmtInsertShare.addBatch();
            }
            pstmtInsertShare.executeBatch();

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Errore durante il rollback: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (pstmtUpdateToDo != null) {
                try {
                    pstmtUpdateToDo.close();
                } catch (SQLException e) { System.err.println("Errore chiusura pstmtUpdateToDo: " + e.getMessage()); }
            }
            if (pstmtDeleteShares != null) {
                try {
                    pstmtDeleteShares.close();
                } catch (SQLException e) { System.err.println("Errore chiusura pstmtDeleteShares: " + e.getMessage()); }
            }
            if (pstmtInsertShare != null) {
                try {
                    pstmtInsertShare.close();
                } catch (SQLException e) { System.err.println("Errore chiusura pstmtInsertShare: " + e.getMessage()); }
            }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) { System.err.println("Errore chiusura connessione: " + e.getMessage()); }
            }
        }
    }

    public ToDoDAO getToDoDAO() {
        return toDoDAO;
    }
}