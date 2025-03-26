package view;
import javax.swing.*;

import model.characters.Barbarian;
import model.characters.Character;
import model.point.Point;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class CharactersMenu {

    private static final int MAX_SELECTION = 3;
    private final List<JPanel> selectedPanels = new ArrayList<>();
    private JButton nextButton;
    public static List<String> nameCharacters;       // Lista di stringhe die nomi die personaggi selezionati
    
    public void startCharactersMenu(List<Character> allAllies,List<Character> selectedAllies, int num_allies) {
        JFrame frame = new JFrame("Characters Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);

        // Sfondo
        JLabel bgLabel = new JLabel(new ImageIcon("images/background/background" + (int)(Math.random() * 6) + ".jpg"));
        bgLabel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Titolo
        JLabel info = new JLabel("Select 3 characters", SwingConstants.CENTER);
        info.setFont(new Font("Arial", Font.BOLD, 20));
        info.setForeground(Color.WHITE);
        gbc.gridx = 1; 
        gbc.gridy = 0;
        bgLabel.add(info, gbc);

        // Pulsante "Next"
        nextButton = new JButton("Next");
        nextButton.setFont(new Font("Arial", Font.BOLD, 16));
        nextButton.setEnabled(false);
        nextButton.addActionListener(e ->
        	// Chiamare il metodo transformList qui
        	
        	frame.dispose()
        );

        // Creazione e posizionamento dei personaggi
        addCharacter(bgLabel, "Archer", "A skilled archer, master of the bow.", "images/characters/archer/archerHero.png", 0, 1, gbc);
        addCharacter(bgLabel, "Barbarian", "A fierce warrior with immense strength.", "images/characters/barbarian/barbarianHero.png", 2, 1, gbc);
        addCharacter(bgLabel, "Juggernaut", "A massive tank, unstoppable force.", "images/characters/juggernaut/juggernautHero.png", 1, 2, gbc);
        addCharacter(bgLabel, "Knight", "A noble knight, brave and strong.", "images/characters/knight/knightHero.png", 0, 3, gbc);
        addCharacter(bgLabel, "Mage", "A master of the arcane arts.", "images/characters/wizard/wizardHero.png", 2, 3, gbc);

        // Aggiunta del pulsante "Next"
        gbc.gridx = 1; 
        gbc.gridy = 4;
        bgLabel.add(nextButton, gbc);

        frame.add(bgLabel);
        frame.setVisible(true);
    }

    private void addCharacter(JLabel bgLabel, String name, String desc, String imgPath, int x, int y, GridBagConstraints gbc) {
    	
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(true);
        panel.setBackground(new Color(30, 30, 30, 200));
        panel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        JLabel imgLabel = new JLabel(new ImageIcon(new ImageIcon(imgPath).getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH)), SwingConstants.CENTER);
        JLabel lbl = new JLabel("<html><center>" + name + "<br>" + desc + "</center></html>", SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setForeground(Color.WHITE);

        panel.add(imgLabel, BorderLayout.NORTH);
        panel.add(lbl, BorderLayout.SOUTH);

        
        panel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (selectedPanels.contains(panel)) {
                	selectedPanels.remove(panel);
                	nameCharacters.remove(name);
                    panel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
                } else if (selectedPanels.size() < MAX_SELECTION) {
                    selectedPanels.add(panel);
                	nameCharacters.add(name);
                    panel.setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
                }
                nextButton.setEnabled(selectedPanels.size() == MAX_SELECTION);
            }
        });

        gbc.gridx = x;
        gbc.gridy = y;
        bgLabel.add(panel, gbc);
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CharactersMenu().startCharactersMenu());
        
    }
}