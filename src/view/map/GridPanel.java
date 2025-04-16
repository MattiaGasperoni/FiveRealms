package view.map;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import model.characters.Character;
import model.point.Point;

public class GridPanel extends JPanel 
{

    private List<Character> enemiesList; // List of enemies
    private List<Character> alliesList;  // List of allied characters
    private JButton[][] gridButtons; // 2D array for grid buttons
    private JLayeredPane layeredPane; // Riferimento al JLayeredPane

    /**
     * Constructor for the GridPanel.
     * @param layeredPane The JLayeredPane where the panel will be placed.
     * @param enemiesList List of enemy characters.
     * @param alliesList List of allied characters.
     */
    public GridPanel(JLayeredPane layeredPane, List<Character> enemiesList, List<Character> alliesList) 
    {
        super(new GridLayout(AbstractMap.GRID_SIZE_WIDTH, AbstractMap.GRID_SIZE_HEIGHT)); // Setting GridLayout
        
        this.layeredPane = layeredPane;
        this.enemiesList = enemiesList;
        this.alliesList  = alliesList;
        this.gridButtons = new JButton[AbstractMap.GRID_SIZE_WIDTH][AbstractMap.GRID_SIZE_HEIGHT];
        
        initializeGrid();
    }

    /**
     * Initializes the grid panel with buttons, setting up their properties and adding them to the panel.
     */
    private void initializeGrid() {
        this.removeAll(); // Clear all components

        for (int i = 0; i < AbstractMap.GRID_SIZE_WIDTH; i++) {
            for (int j = 0; j < AbstractMap.GRID_SIZE_HEIGHT; j++) {
                int row = i;
                int col = j;
                
                JButton button = new JButton(); // Create button
                button.setPreferredSize(new Dimension(AbstractMap.BUTTON_SIZE, AbstractMap.BUTTON_SIZE)); 

                // Rendi il bottone invisibile ma cliccabile
                button.setContentAreaFilled(false);
                button.setBorderPainted(false);
                button.setFocusPainted(false);
                button.setOpaque(false);
                button.setText("");
                button.setIcon(null);
                
                button.addActionListener(e -> showButtonCoordinates(row, col));
                this.gridButtons[i][j] = button;
                
                this.add(button);
            }
        }

        this.revalidate(); 
        this.repaint();

        // SOLO DOPO AVER INIZIALIZZATO TUTTA LA GRIGLIA, fai lo spawn
       // CharacterSpawner.spawnCharacters(this.layeredPane, alliesList, enemiesList, gridButtons);
    }


    /**
     * Displays a dialog with the button's coordinates when clicked.
     * @param row The row index of the button.
     * @param col The column index of the button.
     */
    private void showButtonCoordinates(int row, int col) {
        JOptionPane.showMessageDialog(layeredPane, "Position: [" + row + ", " + col + "]");
    }

    /**
     * Gets the array of grid buttons.
     * @return 2D array of grid buttons.
     */
    public JButton[][] getGridButtons() {
        return gridButtons;
    }

    /**
     * Update the grid layout when the size of the frame changes.
     */
    public void updateGridLayout() {
        // Ensure the grid buttons resize correctly with the frame size
        for (int i = 0; i < AbstractMap.GRID_SIZE_WIDTH; i++) {
            for (int j = 0; j < AbstractMap.GRID_SIZE_HEIGHT; j++) {
                gridButtons[i][j].setPreferredSize(new Dimension(layeredPane.getWidth() / AbstractMap.GRID_SIZE_WIDTH, layeredPane.getHeight() / AbstractMap.GRID_SIZE_HEIGHT));
            }
        }
        revalidate();
        repaint();
    }


    // Metodo che scorre tutti i pulsanti e se hanno un immagine allora li mette in una Mappa
	public Map<JButton, Point>  getImageButtonList() 
	{
		Map<JButton, Point> buttonsWithImage = new HashMap<>();
		
		for (int y = 0; y < gridButtons.length; y++) 
		{
		    for (int x = 0; x < gridButtons[y].length; x++) 
		    {
		        JButton button = gridButtons[y][x];

		        if (button.getIcon() != null)
		        {
		        	buttonsWithImage.put(button, new Point(x, y));
		        }
		    }
		}
        return buttonsWithImage;
	}
}