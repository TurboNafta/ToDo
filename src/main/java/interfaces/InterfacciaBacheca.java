
package interfaces;

import model.*;

/**
 * Interfaccia per la gestione delle operazioni sui ToDo di una bacheca.
 */
public interface InterfacciaBacheca {
    /**
     * Aggiunge un To Do alla bacheca.
     * @param todo To Do da aggiungere
     */
    void aggiungiToDo(ToDo todo);

    /**
     * Elimina un To Do dalla bacheca.
     * @param todo To Do da eliminare
     */
    void eliminaToDo(ToDo todo);
}
