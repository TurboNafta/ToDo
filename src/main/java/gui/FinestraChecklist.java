package gui;

import model.Attivita;
import model.StatoAttivita;
import model.ToDo;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

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

    private final ArrayList<Attivita> attivita;
    private final ArrayList<JCheckBox> checkboxes;
    private boolean okPressed = false;
    private final ToDo todoAssociato;

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
        mainPanel = new JPanel(new BorderLayout());
        checklistPanel = new JPanel();
        checklistPanel.setLayout(new BoxLayout(checklistPanel, BoxLayout.Y_AXIS));

        checklistPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        JScrollPane scrollPane = new JScrollPane(checklistPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        attivitaField = new JTextField(20);
        aggiungiButton = new JButton("Aggiungi attività");
        okButton = new JButton("OK");

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
        attivitaField.addActionListener(e -> aggiungiAttivita());

        // Listener per il pulsante Aggiungi
        aggiungiButton.addActionListener(e -> aggiungiAttivita());

        // Listener per il pulsante OK
        okButton.addActionListener(e -> {
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

            //  pulsante per rimuovere l'attività
            JButton removeButton = new JButton("X");
            removeButton.setPreferredSize(new Dimension(20, 20));
            removeButton.addActionListener(e -> {
                attivita.remove(item);
                aggiornaChecklist();
            });

            itemPanel.add(cb, BorderLayout.CENTER);
            itemPanel.add(removeButton, BorderLayout.EAST);

            checkboxes.add(cb);
            checklistPanel.add(itemPanel);
        }

        checklistPanel.revalidate();
        checklistPanel.repaint();
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