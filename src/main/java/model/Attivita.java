package model;

/**
 * Classe del model attivita, che come attributi ha il titolo e uno stato (COMPLETATA/NONCOMPLETATA)
 */
public class Attivita {
    private String titolo;

    //gestisco enumerazione
    private StatoAttivita stato;

    /**
     * Costruttore per creare una nuova attività
     */
    public Attivita(String titolo, StatoAttivita stato) {
        this.titolo = titolo;
        this.stato = stato;
    }

    /**
     * Funzioni generali per la gestione degli attributi
     */
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
