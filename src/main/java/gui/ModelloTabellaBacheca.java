package gui;

import model.Bacheca;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class ModelloTabellaBacheca extends AbstractTableModel {

    private ArrayList<Bacheca> bachecheDaMostrare = new ArrayList<>();
    private String[] nomiColonne = {"Nome", "Descrizione", "APRI"};

    public void settaDatiDaMostrare(ArrayList<Bacheca> bachecheCheVoglio) {
        this.bachecheDaMostrare = new ArrayList<>(bachecheCheVoglio);
    }

    @Override
    public String getColumnName(int col) {
        return nomiColonne[col];
    }

    @Override
    public int getRowCount() {
        if(bachecheDaMostrare == null){
            return 0;
        }else{
            return bachecheDaMostrare.size();
        }
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    //Bisogna implementare questo metodo che ci restituisce i ToDo(?)
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Bacheca b = bachecheDaMostrare.get(rowIndex);
        switch (columnIndex) {
            case 0: return b.getTitolo();
            case 1: return b.getDescrizione();
            case 2: return "Apri";
            default: return null;
        }
    }

    public Bacheca getBachecaAt(int rowIndex) {
        return bachecheDaMostrare.get(rowIndex);
    }
}
