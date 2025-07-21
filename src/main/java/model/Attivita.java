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
     * Costruttore con id (per oggetti caricati dal DB).
     * @param id id attività
     * @param checklistId id checklist di riferimento
     * @param titolo titolo attività
     * @param stato stato attività
     */
    public Attivita(int id, int checklistId, String titolo, StatoAttivita stato) {
        this.checklistId = checklistId;
        this.titolo = titolo;
        this.stato = stato;
        this.id = id;
    }

    /**
     * Costruttore per creare una nuova attività.
     * @param titolo titolo attività
     * @param stato stato attività
     */
    public Attivita(String titolo, StatoAttivita stato) {
        this.titolo = titolo;
        this.stato = stato;
    }

    /**
     * Restituisce il titolo dell'attività.
     * @return titolo
     */
    public String getTitolo() {
        return titolo;
    }

    /**
     * Imposta il titolo dell'attività.
     * @param titolo nuovo titolo
     */
    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    /**
     * Restituisce lo stato dell'attività.
     * @return stato
     */
    public StatoAttivita getStato() {
        return stato;
    }

    /**
     * Imposta lo stato dell'attività.
     * @param stato nuovo stato
     */
    public void setStato(StatoAttivita stato) {
        this.stato = stato;
    }

    /**
     * Restituisce l'id dell'attività.
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Imposta l'id dell'attività.
     * @param id nuovo id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Restituisce l'id della checklist di riferimento.
     * @return id checklist
     */
    public int getChecklistId() {
        return checklistId;
    }

    /**
     * Imposta l'id della checklist di riferimento.
     * @param checklistId nuovo id checklist
     */
    public void setChecklistId(int checklistId) {
        this.checklistId = checklistId;
    }
}
