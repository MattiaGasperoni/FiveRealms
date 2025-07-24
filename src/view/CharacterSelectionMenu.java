package view;

import javax.swing.*;
import model.characters.Character;
import model.gameStatus.Game;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class CharacterSelectionMenu {

    private JFrame frame;
    private JButton nextButton;
    private final List<JPanel> selectedPanels = new ArrayList<>();
    private final List<String> selectedCharacters = new ArrayList<>();

    public void start(List<Character> allAllies) {
        System.out.println("Open Characters Selection Menu Frame ->");

        frame = new JFrame("Characters Selection Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);

        // Sfondo casuale
        String bgPath = "images/background/background" + (int)(Math.random() * 6) + ".jpg";
        ImageIcon bgIcon = new ImageIcon(new ImageIcon(bgPath).getImage().getScaledInstance(800, 800, Image.SCALE_SMOOTH));
        JLabel bgLabel = new JLabel(bgIcon);
        bgLabel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Titolo
        JLabel info = new JLabel("Select 3 characters", SwingConstants.CENTER);
        info.setFont(new Font("Stencil", Font.BOLD, 28));
        info.setForeground(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 0;
        bgLabel.add(info, gbc);

        // Pulsante "Next" con stile medievale
        nextButton = createStyledButton("Next");
        nextButton.setEnabled(false);
        gbc.gridx = 1;
        gbc.gridy = 4;
        bgLabel.add(nextButton, gbc);

        // Personaggi disponibili
        addCharacter(bgLabel, "Archer", "A skilled archer, master of the bow.", "images/characters/archer/archerHero.png", 0, 1, gbc);
        addCharacter(bgLabel, "Barbarian", "A fierce warrior with immense strength.", "images/characters/barbarian/barbarianHero.png", 2, 1, gbc);
        addCharacter(bgLabel, "Juggernaut", "A massive tank, unstoppable force.", "images/characters/juggernaut/juggernautHero.png", 1, 2, gbc);
        addCharacter(bgLabel, "Knight", "A noble knight, brave and strong.", "images/characters/knight/knightHero.png", 0, 3, gbc);
        addCharacter(bgLabel, "Wizard", "A master of the arcane arts.", "images/characters/wizard/wizardHero.png", 2, 3, gbc);

        frame.add(bgLabel);
        frame.setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Stencil", Font.BOLD, 28)); // aumentata dimensione font
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(34, 34, 34));
        button.setBorder(BorderFactory.createLineBorder(Color.GREEN, 3)); // bordo più spesso
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(300, 100)); // aumentata la dimensione

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(44, 44, 44));
                button.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(new Color(34, 34, 34));
                button.setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
            }
        });

        return button;
    }


    private void addCharacter(JLabel bgLabel, String name, String desc, String imgPath, int x, int y, GridBagConstraints gbc) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(true);
        panel.setBackground(new Color(20, 20, 20, 220));
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        JLabel imgLabel = new JLabel(new ImageIcon(new ImageIcon(imgPath).getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH)), SwingConstants.CENTER);
        JLabel lbl = new JLabel("<html><center>" + name + "<br>" + desc + "</center></html>", SwingConstants.CENTER);
        lbl.setFont(new Font("Georgia", Font.BOLD, 12));
        lbl.setForeground(Color.LIGHT_GRAY);

        panel.add(imgLabel, BorderLayout.NORTH);
        panel.add(lbl, BorderLayout.SOUTH);

        // Animazione selezione
        panel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (selectedPanels.contains(panel)) {
                    selectedPanels.remove(panel);
                    selectedCharacters.remove(name);
                    panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
                } else if (selectedPanels.size() < Game.MAX_ALLIES_PER_ROUND) {
                    selectedPanels.add(panel);
                    selectedCharacters.add(name);
                    panel.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 3)); // glow selezione
                }
                nextButton.setEnabled(selectedPanels.size() == Game.MAX_ALLIES_PER_ROUND);
            }

            public void mouseEntered(MouseEvent e) {
                if (!selectedPanels.contains(panel)) {
                    panel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
                }
            }

            public void mouseExited(MouseEvent e) {
                if (!selectedPanels.contains(panel)) {
                    panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
                }
            }
        });

        gbc.gridx = x;
        gbc.gridy = y;
        bgLabel.add(panel, gbc);
    }

    public void addNextButtonListener(ActionListener listener) {
        nextButton.addActionListener(listener);
    }

    public List<String> getSelectedCharacterNames() {
        return new ArrayList<>(selectedCharacters);
    }

    public void close() {
        frame.dispose();
    }
}
