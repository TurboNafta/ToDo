package gui;

import controller.Controller;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class SelezioneBacheca {
    private JPanel cercaBachecaPanel;
    private JLabel bachecaLabel;
    private JComboBox comboBacheca;
    private JButton buttonCerca;
    private JTable table1;
    private JButton buttonAdd;
    private DefaultTableModel tableModel;

    public static JFrame frameBacheca, frameChiamante;
    private Controller controller;

    // NON CI SERVE IL MAIN IN QUANTO NON È LA PRIMA PAGINA AD ESSERE MOSTRATA

    public SelezioneBacheca(Controller controller, JFrame callFrame){
        this.controller = controller;

        frameChiamante = callFrame;

        // Combo con i tipi di bacheca
        this.comboBacheca.addItem("");
        this.comboBacheca.addItem("Lavoro");
        this.comboBacheca.addItem("Tempo Libero");
        this.comboBacheca.addItem("Università");

        frameBacheca = new JFrame("Bacheca");
        frameBacheca.setContentPane(cercaBachecaPanel);
        frameBacheca.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameBacheca.pack();
        frameBacheca.setVisible(true);


        // Modello tabella
        tableModel = new DefaultTableModel(new Object[]{"Titolo", "Descrizione", "Data Scadenza"}, 0);
        table1.setModel(tableModel);



        buttonAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddToBacheca terzaGui = new AddToBacheca(controller, frameBacheca);
                terzaGui.addFrame.setVisible(true);
                frameBacheca.setVisible(false);
            }
        });
    }
}
