package gui;
import controller.Controller;
import model.*;
import javax.swing.*;
import java.util.*;

/**
 * Classe per la creazione di un nuovo to do all'interno della bacheca selezionata
 */
public class CreaToDo {
    private JPanel panel1;
    private JLabel TitoloLabel;
    private JLabel DescrizioneLabel;
    private JLabel ScadenzaLabel;
    private JPanel ImgLabel;
    private JLabel PosizioneLabel;
    private JLabel URLLabel;
    private JLabel ColoreLabel;
    private JButton addToDoButton;
    private JTextField textFieldTitolo;
    private JTextField textFieldDescrizione;
    private JTextField textFieldData;
    private JTextField textFieldImg;
    private JTextField textFieldPosizione;
    private JTextField textFieldUrl;
    private JTextField textFieldColore;

    private final JFrame frameCreaToDo;
    private final JFrame frameChiamante;

    public JFrame getFrameCreaToDo() {
        return frameCreaToDo;
    }
    public JFrame getFrameChiamante() {
        return frameChiamante;
    }
    private Controller controller;
    private Bacheca bacheca;
    private String utente;
    private JList<String> utentiList;
    private JButton checklistButton;
    private JButton buttonAnnulla;
    private CheckList checklistTemp = new CheckList(null);  // ci servirà per accumulare dati

    /**
     * Costruttore della classe GUI CreaToDo
     */
    public CreaToDo(Controller controller, JFrame frame, Bacheca bacheca, String utente) {
        this.controller = controller;
        this.bacheca=bacheca;
        this.utente = utente;
        frameChiamante = frame;

        frameCreaToDo = new JFrame("PaginaInserimento");
        frameCreaToDo.setContentPane(panel1);
        frameCreaToDo.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frameCreaToDo.pack();
        frameCreaToDo.setLocationRelativeTo(null);
        frameCreaToDo.setVisible(true);

        /**
         * Pulsante che ci permette di aprire la pagina per la checklist ed inserire attività
         */
        checklistButton.addActionListener(e->{
            // Lancia la finestra checklist passando la lista attività attuale, e passo null perchè il to do deve ancora essere creato
            FinestraChecklist checklist = new FinestraChecklist(checklistTemp.getAttivita(),null,frameCreaToDo);
            checklist.setVisible(true);
            // All'uscita aggiorna la checklist temporanea con le attività modificate
            checklistTemp.setAttivita(checklist.getAttivita());
        });

        //Serve a popolare la list per le condivisioni dei To do
        DefaultListModel<String> utentiModel = new DefaultListModel<>();
        for (Utente u : controller.getTuttiUtenti()) {
            if (!u.getUsername().equals(utente))
                utentiModel.addElement(u.getUsername());
        }
        utentiList.setModel(utentiModel);
        utentiList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        /**
         * Pulsante che ci aggiunge il to do in quella bacheca
         */
        addToDoButton.addActionListener(e -> {
            String dataStr = textFieldData.getText().trim();
            if (!controller.isValidDate(dataStr)) {
                JOptionPane.showMessageDialog(frameCreaToDo,
                        "Inserisci la data nel formato gg/MM/aaaa (es: 13/07/2025)",
                        "Errore formato data",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            String posizioneStr = textFieldPosizione.getText().trim();
            if (!controller.isValidPosition(posizioneStr)) {
                JOptionPane.showMessageDialog(frameCreaToDo,
                        "La posizione deve essere un numero intero positivo",
                        "Errore formato posizione",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            String coloreStr = textFieldColore.getText().trim();
            if (!controller.isValidColor(coloreStr)) {
                JOptionPane.showMessageDialog(frameCreaToDo,
                        "Colore non valido. Colori disponibili: rosso, giallo, blu, verde, arancione, rosa, viola, celeste, marrone",
                        "Errore formato colore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Recupera i dati dai campi di testo
                String titolo = textFieldTitolo.getText().trim();
                String descrizione = textFieldDescrizione.getText().trim();
                String dataScadenza = textFieldData.getText().trim();
                String img = textFieldImg.getText().trim();
                String posizione = textFieldPosizione.getText().trim();
                String url = textFieldUrl.getText().trim();
                String colore = textFieldColore.getText().trim();

                // Verifica campi obbligatori
                if (titolo.isEmpty() || descrizione.isEmpty() || dataScadenza.isEmpty() || posizione.isEmpty()) {
                    JOptionPane.showMessageDialog(frameCreaToDo,
                            "I campi Titolo, Descrizione, Data Scadenza e Posizione sono obbligatori",
                            "Campi mancanti",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // recupera l'utente loggato e crea la lista degli utenti possessori
                ArrayList<Utente> utenti = new ArrayList<>();
                Utente utenteCreatore = controller.getUtenteByUsername(utente);
                utenti.add(utenteCreatore);

                // Aggiungi gli utenti selezionati dalla lista
                for (String username : utentiList.getSelectedValuesList()) {
                    Utente u = controller.getUtenteByUsername(username);
                    if (u != null) {
                        utenti.add(u);
                    }
                }
                // Controlla che la posizione non sia già occupata
                boolean posizioneOccupata = false;
                for (ToDo t : bacheca.getTodo()) {
                    if (t.getPosizione().equals(posizione)) {
                        posizioneOccupata = true;
                        break;
                    }
                }

                if (posizioneOccupata) {
                    JOptionPane.showMessageDialog(frameCreaToDo,
                            "Esiste già un ToDo in quella posizione nella bacheca.",
                            "Errore posizione duplicata",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }


                // Crea il nuovo ToDo
                ToDo nuovoToDo = new ToDo(titolo, descrizione, url, dataScadenza, img,
                        posizione, colore, utenti, utenteCreatore);
                nuovoToDo.setStato(StatoToDo.NONCOMPLETATO);

                // Imposta la checklist se presente
                if (checklistTemp != null && !checklistTemp.getAttivita().isEmpty()) {
                    nuovoToDo.setChecklist(checklistTemp);
                }

                // Aggiunge il ToDo alla bacheca
                controller.addToDo(bacheca, nuovoToDo, utente);

                // Chiude la finestra e torna alla vista bacheca
                frameCreaToDo.dispose();
                VistaBacheca vistaBacheca = new VistaBacheca(bacheca, controller, frameChiamante, utente);
                vistaBacheca.frameVista.setVisible(true);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frameCreaToDo,
                        "Errore durante la creazione del ToDo: " + ex.getMessage(),
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        /**
         * Pulsante che ci permette di annullare l'operazione di inserimento
         */
        buttonAnnulla.addActionListener(e -> {
            frameCreaToDo.dispose();
            VistaBacheca vistaBacheca = new VistaBacheca(bacheca, controller, frameChiamante, utente);
            vistaBacheca.frameVista.setVisible(true);
        });

    }
}
