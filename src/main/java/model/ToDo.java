package model;

import dao.ToDoDAO;
import interfaces.InterfacciaToDo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe del model To do, è formata da titolo, descrizione, url, datascandeza, image, posizione, colore sfondo, e Stato.
 * Ogni To Do può essere condiviso tra più utenti, perciò contiene una List per le condivisioni, di cui si salva l'autore e la bacheca
 * Il to do può avere una checklist di attività da completare per completare automaticamente lo stesso
 */
public class ToDo implements InterfacciaToDo {
    private String titolo;
    private String descrizione;
    private String url;

    private GregorianCalendar datascadenza;
    private String image;

    private String posizione;
    private String coloresfondo;

    //gestisco l'enumerazione
    private StatoToDo stato = StatoToDo.NONCOMPLETATO;

    //gestisco la relazione * con condivisione
    private List<Condivisione> utentiPossessori;
    private Utente autore;
    private Bacheca bacheca;

    //gestisco la checklist
    private CheckList checklist;

    private int todoId;

    /**
     * Costruttore per oggetti To Do caricati dal database.
     * @param titolo Titolo del To Do
     * @param descrizione Descrizione del To Do
     * @param url URL collegato al To Do
     * @param date Data di scadenza in formato "gg/mm/aaaa"
     * @param img Percorso dell'immagine associata
     * @param posizione Posizione del To Do
     * @param coloresfondo Colore di sfondo
     * @param utenti Lista di utenti con cui è condiviso
     * @param autore Autore del To Do
     * @param todoId Identificativo del To Do
     * @param bachecaId Identificativo della bacheca di appartenenza
     */
    public ToDo(String titolo, String descrizione, String url, String date, String img, String posizione, String coloresfondo, List<Utente> utenti, Utente autore, int todoId, int bachecaId){
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.url = url;

        String[] dataSplit = date.split("/");
        int anno = Integer.parseInt(dataSplit[2]);
        //Gregorian salva partendo da 0, quindi devo fare così per salvare, quando stampo +1
        int mese = Integer.parseInt(dataSplit[1])-1;
        int gg = Integer.parseInt(dataSplit[0]);
        GregorianCalendar dataScadenza = new GregorianCalendar(anno, mese, gg);
        this.datascadenza = dataScadenza;
        this.image = img;

        this.posizione = posizione;
        this.coloresfondo = coloresfondo;

        this.utentiPossessori = new ArrayList<>();
        for(Utente u: utenti){
            this.utentiPossessori.add(new Condivisione(this, u));
        }
        this.autore = autore;

        this.checklist = new CheckList(this);

    }

    /**
     * Costruttore per creare un nuovo To Do.
     * @param titolo Titolo del To Do
     * @param descrizione Descrizione del To Do
     * @param url URL collegato al To Do
     * @param date Data di scadenza in formato "gg/mm/aaaa"
     * @param img Percorso dell'immagine associata
     * @param posizione Posizione del To Do
     * @param coloresfondo Colore di sfondo
     * @param utenti Lista di utenti con cui è condiviso
     * @param autore Autore del To Do
     */
    public ToDo(String titolo, String descrizione, String url, String date, String img, String posizione, String coloresfondo, List<Utente> utenti, Utente autore){
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.url = url;

        String[] dataSplit = date.split("/");
        int anno = Integer.parseInt(dataSplit[2]);
        //Gregorian salva partendo da 0, quindi devo fare così per salvare, quando stampo +1
        int mese = Integer.parseInt(dataSplit[1])-1;
        int gg = Integer.parseInt(dataSplit[0]);
        GregorianCalendar dataScadenza = new GregorianCalendar(anno, mese, gg);
        this.datascadenza = dataScadenza;
        this.image = img;

        this.posizione = posizione;
        this.coloresfondo = coloresfondo;

        this.utentiPossessori = new ArrayList<>();
        for(Utente u: utenti){
            this.utentiPossessori.add(new Condivisione(this, u));
        }
        this.autore = autore;

        this.checklist = new CheckList(this);
    }

    /**
     * Restituisce il titolo del To Do.
     * @return titolo
     */
    public String getTitolo() {
        return titolo;
    }

    /**
     * Imposta il titolo del To Do.
     * @param titolo nuovo titolo
     */
    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    /**
     * Restituisce la descrizione del To Do.
     * @return descrizione
     */
    public String getDescrizione() {
        return descrizione;
    }

    /**
     * imposta la descrizione del To Do.
     * @param descrizione del To Do
     */
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    /**
     * Restituisce l'URL associato al To Do.
     * @return url
     */
    public String getUrl() {
        return url;
    }

    /**
     * imposta l'url del To Do.
     * @param url nuovo url del To Do
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Restituisce la posizione del To Do.
     * @return posizione
     */
    public String getPosizione() {
        return posizione;
    }

    /**
     * Imposta la posizione del To Do.
     * @param posizione posizione del To Do
     */
    public void setPosizione(String posizione) {
        this.posizione = posizione;
    }

    /**
     * Restituisce il colore di sfondo del To Do.
     * @return colore di sfondo
     */
    public String getColoresfondo() {
        return coloresfondo;
    }

    /**
     * Imposta il colore del To Do.
     * @param coloresfondo nuovo colore di sfondo del To Do
     */
    public void setColoresfondo(String coloresfondo) {
        this.coloresfondo = coloresfondo;
    }

    /**
     * Restituisce la checklist associata al To Do.
     * @return checklist
     */
    public CheckList getChecklist() {
        return checklist;
    }

    /**
     * Imposta la checklist del To Do.
     * @param checklist nuova checklist
     */
    public void setChecklist(CheckList checklist) {
        this.checklist = checklist;
    }

