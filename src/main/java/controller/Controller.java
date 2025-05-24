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

    //Aggiunge un Todo alla bacheca, vede il tipo della bacheca e lo cerca nell'arraylist
    public void addABacheca(ToDo t, TitoloBacheca tipo){
        for(Bacheca b : bachecaList){
            if(b.getTitolo().equals(tipo)){
                b.aggiungiToDo(t);
                return;
            }
        }
    }

    //Stampo i todo di quella bacheca
    public ArrayList<Bacheca> getBachecaList(String tipo){
        ArrayList<Bacheca> toReturn = new ArrayList<>();
        for(Bacheca b : bachecaList){
            if(b.getTitolo().equals(tipo)){
                toReturn.add(b);
            }
        }
        return toReturn;
    }


}