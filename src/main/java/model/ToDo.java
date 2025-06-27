package model;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class ToDo {
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
    private Bacheca bacheca;


    //gestisco la checklist
    private ArrayList<CheckList> attivitaCheckList;

    /*gestisco la composizione con bacheca
    private Bacheca bacheca;
    public ToDo(Bacheca b){
        bacheca = b;
    }*/

    //costruttore
    public ToDo(String titolo, String descrizione, String url, String date, String img, String posizione, String coloresfondo, ArrayList<Utente> utenti){
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

    public void setStato(StatoToDo stato) {
        this.stato = stato;
    }

    public void ModificaSfondo(ToDo todo, String nuovoColore){
        if(todo!=null && nuovoColore!= null){
            todo.setColoresfondo(nuovoColore);
        }

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
    /*public void AggiungiCondivisione(){

    }
    public void EliminaCondivisione(){

    }*/
}
