package view.menu;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.function.Consumer;

public class LoadGameMenu extends AbstractMenu
{   
    
    private JScrollPane saveListScrollPane;
    private JPanel saveListPanel;
    private JButton chooseSaveButton;
    private JButton mainMenuButton;
    
    // Store save files and their corresponding buttons for listener management
    private ArrayList<File> saveFiles;
    private ArrayList<JButton> saveButtons;
    private Consumer<File> saveFileClickHandler;
    
    /**
     * Constructs a new LoadGameMenu.
     */
    public LoadGameMenu() 
    {
        super("FiveRealms - Load Game"); 
        
        this.saveFiles   = new ArrayList<>();
        this.saveButtons = new ArrayList<>();
    }
    
    @Override
    protected void initializeComponents() 
    {
        this.saveListPanel      = this.createSaveListPanel();
        this.saveListScrollPane = this.createScrollPane();
        this.chooseSaveButton   = super.createStyledButton("Choose Save File");
        this.mainMenuButton     = super.createStyledButton("Back to Main Menu");
    }
    
    @Override
    protected void setupLayout() 
    {
    	this.mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Top spacer
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        gbc.fill = GridBagConstraints.BOTH;
        this.mainPanel.add(Box.createGlue(), gbc);
        
        // Title
        gbc.gridy = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 20, 30, 20);
        this.mainPanel.add(super.createTitleLabel("Load Saved Game"), gbc);
        
        // Save files scroll pane
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 20, 20, 20);
        this.mainPanel.add(this.saveListScrollPane, gbc);
        
        // Buttons panel
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 20, 20, 20);
        this.mainPanel.add(super.createButtonPanel(this.chooseSaveButton, this.mainMenuButton), gbc);
        
        // Bottom spacer
        gbc.gridy = 4;
        gbc.weighty = 0.3;
        gbc.fill = GridBagConstraints.BOTH;
        this. mainPanel.add(Box.createGlue(), gbc);
    }
    
    /**
     * Creates the panel that will contain the list of save files.
     * @return JPanel for save file list
     */
    private JPanel createSaveListPanel() 
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        return panel;
    }
    
    /**
     * Creates a scroll pane for the save file list.
     * @return JScrollPane containing the save list panel
     */
    private JScrollPane createScrollPane() 
    {
        JScrollPane scrollPane = new JScrollPane(saveListPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(400, 200));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }
    
    /**
     * Creates a button specifically for save file entries.
     * @param fileName The name of the save file
     * @param saveFile The actual save file object
     * @return Styled save file button
     */
    private JButton createSaveFileButton(String fileName, java.io.File saveFile) 
    {
        JButton saveButton = super.createStyledButton(fileName, new Dimension(350, 40));
        
        // Add click handler that will call the registered listener
        saveButton.addActionListener(e -> 
        {
            if (this.saveFileClickHandler != null) 
            {
                this.saveFileClickHandler.accept(saveFile);
            }
        });
        
        return saveButton;
    }
    
    /**
     * Extracts the file name without extension.
     * @param fullFileName The full file name including extension
     * @return File name without extension
     */
    private String extractFileName(String fullFileName) 
    {
        int dotIndex = fullFileName.lastIndexOf('.');
        return (dotIndex > 0) ? fullFileName.substring(0, dotIndex) : fullFileName;
    }
    
    
    // === LISTENER METHODS ===
    
    /**
     * Sets the handler for save file button clicks.
     * @param handler Consumer that accepts the clicked save file
     */
    public void addSaveFileClickListener(Consumer<File> handler) 
    {
        this.saveFileClickHandler = handler;
    }
        
    /**
     * Adds an ActionListener to the choose save button.
     * @param listener The ActionListener to handle button click events
     */
    public void addChooseSaveListener(ActionListener listener) 
    {
        this.chooseSaveButton.addActionListener(listener);
    }
    
    /**
     * Adds an ActionListener to the main menu button.
     * @param listener The ActionListener to handle button click events
     */
    public void addMainMenuListener(ActionListener listener) 
    {
    	this.mainMenuButton.addActionListener(listener);
    }
    
    /**
     * Populates the save list with available save files.
     * @param saveFiles Array of save files to display
     */
    public void showSaveFile(File[] saveFileArray) 
    {
        // Clear previous data
        this.saveListPanel.removeAll();
        this.saveFiles.clear();
        this.saveButtons.clear();
        
        if (saveFileArray == null || saveFileArray.length == 0) 
        {
            JLabel noSavesLabel = new JLabel("No save files found", SwingConstants.CENTER);
            noSavesLabel.setForeground(TITLE_TEXT);
            noSavesLabel.setFont(new Font("Serif", Font.ITALIC, 16));
            noSavesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            saveListPanel.add(noSavesLabel);
        } 
        else 
        {
            // Store files and create buttons
            for (File saveFile : saveFileArray) 
            {
            	this.saveFiles.add(saveFile);
                
                String fileName = extractFileName(saveFile.getName());
                JButton saveFileButton = createSaveFileButton(fileName, saveFile);
                this.saveButtons.add(saveFileButton);
                
                saveFileButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                this.saveListPanel.add(saveFileButton);
                this.saveListPanel.add(Box.createVerticalStrut(8));
            }
        }
        
        this.saveListPanel.revalidate();
        this.saveListPanel.repaint();
    }
}