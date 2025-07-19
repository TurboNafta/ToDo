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
    private JLabel titoloLabel;
    private JLabel descrizioneLabel;
    private JLabel scadenzaLabel;
    private JPanel imgLabel;
    private JLabel posizioneLabel;
    private JLabel urlLabel;
    private JLabel coloreLabel;
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
    private final Controller controller;
    private final Bacheca bacheca;
    private final String utente;
    private JList<String> utentiList;
    private JButton checklistButton;
    private JButton buttonAnnulla;
    private final CheckList checklistTemp = new CheckList(null);  // ci servirà per accumulare dati

    private static final String ERRORE_TITLE = "Errore";
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

        checklistButton.addActionListener(_ ->{
            // Lancia la finestra checklist passando la lista attività attuale, e passo null perchè il to do deve ancora essere creato
            FinestraChecklist checklist = new FinestraChecklist(checklistTemp.getAttivita(),null,frameCreaToDo);
            checklist.setVisible(true);
            // All'uscita aggiorna la checklist temporanea con le attività modificate
            checklistTemp.setAttivita(checklist.getAttivita());
        });

        //Serve a popolare la list per le condivisioni dei To do
        DefaultListModel<String> utentiModel = new DefaultListModel<>();
        for (Utente u : controller.getTuttiUtentiFromDB()) {
            if (!u.getUsername().equals(utente))
                utentiModel.addElement(u.getUsername());
        }
        utentiList.setModel(utentiModel);
        utentiList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        addToDoButton.addActionListener(_ -> {
            try {
                handleAddToDoButton();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frameCreaToDo, "Errore durante la creazione del ToDo: " + ex.getMessage(), ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonAnnulla.addActionListener(_ -> {
            frameCreaToDo.dispose();
            VistaBacheca vistaBacheca = new VistaBacheca(bacheca, controller, frameChiamante, utente);
            vistaBacheca.frameVista.setVisible(true);
        });
    }

    private void handleAddToDoButton(){
        String dataStr = textFieldData.getText().trim();
        String posizioneStr = textFieldPosizione.getText().trim();
        String coloreStr = textFieldColore.getText().trim();

        if(!validateData(dataStr) || !validatePosition(posizioneStr) || !validateColor(coloreStr)){
            return;
        }

        String titolo = textFieldTitolo.getText().trim();
        String descrizione = textFieldDescrizione.getText().trim();
        String dataScadenza = dataStr;
        String img = textFieldImg.getText().trim();
        String posizione = posizioneStr;
        String url = textFieldUrl.getText().trim();
        String colore = coloreStr;

       if(!validateFields(titolo, descrizione, dataScadenza, posizione)){
           return;
       }

       if(isPositionOccupied(posizione)){
           JOptionPane.showMessageDialog(frameCreaToDo, "Esiste già un ToDo in quella posizione nella bacheca.", "Errore posizione duplicata", JOptionPane.ERROR_MESSAGE);
           return;
       }

       GregorianCalendar data = getGregorianCalendar(dataStr);
       if(data == null){
           return;
       }

       ArrayList<Utente> utenti = buildUtentiList();
       Utente utenteCreatore = utenti.getFirst();

       ToDo nuovoToDo = new ToDo(titolo, descrizione, url, dataScadenza, img, posizione, colore, utenti, utenteCreatore);
       nuovoToDo.setStato(StatoToDo.NONCOMPLETATO);

       if (!checklistTemp.getAttivita().isEmpty()) {
           nuovoToDo.setChecklist(checklistTemp);
       }
       controller.addToDo(bacheca, nuovoToDo, utenteCreatore.getUsername());

       frameCreaToDo.dispose();
       VistaBacheca vistaBacheca = new VistaBacheca(bacheca, controller, frameChiamante, utente);
       vistaBacheca.frameVista.setVisible(true);
    }

    private boolean validateFields(String titolo, String descrizione, String dataScadenza, String posizione){
        if(titolo.isEmpty() || descrizione.isEmpty() || dataScadenza.isEmpty() || posizione.isEmpty()){
            JOptionPane.showMessageDialog(frameCreaToDo, "II campi Titolo, Descrizione, Data Scadenza e Posizione sono obbligatori.", "Campi mancanti", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean validateData(String dataStr){
        if(!controller.isValidDate(dataStr)){
            JOptionPane.showMessageDialog(frameCreaToDo, "Inserisci la data nel formato gg/mm/aaaa", "Errore formato data", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean validatePosition(String posizioneStr){
        if(!controller.isValidPosition(posizioneStr)){
            JOptionPane.showMessageDialog(frameCreaToDo, "La posizione deve essere un numero intero positivo", "Errore formato posizione", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean validateColor(String coloreStr){
        if(!controller.isValidColor(coloreStr)){
            JOptionPane.showMessageDialog(frameCreaToDo, "Colore non valido. Colori disponibili: rosso, giallo, blu, verde, arancione, rosa, viola, celeste, marrone", "Errore formato colore", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean isPositionOccupied(String posizione){
        int nuovaPosizioneInt = Integer.parseInt(posizione);
        for(ToDo t : bacheca.getTodo()){
            if(t.getPosizione() != null && controller.isValidPosition(t.getPosizione())){
                int exdistingPosizioneInt = Integer.parseInt(t.getPosizione());
                if(exdistingPosizioneInt == nuovaPosizioneInt){
                    return true;
                }
            }
        }
        return false;
    }

    private GregorianCalendar getGregorianCalendar(String dataStr){
        try {
            String[] data = dataStr.split("/");
            int anno = Integer.parseInt(data[2]);
            int mese = Integer.parseInt(data[1]) - 1;
            int gg = Integer.parseInt(data[0]);
            return new GregorianCalendar(anno, mese, gg);
        } catch (Exception _) {
            JOptionPane.showMessageDialog(frameCreaToDo, "Inserisci la data nel formato gg/mm/aaaa", "Errore formato data", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private ArrayList<Utente> buildUtentiList(){
        ArrayList<Utente> utenti = new ArrayList<>();
        Utente utenteCreatore = controller.getUtenteByUsername(utente);
        utenti.add(utenteCreatore);

        for(String username : utentiList.getSelectedValuesList()){
            Utente u = controller.getUtenteByUsername(username);
            if( u != null && !u.getUsername().equals(utente) ){
                utenti.add(u);
            }
        }
        return utenti;
    }
}
