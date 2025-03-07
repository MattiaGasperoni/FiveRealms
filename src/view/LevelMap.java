package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.*;

public class LevelMap {
    
    private static final int GRID_SIZE = 6;
    private static final int BUTTON_SIZE = 80; // Nuova dimensione per i bottoni
    private JFrame frame;
    private JPanel gridPanel;
    private String[] enemyImages = {"images/image.jpg"};
    private String[] allyImages = {"images/barbaroAlleato.png"};
    private Random random = new Random();

    public LevelMap() {
        frame = new JFrame("Saga dei 5 Regni");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 1000);
        frame.setLocationRelativeTo(null);

        JPanel contentPanel = new JPanel(null);
        frame.add(contentPanel);

        // Pannello della griglia sopra lo sfondo
        gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        gridPanel.setOpaque(false); // Rendi trasparente per permettere la visibilità dello sfondo
        gridPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        contentPanel.add(gridPanel);

        initializeGrid();
        
        frame.setVisible(true);
    }
    
    private void initializeGrid() {
        for (int row = 0; row < GRID_SIZE; row++) {  
            for (int col = 0; col < GRID_SIZE; col++) {  
                JButton button = new JButton();
                button.setContentAreaFilled(false);  // Rendi il bottone trasparente
                button.setBorderPainted(false);     // Rimuovi il bordo del bottone
                button.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE)); // Imposta la dimensione del bottone

                int chance = random.nextInt(3); // 0 = vuoto, 1 = nemico, 2 = alleato
                
                // Logica per posizionare nemici e alleati
                if (chance == 1 && row < GRID_SIZE / 2) { // Metà superiore: nemici
                    String enemyImage = enemyImages[random.nextInt(enemyImages.length)];
                    ImageIcon resizedEnemyIcon = resizeImage(enemyImage, BUTTON_SIZE, BUTTON_SIZE);
                    button.setIcon(resizedEnemyIcon);
                } else if (chance == 2 && row > GRID_SIZE / 2) { // Metà inferiore: alleati
                    String allyImage = allyImages[random.nextInt(allyImages.length)];
                    ImageIcon resizedAllyIcon = resizeImage(allyImage, BUTTON_SIZE, BUTTON_SIZE);
                    button.setIcon(resizedAllyIcon);
                } else {
                    button.setIcon(null); // Lascia la cella vuota
                }

                button.addActionListener(new CellClickListener(row + 1, col + 1));
                gridPanel.add(button);
            }
        }
    }

    // Metodo per ridimensionare l'immagine
    private ImageIcon resizeImage(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
    }

    private class CellClickListener implements ActionListener {
        private int row, col;

        public CellClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(frame, "Personaggio con coordinate: (" + row + ", " + col + ")", "Informazioni", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LevelMap::new);
    }
}
