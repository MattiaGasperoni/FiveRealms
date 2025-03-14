package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GrapichsMenu {

    public static void main(String[] args) {
        // Creazione della finestra principale
        JFrame frame = new JFrame("Grapichs Menù");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null); // Centra la finestra

        // Pannello per il contenuto
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10)); // Layout a griglia (4 righe, 1 colonna)

        // Pulsante per avviare la partita
        JButton startButton = new JButton("Avvia Gioco");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Logica per avviare il gioco
                System.out.println("Gioco Avviato!");
            }
        });

        // Pulsante per caricare una partita
        JButton loadButton = new JButton("Carica Partita");
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Logica per caricare la partita
                System.out.println("Partita Caricata!");
            }
        });

        // Pulsante per salvare una partita
        JButton saveButton = new JButton("Salva Partita");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Logica per salvare la partita
                System.out.println("Partita Salvata!");
            }
        });

        // Pulsante per uscire dal gioco
        JButton exitButton = new JButton("Esci");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Logica per uscire dal gioco
                System.exit(0);
            }
        });

        // Aggiungere i pulsanti al pannello
        panel.add(startButton);
        panel.add(loadButton);
        panel.add(saveButton);
        panel.add(exitButton);

        // Aggiungere il pannello alla finestra
        frame.add(panel);

        // Rendere la finestra visibile
        frame.setVisible(true);
    }
}
