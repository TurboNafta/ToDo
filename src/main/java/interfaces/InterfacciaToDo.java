package interfaces;

import model.ToDo;
import model.Bacheca;
import java.util.List;

public interface InterfacciaToDo {
    List<ToDo> getToDoByBacheca(Bacheca bacheca);
    void addToDo(ToDo todo, Bacheca bacheca);
    void removeToDo(ToDo todo, Bacheca bacheca);
    void updateToDo(ToDo todo, Bacheca bacheca);
}
