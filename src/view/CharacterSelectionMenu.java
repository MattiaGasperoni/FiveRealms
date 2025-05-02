package view;

import javax.swing.*;

import model.characters.Archer;
import model.characters.Barbarian;
import model.characters.Character;
import model.gameStatus.Game;
import view.map.LevelMap;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class CharacterSelectionMenu 
{      
	private JFrame frame;
	private JButton nextButton;
    private final List<JPanel> selectedPanels = new ArrayList<>();
    private final List<String> selectedCharacters = new ArrayList<>();

    
    public void start(List<Character> allAllies) 
    {
    	System.out.print("Open Characters Selection Menu Frame ->");
        
		// Creazione della finestra principale
    	this.frame = new JFrame("Characters Selection Menu");
    	this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	this.frame.setSize(800, 800);
    	this.frame.setLocationRelativeTo(null);

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
        this.nextButton = new JButton("Next");
        this.nextButton.setFont(new Font("Arial", Font.BOLD, 16));
        this.nextButton.setEnabled(false);
        
        // Aggiunta del pulsante "Next" 
        gbc.gridx = 1; 
        gbc.gridy = 4;
        bgLabel.add(this.nextButton, gbc);

        // Creazione e posizionamento dei personaggi
        addCharacter(bgLabel, "Archer", "A skilled archer, master of the bow.", "images/characters/archer/archerHero.png", 0, 1, gbc);
        addCharacter(bgLabel, "Barbarian", "A fierce warrior with immense strength.", "images/characters/barbarian/barbarianHero.png", 2, 1, gbc);
        addCharacter(bgLabel, "Juggernaut", "A massive tank, unstoppable force.", "images/characters/juggernaut/juggernautHero.png", 1, 2, gbc);
        addCharacter(bgLabel, "Knight", "A noble knight, brave and strong.", "images/characters/knight/knightHero.png", 0, 3, gbc);
        addCharacter(bgLabel, "Mage", "A master of the arcane arts.", "images/characters/wizard/wizardHero.png", 2, 3, gbc);


        this.frame.add(bgLabel);
        this.frame.setVisible(true);
    }
    
 

    public void addNextButtonListener(ActionListener listener) {
    	this.nextButton.addActionListener(listener);
    }

    public List<String> getSelectedCharacterNames() {
        return new ArrayList<>(this.selectedCharacters);
    }

    public void close() {
    	this.frame.dispose();
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
                	selectedCharacters.remove(name);
                    panel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
                } else if (selectedPanels.size() < Game.MAX_ALLIES_PER_ROUND) {
                    selectedPanels.add(panel);
                	selectedCharacters.add(name);
                    panel.setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
                }
                nextButton.setEnabled(selectedPanels.size() == Game.MAX_ALLIES_PER_ROUND);
            }
        });

        gbc.gridx = x;
        gbc.gridy = y;
        bgLabel.add(panel, gbc);
    }
}