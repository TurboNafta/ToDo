package controller;

import interfaces.ListBacheca;
import model.Bacheca;

import java.awt.List;
import java.util.*;
import model.*;


public class Controller {
    private ArrayList<ListBacheca> bachecaList;
    public Controller() {this.bachecaList= new ArrayList<ListBacheca>();}

    public void addBacheca(ListBacheca lb){
        this.bachecaList.add(lb);
    }

    public ListBacheca getBacheca(int i) {
        return this.bachecaList.get(i);
    }
    public ListBacheca removeBacheca(int i) {
        return this.bachecaList.remove(i);
    }
    public int getFirstBacheca() {
        return bachecaList.size();
    }
}
