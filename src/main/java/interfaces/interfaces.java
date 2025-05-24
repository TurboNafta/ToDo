package interfaces;

import model.*;
import java.util.ArrayList;
import java.util.List;

public interface interfaces {
    // BACHECHE
    ArrayList<Bacheca> getBacheche();
    Bacheca getBachecaByTitolo(TitoloBacheca titolo);
    void addBacheca(Bacheca bacheca);
    void removeBacheca(Bacheca bacheca);

    // TODO
    void addToDo(TitoloBacheca titolo, ToDo todo);
    void removeToDo(TitoloBacheca titolo, ToDo todo);
    void updateToDo(TitoloBacheca titolo, ToDo oldToDo, ToDo newToDo);
    ArrayList<ToDo> getToDoByBacheca(TitoloBacheca titolo);
    ArrayList<ToDo> searchToDoByTitle(TitoloBacheca titolo, String search);

    // CHECKLIST
    void addAttivita(ToDo todo, Attivita attivita);
    void removeAttivita(ToDo todo, Attivita attivita);
    void toggleAttivita(ToDo todo, Attivita attivita);

    // CONDIVISIONE
    void condividiToDo(ToDo todo, List<Utente> utentiCondivisi);

    // UTENTI
    List<Utente> getUtenti();
}