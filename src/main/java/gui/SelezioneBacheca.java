package gui;

import controller.Controller;
import model.Bacheca;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.logging.Logger;

/**
 * Classe per la GUI di SelezioneBacheca, una volta aver effettuato l'accesso/registrazione, si entra in questa pagina,
 * dov'è possibile cercare e/o selezionare la bacheca sulla quale si vuole interagire
 */
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

    /**
     * Restituisce il frame bacheca
     * @return frame bacheca
     */
    public JFrame getFrameBacheca() {
        return frameBacheca;
    }

    /**
     * Restituisce il frame chiamante
     * @return frame chiamante
     */
    public JFrame getFrameChiamante() {
        return frameChiamante;
    }

    private final String utentelog;

    /**
     * Costruttore della schermata di selezione bacheca.
     * @param controller Controller principale
     * @param frame Frame chiamante (può essere null)
     * @param utentelog Username dell'utente loggato
     */
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

        /**
         * Pulsante che permette di passare alla pagina per la Creazione di una nuova Bacheca
         */
        creaNuovaBachecaButton.addActionListener(e -> {
            CreaBacheca terzaGui = new CreaBacheca(controller, getFrameBacheca(), utentelog);

            terzaGui.getFrameCreaBacheca().setVisible(true);
            getFrameBacheca().dispose();
        });

        /**
         * Pulsante che permette di Cercare una nuova Bacheca a seconda dei dati inseriti
         */
        buttonCerca.addActionListener(e -> {
            String bachecaDaCercare = (String) comboBox1.getSelectedItem();
           controller.getBachecaList(bachecaDaCercare, utentelog);

            aggiornaBachecaPanel();
        });

        /**
         * Pulsante che permette di passare alla pagina per il login
         */
        buttonIndietro.addActionListener(e -> {
            new Accesso(controller);
            frameBacheca.dispose();
        });
    }

    /**
     * Aggiorna il pannello delle bacheche mostrando tutte le bacheche trovate.
     */
    private void aggiornaBachecaPanel() {
        String tipo = (String) comboBox1.getSelectedItem();
        List<Bacheca> bacheche = controller.getBachecaList(tipo, utentelog);

        bachechePanel.removeAll();
        JPanel contentPanel = creaCardsPanel(bacheche);

        // Creiamo uno scroll pane che permette lo scorrimento in entrambe le direzioni
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        bachechePanel.setLayout(new BorderLayout());
        bachechePanel.add(scrollPane, BorderLayout.CENTER);
        bachechePanel.revalidate();
        bachechePanel.repaint();
    }


    /**
     * Crea dinamicamente i pannelli/card per ogni bacheca dell'utente.
     * Ogni card contiene le info della bacheca e i relativi pulsanti di azione.
     * @param bacheche Elenco delle bacheche da visualizzare
     * @return JPanel contenente tutte le card
     */
    private JPanel creaCardsPanel(List<Bacheca> bacheche) {
        // Creiamo un panel principale con GridBagLayout
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        if (bacheche.isEmpty()) {
            panel.add(new JLabel("Nessuna bacheca trovata."));
            return panel;
        }

        // Calcoliamo quante colonne vogliamo in base alla larghezza della finestra
        int numeroColonne = 3; // Puoi modificare questo numero per cambiare il numero di colonne

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Spazio tra le card
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        int riga = 0;
        int colonna = 0;

        for (Bacheca b : bacheche) {
            gbc.gridx = colonna;
            gbc.gridy = riga;

            panel.add(creaCard(b), gbc);

            colonna++;
            if (colonna >= numeroColonne) {
                colonna = 0;
                riga++;
            }
        }

        return panel;
    }

    /**
     * Crea una card grafica per una singola bacheca, con pulsanti Apri, Modifica ed Elimina.
     * @param b Bacheca di riferimento
     * @return JPanel card
     */
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

        /**
         * Pulsante che ci permette di aprire la bacheca e vedere i to do contenuti al suo interno
         */
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

        /**
         * Pulsante che ci permette di modificare la descrizione della bacheca in questione
         */
        JButton modificaButton = new JButton("Modifica");
        styleButton(modificaButton, new Color(255, 200, 80), Color.BLACK);
        modificaButton.addActionListener(ev -> {
            String nuovaDescrizione = JOptionPane.showInputDialog(frameBacheca,
                    "Inserisci nuova descrizione:",
                    b.getDescrizione());
            if (nuovaDescrizione != null && !nuovaDescrizione.trim().isEmpty()) {
                try {
                    controller.modificaBacheca(b, nuovaDescrizione.trim());
                    aggiornaBachecaPanel();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frameBacheca,
                            "Errore durante la modifica: " + e.getMessage(),
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        //AGGIUNGO IL BOTTONE A CARD
        card.add(Box.createVerticalStrut(5));
        card.add(modificaButton);

        /**
         * Pulsante che ci permette di eliminare la bacheca in questione
         */
        JButton eliminaButton = new JButton("Elimina");
        styleButton(eliminaButton, new Color(255, 80, 80), Color.WHITE);
        eliminaButton.addActionListener(ev -> {
            int conferma = JOptionPane.showConfirmDialog(frameBacheca,
                    "Sei sicuro di voler eliminare questa bacheca?\n" +
                            "Verranno eliminati anche tutti i ToDo associati.",
                    "Conferma eliminazione",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (conferma == JOptionPane.YES_OPTION) {
                try {
                    controller.eliminaBacheca(b);
                    aggiornaBachecaPanel();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frameBacheca,
                            "Errore durante l'eliminazione: " + e.getMessage(),
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        //AGGIUNGO IL BOTTONE A CARD
        card.add(Box.createVerticalStrut(5));
        card.add(eliminaButton);

        return card;
    }

    /**
     * Imposta lo stile grafico di un JButton.
     * @param button bottone da stilizzare
     * @param bg colore di sfondo
     * @param fg colore del testo
     */
    private void styleButton(JButton button, Color bg, Color fg) {
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
    }
}