package view.map;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import model.characters.Character;

public class CharacterTooltipManager 
{
    // Mappa per tenere traccia dei listener associati ai tooltip per ogni bottone
    private final Map<JButton, TooltipListenerData> tooltipListeners = new ConcurrentHashMap<>();
    
    // Classe interna per contenere i dati del listener e della finestra tooltip
    private static class TooltipListenerData 
    {
        final MouseListener mouseListener;
        final JWindow tooltipWindow;
        
        TooltipListenerData(MouseListener listener, JWindow window) 
        {
            this.mouseListener = listener;
            this.tooltipWindow = window;
        }
    }
    
    public void showCharacterTooltip(Character character, JButton button) 
    {
        // Se esiste già un tooltip per questo bottone, rimuovilo prima
        this.removeCharacterTooltip(button);
        
        JWindow tooltipWindow = new JWindow();
        
        // Panel principale con bordo arrotondato
        JPanel mainPanel = new JPanel() 
        {
			private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) 
			{
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Sfondo pergamena con gradiente
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(244, 228, 188),
                    getWidth(), getHeight(), new Color(230, 211, 163)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Bordo medievale
                g2d.setStroke(new BasicStroke(3));
                g2d.setColor(new Color(139, 69, 19));
                g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 20, 20);
                
                // Bordo interno decorativo
                g2d.setStroke(new BasicStroke(1));
                g2d.setColor(new Color(139, 69, 19, 80));
                g2d.drawRoundRect(8, 8, getWidth()-17, getHeight()-17, 15, 15);
                
                // Ornamenti agli angoli
                drawCornerOrnaments(g2d);
                
                g2d.dispose();
            }
            
