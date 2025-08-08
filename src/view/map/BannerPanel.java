package view.map;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

/**
 * Classe che gestisce sia il banner i completamento livello che quelo con i consigli per l'utente
 */
public class BannerPanel extends JPanel 
{
    private static final long serialVersionUID = 1L;
    
    private int width;
    private int height;
    private final JLabel messageLabel;
    private final Timer timer;
    
    
    public BannerPanel(int width, int height) 
    {    
        this.width        = width;
        this.height       = height;
        this.messageLabel = this.createMessageLabel();
        this.timer        = this.createAutoHideTimer();
        
        this.setupLayout();
        this.addComponents();
        this.setPositionBanner(this.width, this.height);
        this.setVisible(false);
    }
    
    /* Banner che dice all'utente cosa deve fare */
    public void showMessage(String text) 
    {
    	this.resetToNormalMode();
    	
    	this.messageLabel.setText(text);
    	
        this.showBannerWithTimer();
    }
    
    /* Banner da mostrare quando si completa un Livello */
    public void showFullScreenMessage() 
    {
        this.resetToNormalMode();
        
        // Imposta dimensioni a schermo intero
        this.setBounds(0, 0, this.width, this.height);
        
        // Rimuovi tutti i componenti esistenti temporaneamente
        this.removeAll();
        
        // Pannello principale con sfondo medievale
        JPanel bannerPanel = new JPanel() 
        {
			private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) 
			{
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                // Antialiasing per bordi lisci
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Sfondo pergamena invecchiata con gradiente
                GradientPaint parchmentGradient = new GradientPaint(
                    0, 0, new Color(240, 225, 180),
                    0, getHeight(), new Color(210, 185, 130)
                );
                g2d.setPaint(parchmentGradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Texture pergamena leggera
                g2d.setColor(new Color(180, 155, 100, 20));
                Random rand = new Random(123);
                for (int i = 0; i < 100; i++) {
                    int x = rand.nextInt(getWidth());
                    int y = rand.nextInt(getHeight());
                    int size = rand.nextInt(15) + 3;
                    g2d.fillOval(x, y, size, size);
                }
                
                // Bordo semplice
                g2d.setStroke(new BasicStroke(6.0f));
                g2d.setColor(new Color(139, 69, 19));
                g2d.drawRect(40, 40, getWidth() - 80, getHeight() - 80);
                
                g2d.dispose();
            }
        };
        
        bannerPanel.setLayout(new GridBagLayout());
        bannerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Congratulations!
        JLabel congratsLabel = new JLabel("Congratulations!");
        congratsLabel.setFont(new Font("Serif", Font.BOLD, 54));
        congratsLabel.setForeground(new Color(139, 69, 19));
        congratsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 40, 0);
        bannerPanel.add(congratsLabel, gbc);
        
        // The next level is starting!
        JLabel nextLevelLabel = new JLabel("The next level is starting!");
        nextLevelLabel.setFont(new Font("Serif", Font.ITALIC, 32));
        nextLevelLabel.setForeground(new Color(160, 82, 45));
        nextLevelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        bannerPanel.add(nextLevelLabel, gbc);
        
        // Aggiungi il banner alla finestra principale
        this.add(bannerPanel);
        this.revalidate();
        this.repaint();
        
        this.showBannerWithTimer();
    }
    
    private JLabel createMessageLabel() 
    {
        JLabel label = new JLabel("", SwingConstants.CENTER);
        label.setForeground(Color.BLACK);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        return label;
    }
        
    private Timer createAutoHideTimer() 
    {
    	// Nasconde automaticamente il banner dopo 7 secondi
        Timer timer = new Timer(7000, e -> setVisible(false));
        timer.setRepeats(false);
        return timer;
    }
    
    private void setupLayout() 
    {
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(255, 200, 100)); // arancione chiaro
    }
    
    private void addComponents() 
    {
        this.add(messageLabel, BorderLayout.CENTER);
    }
    
    private void setPositionBanner(int width, int frameHeight) 
    {
        int bannerHeight = 30;
        this.setBounds(0, frameHeight - bannerHeight, width, bannerHeight);
    }
    
    private void resetToNormalMode() 
    {        
        // Reset stile normale
    	this.setBackground(new Color(255, 200, 100));
    	
    	this.messageLabel.setFont(new Font("Arial", Font.BOLD, 14));     
    	this.messageLabel.setForeground(Color.BLACK);
    }
    
    private void showBannerWithTimer() 
    {
        setVisible(true);
        
        if (timer.isRunning()) 
        {
            timer.restart();
        } 
        else 
        {
            timer.start();
        }
    }
}