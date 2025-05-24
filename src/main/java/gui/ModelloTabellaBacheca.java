package gui;

import model.Bacheca;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class ModelloTabellaBacheca extends AbstractTableModel {
    private ArrayList<Bacheca> bachecheDaMostrare;
    private String[] nomiColonne = {"Nome", "Descrizione"};

    public void settaDatiDaMostrare(ArrayList<Bacheca> bachecheCheVoglio) {
        this.bachecheDaMostrare = bachecheCheVoglio;
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
        return 2;
    }

    //Bisogna implementare questo metodo che ci restituisce i ToDo(?)
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return null;
    }
}
