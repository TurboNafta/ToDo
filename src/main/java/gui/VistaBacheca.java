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
import java.util.Calendar;

public class VistaBacheca {
    private JPanel principale;
    private JPanel ricerca;
    private JTextField textField1;
    private JComboBox <String> comboBoxOrdina;
    private JButton tornaAllaHomeButton;
    private JButton creaToDoButton;
    private JButton buttonCerca;
    private JPanel todoPanelris;
    private JComboBox RicercacomboBox1;
    private JLabel OrdinaLabel;

    //JFrame e Controller
    public JFrame frameVista, frameChiamante;
    private Controller controller;
    private String utenteLoggato;
    private Bacheca bacheca;


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
        ricerca.add(new JLabel("Ordina per:"));
        ricerca.add(comboBoxOrdina);

        //combobox filtro ordina
        comboBoxOrdina.removeAllItems();
        comboBoxOrdina.addItem("Nessun Ordinamento");
        comboBoxOrdina.addItem("Titolo A-Z");
        comboBoxOrdina.addItem("Data di Scadenza");
        comboBoxOrdina.addItem("Posizione");
        comboBoxOrdina.addItem("Stato Completamento");

        // Abilita/disabilita la barra di ricerca in base al criterio
        RicercacomboBox1.addActionListener(e -> {
            String criterioRicerca = (String) RicercacomboBox1.getSelectedItem();
            if ("Titolo".equals(criterioRicerca) || "In scadenza entro".equals(criterioRicerca)) {
                textField1.setEnabled(true);
            } else {
                textField1.setText("");
                textField1.setEnabled(false);
            }
        });
        // Default: Titolo selezionato
        RicercacomboBox1.setSelectedIndex(0);
        textField1.setEnabled(true);

        //ordinamento live sulla lista generale
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

        //Bottone CERCA con filtro e ordinamento
        buttonCerca.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //ArrayList<ToDo> toDoDaCercare = controller.getToDoPerBachecaUtente(utenteLoggato, bacheca, textField1.getText());
                String criterioOrdine = (String) comboBoxOrdina.getSelectedItem();
                String criterioRicerca = (String) RicercacomboBox1.getSelectedItem();
                String testo = textField1.getText().trim();
                ArrayList<ToDo> risultati = new ArrayList<>();

                //RICERCA PER TITOLO
                try {
                    if ("Titolo".equals(criterioRicerca)) {
                        risultati = controller.getToDoPerBachecaUtente(utenteLoggato, bacheca, testo);
                    } else if ("Scadenza oggi".equals(criterioRicerca)) {
                        risultati = controller.getToDoInScadenzaOggi(utenteLoggato, bacheca);
                    } else if ("In scadenza entro".equals(criterioRicerca)) {
                        risultati = controller.getToDoInScadenzaEntro(utenteLoggato, bacheca, testo);
                    }
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(frameVista, ex.getMessage(), "Errore ricerca", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                //ORDINAMENTO
                if (criterioOrdine != null) {
                    switch (criterioOrdine) {
                        case "Titolo A-Z":
                            risultati.sort(Comparator.comparing(ToDo::getTitolo, String.CASE_INSENSITIVE_ORDER));
                            break;
                        case "Data di Scadenza":
                            risultati.sort(Comparator.comparing(ToDo::getDatascadenza));
                            break;
                        case "Posizione"://la posizione che si intende?????????????????????
                            risultati.sort(Comparator.comparing(t -> parseIntSafe(t.getPosizione())));
                            break;
                        case "Stato Completamento":
                            risultati.sort(Comparator.comparing(ToDo::getStato));//fare qualcosa x mettere prima i nn completati
                            break;
                    }
                }
                mostraListaToDo(risultati);
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

        // Mostra all'avvio
        aggiornaListaToDo();

    }
    // Mostra tutti i ToDo della bacheca con eventuale ordinamento
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
                    lista.sort(Comparator.comparing(t -> parseIntSafe(t.getPosizione())));
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
    // Converte posizione, gestendo errori
    private int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception ex) {
            return 0;
        }
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

                JLabel labelTitolo = new JLabel(t.getTitolo());
                Calendar oggi = Calendar.getInstance();
                if (t.getDatascadenza().before(oggi)) {
                    labelTitolo.setForeground(Color.RED);
                } else {
                    labelTitolo.setForeground(Color.BLACK);
                }
                card.add(labelTitolo);


                // Colore sfondo dei todo
                String colore = t.getColoresfondo();
                if (colore != null) {
                    switch (colore.toLowerCase()) {
                        case "rosso":
                            card.setBackground(new Color(255, 153, 153)); // rosso chiaro
                            break;
                        case "giallo":
                            card.setBackground(new Color(255, 255, 153)); // giallo chiaro
                            break;
                        case "blu":
                            card.setBackground(new Color(153, 204, 255)); // azzurro chiaro
                            break;
                        case "verde":
                            card.setBackground(new Color(153, 255, 153)); // verde chiaro
                            break;
                        case "arancione":
                            card.setBackground(new Color(255, 204, 153)); // arancione chiaro
                            break;
                        case "rosa":
                            card.setBackground(new Color(255, 204, 229)); // rosa pastello
                            break;
                        case "viola":
                            card.setBackground(new Color(204, 153, 255)); // lilla
                            break;
                        case "celeste":
                            card.setBackground(new Color(204, 255, 255)); // celeste chiaro
                            break;
                        case "marrone":
                            card.setBackground(new Color(210, 180, 140)); // marroncino chiaro
                            break;
                        default: card.setBackground(Color.LIGHT_GRAY);
                    }
                    card.setOpaque(true);
                } else {
                    card.setBackground(Color.LIGHT_GRAY);
                }



