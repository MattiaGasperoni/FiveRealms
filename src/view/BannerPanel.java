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
    }

    public void showMessage(String text) {
        messageLabel.setText(text);
        this.setVisible(true); // mostra il banner

        if (resetTimer != null && resetTimer.isRunning()) {
            resetTimer.stop();
        }

        resetTimer = new Timer(100000, e -> {
            this.setVisible(false); // nasconde il banner
        });
        resetTimer.setRepeats(false);
        resetTimer.start();
    }
}
