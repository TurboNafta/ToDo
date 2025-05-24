package gui;

import controller.Controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SelezioneBacheca {
    private JPanel principale;
    private JComboBox comboBox1;
    private JButton buttonCerca;
    private JLabel titoloLabel;
    private JPanel ricercaPanel;
    private JButton creaNuovaBachecaButton;
    private JPanel creaPanel;
    private JTable tableResult;

    public static JFrame frameBacheca, frameChiamante;
    private Controller controller;

    public SelezioneBacheca(Controller controller, JFrame frame) {
        comboBox1.addItem("");
        comboBox1.addItem("Università");
        comboBox1.addItem("Lavoro");
        comboBox1.addItem("Tempo Libero");

        this.controller = controller;
        frameChiamante = frame;

        frameBacheca = new JFrame("Seleziona Bacheca");
        frameBacheca.setContentPane(principale);
        frameBacheca.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameBacheca.pack();
        frameBacheca.setVisible(true);

        ModelloTabellaBacheca modello = new ModelloTabellaBacheca();
        tableResult.setModel(modello);

        //PERMETTE DI APRIRE LA PAGINA PER CREARE UNA BACHECA
        creaNuovaBachecaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CreaBacheca terzaGui = new CreaBacheca(controller, frameChiamante);
                terzaGui.frameCreaBacheca.setVisible(true);
                frameBacheca.setVisible(false);
            }
        });

        //PERMETTE DI TROVARE LE BACHECHE CREATE
        buttonCerca.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }
}
