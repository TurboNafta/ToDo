package manager;

import interfaces.InterfacciaBacheca;
import model.Bacheca;
import model.Utente;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BachecaManager implements InterfacciaBacheca {
    private ArrayList<Bacheca> bacheche = new ArrayList<>();

    @Override
    public ArrayList<Bacheca> getAllBacheche() {
        return new ArrayList<>(bacheche);
    }

    @Override
    public ArrayList<Bacheca> getBachecheByUtente(Utente utente) {
        return bacheche.stream()
                .filter(b -> b.getUtente().equals(utente))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void addBacheca(Bacheca bacheca, Utente utente) {
        bacheca.setUtente(utente); // Associa la bacheca all'utente
        bacheche.add(bacheca);
    }
}