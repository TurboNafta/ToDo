package model;

import java.awt.font.ImageGraphicAttribute;
import java.util.ArrayList;
import java.util.Date;

public class ToDo {
    private String titolo;
    private String descrizione;
    private Date datascadenza;
    private String url;
    private ImageGraphicAttribute image;
    private String posizione;
    private String coloresfondo;


    //gestisco l'enumerazione
    private StatoToDo stato = StatoToDo.NONCOMPLETATO;


    //gestisco la relazione * con condivisione
    private ArrayList <Condivisione> condivisione;


    //gestisco la checklist
    private CheckList checklist;

    //gestisco la composizione con bacheca
    private Bacheca bacheca;
    public ToDo(Bacheca b){
        bacheca = b;
    }

    //costruttore
    public ToDo(String titolo, String descrizione, Date date, String url, ImageGraphicAttribute img, String posizione, String coloresfondo, StatoToDo stato, CheckList checklist){
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.datascadenza = date;
        this.url = url;
        this.image = img;
        this.posizione = posizione;
        this.coloresfondo = coloresfondo;
        this.stato = stato;

        this.condivisione = new ArrayList<Condivisione>();
        this.checklist = checklist;
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

    public Date getDatascadenza() {
        return datascadenza;
    }

    public void setDatascadenza(Date datascadenza) {
        this.datascadenza = datascadenza;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ImageGraphicAttribute getImage() {
        return image;
    }

    public void setImage(ImageGraphicAttribute image) {
        this.image = image;
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

    public ArrayList<Condivisione> getCondivisione() {
        return condivisione;
    }

    public void setCondivisione(ArrayList<Condivisione> condivisione) {
        this.condivisione = condivisione;
    }

    public CheckList getChecklist() {
        return checklist;
    }

    public void setChecklist(CheckList checklist) {
        this.checklist = checklist;
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
    /*public void AggiungiCondivisione(){

    }
    public void EliminaCondivisione(){

    }*/
}
