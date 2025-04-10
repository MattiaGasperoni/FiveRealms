package view.map;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import model.characters.Character;
import model.point.Point;

public class GridPanel extends JPanel {

    private List<Character> enemiesList; // List of enemies
    private List<Character> alliesList;  // List of allies
    private JButton[][] gridButtons; // 2D array for grid buttons
    private JFrame frame;

    /**
     * Constructor for the GridPanel.
     * @param frame The main frame where the panel is placed.
     * @param enemiesList List of enemy characters.
     * @param alliesList List of allied characters.
     */
    public GridPanel(JFrame frame, List<Character> enemiesList, List<Character> alliesList) {
        super(new GridLayout(AbstractMap.GRID_SIZE, AbstractMap.GRID_SIZE)); // Setting GridLayout
        this.frame = frame;
        this.enemiesList = enemiesList;
        this.alliesList = alliesList;
        this.gridButtons = new JButton[AbstractMap.GRID_SIZE][AbstractMap.GRID_SIZE];
        initializeGrid();
    }

    /**
     * Initializes the grid panel with buttons, setting up their properties and adding them to the panel.
     */
    private void initializeGrid() {
        this.removeAll(); // Clear all components
        for (int i = 0; i < AbstractMap.GRID_SIZE; i++) {
            for (int j = 0; j < AbstractMap.GRID_SIZE; j++) {
                final int row = i;
                final int col = j;
                gridButtons[i][j] = new JButton(); // Create button
                gridButtons[i][j].setPreferredSize(new Dimension(AbstractMap.BUTTON_SIZE, AbstractMap.BUTTON_SIZE)); // Set size
                gridButtons[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Add border for debugging

                gridButtons[i][j].addActionListener(e -> showButtonCoordinates(row, col));

                CharacterSpawner.spawnCharacter(gridButtons[i][j], row, col, alliesList, enemiesList);
                
                this.add(gridButtons[i][j]);
            }
        }
        this.revalidate(); 
        this.repaint(); 
    }
    

    

    /**
     * Displays a dialog with the button's coordinates when clicked.
     * @param row The row index of the button.
     * @param col The column index of the button.
     */
    private void showButtonCoordinates(int row, int col) {
        JOptionPane.showMessageDialog(frame, "Position: [" + row + ", " + col + "]");
    }

    /**
     * Gets the array of grid buttons.
     * @return 2D array of grid buttons.
     */
    public JButton[][] getGridButtons() {
        return gridButtons;
    }
}
