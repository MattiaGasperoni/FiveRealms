package view;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BannerPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JLabel messageLabel;
    private Timer resetTimer;
    private JPanel buttonPanel;
    private boolean isVictoryMode = false;
    
    public BannerPanel(int width, int frameHeight) 
    {
        int bannerHeight = 30;
        this.setLayout(new BorderLayout());
        // Sfondo arancione chiaro
        this.setBackground(new Color(255, 200, 100));
        // Posizionato in basso
        this.setBounds(0, frameHeight - bannerHeight, width, frameHeight);
        
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setForeground(Color.BLACK);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        this.add(messageLabel, BorderLayout.CENTER);
        
        // Panel per i pulsanti (inizialmente nascosto)
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);
        this.add(buttonPanel, BorderLayout.SOUTH);
        
        this.setVisible(false); // inizialmente nascosto
        
        resetTimer = new Timer(3000, e -> this.setVisible(false));
        resetTimer.setRepeats(false);
    }
    
    public void showMessage(String text) 
    {
        // Reset a modalità normale
        isVictoryMode = false;
        buttonPanel.setVisible(false);
        
        messageLabel.setText(text);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 14));
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
    
    /* Metodo per fare il banner a tutto schermo*/
    public void showFullScreenMessage(String text, int width, int height)
    {
        // Reset a modalità normale
        isVictoryMode = false;
        buttonPanel.setVisible(false);
        
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
    
    /* Nuovo metodo per il messaggio di vittoria */
    public void showVictoryMessage(String text, int width, int height) 
    {
        isVictoryMode = true;
        
        // Ferma il timer automatico per la vittoria
        if (resetTimer.isRunning()) {
            resetTimer.stop();
        }
        
        // Imposta dimensioni a tutto schermo
        this.setBounds(0, 0, width, height);
        
        // Sfondo dorato per la vittoria
        this.setBackground(new Color(255, 215, 0)); // Oro
        
        // Testo di vittoria con stile speciale
        messageLabel.setText("<html><div style='text-align: center;'>" +
                           "🎉 " + text + " 🎉<br/>" +
                           "<font size='6'>HAI VINTO!</font>" +
                           "</div></html>");
        messageLabel.setFont(new Font("Arial", Font.BOLD, 28));
        messageLabel.setForeground(new Color(139, 69, 19)); // Marrone scuro per contrasto
        
        // Crea i pulsanti per la vittoria
        createVictoryButtons();
        
        this.setVisible(true);
    }
    
    private void createVictoryButtons() 
    {
        buttonPanel.removeAll(); // Pulisce eventuali pulsanti precedenti
        
        // Pulsante per chiudere il gioco
        JButton exitButton = new JButton("Esci dal Gioco");
        exitButton.setFont(new Font("Arial", Font.BOLD, 16));
        exitButton.setPreferredSize(new Dimension(180, 40));
        exitButton.setBackground(new Color(220, 20, 60)); // Rosso scuro
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setBorder(BorderFactory.createRaisedBevelBorder());
        
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        // Pulsante per nascondere il banner (opzionale)
        JButton hideButton = new JButton("Nascondi Banner");
        hideButton.setFont(new Font("Arial", Font.BOLD, 16));
        hideButton.setPreferredSize(new Dimension(180, 40));
        hideButton.setBackground(new Color(70, 130, 180)); // Blu acciaio
        hideButton.setForeground(Color.WHITE);
        hideButton.setFocusPainted(false);
        hideButton.setBorder(BorderFactory.createRaisedBevelBorder());
        
        hideButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                isVictoryMode = false;
            }
        });
        
        buttonPanel.add(hideButton);
        buttonPanel.add(exitButton);
        buttonPanel.setVisible(true);
    }
    
    // Getter per sapere se siamo in modalità vittoria
    public boolean isVictoryMode() {
        return isVictoryMode;
    }
    
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (!visible) {
            isVictoryMode = false;
            buttonPanel.setVisible(false);
        }
    }
}