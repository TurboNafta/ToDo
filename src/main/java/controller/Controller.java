package controller;

import model.Bacheca;
import model.TitoloBacheca;
import model.ToDo;

import java.util.*;

public class Controller{
    private ArrayList<Bacheca> bachecaList;

    public Controller(){
        this.bachecaList = new ArrayList<>();
    }

    public void addToBacheca(ToDo t, TitoloBacheca tipo){
        for(Bacheca b : bachecaList){
            if(b.getTitolo().equals(tipo)){
                b.aggiungiToDo(t);
                return;
            }
        }
    }
}