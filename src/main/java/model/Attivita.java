package model;


public class Attivita {
    private String titolo;


    //gestisco enumerazione
    private StatoAttivita stato;


    //costruttore
    public Attivita(String titolo, StatoAttivita stato) {
        this.titolo = titolo;
        this.stato = stato;
    }


    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public StatoAttivita getStato() {
        return stato;
    }

    public void setStato(StatoAttivita stato) {
        this.stato = stato;
    }
}
