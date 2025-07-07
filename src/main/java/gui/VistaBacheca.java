package gui;

import controller.Controller;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;

public class VistaBacheca {
    private JPanel principale;
    private JPanel ricerca;
    private JTextField textField1;
    private JComboBox <String> comboBoxOrdina;
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

        comboBoxOrdina.removeAllItems();
        comboBoxOrdina.addItem("Nessun Ordinamento");
        comboBoxOrdina.addItem("Titolo A-Z");
        comboBoxOrdina.addItem("Data di Scadenza");
        comboBoxOrdina.addItem("Posizione");
        comboBoxOrdina.addItem("Stato Completamento");

        comboBoxOrdina.addActionListener(e->aggiornaListaToDo());

        //BOTTONE CHE CREA TO DO
        creaToDoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CreaToDo quartaGui = new CreaToDo(controller, frameChiamante, bacheca,utenteLoggato);
                quartaGui.frameCreaToDo.setVisible(true);
                frameVista.setVisible(false);
            }
        });

        //PERMETTE DI TROVARE I TODO
        buttonCerca.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<ToDo> toDoDaCercare = controller.getToDoPerBachecaUtente(utenteLoggato, bacheca, textField1.getText());
                String criterio = (String) comboBoxOrdina.getSelectedItem();

                if (criterio != null) {
                    switch (criterio) {
                        case "Titolo A-Z":
                            toDoDaCercare.sort(Comparator.comparing(ToDo::getTitolo, String.CASE_INSENSITIVE_ORDER));
                            break;
                        case "Data di Scadenza":
                            toDoDaCercare.sort(Comparator.comparing(ToDo::getDatascadenza));
                            break;
                        case "Posizione":
                            toDoDaCercare.sort(Comparator.comparing(t -> Integer.parseInt(t.getPosizione())));
                            break;
                        case "Stato Completamento":
                            toDoDaCercare.sort(Comparator.comparing(ToDo::getStato));
                            break;
                    }
                }

                todoPanelris.removeAll();

                JPanel cardsPanel = new JPanel();
                cardsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20)); // orizzontale, con margini tra le card
                cardsPanel.setBackground(Color.WHITE);

                if(toDoDaCercare.isEmpty()){
                    cardsPanel.add(new JLabel("Nessun ToDo trovato."));
                }else{
                    for (ToDo t : toDoDaCercare) {
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
                        card.add(new JLabel("Stato: " + t.getStato()));
                        card.add(new JLabel("Colore Sfondo: " + t.getColoresfondo()));

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
                        eliminaButton.setForeground(Color.BLACK);
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

                        //BOTTONE CHE SPOSTA I TO DO IN UN ALTRA BACHECA
                        JButton spostaButton = new JButton("Sposta in un altra Bacheca");
                        spostaButton.setBackground(new Color(80, 150, 255));
                        spostaButton.setForeground(Color.BLACK);
                        spostaButton.setFocusPainted(false);
                        spostaButton.addActionListener(ev -> {
                            String[] bacheche = {"Università", "Lavoro", "Tempo Libero"};
                            String scelta = (String) JOptionPane.showInputDialog(
                                    frameVista,
                                    "Scegli bacheca di destinazione: ",
                                    "Sposta To Do",
                                    JOptionPane.PLAIN_MESSAGE,
                                    null,
                                    bacheche,
                                    bacheche[0]
                            );
                            if(scelta != null && !scelta.equalsIgnoreCase(bacheca.getTitolo().toString())){
                                Bacheca destinazione = controller.getBachecaPerUtente(utenteLoggato, scelta);
                                if(destinazione != null) {
                                    controller.spostaToDoInAltraBacheca(t, bacheca, destinazione);
                                    aggiornaListaToDo();
                                    JOptionPane.showMessageDialog(frameVista, "ToDo spostato in'" + scelta + "'");
                                } else {
                                    JOptionPane.showMessageDialog(frameVista, "Errore: bacheca non trovata.");
                                }
                            }
                        });
                        //AGGIUNGO SPOSTA IN CARD
                        card.add(Box.createVerticalStrut(5));
                        card.add(spostaButton);

                        //PRENDE IL COLORE IN INPUT E LO METTE COME SFONDO
                        cardsPanel.add(card);
                        cardsPanel.add(Box.createHorizontalStrut(20));
                    }
                }
                todoPanelris.add(cardsPanel);
                todoPanelris.revalidate();
                todoPanelris.repaint();
            }
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

    private void aggiornaListaToDo() {
        ArrayList<ToDo> lista = new ArrayList<>(bacheca.getTodo());

        String criterio = (String) comboBoxOrdina.getSelectedItem();
        if (criterio != null) {
            switch (criterio) {
                case "Titolo A-Z":
                    lista.sort(Comparator.comparing(ToDo::getTitolo, String.CASE_INSENSITIVE_ORDER));
                    break;
                case "Data di Scadenza":
                    lista.sort(Comparator.comparing(ToDo::getDatascadenza));
                    break;
                case "Posizione":
                    lista.sort(Comparator.comparing(ToDo::getPosizione, String.CASE_INSENSITIVE_ORDER));
                    break;
                case "Stato Completamento":
                    lista.sort(Comparator.comparing(ToDo::getStato));
                    break;
                case "Nessun Ordinamento":
                default:
                    break;
            }
        }
        mostraListaToDo(lista);
    }

    private void mostraListaToDo(ArrayList<ToDo> lista) {
        todoPanelris.removeAll();

        JPanel cardsPanel = new JPanel();
        cardsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        cardsPanel.setBackground(Color.WHITE);

        if (lista.isEmpty()) {
            cardsPanel.add(new JLabel("Nessun ToDo presente."));
        } else {
            for (ToDo t : lista) {
                JPanel card = new JPanel();
                card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                card.setMaximumSize(new Dimension(350, Integer.MAX_VALUE));
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(90, 90, 90), 2, true),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));

                // Colore sfondo
                String colore = t.getColoresfondo();
                if (colore != null) {
                    switch (colore.toLowerCase()) {
                        case "rosso": card.setBackground(Color.RED); break;
                        case "giallo": card.setBackground(Color.YELLOW); break;
                        case "blu": card.setBackground(Color.BLUE); break;
                        default: card.setBackground(Color.LIGHT_GRAY);
                    }
                    card.setOpaque(true);
                }

                card.add(new JLabel("Titolo: " + t.getTitolo()));
                card.add(new JLabel("Descrizione: " + t.getDescrizione()));
                card.add(new JLabel("URL: " + t.getUrl()));
                card.add(new JLabel("Posizione: " + t.getPosizione()));

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String dataString = t.getDatascadenza() != null ? sdf.format(t.getDatascadenza().getTime()) : "N/D";
                card.add(new JLabel("Data Scadenza: " + dataString));
                card.add(new JLabel("Immagine: " + t.getImage()));
                card.add(new JLabel("Stato: " + t.getStato()));
                card.add(new JLabel("Colore Sfondo: " + t.getColoresfondo()));

                JButton modificaButton = new JButton("Modifica");
                modificaButton.setBackground(new Color(255, 200, 80));
                modificaButton.setForeground(Color.BLACK);
                modificaButton.setFocusPainted(false);
                modificaButton.addActionListener(ev -> {
                    ModificaToDo modificaGui = new ModificaToDo(controller, frameVista, bacheca, utenteLoggato, t);
                    modificaGui.frameModificaToDo.setVisible(true);
                    frameVista.setVisible(false);
                });
                card.add(Box.createVerticalStrut(5));
                card.add(modificaButton);

                JButton eliminaButton = new JButton("Elimina");
                eliminaButton.setBackground(new Color(255, 80, 80));
                eliminaButton.setForeground(Color.BLACK);
                eliminaButton.setFocusPainted(false);
                eliminaButton.addActionListener(ev -> {
                    int conferma = JOptionPane.showConfirmDialog(frameVista, "Vuoi eliminare questo ToDo?", "Conferma", JOptionPane.YES_NO_OPTION);
                    if (conferma == JOptionPane.YES_OPTION) {
                        controller.eliminaToDo(bacheca, t);
                        aggiornaListaToDo();
                    }
                });
                card.add(Box.createVerticalStrut(5));
                card.add(eliminaButton);

                JButton spostaButton = new JButton("Sposta in un altra Bacheca");
                spostaButton.setBackground(new Color(80, 150, 255));
                spostaButton.setForeground(Color.BLACK);
                spostaButton.setFocusPainted(false);
                spostaButton.addActionListener(ev -> {
                    String[] bacheche = {"Università", "Lavoro", "Tempo Libero"};
                    String scelta = (String) JOptionPane.showInputDialog(
                            frameVista,
                            "Scegli bacheca di destinazione: ",
                            "Sposta To Do",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            bacheche,
                            bacheche[0]
                    );
                    if(scelta != null && !scelta.equalsIgnoreCase(bacheca.getTitolo().toString())){
                        Bacheca destinazione = controller.getBachecaPerUtente(utenteLoggato, scelta);
                        if(destinazione != null) {
                            controller.spostaToDoInAltraBacheca(t, bacheca, destinazione);
                            aggiornaListaToDo();
                            JOptionPane.showMessageDialog(frameVista, "ToDo spostato in'" + scelta + "'");
                        } else {
                            JOptionPane.showMessageDialog(frameVista, "Errore: bacheca non trovata.");
                        }
                    }
                });
                card.add(Box.createVerticalStrut(5));
                card.add(spostaButton);

                cardsPanel.add(card);
                cardsPanel.add(Box.createHorizontalStrut(20));
            }
        }

        todoPanelris.add(cardsPanel);
        todoPanelris.revalidate();
        todoPanelris.repaint();
    }
}
