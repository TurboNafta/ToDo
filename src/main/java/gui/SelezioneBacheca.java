package gui;

import controller.Controller;
import model.Bacheca;
import model.Utente;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class SelezioneBacheca {
    private JPanel principale;
    private JComboBox comboBox1;
    private JButton buttonCerca;
    private JLabel titoloLabel;
    private JPanel ricercaPanel;
    private JButton creaNuovaBachecaButton;
    private JPanel creaPanel;
    private JPanel bachechePanel;
    private JTable tableResult;

    public static JFrame frameBacheca, frameChiamante;
    private Controller controller;
    private String utentelog;

    public SelezioneBacheca(Controller controller, JFrame frame, String utentelog) {
        comboBox1.addItem("");
        comboBox1.addItem("Università");
        comboBox1.addItem("Lavoro");
        comboBox1.addItem("Tempo Libero");

        this.controller = controller;
        frameChiamante = frame;
        this.utentelog = utentelog;

        frameBacheca = new JFrame("Seleziona Bacheca");
        frameBacheca.setContentPane(principale);
        frameBacheca.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameBacheca.pack();
        frameBacheca.setVisible(true);

        ModelloTabellaBacheca modello = new ModelloTabellaBacheca();
        tableResult.setModel(modello);

        controller.buildBacheche();

        //PERMETTE DI APRIRE LA PAGINA PER CREARE UNA BACHECA
        creaNuovaBachecaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CreaBacheca terzaGui = new CreaBacheca(controller, frameBacheca);

                terzaGui.frameCreaBacheca.setVisible(true);
                frameBacheca.setVisible(false);
            }
        });

        //PERMETTE DI TROVARE LE BACHECHE CREATE
        buttonCerca.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String bachecaDaCercare = (String) comboBox1.getSelectedItem();
                ArrayList<Bacheca> bachecaDaMostrare = controller.getBachecaList(bachecaDaCercare,utentelog);

                modello.settaDatiDaMostrare(bachecaDaMostrare);
                modello.fireTableDataChanged();
            }
        });

    }

}