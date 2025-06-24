package gui;

import controller.Controller;
import model.Bacheca;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private JButton buttonIndietro;

    //Frame e controller
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

        frameBacheca = new JFrame("Selezione Bacheca");
        frameBacheca.setContentPane(principale);
        frameBacheca.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameBacheca.setSize(900, 600);
        frameBacheca.setLocationRelativeTo(null);
        frameBacheca.setVisible(true);

        //Funzione che crea Bacheche per Admin
        if(this.utentelog.equals("Admin")){
            controller.buildBacheche();
        }

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

                bachechePanel.removeAll();
                if(bachecaDaMostrare.isEmpty()){
                    bachechePanel.add(new JLabel("Nessuna bacheca trovata."));
                }else{
                    for(Bacheca b: bachecaDaMostrare){
                        JPanel card = new JPanel();
                        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                        card.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
                        card.add(new JLabel("Titolo: " + b.getTitolo()));
                        card.add(new JLabel("Descrizione: " + b.getDescrizione()));

                        bachechePanel.add(card);
                        bachechePanel.add(Box.createHorizontalStrut(20));
                    }
                }
                bachechePanel.revalidate();
                bachechePanel.repaint();
            }
        });

        //BOTTONE PER TORNARE INDIETRO
        buttonIndietro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Accesso secondGui = new Accesso();
                secondGui.frameAccesso.setVisible(true);
                frameBacheca.setVisible(false);
            }
        });
    }
}