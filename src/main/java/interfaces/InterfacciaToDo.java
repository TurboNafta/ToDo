package interfaces;

import model.StatoToDo;
import model.ToDo;


/**
 * Interfaccia per la gestione delle operazioni sui ToDo.
 */
public interface InterfacciaToDo {
    /**
     * Modifica i dati di un oggetto To Do con i nuovi valori forniti.
     * @param todo l'oggetto To Do da modificare
     * @param titolo nuovo titolo del To Do
     * @param descrizione nuova descrizione
     * @param dataScadenza nuova data di scadenza (stringa, formato dd/MM/yyyy)
     * @param img nuovo riferimento immagine
     * @param posizione nuova posizione
     * @param url nuovo url
     * @param colore nuovo colore di sfondo
     * @param stato nuovo stato del To Do
     */
    void modificaToDo(ToDo todo, String titolo, String descrizione, String dataScadenza, String img, String posizione, String url, String colore, StatoToDo stato);
}


