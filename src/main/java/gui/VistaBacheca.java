package gui;

import controller.Controller;
import model.Bacheca;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VistaBacheca {
    private JPanel principale;
    private JPanel ricerca;
    private JTextField textField1;
    private JComboBox comboBox1;
    private JButton tornaAllaHomeButton;
    private JButton creaToDoButton;
    private JButton buttonCerca;
    private JPanel todoPanelris;

    public JFrame frameVista, frameChiamante;
    private Controller controller;
    public JFrame getFrameVista() {
        return frameVista;
    }

    public VistaBacheca(Bacheca bacheca,Controller controller,JFrame frame) {
        this.frameChiamante = frame;
        this.controller = controller;

        frameVista = new JFrame("Seleziona To Do");
        frameVista.setContentPane(principale);
        frameVista.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameVista.pack();
        frameVista.setLocationRelativeTo(null);

        comboBox1 = new JComboBox<>();
        comboBox1.addItem("titolo");
        comboBox1.addItem("descrizione");
        comboBox1.addItem("data scadenza");
        comboBox1.addItem("colore sfondo");
        comboBox1.addItem("url");
        comboBox1.addItem("posizione");
        comboBox1.addItem("stato");

        //BOTTONE CHE CREA TO DO
        creaToDoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CreaToDo quartaGui = new CreaToDo(controller, frameChiamante, bacheca);
                quartaGui.frameCreaToDo.setVisible(true);
                frameVista.setVisible(false);
            }
        });



        //PERMETTE DI TROVARE I TODO
        buttonCerca.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }
}