                //CARD DEL TODO
                card.add(new JLabel("Titolo: " + t.getTitolo()));
                card.add(new JLabel("Descrizione: " + t.getDescrizione()));
                card.add(new JLabel("URL: " + t.getUrl()));
                //card.add(new JLabel("Posizione: " + t.getPosizione()));
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String dataString = t.getDatascadenza() != null ? sdf.format(t.getDatascadenza().getTime()) : "N/D";
                card.add(new JLabel("Data Scadenza: " + dataString));
                card.add(new JLabel("Immagine: " + t.getImage()));
                card.add(new JLabel("Stato: " + t.getStatoString()));

                // Pulsanti disponibili solo se autore
                if (t.getAutore() != null && t.getAutore().getUsername().equals(utenteLoggato)) {
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
                    //aggiungo a card
                    card.add(Box.createVerticalStrut(5));
                    card.add(modificaButton);

                    //BUTTON ELIMINA
                    JButton eliminaButton = new JButton("Elimina");
                    eliminaButton.setBackground(new Color(255, 80, 80));
                    eliminaButton.setForeground(Color.BLACK);
                    eliminaButton.setFocusPainted(false);
                    eliminaButton.addActionListener(ev -> {
                        int conferma = JOptionPane.showConfirmDialog(frameVista, "Vuoi eliminare questo ToDo?", "Conferma", JOptionPane.YES_NO_OPTION);
                        if (conferma == JOptionPane.YES_OPTION) {
                            controller.eliminaToDo(bacheca, t);
                            aggiornaListaToDo();//buttonCerca.doClick()
                        }
                    });
                    // aggiungo il bottone a card
                    card.add(Box.createVerticalStrut(5));
                    card.add(eliminaButton);
                    //cardsPanel.add(card);

                    //BUTTON SPOSTA
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
                        if (scelta != null && !scelta.equalsIgnoreCase(bacheca.getTitolo().toString())) {
                            Bacheca destinazione = controller.getBachecaPerUtente(utenteLoggato, scelta);
                            if (destinazione != null) {
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
                    //cardsPanel.add(Box.createHorizontalStrut(20));
                }

                //BOTTONE PER VEDERE CONDIVISIONI
                ArrayList<Utente> utentiCondivisi = new ArrayList<>(t.getUtentiPossessori());
                //utentiCondivisi.removeIf(u -> u.getUsername().equals(t.getAutore().getUsername()));
                if (!utentiCondivisi.isEmpty()) {
                    JButton condivisiButton = new JButton("Condiviso con...");
                    condivisiButton.setBackground(new Color(120, 200, 255));
                    condivisiButton.setForeground(Color.BLACK);
                    condivisiButton.setFocusPainted(false);
                    condivisiButton.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
                    condivisiButton.addActionListener(ev -> {
                        StringBuilder sb = new StringBuilder();

                        //CI RIEMPIE LA LISTA DI UTENTI CHE HANNO LA CONDIVISIONE
                        for (Utente u : utentiCondivisi) {
                            sb.append(u.getUsername()).append("\n");
                        }

                        JOptionPane.showMessageDialog(frameVista,
                                sb.length() > 0 ? sb.toString() : "Non condiviso con nessuno.",
                                "Utenti con cui è condiviso", JOptionPane.INFORMATION_MESSAGE);
                    });
                    card.add(Box.createVerticalStrut(5));
                    card.add(condivisiButton);
                }

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

    /*private void mostraToDo (ArrayList < ToDo > lista) {
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
              /*  if (t.getAutore() != null && t.getAutore().getUsername().equals(utenteLoggato)) {
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
                }

                //BOTTONE PER VEDERE CONDIVISIONI
                ArrayList<Utente> utentiCondivisi = new ArrayList<>(t.getUtentiPossessori());
                //utentiCondivisi.removeIf(u -> u.getUsername().equals(t.getAutore().getUsername()));
                if (!utentiCondivisi.isEmpty()) {
                    JButton condivisiButton = new JButton("Condiviso con...");
                    condivisiButton.setBackground(new Color(120, 200, 255));
                    condivisiButton.setForeground(Color.BLACK);
                    condivisiButton.setFocusPainted(false);
                    condivisiButton.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
                    condivisiButton.addActionListener(ev -> {
                        StringBuilder sb = new StringBuilder();

                        if (t.getAutore() != null && t.getAutore().getUsername().equals(utenteLoggato)) {
                            // L'autore vede tutti gli utenti (escluso sé stesso)
                            for (Utente u : utentiCondivisi) {
                                sb.append(u.getUsername()).append("\n");
                            }
                        } else {
                            // Un utente condiviso vede solo sé stesso
                            if (utentiCondivisi.stream().anyMatch(u -> u.getUsername().equals(utenteLoggato))) {
                                sb.append(utenteLoggato).append("\n");
                            }
                        }

                        JOptionPane.showMessageDialog(frameVista,
                                sb.length() > 0 ? sb.toString() : "Non condiviso con nessuno.",
                                "Utenti con cui è condiviso", JOptionPane.INFORMATION_MESSAGE);
                    });
                    card.add(Box.createVerticalStrut(5));
                    card.add(condivisiButton);
                }

                //PRENDE IL COLORE IN INPUT E LO METTE COME SFONDO
                cardsPanel.add(card);
                cardsPanel.add(Box.createHorizontalStrut(20));

            }
        }
        todoPanelris.add(cardsPanel);
        todoPanelris.revalidate();
        todoPanelris.repaint();

    }
}*/
