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
    public  JFrame frameBacheca, frameChiamante;
    private Controller controller;
    private String utentelog;

    public SelezioneBacheca(Controller controller, JFrame frame, String utentelog) {
        this.controller = controller;
        frameChiamante = frame;
        this.utentelog = utentelog;

        comboBox1.addItem("");
        comboBox1.addItem("Università");
        comboBox1.addItem("Lavoro");
        comboBox1.addItem("Tempo Libero");

        frameBacheca = new JFrame("Selezione Bacheca");
        frameBacheca.setContentPane(principale);
        bachechePanel.setLayout(new BoxLayout(bachechePanel, BoxLayout.X_AXIS));
        frameBacheca.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameBacheca.setSize(900, 600);
        frameBacheca.setLocationRelativeTo(null);
        bachechePanel.setLayout(new BoxLayout(bachechePanel, BoxLayout.Y_AXIS));
        frameBacheca.setVisible(true);

        //Funzione che crea Bacheche per Admin
        System.out.println(this.utentelog);

        //PERMETTE DI APRIRE LA PAGINA PER CREARE UNA BACHECA
        creaNuovaBachecaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CreaBacheca terzaGui = new CreaBacheca(controller, frameBacheca, utentelog);

                terzaGui.frameCreaBacheca.setVisible(true);
                frameBacheca.dispose();
            }
        });

        //PERMETTE DI TROVARE LE BACHECHE CREATE
        buttonCerca.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String bachecaDaCercare = (String) comboBox1.getSelectedItem();
                ArrayList<Bacheca> bachecaDaMostrare = controller.getBachecaList(bachecaDaCercare, utentelog);
                bachechePanel.removeAll();

                JPanel cardsPanel = new JPanel();
                cardsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20)); // orizzontale, con margini tra le card
                cardsPanel.setBackground(Color.WHITE); // sfondo bianco per un aspetto moderno

                if (bachecaDaMostrare.isEmpty()) {
                    cardsPanel.add(new JLabel("Nessuna bacheca trovata."));
                } else {
                    for (Bacheca b : bachecaDaMostrare) {
                        JPanel card = new JPanel();
                        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                        card.setPreferredSize(new Dimension(250, 160));
                        card.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(new Color(90, 90, 90), 2, true),
                                BorderFactory.createEmptyBorder(10, 15, 10, 15)
                        ));
                        card.setBackground(new Color(245, 245, 250)); // colore tenue e moderno

                        JLabel titolo = new JLabel(b.getTitolo().toString());
                        titolo.setFont(new Font("Arial", Font.BOLD, 16));
                        titolo.setForeground(new Color(40, 40, 90));
                        card.add(titolo);

                        JLabel descr = new JLabel("<html><body style='width:180px'>" + b.getDescrizione() + "</body></html>");
                        descr.setFont(new Font("Arial", Font.PLAIN, 13));
                        descr.setForeground(new Color(60, 60, 60));
                        card.add(descr);
                        card.add(Box.createVerticalGlue());

                        //BOTTONE PER APRIRE LA BACHECA
                        JButton apriButton = new JButton("Apri");
                        apriButton.setBackground(new Color(80, 150, 255));
                        apriButton.setForeground(Color.WHITE);
                        apriButton.setFocusPainted(false);
                        apriButton.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));
                        apriButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                VistaBacheca vista = new VistaBacheca(b, controller, frameBacheca, utentelog);
                                vista.frameVista.setVisible(true);
                                vista.frameVista.toFront();
                                vista.frameVista.requestFocus();
                                frameBacheca.dispose();

                            }
                        });
                        //AGGIUNGO IL BOTTONE A CARD
                        card.add(Box.createVerticalStrut(5));
                        card.add(apriButton);
                        cardsPanel.add(card);

                        //BOTTONE PER MODIFICARE LA DESCRIZIONE
                        JButton modificaButton = new JButton("Modifica");
                        modificaButton.setBackground(new Color(255, 200, 80));
                        modificaButton.setForeground(Color.BLACK);
                        modificaButton.setFocusPainted(false);
                        modificaButton.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
                        modificaButton.addActionListener(ev -> {
                            String nuovaDescrizione = JOptionPane.showInputDialog(frameBacheca, "Inserisci nuova descrizione:", b.getDescrizione());
                            if (nuovaDescrizione != null && !nuovaDescrizione.trim().isEmpty()) {
                                b.setDescrizione(nuovaDescrizione);
                                buttonCerca.doClick(); // aggiorna la lista
                            }
                        });
                        //AGGIUNGO IL BOTTONE A CARD
                        card.add(Box.createVerticalStrut(5));
                        card.add(modificaButton);

                        //BOTTONE PRE ELIMINARE LA BACHECA
                        JButton eliminaButton = new JButton("Elimina");
                        eliminaButton.setBackground(new Color(255, 80, 80));
                        eliminaButton.setForeground(Color.WHITE);
                        eliminaButton.setFocusPainted(false);
                        eliminaButton.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
                        eliminaButton.addActionListener(ev -> {
                            int conferma = JOptionPane.showConfirmDialog(frameBacheca, "Vuoi eliminare questa bacheca?", "Conferma", JOptionPane.YES_NO_OPTION);
                            if (conferma == JOptionPane.YES_OPTION) {
                                b.getUtente().eliminaBacheca(b);
                                buttonCerca.doClick(); // aggiorna la lista
                            }
                        });
                        //AGGIUNGO IL BOTTONE A CARD
                        card.add(Box.createVerticalStrut(5));
                        card.add(eliminaButton);
                        cardsPanel.add(card);
                    }
                }
                JScrollPane scroll = new JScrollPane(cardsPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                scroll.setBorder(null);
                scroll.getHorizontalScrollBar().setUnitIncrement(16);
                bachechePanel.setLayout(new BorderLayout()); // assicura che il pannello scrollabile occupi tutto
                bachechePanel.add(scroll, BorderLayout.CENTER);

                bachechePanel.revalidate();
                bachechePanel.repaint();
            }
        });

        //BOTTONE PER TORNARE INDIETRO
        buttonIndietro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Accesso(controller);
                frameBacheca.dispose();
            }
        });
    }
}