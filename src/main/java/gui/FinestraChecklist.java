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
     * Costruttore della GUI FinestraChecklist.
     * @param attivitaIniziale lista iniziale delle attività (può essere null)
     * @param todo To Do associato (può essere null)
     * @param owner Frame padre
     */
    public FinestraChecklist(List<Attivita> attivitaIniziale, ToDo todo, Frame owner) {
        super(owner, "Checklist", true);
        this.attivita = new ArrayList<>(attivitaIniziale != null ? attivitaIniziale : new ArrayList<>());
        this.checkboxes = new ArrayList<>();
        this.todoAssociato = todo;
        inizializzaGUI();
        setupListeners();
        aggiornaChecklist();
    }

    /**
     * Metodo per inizializzare la GUI
     */
    private void inizializzaGUI() {
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
     * Aggiunge una nuova attività alla checklist.
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
     * Aggiorna la visualizzazione della checklist in base allo stato delle attività.
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

    /**
     * Crea il pulsante "X" per rimuovere un'attività dalla checklist.
     * Se l'attività esiste nel DB, la elimina anche dal DB.
     */
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

    /**
     * Aggiorna lo stato delle attività in base alle checkbox selezionate.
     */
    private void aggiornaDallaCheckBox() {
        for (int i = 0; i < attivita.size(); i++) {
            if (i < checkboxes.size()) {
                attivita.get(i).setStato(
                        checkboxes.get(i).isSelected() ? StatoAttivita.COMPLETATA : StatoAttivita.NONCOMPLETATA
                );
            }
        }
    }

    /**
     * Restituisce la lista aggiornata delle attività della checklist.
     * @return lista delle attività (copia)
     */
    public List<Attivita> getAttivita() {
        return new ArrayList<>(attivita); // Ritorna una copia della lista
    }
}