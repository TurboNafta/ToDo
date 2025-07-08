package model;

import interfaces.InterfacciaToDo;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class ToDo implements InterfacciaToDo {
    private String titolo;
    private String descrizione;
    private String url;

    //GESTISCO DATA E IMMAGINE COME STRINGHE, NON SO COME FARE AL MOMENTO
    private GregorianCalendar datascadenza;
    private String image;

    private String posizione;
    private String coloresfondo;


    //gestisco l'enumerazione
    private StatoToDo stato = StatoToDo.NONCOMPLETATO;


    //gestisco la relazione * con condivisione
    private ArrayList <Utente> utentiPossessori;
    private Utente autore;
    private Bacheca bacheca;


    //gestisco la checklist
    private ArrayList<CheckList> attivitaCheckList;

    /*gestisco la composizione con bacheca
    private Bacheca bacheca;
    public ToDo(Bacheca b){
        bacheca = b;
    }*/

    //costruttore
    public ToDo(String titolo, String descrizione, String url, String date, String img, String posizione, String coloresfondo, ArrayList<Utente> utenti, Utente autore){
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
        this.stato = stato;

        this.utentiPossessori = utenti;
        this.autore = autore;

        this.attivitaCheckList = new ArrayList<>();
        /*
        this.condivisione = new ArrayList<Condivisione>();
        this.checklist = checklist;
        */
    }


    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPosizione() {
        return posizione;
    }

    public void setPosizione(String posizione) {
        this.posizione = posizione;
    }

    public String getColoresfondo() {
        return coloresfondo;
    }

    public void setColoresfondo(String coloresfondo) {
        this.coloresfondo = coloresfondo;
    }

    /*public ArrayList<Condivisione> getCondivisione() {
        return condivisione;
    }

    public void setCondivisione(ArrayList<Condivisione> condivisione) {
        this.condivisione = condivisione;
    }
    */

    public ArrayList<CheckList> getAttivitaCheckList() {
        return attivitaCheckList;
    }

    public void setAttivitaCheckList(ArrayList<CheckList> attivitaCheckList) {
        this.attivitaCheckList = attivitaCheckList;
    }

    public Bacheca getBacheca() {
        return bacheca;
    }

    public void setBacheca(Bacheca bacheca) {
        this.bacheca = bacheca;
    }

    public StatoToDo getStato() {
        return stato;
    }
    public String getStatoString(){
        String statoString = "";
        if(stato == StatoToDo.NONCOMPLETATO){
            statoString = "NON COMPLETATO";
        } else if(stato == StatoToDo.COMPLETATO){
            statoString = "COMPLETATO";
        }
        return statoString;
    }

    public void setStato(StatoToDo stato) {
        this.stato = stato;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public GregorianCalendar getDatascadenza() {
        return datascadenza;
    }

    public void setDatascadenza(GregorianCalendar datascadenza) {
        this.datascadenza = datascadenza;
    }

    public ArrayList<Utente> getUtentiPossessori() {
        return utentiPossessori;
    }

    public void setUtentiPossessori(ArrayList<Utente> utentiPossessori){
        this.utentiPossessori = utentiPossessori;
    }

    public Utente getAutore() {
        return autore;
    }

    public void setAutore(Utente autore) {
        this.autore = autore;
    }
    //INTERFACCIA TO DO
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


}
