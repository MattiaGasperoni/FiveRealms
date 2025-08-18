package view.map;

import javax.swing.*;

import controller.GameController;

import java.awt.*;
import java.util.List;
import model.characters.Character;

public class TutorialMap extends AbstractMap 
{
    private static final Color ALLY_HIGHLIGHT   = Color.RED;
    private static final Color ENEMY_HIGHLIGHT  = Color.BLUE;
    private static final Color POPUP_BG         = new Color(245, 230, 200);
    private static final Color POPUP_BORDER     = new Color(150, 100, 50);
    private static final Color TEXT_COLOR       = new Color(70, 30, 10);
    private static final Font POPUP_FONT        = new Font("Times New Roman", Font.BOLD, 18);
    private static final Font BUTTON_FONT       = new Font("Times New Roman", Font.BOLD, 14);
    private static final int HIGHLIGHT_DURATION = 1500;
    
    
    public TutorialMap(List<Character> enemiesList, List<Character> alliesList, GameController controller) 
    {
        super(enemiesList, alliesList, 0, controller);
    }
        
    public void showTutorialPopup(String message, Runnable onClose) 
    {
        JDialog dialog = createDialog();
        JPanel panel = createStyledPanel();
        
        addMessageLabel(panel, message);
        addOkButton(panel, dialog, onClose);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    public void highlightAlliesArea(Runnable afterAction) 
    {
        highlightRows(0, AbstractMap.GRID_SIZE_HEIGHT / 2, ALLY_HIGHLIGHT, afterAction);
    }
    
    public void highlightEnemiesArea(Runnable afterAction) 
    {
        highlightRows(AbstractMap.GRID_SIZE_WIDTH / 2, AbstractMap.GRID_SIZE_WIDTH - 1, ENEMY_HIGHLIGHT, afterAction);
    }
    
    public void clearHighlights() 
    {
        for (int i = 0; i < AbstractMap.GRID_SIZE_WIDTH; i++) 
        {
            for (int j = 0; j < AbstractMap.GRID_SIZE_HEIGHT; j++) 
            {
                JButton btn = gridPanel.getGridButtons()[i][j];
                btn.setBackground(null);
                btn.setOpaque(false);
                btn.setContentAreaFilled(false);
                btn.setBorderPainted(false);
            }
        }
    }
    
    
    private JDialog createDialog() 
{
        JDialog dialog = new JDialog();
        dialog.setSize(360, 120);
        dialog.setUndecorated(true);
        dialog.setModal(true);
        dialog.setLocationRelativeTo(gridPanel);
        return dialog;
    }
    
    private JPanel createStyledPanel() 
{
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(POPUP_BG);
        panel.setBorder(BorderFactory.createLineBorder(POPUP_BORDER, 2));
        return panel;
    }
    
    private void addMessageLabel(JPanel panel, String message) 
{
        JLabel messageLabel = new JLabel(
            "<html><div style='text-align: center;'>" + message + "</div></html>", 
            SwingConstants.CENTER
        );
        messageLabel.setFont(POPUP_FONT);
        messageLabel.setForeground(TEXT_COLOR);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        panel.add(messageLabel, BorderLayout.CENTER);
    }
    
    private void addOkButton(JPanel panel, JDialog dialog, Runnable onClose) 
{
        JButton okButton = new JButton("OK");
        okButton.setFont(BUTTON_FONT);
        okButton.setBackground(POPUP_BORDER);
        okButton.setForeground(Color.WHITE);
        okButton.setBorder(BorderFactory.createEmptyBorder(4, 15, 4, 15));
        okButton.setBorderPainted(false);
        okButton.setFocusPainted(false);
        
        okButton.addActionListener(e -> {
            dialog.dispose();
            clearHighlights();
            if (onClose != null) {
                onClose.run();
            }
        });
        
        panel.add(okButton, BorderLayout.SOUTH);
    }
    
    private void highlightRows(int startRow, int endRow, Color color, Runnable afterAction) 
{
        // Evidenzia le righe
        for (int i = startRow; i <= endRow; i++) {
            for (int j = 0; j < AbstractMap.GRID_SIZE_HEIGHT; j++) {
                JButton btn = gridPanel.getGridButtons()[i][j];
                btn.setBackground(color);
                btn.setOpaque(false);
                btn.setContentAreaFilled(true);
                btn.setBorderPainted(false);
            }
        }
        
        // Timer to remove highlight
        Timer timer = new Timer(HIGHLIGHT_DURATION, e -> {
            clearHighlights();
            if (afterAction != null) {
                afterAction.run();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
}
