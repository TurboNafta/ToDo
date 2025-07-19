package gui;

import dao.AttivitaDAO;
import model.Attivita;
import model.StatoAttivita;
import model.ToDo;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe per la GUI di finestrachecklist, ci permette di aggiungere delle attività alla checklist, e di spuntarle per indicare che
 * sono completate
 */
public class FinestraChecklist extends JDialog {
    private JPanel mainPanel;
    private JCheckBox checkBox1;
    private JTextField textField1;
    private JPanel checklistPanel;
    private JTextField attivitaField;
    private JButton aggiungiButton;
    private JButton okButton;
    private JButton xButton;

    private final transient ArrayList<Attivita> attivita;
    private final ArrayList<JCheckBox> checkboxes;
    private boolean okPressed = false;
    private final transient ToDo todoAssociato;
    private static final Logger LOGGER = Logger.getLogger(FinestraChecklist.class.getName());
    /**
     * Costruttore la gui FinestraCheckList
     */
    public FinestraChecklist(List<Attivita> attivitaIniziale, ToDo todo, Frame owner) {
        super(owner, "Checklist", true);
        this.attivita = new ArrayList<>(attivitaIniziale != null ? attivitaIniziale : new ArrayList<>());
        this.checkboxes = new ArrayList<>();
        this.todoAssociato = todo;
        initializeUI();
        setupListeners();
        aggiornaChecklist();
    }

    /**
     * Metodo per inizializzare la GUI
     */
    private void initializeUI() {
        mainPanel = new JPanel();
        checklistPanel = new JPanel();
        attivitaField = new JTextField(20);
        aggiungiButton = new JButton("Aggiungi attività");
        okButton = new JButton("OK");

        mainPanel.setLayout(new BorderLayout());
        checklistPanel.setLayout(new BoxLayout(checklistPanel, BoxLayout.Y_AXIS));
        checklistPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        JScrollPane scrollPane = new JScrollPane(checklistPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(attivitaField);
        inputPanel.add(aggiungiButton);

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(okButton, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setSize(400, 400);
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    /**
     * Setup dei pulsanti per aggiungere le attività e salvarle
     */
    private void setupListeners() {
        // Listener per il campo di testo
        attivitaField.addActionListener(_ -> aggiungiAttivita());

        // Listener per il pulsante Aggiungi
        aggiungiButton.addActionListener(_ -> aggiungiAttivita());

        // Listener per il pulsante OK
        okButton.addActionListener(_ -> {
            aggiornaDallaCheckBox();
            okPressed = true;
            dispose();
        });
    }

    /**
     * Metodo che ci aggiunge l'attività alla checklist
     */
    private void aggiungiAttivita() {
        String text = attivitaField.getText().trim();
        if (!text.isEmpty()) {
            Attivita nuovaAttivita = new Attivita(text, StatoAttivita.NONCOMPLETATA);
            attivita.add(nuovaAttivita);
            attivitaField.setText("");
            aggiornaChecklist();

        }
    }

    /**
     * Metodo che aggiorna la checklist a seconda delle attività e del relativo stato
     */
    private void aggiornaChecklist() {
        checklistPanel.removeAll();
        checkboxes.clear();

        for (Attivita item : attivita) {
            JPanel itemPanel = new JPanel(new BorderLayout());
            JCheckBox cb = new JCheckBox(item.getTitolo(), item.getStato() == StatoAttivita.COMPLETATA);

            JButton removeButton = creaRemoveButton(item);

            itemPanel.add(cb, BorderLayout.CENTER);
            itemPanel.add(removeButton, BorderLayout.EAST);

            checkboxes.add(cb);
            checklistPanel.add(itemPanel);
        }

        checklistPanel.revalidate();
        checklistPanel.repaint();
    }

    private JButton creaRemoveButton(Attivita item){
        JButton removeButton = new JButton("X");
        removeButton.setPreferredSize(new Dimension(20, 20));
        removeButton.addActionListener(_ -> {
            // Se l'attività esiste già nel DB (cioè ha id > 0), elimino dal DB
            if (item.getId() > 0) {
                try {
                    new AttivitaDAO().eliminaAttivitaById(item.getId());
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Eccezione catturata", ex);
                    JOptionPane.showMessageDialog(this, "Errore durante l'eliminazione dal database: " + ex.getMessage());
                }
            }
            attivita.remove(item);
            aggiornaChecklist();
        });
        return removeButton;
    }

    private void aggiornaDallaCheckBox() {
        for (int i = 0; i < attivita.size(); i++) {
            if (i < checkboxes.size()) {
                attivita.get(i).setStato(
                        checkboxes.get(i).isSelected() ? StatoAttivita.COMPLETATA : StatoAttivita.NONCOMPLETATA
                );
            }
        }
    }

    public List<Attivita> getAttivita() {
        return new ArrayList<>(attivita); // Ritorna una copia della lista
    }
}