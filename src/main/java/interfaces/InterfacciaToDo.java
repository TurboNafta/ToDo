package interfaces;

import model.StatoToDo;
import model.ToDo;


// InterfacciaToDo.java
public interface InterfacciaToDo {
    public void modificaToDo(ToDo todo, String titolo, String descrizione, String dataScadenza, String img, String posizione, String url, String colore, StatoToDo stato);
}


