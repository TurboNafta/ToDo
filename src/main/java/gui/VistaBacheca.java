package gui;

import controller.Controller;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class VistaBacheca {
    private JPanel principale;
    private JPanel ricerca;
    private JTextField textField1;
    private JComboBox RicercacomboBox1;
    private JButton tornaAllaHomeButton;
    private JButton creaToDoButton;
    private JButton buttonCerca;
    private JPanel todoPanelris;


    //JFrame e Controller
    public JFrame frameVista, frameChiamante;
    private Controller controller;
    private String utenteLoggato;
    private Bacheca bacheca;

    /*public JFrame getFrameVista() {
        return frameVista;
    }*/

    public VistaBacheca(Bacheca bacheca,Controller controller,JFrame frame, String utenteLoggato) {
        this.frameChiamante = frame;
        this.controller = controller;
        this.utenteLoggato = utenteLoggato;
        this.bacheca = bacheca;

        frameVista = new JFrame("Selezione ToDo");
        frameVista.setContentPane(principale);
        todoPanelris.setLayout(new BoxLayout(todoPanelris, BoxLayout.X_AXIS));
        frameVista.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameVista.setSize(900, 600);
        frameVista.setLocationRelativeTo(null);
        todoPanelris.setLayout(new BoxLayout(todoPanelris, BoxLayout.Y_AXIS));
        frameVista.setVisible(true);

        // --- NUOVO: ComboBox criteri di ricerca ---
        String[] criteri = {"Titolo", "Scadenza oggi", "In scadenza entro"};
        RicercacomboBox1 = new JComboBox<>(criteri);
        textField1 = new JTextField(20); // unica barra di ricerca

        // Pulisci e ricostruisci il pannello ricerca
        ricerca.removeAll();
        ricerca.setLayout(new FlowLayout(FlowLayout.LEFT));
        ricerca.add(new JLabel("Criterio:"));
        ricerca.add(RicercacomboBox1);
        ricerca.add(textField1);

        buttonCerca = new JButton("Cerca");
        ricerca.add(buttonCerca);


        // Abilita/disabilita la barra di ricerca in base al criterio
        RicercacomboBox1.addActionListener(e -> {
            String criterio = (String) RicercacomboBox1.getSelectedItem();
            if ("Titolo".equals(criterio) || "In scadenza entro".equals(criterio)) {
                textField1.setEnabled(true);
            } else {
                textField1.setText("");
                textField1.setEnabled(false);
            }
        });
        // Default: Titolo selezionato
        RicercacomboBox1.setSelectedIndex(0);
        textField1.setEnabled(true);


        //BOTTONE CHE CREA TO DO
        creaToDoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CreaToDo quartaGui = new CreaToDo(controller, frameChiamante, bacheca, utenteLoggato);
                quartaGui.frameCreaToDo.setVisible(true);
                frameVista.setVisible(false);
            }
        });


        //PERMETTE DI TROVARE I TODO

        // BOTTONE CERCA: logica basata sul criterio selezionato
        buttonCerca.addActionListener(e -> {
            String criterio = (String) RicercacomboBox1.getSelectedItem();
            String testo = textField1.getText().trim();
            ArrayList<ToDo> risultati = new ArrayList<>();

            if ("Titolo".equals(criterio)) {
                risultati = controller.getToDoPerBachecaUtente(utenteLoggato, bacheca, testo);
            } else if ("Scadenza oggi".equals(criterio)) {
                risultati = controller.getToDoInScadenzaOggi(utenteLoggato, bacheca);
            } else if ("In scadenza entro".equals(criterio)) {
                risultati = controller.getToDoInScadenzaEntro(utenteLoggato, bacheca, testo);
            }
            mostraToDo(risultati);
        });
        //PERMETTE DI TORNARE ALLA HOME
        tornaAllaHomeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // frameChiamante.setVisible(true); // RIMUOVI QUESTA RIGA!
                // frameVista.dispose();

                // Crea una nuova SelezioneBacheca aggiornata
                SelezioneBacheca selez = new SelezioneBacheca(controller, null, utenteLoggato);
                selez.frameBacheca.setVisible(true);
                frameVista.dispose();
            }
        });
    }
    private void mostraToDo (ArrayList < ToDo > lista) {
        todoPanelris.removeAll();
        JPanel cardsPanel = new JPanel();
        cardsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        cardsPanel.setBackground(Color.WHITE);

        if (lista.isEmpty()) {
            cardsPanel.add(new JLabel("Nessun ToDo trovato."));
        } else {
            for (ToDo t : lista) {
                JPanel card = new JPanel();
                card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                card.setMaximumSize(new Dimension(350, Integer.MAX_VALUE));
                //card.setPreferredSize(new Dimension(190, 100));
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(90, 90, 90), 2, true),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
                card.setBackground(new Color(245, 245, 250));

                card.add(new JLabel("Titolo: " + t.getTitolo()));
                card.add(new JLabel("Descrizione: " + t.getDescrizione()));
                card.add(new JLabel("URL: " + t.getUrl()));
                card.add(new JLabel("Posizione: " + t.getPosizione()));

                //GESTIONE COLORE SFONDO
                String colore = t.getColoresfondo();
                if (colore != null) {
                    switch (colore.toLowerCase()) {
                        case "rosso":
                            card.setBackground(Color.RED);
                            break;
                        case "giallo":
                            card.setBackground(Color.YELLOW);
                            break;
                        case "blu":
                            card.setBackground(Color.BLUE);
                            break;
                        default:
                            //COLORE DEFAULT
                            card.setBackground(Color.LIGHT_GRAY);
                    }
                    //RENDO BACKGROUND VISIBILE
                    card.setOpaque(true);
                }

                //GESTIONE DATA
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String dataString = sdf.format(t.getDatascadenza().getTime());
                card.add(new JLabel("Data Scadenza: " + dataString));

                card.add(new JLabel("Immagine: " + t.getImage()));
                card.add(new JLabel("Stato: " + t.getStatoString()));
                //card.add(new JLabel("Colore Sfondo: " + t.getColoresfondo()));

                /******************************************************************************************************************************/
                /******************************************************************************************************************************/

                //BOTTONE PER MODIFICARE LA DATI
                JButton modificaButton = new JButton("Modifica");
                modificaButton.setBackground(new Color(255, 200, 80));
                modificaButton.setForeground(Color.BLACK);
                modificaButton.setFocusPainted(false);
                modificaButton.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
                modificaButton.addActionListener(ev -> {
                    ModificaToDo modificaGui = new ModificaToDo(controller, frameVista, bacheca, utenteLoggato, t);
                    modificaGui.frameModificaToDo.setVisible(true);
                    frameVista.setVisible(false);
                });
                //AGGIUNGO IL BOTTONE A CARD
                card.add(Box.createVerticalStrut(5));
                card.add(modificaButton);


                //BOTTONE PER CANCELLARE IL TODO
                JButton eliminaButton = new JButton("Elimina");
                eliminaButton.setBackground(new Color(255, 80, 80));
                eliminaButton.setForeground(Color.WHITE);
                eliminaButton.setFocusPainted(false);
                eliminaButton.addActionListener(ev -> {
                    int conferma = JOptionPane.showConfirmDialog(frameVista, "Vuoi eliminare questo ToDo?", "Conferma", JOptionPane.YES_NO_OPTION);
                    if (conferma == JOptionPane.YES_OPTION) {
                        controller.eliminaToDo(bacheca, t);
                        buttonCerca.doClick(); // aggiorna la lista
                    }
                });

                //AGGIUNGO ELIMINA A CARD
                card.add(Box.createVerticalStrut(5));
                card.add(eliminaButton);
                cardsPanel.add(card);

                //PRENDE IL COLORE IN INPUT E LO METTE COME SFONDO
                cardsPanel.add(card);
                cardsPanel.add(Box.createHorizontalStrut(20));
            }
        }
        todoPanelris.add(cardsPanel);
        todoPanelris.revalidate();
        todoPanelris.repaint();

    }
}



