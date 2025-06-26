package model;

import java.util.*;

/**
 * The type Utente.
 */
public class Utente {
    private final String username;
    private String password;


    //serve a gestire la relazione 3..* con Bacheca
    private ArrayList<Bacheca> bacheca;

    //in questo modo gestisco la relazione * con Condivisione
    private ArrayList<Condivisione> condivisione;

    //costruttore
    public Utente(String username, String password){
        this.username = username;
        this.password = password;
        this.condivisione = new ArrayList<>();
        this.bacheca = new ArrayList<>();
    }

    //funzioni future
    public void CreaBacheca(Bacheca b) {
        bacheca.add(b);//aggiungo b
    }

    public void ModificaBacheca(Bacheca bmod) {
        for(int i=0; i<bacheca.size(); i++){//ciclo sulle bacheche
            Bacheca b= bacheca.get(i);//creo una bacheca temporanea dove metto l'oggetto nella posizione i della lista
            if (b.getTitolo().equals(bmod.getTitolo())) {// se trovo una corrispondenza
                bacheca.set(i, bmod);//sostituisce con la nuova versione
                return;
            }

        }
    }

    public void eliminaBacheca(Bacheca b) {
        bacheca.remove(b);
    }

    public void LeggereUtenti(ToDo todo){
      //da implementare
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<Condivisione> getCondivisione() {
        return condivisione;
    }

    public void setCondivisione(ArrayList<Condivisione> condivisione) {
        this.condivisione = condivisione;
    }

    public ArrayList<Bacheca> getBacheca() {
        return bacheca;
    }

    public void setBacheca(ArrayList<Bacheca> bacheca) {
        this.bacheca = bacheca;
    }

    public String getUsername() {
        return username;
    }
}
