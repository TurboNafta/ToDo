package controller;

import model.Bacheca;
import model.TitoloBacheca;
import model.ToDo;

import java.util.*;


public class Controller {
    private ArrayList<Bacheca> bachecaList;
    public Controller() {this.bachecaList= new ArrayList<Bacheca>();}

    public void addBacheca(ToDo t, TitoloBacheca tipo){
        for(Bacheca b : bachecaList){
            if(b.getTitolo() == tipo){
                b.aggiungiToDo(t);
                return;
            }
        }
        System.err.println("Bacheca non trovata");
    }

    public Bacheca getBacheca(int i) {
        return this.bachecaList.get(i);
    }
    public Bacheca removeBacheca(int i) {
        return this.bachecaList.remove(i);
    }
    public int getFirstBacheca() {
        return bachecaList.size();
    }
}
