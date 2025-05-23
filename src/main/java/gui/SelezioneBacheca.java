package gui;

import controller.Controller;

import javax.swing.*;
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

    public static JFrame frameBacheca, frameChiamante;
    private Controller controller;

    // NON CI SERVE IL MAIN IN QUANTO NON È LA PRIMA PAGINA AD ESSERE MOSTRATA

    public SelezioneBacheca(Controller controller, JFrame callFrame){
        this.controller = controller;

        frameChiamante = callFrame;

        this.comboBacheca.addItem("");
        this.comboBacheca.addItem("Lavoro");
        this.comboBacheca.addItem("Tempo Libero");
        this.comboBacheca.addItem("Università");


    }
}
