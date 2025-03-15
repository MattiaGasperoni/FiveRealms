package view;

import javax.swing.*;
import java.awt.*;

public class LoadGameMenu extends JFrame{

    public LoadGameMenu() {
        // Crea un nuovo JFrame
        JFrame frame = new JFrame("Load Game");
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);

        // Sfondo (puoi usare lo stesso sfondo di GraphicsMenu)
        JLabel backgroundLabel = new JLabel(new ImageIcon("images/Background/background4.jpg"));
        backgroundLabel.setLayout(new GridBagLayout());

        // Pannello per i bottoni
        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setOpaque(false);

        // Creiamo i bottoni
        panel.add(createButton("Level 1", e -> System.out.println("Inizia partita Level 1")));
        panel.add(createButton("Level 2", e -> System.out.println("Inizia partita Level 2")));
        panel.add(createButton("Level 3", e -> System.out.println("Inizia partita Level 3")));
        panel.add(createButton("Level 4", e -> System.out.println("Inizia partita Level 4")));
        panel.add(createButton("Exit", e -> System.exit(0)));

        // Posizionamento dei pulsanti al centro
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridy = 0;
        backgroundLabel.add(panel, gbc);

        // Etichetta informativa in basso (centrata, bianca, grande)
        JLabel infoLabel = new JLabel("Click a save to load", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Arial", Font.BOLD, 20));
        infoLabel.setForeground(Color.WHITE);
        gbc.gridy = 1;
        backgroundLabel.add(infoLabel, gbc);

        // Aggiungi il pannello al frame
        frame.add(backgroundLabel);
        frame.setVisible(true); // Mostra la finestra
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
}
