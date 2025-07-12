package gui;

import controller.Controller;
import model.Bacheca;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.logging.Logger;

public class SelezioneBacheca {
    private static final Logger logger = Logger.getLogger(SelezioneBacheca.class.getName());
    private JPanel principale;
    private JComboBox <String> comboBox1;
    private JButton buttonCerca;
    private JLabel titoloLabel;
    private JPanel ricercaPanel;
    private JButton creaNuovaBachecaButton;
    private JPanel creaPanel;
    private JPanel bachechePanel;
    private JButton buttonIndietro;

    //Frame e controller
    private final JFrame frameBacheca;
    private final JFrame frameChiamante;
    private final Controller controller;

    public JFrame getFrameBacheca() {
        return frameBacheca;
    }

    public JFrame getFrameChiamante() {
        return frameChiamante;
    }

    private final String utentelog;

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
        frameBacheca.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frameBacheca.setSize(900, 600);
        frameBacheca.setLocationRelativeTo(null);
        bachechePanel.setLayout(new BoxLayout(bachechePanel, BoxLayout.Y_AXIS));
        frameBacheca.setVisible(true);

        //Funzione che crea Bacheche per Admin
        logger.info(this.utentelog);

        //PERMETTE DI APRIRE LA PAGINA PER CREARE UNA BACHECA
        creaNuovaBachecaButton.addActionListener(e -> {
            CreaBacheca terzaGui = new CreaBacheca(controller, getFrameBacheca(), utentelog);

            terzaGui.getFrameCreaBacheca().setVisible(true);
            getFrameBacheca().dispose();
        });

        //PERMETTE DI TROVARE LE BACHECHE CREATE
        buttonCerca.addActionListener(e -> {
            String bachecaDaCercare = (String) comboBox1.getSelectedItem();
           controller.getBachecaList(bachecaDaCercare, utentelog);

            aggiornaBachecaPanel();
        });

        //BOTTONE PER TORNARE INDIETRO
        buttonIndietro.addActionListener(e -> {
            new Accesso(controller);
            frameBacheca.dispose();
        });
    }

    private void aggiornaBachecaPanel () {
        String tipo = (String) comboBox1.getSelectedItem();
        List<Bacheca> bacheche = controller.getBachecaList(tipo, utentelog);

        bachechePanel.removeAll();
        JScrollPane scrollPane=creaScrollPane(creaCardsPanel(bacheche));
        bachechePanel.setLayout(new BorderLayout());
        bachechePanel.add(scrollPane, BorderLayout.CENTER);
        bachechePanel.revalidate();
        bachechePanel.repaint();
    }

    private JPanel creaCardsPanel (List<Bacheca> bacheche) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20)); // orizzontale, con margini tra le card
        panel.setBackground(Color.WHITE);// sfondo bianco per un aspetto moderno

        if(bacheche.isEmpty()) {
            panel.add(new JLabel("Nessuna bacheca trovata."));
        } else {
            for(Bacheca b : bacheche) {
                panel.add(creaCard(b));
            }
        }
        return panel;
    }

    private JPanel creaCard (Bacheca b){
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
        styleButton(apriButton, new Color(80, 150, 255), Color.WHITE);
        apriButton.addActionListener(ev -> {
            VistaBacheca vista = new VistaBacheca(b, controller, frameBacheca, utentelog);
            vista.frameVista.setVisible(true);
            vista.frameVista.toFront();
            vista.frameVista.requestFocus();
            frameBacheca.dispose();
        });
        //AGGIUNGO IL BOTTONE A CARD
        card.add(Box.createVerticalStrut(5));
        card.add(apriButton);

        //BOTTONE PER MODIFICARE LA DESCRIZIONE
        JButton modificaButton = new JButton("Modifica");
        styleButton(modificaButton, new Color(255, 200, 80), Color.BLACK);
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
        styleButton(eliminaButton, new Color(255, 80, 80), Color.WHITE);
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

        return card;
    }

    private JScrollPane creaScrollPane(JPanel cardsPanel) {
        JScrollPane scrollPane = new JScrollPane(cardsPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private void styleButton(JButton button, Color bg, Color fg) {
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
    }
}