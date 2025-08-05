package view;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoadGameMenu 
{   
    private JFrame frame;
    private JLabel backgroundLabel;
    
    private JButton choseSaveButton;
	private JButton mainMenuButton;

	
	

	 public LoadGameMenu()
    {
        this.frame = new JFrame("FiveRealms - MainMenu");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(800, 800);
        this.frame.setLocationRelativeTo(null);
        this.frame.setResizable(false);
        this.choseSaveButton = createButton("Chose a Save");
        this.mainMenuButton  = createButton("Back to Main Menu");
        this.setupLayout();
    }
	 
    /**
     * Makes the main menu frame visible to the user
     */
    public void show() {
        this.frame.setVisible(true);
    }
    
    /**
     * Closes and disposes of the main menu frame
     */
    public void close() {
        this.frame.dispose();
    }
    
	 
    public void setupLayout() 
    {
    	this.backgroundLabel = new JLabel(new ImageIcon("images/Background/background4.jpg"));
        this.backgroundLabel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Riga vuota sopra (spinge il contenuto in basso)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;  // peso verticale per spingere in basso
        backgroundLabel.add(Box.createGlue(), gbc);
        
        // Titolo
        gbc.gridy = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 10, 20, 10);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        backgroundLabel.add(this.createTitleLabel(), gbc);


        // Pulsanti load game
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);
         
        /*  LOGICA CHE MOSTRA I SALVATGGI DA IMPLEMENTARE
        File[] saveFiles = manager.getSaveFiles();
        int row = gbc.gridy;
        for (File saveFile : saveFiles) 
        {
            String fileName = saveFile.getName();
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0) fileName = fileName.substring(0, dotIndex);
            JButton loadGameButton = createButton(fileName, e -> 
            {
                System.out.println("Loading saved game: " + saveFile.getAbsolutePath());
                frame.dispose();
                // caricamento del file saveFile.getAbsolutePath()
            });
            gbc.gridy = row++;
            gbc.gridx = 0;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.anchor = GridBagConstraints.CENTER;
            backgroundLabel.add(loadGameButton, gbc);
        }
        gbc.gridy = row;
    	*/
		    
	    // Riga vuota sotto (spinge il contenuto in alto)
	    gbc.gridy++;
	    gbc.weighty = 1.0;
	    backgroundLabel.add(Box.createGlue(), gbc);
	    
	    
        this.addButtonsToBackground(this.backgroundLabel, this.choseSaveButton, this.mainMenuButton, gbc);

	    this.frame.add(this.backgroundLabel);
    }
    
    private void addButtonsToBackground(JLabel backgroundLabel, JButton choseSaveButton, JButton mainMenuButton, GridBagConstraints gbc) {
        gbc.gridy = 2;
        backgroundLabel.add(choseSaveButton, gbc);
        gbc.gridy = 3;
        backgroundLabel.add(mainMenuButton, gbc);
    }
    
    /**
     * Creates the main title label with medieval fantasy styling
     * @return A JLabel containing the game title with appropriate formatting
     */
    private JLabel createTitleLabel()
    {
        JLabel titleLabel = new JLabel(
        	    "<html>"
        	        + "<body style='text-align: center; background-color: rgba(0,0,0,0.8); "
        	        + "border: 2px solid #8B4513; padding: 6px 10px;'>"
        	        + "<span style='font-family: Serif; font-size: 16px; color: #D2B48C;'>"
        	        + "Select the appropriate save File"
        	        + "</span>"
        	        + "</body>"
        	        + "</html>",
        	    SwingConstants.CENTER
        	);
    	return titleLabel; 
    }
    
    /**
     * Adds an ActionListener to the start game button
     * @param listener The ActionListener to handle start button click events
     */
    public void addChoseSaveListener(ActionListener listener) {
        this.choseSaveButton.addActionListener(listener);
    }
    
    /**
     * Adds an ActionListener to the load game button
     * @param listener The ActionListener to handle load button click events
     */
    public void addMainMenuListener(ActionListener listener) {
        this.mainMenuButton.addActionListener(listener);
    }
        
    /**
     * Creates a styled button with consistent appearance and properties
     * @param buttonText The text to display on the button
     * @return A JButton with standardized styling applied
     */
    private JButton createButton(String buttonText)
    {
        JButton button = new JButton(buttonText);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(139, 69, 19));
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 50));
        return button;
    }
}