            private void drawCornerOrnaments(Graphics2D g2d) {
                g2d.setColor(new Color(139, 69, 19));
                int ornamentSize = 8;
                
                // Angolo superiore sinistro
                g2d.fillOval(5, 5, ornamentSize, ornamentSize);
                // Angolo superiore destro
                g2d.fillOval(getWidth() - ornamentSize - 5, 5, ornamentSize, ornamentSize);
                // Angolo inferiore sinistro
                g2d.fillOval(5, getHeight() - ornamentSize - 5, ornamentSize, ornamentSize);
                // Angolo inferiore destro
                g2d.fillOval(getWidth() - ornamentSize - 5, getHeight() - ornamentSize - 5, ornamentSize, ornamentSize);
            }
        };
        
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setOpaque(false);
        
        // Header con nome del personaggio
        JLabel nameLabel = new JLabel(character.getClass().getSimpleName(), JLabel.CENTER);
        nameLabel.setFont(new Font("Serif", Font.BOLD, 16));
        nameLabel.setForeground(new Color(139, 69, 19));
        nameLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 5, 8));
        
        // Panel per le statistiche
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(3, 10, 8, 10));
        
        // Separatore decorativo
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(139, 69, 19));
        separator.setBackground(new Color(139, 69, 19));
        
        // Aggiunta delle statistiche con icone e colori
	    statsPanel.add(createStatRow("❤", "HP", character.getCurrentHealth() + "/" + character.getMaxHealth(), new Color(220, 20, 60)));
	    statsPanel.add(Box.createVerticalStrut(2));
	    statsPanel.add(createStatRow("⚔", "ATT", String.valueOf(character.getPower()), new Color(255, 69, 0)));
	    statsPanel.add(Box.createVerticalStrut(2));
	    statsPanel.add(createStatRow("🛡", "DEF", String.valueOf(character.getDefence()), new Color(70, 130, 180)));
	    statsPanel.add(Box.createVerticalStrut(2));
	    statsPanel.add(createStatRow("⚡", "SPD", String.valueOf(character.getSpeed()), new Color(50, 205, 50)));
	    statsPanel.add(Box.createVerticalStrut(2));
	    statsPanel.add(createStatRow("🗡", "Arma", character.getWeapon().getClass().getSimpleName(), new Color(184, 134, 11)));
	    
        mainPanel.add(nameLabel, BorderLayout.NORTH);
        mainPanel.add(separator, BorderLayout.CENTER);
        mainPanel.add(statsPanel, BorderLayout.SOUTH);
        
        tooltipWindow.add(mainPanel);
        tooltipWindow.pack();
        
        // Imposta la forma arrotondata della finestra
        tooltipWindow.setShape(new RoundRectangle2D.Double(0, 0, tooltipWindow.getWidth(), tooltipWindow.getHeight(), 20, 20));
        
        // Crea il MouseListener dedicato per questo tooltip
        MouseListener tooltipMouseListener = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Verifica se il bottone è ancora valido
                if (!button.isShowing()|| !button.isDisplayable()) 
                {
                    tooltipWindow.setVisible(false);
                    return;
                }
                Point locationOnScreen = button.getLocationOnScreen();
                int x = locationOnScreen.x + button.getWidth() + 10;
                int y = locationOnScreen.y;
                
                // Controlla se il tooltip esce dallo schermo
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                if (x + tooltipWindow.getWidth() > screenSize.width) {
                    x = locationOnScreen.x - tooltipWindow.getWidth() - 10;
                }
                if (y + tooltipWindow.getHeight() > screenSize.height) {
                    y = screenSize.height - tooltipWindow.getHeight() - 10;
                }
                
                tooltipWindow.setLocation(x, y);
                tooltipWindow.setVisible(true);
            }
            
            @Override
            public void mouseExited(MouseEvent e) 
            {
            	if (tooltipWindow != null && tooltipWindow.isVisible()) 
            	{
                    tooltipWindow.setVisible(false);
                }
        	}
        };
        
        // Aggiungi il listener al bottone
        button.addMouseListener(tooltipMouseListener);
        
        // Salva i dati del tooltip nella mappa
        this.tooltipListeners.put(button, new TooltipListenerData(tooltipMouseListener, tooltipWindow));
    }
    
    public void removeCharacterTooltip(JButton button) 
    {
        // Recupera i dati del tooltip per questo bottone
        TooltipListenerData tooltipData = tooltipListeners.get(button);
        
        if (tooltipData != null) 
        {
            // Nascondi il tooltip se è visibile
            if (tooltipData.tooltipWindow.isVisible()) 
            {
                tooltipData.tooltipWindow.setVisible(false);
            }
            
            // Rimuovi solo il MouseListener specifico del tooltip
            button.removeMouseListener(tooltipData.mouseListener);
            
            // Pulisci la finestra tooltip
            tooltipData.tooltipWindow.dispose();
            tooltipData.tooltipWindow.getContentPane().removeAll();
           
            // Rimuovi dalla mappa
            tooltipListeners.remove(button);
        }
    }
    
    /**
     * Controlla se un bottone ha un tooltip attivo
     */
    public boolean hasTooltip(JButton button) {
        return tooltipListeners.containsKey(button);
    }
    
    /**
     * Ottiene il numero di tooltip attivi
     */
    public int getActiveTooltipCount() {
        return tooltipListeners.size();
    }
    
    // Metodo helper per creare le righe delle statistiche
    private JPanel createStatRow(String icon, String label, String value, Color valueColor) {
        JPanel row = new JPanel() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Sfondo semitrasparente per la riga
                g2d.setColor(new Color(139, 69, 19, 20));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Bordo sinistro decorativo
                g2d.setColor(new Color(139, 69, 19));
                g2d.fillRoundRect(0, 1, 3, getHeight()-2, 2, 2);
                
                g2d.dispose();
            }
        };
        
        row.setLayout(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        
        // Panel per icona e label
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        leftPanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        
        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Serif", Font.BOLD, 12));
        labelText.setForeground(new Color(101, 67, 33));
        
        leftPanel.add(iconLabel);
        leftPanel.add(labelText);
        
        // Label per il valore
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Serif", Font.BOLD, 12));
        valueLabel.setForeground(valueColor);
        valueLabel.setOpaque(true);
        valueLabel.setBackground(new Color(255, 255, 255, 60));
        valueLabel.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
        
        row.add(leftPanel, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);
        
        // Effetto hover
        row.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                row.repaint();
            }
            
            @Override  
            public void mouseExited(MouseEvent e) {
                row.repaint();
            }
        });
        
        return row;
    }
    
    
    public void removeAllTooltips() {
        for (TooltipListenerData data : tooltipListeners.values()) {
            if (data.tooltipWindow != null && data.tooltipWindow.isVisible()) {
                data.tooltipWindow.setVisible(false);
            }
        }
        tooltipListeners.clear();
    }

}
