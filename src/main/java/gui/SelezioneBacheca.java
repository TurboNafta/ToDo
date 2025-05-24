package gui;

import controller.Controller;
import model.Bacheca;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class SelezioneBacheca {
    private JPanel principale;
    private JComboBox comboBox1;
    private JButton buttonCerca;
    private JLabel titoloLabel;
    private JPanel ricercaPanel;
    private JButton creaNuovaBachecaButton;
    private JPanel creaPanel;
    private JTable tableResult;

    public static JFrame frameBacheca, frameChiamante;
    private Controller controller;
    private ArrayList<Bacheca> listaBacheche;

    public SelezioneBacheca(Controller controller, JFrame frame) {
        comboBox1.addItem("");
        comboBox1.addItem("Università");
        comboBox1.addItem("Lavoro");
        comboBox1.addItem("Tempo Libero");

        this.controller = controller;
        frameChiamante = frame;

        frameBacheca = new JFrame("Seleziona Bacheca");
        frameBacheca.setContentPane(principale);
        frameBacheca.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameBacheca.pack();
        frameBacheca.setVisible(true);

        ModelloTabellaBacheca modello = new ModelloTabellaBacheca();
        tableResult.setModel(modello);

        //PERMETTE DI APRIRE LA PAGINA PER CREARE UNA BACHECA
        creaNuovaBachecaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CreaBacheca terzaGui = new CreaBacheca(controller, frameChiamante);
                terzaGui.frameCreaBacheca.setVisible(true);
                frameBacheca.setVisible(false);
            }
        });

        //PERMETTE DI TROVARE LE BACHECHE CREATE
        buttonCerca.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filtro = (String) comboBox1.getSelectedItem();
                if (filtro == null || filtro.isEmpty()) {
                    // Nessun filtro, mostra tutte le bacheche
                    listaBacheche = controller.getBachecaList();
                } else {
                    // Filtra in base al titolo della bacheca
                    listaBacheche = new ArrayList<>();
                    for (Bacheca b : controller.getBachecaList()) {
                        if (b.getTitolo().toString().equalsIgnoreCase(filtro)) {
                            listaBacheche.add(b);
                        }
                    }
                }
                ModelloTabellaBacheca modello = (ModelloTabellaBacheca) tableResult.getModel();
                modello.settaDatiDaMostrare(listaBacheche);
                modello.fireTableDataChanged();
            }
        });

        // Popola la tabella con le bacheche
        listaBacheche = controller.getBachecaList();
        modello.settaDatiDaMostrare(listaBacheche);

        // MouseListener per la colonna "Apri"
        tableResult.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tableResult.rowAtPoint(evt.getPoint());
                int col = tableResult.columnAtPoint(evt.getPoint());
                if (col == 2) { // Colonna "APRI"
                    ModelloTabellaBacheca modello = (ModelloTabellaBacheca) tableResult.getModel();
                    Bacheca bacheca = modello.getBachecaAt(row);
                    new VistaBacheca(bacheca, controller,frameChiamante);
                    frameBacheca.setVisible(false);
                }
            }
        });
    }
}
