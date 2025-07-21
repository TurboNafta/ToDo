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
    private final CondivisioneDAO condivisioneDAO = new CondivisioneDAO();
    private final CheckListDAO checkListDAO = new CheckListDAO();

    /**
     * Costruttore del Controller
     */
    public Controller() {
        this.listaUtenti = new ArrayList<>();
    }

    /**
     * Restituisce il DAO delle checklist.
     * @return oggetto CheckListDAO
     */
    public CheckListDAO getCheckListDAO() {
        return checkListDAO;
    }

    /**
     * Restituisce la lista degli utenti con cui un To Do è condiviso.
     * @param todoId id del To Do
     * @return lista di utenti
     * @throws SQLException se avvengono errori SQL
     */
    public List<Utente> getUtentiCondivisiByToDoId(int todoId) throws SQLException {
        return condivisioneDAO.getUtentiCondivisiByToDoId(todoId);
    }

    /**
     * Restituisce la lista delle bacheche per titolo e utente.
     * Se titolo è null o vuoto restituisce tutte le bacheche dell'utente.
     * @param titolo titolo bacheca (stringa)
     * @param username utente proprietario
     * @return lista di bacheche filtrata
     */
    public List<Bacheca> getBachecaList(String titolo, String username) {
        try {
            Utente utente = getUtenteByUsername(username);
            if (utente == null) {
                throw new IllegalArgumentException("Utente non trovato: " + username);
            }
            List<Bacheca> bachecheDalDB = bachecaDAO.getBachecheByUtente(username);
            List<ToDo> condivisi = toDoDAO.getToDoCondivisiConUtente(username);
            for (ToDo t : condivisi) {
                Bacheca bachecaOrig = bachecaDAO.getBachecaById(t.getBacheca().getId());
                boolean presente = false;
                for (Bacheca b : bachecheDalDB) {
                    if (b.getId() == bachecaOrig.getId()) {
                        presente = true;
                        break;
                    }
                }
                if (!presente) {
                    bachecheDalDB.add(bachecaOrig);
                }
            }

            // Popola i To Do corretti
            for (Bacheca b : bachecheDalDB) {
                b.setTodo(toDoDAO.getToDoByBachecaAndUtente(b.getId(), username));
            }

            utente.setBacheca(bachecheDalDB);

            // Filtra per titolo
            if (titolo == null || titolo.isEmpty()) {
                return bachecheDalDB;
            }
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
     * Converte il titolo da stringa a enum TitoloBacheca.
     * @param titoloStr titolo in stringa
     * @return TitoloBacheca corrispondente o null se non valido
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
     * Aggiunge una nuova bacheca all'utente.
     * @param titolo titolo bacheca
     * @param descrizione descrizione bacheca
     * @param username utente proprietario
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
     * Restituisce i To Do relativi a una bacheca e ad un utente.
     * @param bachecaId id della bacheca
     * @param username username utente
     * @return lista di To Do
     * @throws SQLException se avvengono errori SQL
     */
    public List<ToDo> getToDoByBachecaAndUtente(int bachecaId, String username) throws SQLException {
        return toDoDAO.getToDoByBachecaAndUtente(bachecaId, username);
    }

    /**
     * Imposta l'utente attualmente loggato.
     * @param utente utente autenticato
     */
    public void setUtenteLoggato(Utente utente) {
        this.utenteLoggato = utente;
    }

    /**
     * Recupera i To Do per una bacheca e filtra per titolo se indicato.
     * @param bacheca bacheca di riferimento
     * @param nomeToDo titolo da filtrare (case insensitive); se vuoto/null restituisce tutti i ToDo della bacheca
     * @return lista filtrata di To Do
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
     * Aggiunge un nuovo To Do a una bacheca per uno specifico utente.
     * @param bacheca bacheca di riferimento
     * @param todo To Do da aggiungere
     * @param username utente proprietario
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
     * Restituisce i To Do in scadenza oggi per una bacheca.
     * @param bacheca bacheca di riferimento
     * @return lista di To Do in scadenza oggi
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
     * Restituisce i To Do in scadenza entro una certa data.
     * @param bacheca bacheca di riferimento
     * @param dataLimiteStr data limite (formato dd/MM/yyyy)
     * @return lista di To Do in scadenza entro la data
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

    /**
     * Recupera l'utente tramite username.
     * @param username username dell'utente
     * @return oggetto Utente o null se non trovato
     */
    public Utente getUtenteByUsername(String username) {
        try {
            return utenteDAO.getUtenteByUsernameDAO(username);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Aggiunge un nuovo utente al database.
     * @param utente utente da aggiungere
     */
    public void addUtente(Utente utente){
        try {
            utenteDAO.inserisci(utente);
        } catch (SQLException e) {
            throw new RuntimeException("Errore inserimento utente: " + e.getMessage());
        }
    }

    /**
     * Verifica l'esistenza di un utente con username e password.
     * @param username username dell'utente
     * @param password password dell'utente
     * @return true se esiste, false altrimenti
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
     * Crea l'utente admin nel database se non esiste già.
     */
    public void buildAdmin() {
        try {
            // Controlla se admin esiste già nel database
            Utente adminEsistente = utenteDAO.getUtenteByUsernameDAO(NOME_UTENTE_AMMINISTRATORE);
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
     * Crea delle bacheche di prova per l'admin se non ne ha già.
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
     * Crea dei To Do di esempio nelle bacheche dell'admin se non già presenti.
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
     * Elimina un To Do da una bacheca e dal database.
     * @param bacheca bacheca di riferimento
     * @param t To Do da eliminare
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
     * Cerca una bacheca per titolo e descrizione, e la crea se non esiste.
     * @param titolo titolo bacheca
     * @param descrizione descrizione bacheca
     * @param username utente proprietario
     * @return bacheca trovata o creata
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
     * Restituisce una bacheca per titolo e descrizione, creandola se necessario.
     * @param titolo titolo bacheca
     * @param descrizione descrizione
     * @param username utente proprietario
     * @return bacheca trovata o creata
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
     * Sposta un To Do da una bacheca a un'altra.
     * @param todo To Do da spostare
     * @param bachecaOrigine bacheca di partenza
     * @param bachecaDestinazione bacheca di destinazione
     */
    public void spostaToDoInAltraBacheca(ToDo todo, Bacheca bachecaOrigine, Bacheca bachecaDestinazione) {
        try {
            // Aggiorna DB
            String nuovaPosizione = String.valueOf(bachecaDestinazione.getTodo().size() + 1);
            toDoDAO.aggiornaBachecaToDo(todo.getTodoId(), bachecaDestinazione.getId(), nuovaPosizione);

            // Aggiorna oggetti in memoria
            bachecaOrigine.setTodo(toDoDAO.getToDoByBacheca(bachecaOrigine.getId()));
            bachecaDestinazione.setTodo(toDoDAO.getToDoByBacheca(bachecaDestinazione.getId()));

            todo.setBacheca(bachecaDestinazione);
            todo.setPosizione(nuovaPosizione);

        } catch (SQLException e) {
            throw new RuntimeException("Errore durante lo spostamento del ToDo: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica se una data in stringa è valida (formato dd/MM/yyyy).
     * @param dateStr data da verificare
     * @return true se valida, false altrimenti
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
     * Verifica se una posizione è un numero positivo.
     * @param posizioneStr stringa posizione
     * @return true se > 0, false altrimenti
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
     * Verifica se il colore inserito è valido.
     * @param colore stringa colore
     * @return true se ammesso, false altrimenti
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
     * Modifica la descrizione di una bacheca e aggiorna il database.
     * @param bacheca bacheca da modificare
     * @param nuovaDescrizione nuova descrizione
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
     * Elimina una bacheca dal database e dalla memoria.
     * @param bacheca bacheca da eliminare
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
     * Restituisce la lista di tutti gli utenti dal database.
     * @return lista di utenti
     */
    public List<Utente> getTuttiUtentiDalDB(){
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

    /**
     * Aggiorna completamente un To Do: modifica i dati, aggiorna le condivisioni e la checklist.
     * @param toDoModificato To Do da aggiornare
     * @throws SQLException se avvengono errori SQL
     */
    public void aggiornaToDoCompleto(ToDo toDoModificato) throws SQLException {
        toDoDAO.modifica(toDoModificato);

        List<Utente> vecchiUtenti = getUtentiCondivisiByToDoId(toDoModificato.getTodoId());
        for (Utente u : vecchiUtenti) {
            condivisioneDAO.eliminaCondivisione(toDoModificato.getTodoId(), u.getUsername());
        }
        condivisioneDAO.inserisciPossessori(toDoModificato.getTodoId(), toDoModificato.getUtentiPossessori());

        checkListDAO.aggiornaChecklistEAttivita(toDoModificato);
    }

    /**
     * Restituisce il DAO dei To Do.
     * @return oggetto ToDoDAO
     */
    public ToDoDAO getToDoDAO() {
        return toDoDAO;
    }
}