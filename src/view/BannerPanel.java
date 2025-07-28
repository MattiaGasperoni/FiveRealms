package view;

import javax.swing.*;
import java.awt.*;

public class BannerPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JLabel messageLabel;
    private Timer resetTimer;

    public BannerPanel(int width, int frameHeight) 
    {
        int bannerHeight = 30;
        this.setLayout(new BorderLayout());

        // Sfondo arancione chiaro
        this.setBackground(new Color(255, 200, 100));

        // Posizionato in basso
        this.setBounds(0, frameHeight - bannerHeight, width, bannerHeight);

        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setForeground(Color.BLACK);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 14));

        this.add(messageLabel, BorderLayout.CENTER);

        this.setVisible(false); // inizialmente nascosto
        
        resetTimer = new Timer(3000, e -> this.setVisible(false));
        resetTimer.setRepeats(false);
    }

    public void showMessage(String text) 
    {
        if (!SwingUtilities.isEventDispatchThread()) 
        {
            SwingUtilities.invokeLater(() -> showMessage(text));
            return;
        }

        messageLabel.setText(text);
        
        this.setVisible(true);

        if (resetTimer.isRunning()) 
        {
            resetTimer.restart();
        } 
        else
        {
            resetTimer.start();
        }
    }
    /* Metodo per fare il banner a tutto schermo*/
    public void showFullScreenMessage(String text, int width, int height)
    {
        if (!SwingUtilities.isEventDispatchThread()) 
        {
            SwingUtilities.invokeLater(() -> showFullScreenMessage(text, width, height));
            return;
        }

        this.setBounds(0, 0, width, height);
        this.setBackground(new Color(255, 150, 50)); // colore più acceso se vuoi
        messageLabel.setText(text);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 32)); // testo più grande
        messageLabel.setForeground(Color.BLACK);

        this.setVisible(true);

        if (resetTimer.isRunning()) 
        {
            resetTimer.restart();
        } 
        else 
        {
            resetTimer.start();
        }
    }
}

