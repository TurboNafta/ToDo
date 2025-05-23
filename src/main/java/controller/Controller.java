package controller;

import model.Bacheca;
import java.util.*;
import model.*;


public class Controller {
    private ArrayList<Bacheca> bachecaList;
    public Controller() {this.bachecaList= new ArrayList<Bacheca>();}

    public void addBacheca(Bacheca b){
        this.bachecaList.add(b);}

    public ArrayList<Bacheca> getBacheca(int i) {
        return this.bachecaList.get(i);
    }
    public ArrayList<Bacheca> removeBacheca(int i) {
        return this.bachecaList.remove(i);
    }
    public ArrayList<Bacheca> getFirstBacheca() {
        return bachecaList.size();
    }
    public ArrayList<Bacheca> getBachece(String type) {

    }

}
