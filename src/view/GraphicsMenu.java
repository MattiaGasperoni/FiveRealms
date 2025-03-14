package view;

import javax.swing.*;

import model.gameStatus.Game;

import java.awt.*;

public class GraphicsMenu {
	
	private static Game g;
	
    public static void main(String[] args) {
        // Avvia il menù
        startMenu();
    }

    public static void startMenu() {
        JFrame frame = new JFrame("Grapichs Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);

        // Pannello personalizzato per il background
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon background = new ImageIcon("images/Background/background4.jpg"); // Cambia con il percorso della tua immagine
                g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Creazione dei pulsanti
        JButton startButton = createStyledButton("Gioca");
        JButton loadButton = createStyledButton("Carica Partita");
        JButton exitButton = createStyledButton("Esci");

        // Aggiunta azioni ai pulsanti
        startButton.addActionListener(e -> g.startGame());
        loadButton.addActionListener(e -> System.out.println("Partita Caricata!"));
        exitButton.addActionListener(e -> System.exit(0));

        // Posizionamento dei pulsanti
        gbc.gridy = 0;
        panel.add(startButton, gbc);
        gbc.gridy = 1;
        panel.add(loadButton, gbc);
        gbc.gridy = 2;
        panel.add(exitButton, gbc);

        frame.add(panel);
        frame.setVisible(true);
    }

    private static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(139, 69, 19)); // Marrone stile legno
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 50));
        return button;
    }
}
