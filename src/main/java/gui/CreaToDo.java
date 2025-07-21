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
     * Costruttore della GUI di creazione To Do.
     * Inizializza componenti, listener e popola la lista utenti per la condivisione.
     * @param controller Controller dell'applicazione
     * @param frame Finestra chiamante
     * @param bacheca Bacheca di riferimento
     * @param utente Username dell'utente creatore
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
        for (Utente u : controller.getTuttiUtentiDalDB()) {
            if (!u.getUsername().equals(utente))
                utentiModel.addElement(u.getUsername());
        }
        utentiList.setModel(utentiModel);
        utentiList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        addToDoButton.addActionListener(_ -> {
            try {
                gestisciAddButton();
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

    /**
     * Gestisce la logica di creazione del To Do.
     * Esegue la validazione dei campi e, se tutto è corretto, aggiunge il ToDo alla bacheca e passa alla vista bacheca.
     */
    private void gestisciAddButton(){
        String dataStr = textFieldData.getText().trim();
        String posizioneStr = textFieldPosizione.getText().trim();
        String coloreStr = textFieldColore.getText().trim();

        if(!validaData(dataStr) || !validaPosizione(posizioneStr) || !validaColore(coloreStr)){
            return;
        }

        String titolo = textFieldTitolo.getText().trim();
        String descrizione = textFieldDescrizione.getText().trim();
        String dataScadenza = dataStr;
        String img = textFieldImg.getText().trim();
        String posizione = posizioneStr;
        String url = textFieldUrl.getText().trim();
        String colore = coloreStr;

       if(!validaCampi(titolo, descrizione, dataScadenza, posizione)){
           return;
       }

       if(isPosizioneOccupata(posizione)){
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

    /**
     * Valida che i campi principali non siano vuoti.
     */
    private boolean validaCampi(String titolo, String descrizione, String dataScadenza, String posizione){
        if(titolo.isEmpty() || descrizione.isEmpty() || dataScadenza.isEmpty() || posizione.isEmpty()){
            JOptionPane.showMessageDialog(frameCreaToDo, "II campi Titolo, Descrizione, Data Scadenza e Posizione sono obbligatori.", "Campi mancanti", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Valida che la data sia nel formato corretto.
     */
    private boolean validaData(String dataStr){
        if(!controller.isValidData(dataStr)){
            JOptionPane.showMessageDialog(frameCreaToDo, "Inserisci la data nel formato gg/mm/aaaa", "Errore formato data", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Valida che la posizione sia un numero positivo.
     */
    private boolean validaPosizione(String posizioneStr){
        if(!controller.isValidPosizione(posizioneStr)){
            JOptionPane.showMessageDialog(frameCreaToDo, "La posizione deve essere un numero intero positivo", "Errore formato posizione", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Valida che il colore inserito sia tra quelli consentiti.
     */
    private boolean validaColore(String coloreStr){
        if(!controller.isValidColore(coloreStr)){
            JOptionPane.showMessageDialog(frameCreaToDo, "Colore non valido. Colori disponibili: rosso, giallo, blu, verde, arancione, rosa, viola, celeste, marrone", "Errore formato colore", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Controlla che la posizione inserita non sia già occupata da un altro ToDo nella bacheca.
     */
    private boolean isPosizioneOccupata(String posizione){
        int nuovaPosizioneInt = Integer.parseInt(posizione);
        for(ToDo t : bacheca.getTodo()){
            if(t.getPosizione() != null && controller.isValidPosizione(t.getPosizione())){
                int exdistingPosizioneInt = Integer.parseInt(t.getPosizione());
                if(exdistingPosizioneInt == nuovaPosizioneInt){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Converte una stringa data in un oggetto GregorianCalendar.
     */
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

    /**
     * Costruisce la lista degli utenti con cui condividere il To Do, incluso l'autore.
     */
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
