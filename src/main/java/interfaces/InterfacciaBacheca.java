
package interfaces;

import model.*;
import java.util.*;

public interface InterfacciaBacheca {
    void aggiungiToDo(ToDo todo);
    void eliminaToDo(ToDo todo);
    void modificaDescrizione(String nuovaDescrizione);

}
