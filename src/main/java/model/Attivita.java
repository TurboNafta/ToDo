package model;

/**
 * Classe del model attivita, che come attributi ha il titolo e uno stato (COMPLETATA/NONCOMPLETATA)
 */
public class Attivita {
    private String titolo;
    //gestisco enumerazione
    private StatoAttivita stato;

    private int id;
    private int checklistId;

    /**
     * Costruttore con id (per oggetti caricati dal DB)
     */
    public Attivita(int id, int checklistId, String titolo, StatoAttivita stato) {
        this.checklistId = checklistId;
        this.titolo = titolo;
        this.stato = stato;
        this.id = id;
    }

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
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getChecklistId() {
        return checklistId;
    }
    public void setChecklistId(int checklistId) {
        this.checklistId = checklistId;
    }
}
