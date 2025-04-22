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
    private List <Condivisione> condivisione;


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
}
