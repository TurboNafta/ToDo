package interfaces;
import model.*;
import java.util.*;
public interface InterfacciaCheckList {
    // Metodi per l'aggregazione delle Attivita
    void setAttivita(ArrayList<Attivita> attivitaList);
    ArrayList<Attivita> getAttivita();
    void aggiungiAttivita(Attivita attivita);
    void rimuoviAttivita(Attivita attivita); // Aggiunto e reso pubblico se necessario
    void setStatoAttivita(int index, StatoAttivita stato); // Per modificare lo stato di una singola attività

    // Metodi per lo stato della Checklist
    boolean tutteCompletate(); // Metodo fondamentale per la logica di completamento
    boolean isCompletata();
}
