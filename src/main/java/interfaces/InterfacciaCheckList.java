package interfaces;
import model.*;
import java.util.*;

/**
 * Interfaccia per la gestione delle checklist di attività.
 */
public interface InterfacciaCheckList {
    /**
     * Imposta la lista delle attività della checklist.
     * @param attivitaList nuova lista di attività
     */
    void setAttivita(List<Attivita> attivitaList);

    /**
     * Restituisce la lista delle attività della checklist.
     * @return lista di attività
     */
    List<Attivita> getAttivita();

    /**
     * Verifica se tutte le attività della checklist sono completate.
     * @return true se tutte completate, false altrimenti
     */
    boolean tutteCompletate();
}
