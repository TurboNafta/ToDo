package manager;

import interfaces.InterfacciaToDo;
import model.Bacheca;
import model.ToDo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ToDoManager implements InterfacciaToDo {
    private ArrayList<ToDo> todos = new ArrayList<>();

    @Override
    public ArrayList<ToDo> getToDoByBacheca(Bacheca bacheca) {
        if (todos == null || bacheca == null) {
            return new ArrayList<>();
        }
        return todos.stream()
                .filter(t -> bacheca.equals(t.getBacheca()))
                .collect(Collectors.toCollection(ArrayList::new));
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
