package view;

import javax.swing.*;
import java.awt.*;

public class LevelMap {
    public static final int GRID_SIZE = 6;
    private static final int BUTTON_SIZE = 100; // Aumentato per adattarsi a 800x800

    private JFrame frame;
    private JPanel gridPanel;
    private JButton[][] gridButtons = new JButton[GRID_SIZE][GRID_SIZE];

    public LevelMap() {
        initializeFrame();
        initializeGrid();
    }

    private void initializeFrame() {
        frame = new JFrame("Saga dei 5 Regni");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800); 
        frame.setLocationRelativeTo(null); 
        gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        frame.add(gridPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private void initializeGrid() {
        gridPanel.removeAll();
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                gridButtons[i][j] = new JButton();
                gridButtons[i][j].setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
                gridPanel.add(gridButtons[i][j]);
            }
        }
        gridPanel.repaint();
        gridPanel.revalidate();
    }

    public JButton getButtonAt(int x, int y) {
        if (x >= 0 && x < GRID_SIZE && y >= 0 && y < GRID_SIZE) {
            return gridButtons[x][y];
        }
        return null; // Se fuori dai limiti, restituisce null
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(LevelMap::new);
    }

}
