package view.map;

import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
//import javax.swing.JOptionPane;
import javax.swing.JPanel;
import model.point.Point;

public class GridPanel extends JPanel 
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton[][] gridButtons;  // 2D array for grid buttons

    /**
     * Constructor for the GridPanel.
     * @param layeredPane The JLayeredPane where the panel will be placed.
     * @param enemiesList List of enemy characters.
     * @param alliesList List of allied characters.
     */
    public GridPanel(JLayeredPane layeredPane) 
    {
        super(new GridLayout(AbstractMap.GRID_SIZE_WIDTH, AbstractMap.GRID_SIZE_HEIGHT)); // Setting GridLayout
        
        //this.layeredPane = layeredPane;
        
        this.gridButtons = new JButton[AbstractMap.GRID_SIZE_WIDTH][AbstractMap.GRID_SIZE_HEIGHT];
        
        initializeGrid();
    }

    /**
     * Initializes the grid panel with buttons, setting up their properties and adding them to the panel.
     */
    private void initializeGrid() 
    {
        this.removeAll(); // Clear all components

        for (int i = 0; i < AbstractMap.GRID_SIZE_WIDTH; i++) 
        {
            for (int j = 0; j < AbstractMap.GRID_SIZE_HEIGHT; j++) 
            {   
                JButton button = new JButton(); // Create button
                                
                button.setContentAreaFilled(false);
                button.setBorderPainted(false);
                
                this.gridButtons[i][j] = button;
                
                this.add(button);
            }
        } 

        this.revalidate(); 
        this.repaint();
    }

    /**
     * Gets the array of grid buttons.
     * @return 2D array of grid buttons.
     */
    public JButton[][] getGridButtons() {
        return gridButtons;
    }


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