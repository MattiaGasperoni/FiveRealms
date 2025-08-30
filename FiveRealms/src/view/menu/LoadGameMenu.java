package view.menu;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.function.Consumer;

public class LoadGameMenu extends AbstractMenu
{   
	private static final long serialVersionUID = 1L;
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
    
    /**=== SETUP METHODS ===*/
    @Override
    protected void initializeComponents() 
    {
        this.saveListPanel      = this.createSaveListPanel();
        this.saveListScrollPane = this.createScrollPane();
        this.chooseSaveButton   = super.createStyledButton("Choose Save File");
        this.mainMenuButton     = super.createStyledButton("Back to Main Menu");
    }
    
    /**=== LAYOUT METHODS ===*/
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
        scrollPane.setPreferredSize(new Dimension(420, 200));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(139, 69, 19, 200);        // Marrone per il thumb
                this.trackColor = new Color(101, 67, 33, 150);        // Marrone scuro per il track
                this.thumbHighlightColor = new Color(160, 82, 45);    // Marrone chiaro al hover
                this.thumbLightShadowColor = new Color(101, 67, 33);  // Ombra del thumb
                this.thumbDarkShadowColor = new Color(61, 43, 31);    // Ombra scura
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                JButton button = super.createDecreaseButton(orientation);
                button.setBackground(new Color(139, 69, 19));
                button.setForeground(new Color(255, 248, 220));
                return button;
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) {
                JButton button = super.createIncreaseButton(orientation);
                button.setBackground(new Color(139, 69, 19));
                button.setForeground(new Color(255, 248, 220));
                return button;
            }
        });
        
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(16, 0));
        
        return scrollPane;
    }
    
    /**
     * Creates a panel for a single save file with load button and options menu.
     * @param saveFile The save file object
     * @return Panel containing the load button and options button
     */
    private JPanel createSaveFilePanel(File saveFile) 
    {
        JPanel savePanel = new JPanel(new BorderLayout());
        savePanel.setOpaque(false);
        savePanel.setMaximumSize(new Dimension(400, 50));
        savePanel.setPreferredSize(new Dimension(400, 50));
        savePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Main load button
        String fileName = extractFileName(saveFile.getName());
        JButton loadButton = super.createStyledButton(fileName, new Dimension(320, 40));
        loadButton.addActionListener(e -> {
            if (this.saveFileClickHandler != null) {
                this.saveFileClickHandler.accept(saveFile);
            }
        });
        
        // Options button with three vertical dots
        JButton optionsButton = createOptionsButton(saveFile);
        
        savePanel.add(loadButton, BorderLayout.CENTER);
        savePanel.add(optionsButton, BorderLayout.EAST);
        
        return savePanel;
    }
    
    /**
     * Creates the options button with three vertical dots.
     * @param saveFile The save file this button is associated with
     * @return Styled options button
     */
    private JButton createOptionsButton(File saveFile) 
    {
        // Background image
        ImageIcon icon = new ImageIcon("images/vertical_ellipsis.png");

        Image scaledImage = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        icon = new ImageIcon(scaledImage);

        // Button styling
        JButton optionsButton = new JButton(icon);
        optionsButton.setPreferredSize(new Dimension(50, 40));
        optionsButton.setMaximumSize(new Dimension(50, 40));
        optionsButton.setFocusPainted(false);
        optionsButton.setContentAreaFilled(false); // Se vuoi sfondo visibile
        optionsButton.setOpaque(true);
        optionsButton.setBackground(new Color(139, 69, 19, 180));
        optionsButton.setBorder(BorderFactory.createLineBorder(new Color(218, 165, 32), 1));
        optionsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        optionsButton.addMouseListener(new java.awt.event.MouseAdapter() 
        {
            public void mouseEntered(java.awt.event.MouseEvent evt) 
            {
                optionsButton.setBackground(new Color(160, 82, 45, 180));
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) 
            {
                optionsButton.setBackground(new Color(139, 69, 19, 180));
            }
        });

        // Show options menu on click
        optionsButton.addActionListener(e -> showOptionsMenu(optionsButton, saveFile));

        return optionsButton;
    }

    
    /**
     * Shows the popup menu with rename and delete options.
     * @param sourceButton The button that triggered the menu
     * @param saveFile The save file to operate on
     */
    private void showOptionsMenu(JButton sourceButton, File saveFile) 
    {
        JPopupMenu popup = new JPopupMenu();
        popup.setBackground(new Color(139, 69, 19, 220));
        popup.setBorder(BorderFactory.createLineBorder(new Color(101, 67, 33), 2));
        
        // Utility font for emojis
        Font emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, 14); 

        // Rename option
        JMenuItem renameItem = new JMenuItem("🏷️ Rename");
        renameItem.setPreferredSize(new Dimension(200, 40));
        renameItem.setBackground(new Color(139, 69, 19, 220));
        renameItem.setForeground(Color.WHITE);
        renameItem.setFont(emojiFont);  
        renameItem.addActionListener(e -> renameSaveFile(saveFile));

        // Delete option
        JMenuItem deleteItem = new JMenuItem("🗑️ Delete");
        deleteItem.setPreferredSize(new Dimension(200, 40));
        deleteItem.setBackground(new Color(139, 69, 19, 220));
        deleteItem.setForeground(Color.WHITE);
        deleteItem.setFont(emojiFont);  
        deleteItem.addActionListener(e -> deleteSaveFile(saveFile));
        
        // Add hover effects
        addHoverEffect(renameItem);
        addHoverEffect(deleteItem);
        
        popup.add(renameItem);
        popup.addSeparator();
        popup.add(deleteItem);

        // Set preferred size to ensure consistent width
        popup.setPreferredSize(new Dimension(100, 100));

        // Show the popup below the options button
        popup.show(sourceButton, 0, sourceButton.getHeight());
    }


    
    /**
     * Adds hover effect to menu items.
     * @param item The menu item to add hover effect to
     */
    private void addHoverEffect(JMenuItem item) {
        item.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                item.setBackground(new Color(160, 82, 45, 240));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                item.setBackground(new Color(139, 69, 19, 220));
            }
        });
    }

    
    /**
     * Shows rename dialog for a save file.
     * @param saveFile The file to rename
     */
    private void renameSaveFile(File saveFile) 
    {
        String currentName = extractFileName(saveFile.getName());
        
        // Create custom rename dialog
        JDialog renameDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Rename this File", true);
        renameDialog.setSize(400, 200);
        renameDialog.setLocationRelativeTo(this);
        renameDialog.setResizable(false);
        renameDialog.getContentPane().setBackground(new Color(34, 34, 34));
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(34, 34, 34));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Label
        JLabel label = new JLabel("Enter new name:");
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Text field
        JTextField textField = new JTextField(currentName);
        textField.setBackground(new Color(60, 60, 60));
        textField.setForeground(Color.WHITE);
        textField.setCaretColor(Color.WHITE);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(139, 69, 19), 2),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(34, 34, 34));
        
        JButton confirmButton = createStyledDialogButton("Confirm");
        JButton cancelButton  = createStyledDialogButton("Cancel");
        
        confirmButton.addActionListener(e -> 
        {
            String newName = textField.getText().trim();
            if (!newName.isEmpty() && !newName.equals(currentName)) 
            {
                if (performRename(saveFile, newName)) 
                {
                    refreshSaveFilesList();
                    renameDialog.dispose();
                    showCustomMessageDialog("Success", "Save file rename successfully!");
                }
            	else 
            	{
                    JOptionPane.showMessageDialog(renameDialog, 
                        "Failed to rename file. Make sure the name is valid.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            else
            {
                renameDialog.dispose();
            }
        });
        
        cancelButton.addActionListener(e -> renameDialog.dispose());
        
        textField.addActionListener(e -> confirmButton.doClick());
        
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(textField, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        renameDialog.add(panel);
        textField.selectAll();
        renameDialog.setVisible(true);
    }
    
    /**
     * Shows custom-styled confirmation dialog and deletes save file.
     * @param saveFile The file to delete
     */
    private void deleteSaveFile(File saveFile) 
    {
        String saveName = extractFileName(saveFile.getName());

        // Custom delete confirmation dialog
        JDialog deleteDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Confirm Delete", true);
        deleteDialog.setSize(400, 200);
        deleteDialog.setLocationRelativeTo(this);
        deleteDialog.setResizable(false);
        deleteDialog.getContentPane().setBackground(new Color(34, 34, 34));

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(34, 34, 34));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel("<html><div style='text-align: center;'>"
        	    + "Are you sure you want to delete this save file?<br><br>"
        	    + "<span style='font-size: 16px;'>" + saveName + "</span>"
        	    + "</div></html>");
    	label.setForeground(Color.WHITE);
    	label.setFont(new Font("Arial", Font.PLAIN, 14));
    	label.setHorizontalAlignment(SwingConstants.CENTER); 


        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(34, 34, 34));

        JButton confirmButton = createStyledDialogButton("Delete");
        JButton cancelButton  = createStyledDialogButton("Cancel");

        confirmButton.addActionListener(e -> 
        {
        	deleteDialog.dispose();
    		if (saveFile.delete()) 
    		{
    		    showCustomMessageDialog("Success", "Save file deleted successfully!");
    		    refreshSaveFilesList();
    		} 
    		else 
    		{
    		    showCustomMessageDialog("Error", "Failed to delete save file!");
    		}
        });

        cancelButton.addActionListener(e -> deleteDialog.dispose());

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        panel.add(label, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        deleteDialog.add(panel);
        deleteDialog.setVisible(true);
    }
    
	/**
	 * Shows a custom message dialog with specified title and message.
	 * 
	 * @param title   The dialog title
	 * @param message The message to display
	 */
    private void showCustomMessageDialog(String title, String message) 
    {
        JDialog messageDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        messageDialog.setSize(400, 160);
        messageDialog.setResizable(false);
        messageDialog.setLocationRelativeTo(this);
        messageDialog.getContentPane().setBackground(new Color(34, 34, 34));

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(34, 34, 34));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>" + message + "</div></html>");
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton okButton = createStyledDialogButton("OK");
        okButton.addActionListener(e -> messageDialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(34, 34, 34));
        buttonPanel.add(okButton);

        panel.add(messageLabel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        messageDialog.add(panel);
        messageDialog.setVisible(true);
    }


    
    /**
     * Performs the actual file rename operation.
     * @param oldFile The original file
     * @param newName The new name (without extension)
     * @return true if rename was successful
     */
    private boolean performRename(File oldFile, String newName) 
    {
        try
        {
            // Ensure new name has correct extension
            if (!newName.endsWith(".sav")) 
            {
                newName += ".sav";
            }
            
            File newFile = new File(oldFile.getParent(), newName);
            
            // Check if file with new name already exists
            if (newFile.exists()) 
            {
                JOptionPane.showMessageDialog(this, 
                    "A file with this name already exists!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            return oldFile.renameTo(newFile);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Creates styled buttons for dialogs.
     * @param text Button text
     * @return Styled dialog button
     */
    private JButton createStyledDialogButton(String text) 
    {
        JButton button = new JButton(text);
        button.setBackground(new Color(139, 69, 19));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(80, 30));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() 
        {
            public void mouseEntered(java.awt.event.MouseEvent evt) 
            {
                button.setBackground(new Color(160, 82, 45));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) 
            {
                button.setBackground(new Color(139, 69, 19));
            }
        });
        
        return button;
    }
    
    /**
     * Refreshes the save files list after rename or delete operations.
     */
    private void refreshSaveFilesList() 
    {
        // Get current save files from the same source used originally
        // This assumes you have access to the GameStateManager or similar
        // For now, we'll just clear and let the caller repopulate
        this.saveListPanel.removeAll();
        this.saveFiles.clear();
        this.saveButtons.clear();
        
        // Add a placeholder message
        JLabel refreshLabel = new JLabel("Please refresh the save list", SwingConstants.CENTER);
        refreshLabel.setForeground(TITLE_TEXT);
        refreshLabel.setFont(new Font("Serif", Font.ITALIC, 14));
        refreshLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.saveListPanel.add(refreshLabel);
        
        this.saveListPanel.revalidate();
        this.saveListPanel.repaint();
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
     * @param saveFileArray Array of save files to display
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
            // Create panels with options menu for each save file
            for (File saveFile : saveFileArray) 
            {
                this.saveFiles.add(saveFile);
                
                // Use new panel-based approach instead of single button
                JPanel saveFilePanel = createSaveFilePanel(saveFile);
                this.saveListPanel.add(saveFilePanel);
                this.saveListPanel.add(Box.createVerticalStrut(8));
            }
        }
        
        this.saveListPanel.revalidate();
        this.saveListPanel.repaint();
    }
}