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
    private JComboBox comboBox1;
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
                ArrayList<ToDo> toDoDaCercare = controller.getToDoPerBachecaUtente(utenteLoggato, bacheca, textField1.getText());

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
                        card.setPreferredSize(new Dimension(250, 160));
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

                        //PRENDE IL COLORE IN INPUT E LO METTE COME SFONDO
                        todoPanelris.add(card);
                        todoPanelris.add(Box.createHorizontalStrut(20));
                    }
                }
                todoPanelris.revalidate();
                todoPanelris.repaint();
            }
        });

        //PERMETTE DI TORNARE ALLA HOME
        tornaAllaHomeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameChiamante.setVisible(true);
                frameVista.dispose();
            }
        });
    }
}
