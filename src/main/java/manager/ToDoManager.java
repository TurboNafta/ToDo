package manager;

import interfaces.InterfacciaToDo;
import model.Bacheca;
import model.ToDo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ToDoManager implements InterfacciaToDo {
    private List<ToDo> todos = new ArrayList<>();

    @Override
    public List<ToDo> getToDoByBacheca(Bacheca bacheca) {
        return todos.stream()
                .filter(t -> t.getBacheca().equals(bacheca))
                .collect(Collectors.toList());
    }

    @Override
    public void addToDo(ToDo todo, Bacheca bacheca) {
        todo.setBacheca(bacheca); // Collega il ToDo alla bacheca
        todos.add(todo);
    }

    @Override
    public void removeToDo(ToDo todo, Bacheca bacheca) {
        todos.remove(todo); // Opzionalmente controlla anche la bacheca
    }

    @Override
    public void updateToDo(ToDo todo, Bacheca bacheca) {
        // Per semplicità, questa implementazione non fa nulla di speciale
        // In un caso reale, cercheresti il ToDo e aggiorneresti i suoi dati
    }
}
