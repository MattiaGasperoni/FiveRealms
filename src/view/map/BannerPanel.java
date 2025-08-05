package view.map;

import javax.swing.*;
import java.awt.*;

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
    
    /* Banner da mostare quando si completa un Livello */
    public void showFullScreenMessage(String text) 
    {
    	this.resetToNormalMode();
        
    	this.setBounds(0, 0, this.width, this.height);
    	this.setBackground(new Color(255, 150, 50));
        
    	this.messageLabel.setText(text);
    	this.messageLabel.setFont(new Font("Arial", Font.BOLD, 32));
        
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
    	// Nasconde automaticamente il banner dopo 3 secondi
        Timer timer = new Timer(3000, e -> setVisible(false));
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