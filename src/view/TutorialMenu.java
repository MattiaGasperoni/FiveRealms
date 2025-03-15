package view;

import javax.swing.*;
import java.awt.*;

public class TutorialMenu extends JFrame {

    public TutorialMenu() {
        // Imposta il frame come JFrame (la finestra principale)
        setTitle("Tutorial Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 800);
        setLocationRelativeTo(null);
        setResizable(true);

        // Set background and layout
        JLabel backgroundLabel = new JLabel(new ImageIcon("images/Background/background4.jpg"));
        backgroundLabel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Etichetta informativa centrata in alto
        JLabel infoLabel = new JLabel("Play tutorial easy level?", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Arial", Font.BOLD, 20));
        infoLabel.setForeground(Color.WHITE);
        gbc.gridy = 0; // Posiziona l'etichetta in alto
        backgroundLabel.add(infoLabel, gbc);

        // Pulsanti
        JButton YesButton = createButton("Yes", e -> System.out.println("Yes"));
        JButton NoButton = createButton("No", e -> System.out.println("No"));
        JButton exitButton = createButton("Exit", e -> System.exit(0));

        // Posizione dei pulsanti
        gbc.gridy = 1;
        backgroundLabel.add(YesButton, gbc);
        gbc.gridy = 2;
        backgroundLabel.add(NoButton, gbc);
        gbc.gridy = 3;
        backgroundLabel.add(exitButton, gbc);

        // Aggiungi il backgroundLabel alla finestra (this è la finestra corrente)
        add(backgroundLabel);
        setVisible(true); // Rende visibile la finestra
    }

    private static JButton createButton(String text, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(139, 69, 19));
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 50));
        button.addActionListener(action);
        return button;
    }

    public static void main(String[] args) {
        // Avvia il menu tutorial
        SwingUtilities.invokeLater(TutorialMenu::new);
    }
}
