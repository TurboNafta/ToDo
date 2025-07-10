package gui;

import model.Attivita;
import model.StatoAttivita;
import model.ToDo;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;


public class FinestraChecklist extends JDialog {
    private JPanel mainPanel;
    private JCheckBox checkBox1;
    private JTextField textField1;
    private JPanel checklistPanel;
    private JTextField attivitaField;
    private JButton aggiungiButton;
    private JButton okButton;

    //private JFrame frame;
    private ArrayList<Attivita> attivita;
    private ArrayList<JCheckBox> checkboxes;

    private boolean okPressed = false;
    private ToDo todoAssociato;


    public FinestraChecklist(ArrayList<Attivita> attivitaIniziale,ToDo todo, Frame owner) {
        super(owner, "Checklist", true);
        attivita = new ArrayList<>(attivitaIniziale != null ? attivitaIniziale : new ArrayList<>());
        checkboxes = new ArrayList<>();
        this.todoAssociato=todo;

        mainPanel = new JPanel(new BorderLayout());

        checklistPanel = new JPanel();
        checklistPanel.setLayout(new BoxLayout(checklistPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(checklistPanel);


        attivitaField = new JTextField(20);
        aggiungiButton = new JButton("Aggiungi attività");
        okButton = new JButton("OK");

        JPanel inputPanel = new JPanel();
        inputPanel.add(attivitaField);
        inputPanel.add(aggiungiButton);

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(okButton, BorderLayout.SOUTH);

        this.setContentPane(mainPanel);
        this.setSize(400, 400);
        this.setLocationRelativeTo(owner);

        aggiornaChecklist();

        aggiungiButton.addActionListener(e -> {
            String text = attivitaField.getText().trim();
            if (!text.isEmpty()) {
                attivita.add(new Attivita(text, StatoAttivita.NONCOMPLETATA));
                attivitaField.setText("");
                aggiornaChecklist();
            }
        });

        okButton.addActionListener(e -> {
            aggiornaDallaCheckBox();
            okPressed = true;
            this.dispose();
        });
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }
    public FinestraChecklist(ArrayList<Attivita> attivitaIniziale) {
        this(attivitaIniziale, null, null); // Pass null for ToDo and owner if no specific parent
    }

    private void aggiornaChecklist() {
        checklistPanel.removeAll();
        checkboxes.clear();
        for (Attivita item : attivita) {
            JCheckBox cb = new JCheckBox(item.getTitolo(), item.getStato() == StatoAttivita.COMPLETATA);
            checkboxes.add(cb);
            checklistPanel.add(cb);
        }
        checklistPanel.revalidate();
        checklistPanel.repaint();
    }

    private void aggiornaDallaCheckBox() {
        for(int i = 0; i < attivita.size(); i++){
            attivita.get(i).setStato(checkboxes.get(i).isSelected() ? StatoAttivita.COMPLETATA : StatoAttivita.NONCOMPLETATA);
        }
    }


    public boolean isOkPressed(){
        return okPressed;
    }

    public ArrayList<Attivita> getAttivita() {
        //aggiornaDallaCheckBox();
        return attivita;
    }
}