    /**
     * Restituisce la bacheca di appartenenza.
     * @return bacheca
     */
    public Bacheca getBacheca() {
        return bacheca;
    }

    /**
     * Imposta la bacheca del To Do.
     * @param bacheca bacheca associata al To Do
     */
    public void setBacheca(Bacheca bacheca) {
        this.bacheca = bacheca;
    }

    /**
     * Restituisce lo stato del To Do.
     * @return stato
     */
    public StatoToDo getStato() {
        return stato;
    }

    /**
     * Restituisce lo stato del To Do come stringa.
     * @return stato come stringa
     */
    public String getStatoString(){
        String statoString = "";
        if(stato == StatoToDo.NONCOMPLETATO){
            statoString = "NON COMPLETATO";
        } else if(stato == StatoToDo.COMPLETATO){
            statoString = "COMPLETATO";
        }
        return statoString;
    }

    /**
     * Imposta lo stato del To Do.
     * @param stato nuovo stato
     */
    public void setStato(StatoToDo stato) {
        this.stato = stato;
    }

    /**
     * Restituisce il percorso dell'immagine associata.
     * @return immagine
     */
    public String getImage() {
        return image;
    }

    /**
     * imposta l'immagine del To Do.
     * @param image nuova immagine
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Restituisce la data di scadenza.
     * @return data scadenza
     */
    public GregorianCalendar getDatascadenza() {
        return datascadenza;
    }

    /**
     * imposta la data di scadenza del To Do.
     * @param datascadenza nuova data di scadenza
     */
    public void setDatascadenza(GregorianCalendar datascadenza) {
        this.datascadenza = datascadenza;
    }

    /**
     * Restituisce la lista degli utenti possessori (con cui è condiviso il To Do).
     * @return lista utenti
     */
    public List<Utente> getUtentiPossessori() {
        return utentiPossessori.stream()
                .map(Condivisione::getUtente)
                .collect(Collectors.toList());
    }

    /**
     * Imposta la lista degli utenti possessori.
     * @param utentiPossessori lista utenti
     */
    public void setUtentiPossessori(List<Utente> utentiPossessori){
        this.utentiPossessori.clear();
        for(Utente u: utentiPossessori){
            this.utentiPossessori.add(new Condivisione(this, u));
        }
    }

    /**
     * Aggiunge una condivisione con un utente.
     * @param utente Utente con cui condividere
     */
    public void aggiungiCondivisione(Utente utente) {
        this.utentiPossessori.add(new Condivisione(this, utente));
    }

    /**
     * Rimuove una condivisione con un utente.
     * @param utente Utente da rimuovere
     */
    public void rimuoviCondivisione(Utente utente) {
        this.utentiPossessori.removeIf(c -> c.getUtente().equals(utente));
    }

    /**
     * Restituisce la lista delle condivisioni.
     * @return lista di oggetti Condivisione
     */
    public List<Condivisione> getCondivisioni() {
        return utentiPossessori;
    }

    /**
     * Restituisce l'autore del ToDo.
     * @return autore
     */
    public Utente getAutore() {
        return autore;
    }

    /**
     * Modifica i dati di un To Do esistente.
     * @param todo To Do da modificare
     * @param titolo nuovo titolo
     * @param descrizione nuova descrizione
     * @param dataScadenza nuova data di scadenza
     * @param img nuova immagine
     * @param posizione nuova posizione
     * @param url nuovo url
     * @param colore nuovo colore di sfondo
     * @param stato nuovo stato
     */
    @Override
    public void modificaToDo(ToDo todo, String titolo, String descrizione, String dataScadenza,
                             String img, String posizione, String url, String colore, StatoToDo stato) {
        if (todo == null) {
            return;
        }

        // Aggiorna i dati nell'oggetto
        if (titolo != null) todo.setTitolo(titolo);
        if (descrizione != null) todo.setDescrizione(descrizione);
        if (url != null) todo.setUrl(url);

        if (dataScadenza != null) {
            String[] dataSplit = dataScadenza.split("/");
            int anno = Integer.parseInt(dataSplit[2]);
            int mese = Integer.parseInt(dataSplit[1])-1;
            int gg = Integer.parseInt(dataSplit[0]);
            GregorianCalendar dataScadenza2 = new GregorianCalendar(anno, mese, gg);
            todo.setDatascadenza(dataScadenza2);
        }

        if (img != null) todo.setImage(img);
        if (posizione != null) todo.setPosizione(posizione);
        if (colore != null) todo.setColoresfondo(colore);
        if (stato != null) todo.setStato(stato);

        // Aggiorna il database
        try {
            ToDoDAO todoDAO = new ToDoDAO();
            todoDAO.modifica(todo);
        } catch (SQLException e) {
            // Gestisci l'errore in modo appropriato
            throw new RuntimeException("Errore durante il salvataggio delle modifiche nel database", e);
        }
    }

    /**
     * Salva le modifiche di un To Do nel database.
     * @param todo To Do da salvare
     * @throws SQLException eccezione SQL in caso di errore
     */
    private void salvaModificheNelDatabase(ToDo todo) throws SQLException {
        ToDoDAO todoDAO = new ToDoDAO();
        todoDAO.modifica(todo);
    }

    /**
     * Restituisce l'id del To Do.
     * @return id
     */
    public int getTodoId() {
        return todoId;
    }

    /**
     * imposta l'id del To Do.
     * @param todoId del To Do
     */
    public void setTodoId(int todoId) {
        this.todoId = todoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ToDo that = (ToDo) o;
        return this.todoId == that.todoId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(todoId);
    }
}
