package model;

public class Attivita {
    private String titolo;


    //gestisco enumerazione
    private StatoAttivita stato = StatoAttivita.NONCOMPLETATA;


    //gestisco la checklist
    private CheckList checklist = null;


    //costruttore
    public Attivita(String titolo, StatoAttivita stato) {
        this.titolo = titolo;
        this.stato = stato;
    }


    //funzioni future
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
