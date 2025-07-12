package interfaces;
import model.*;
import java.util.*;
public interface InterfacciaCheckList {
    // Metodi per l'aggregazione delle Attivita
    void setAttivita(List<Attivita> attivitaList);
    List<Attivita> getAttivita();

    // Metodi per lo stato della Checklist
    boolean tutteCompletate(); // Metodo fondamentale per la logica di completamento
}
