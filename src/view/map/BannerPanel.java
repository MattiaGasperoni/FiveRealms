package view.map;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

/**
 * Panel class that manages both level completion banners and user guidance messages.
 * This component provides two display modes:
 * 1. Normal banner mode for displaying brief user guidance messages at the bottom of the screen
 * 2. Full-screen mode for displaying level completion celebrations with medieval-themed styling
 * 
 * The panel automatically hides after a configurable timeout period and supports
 * custom styling for different message types.
 */
public class BannerPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final int timerFadeMS = 7000;

	private int width;
	private int height;
	private final JLabel messageLabel;

	/** Timer that automatically hides the banner after timerFade milliseconds */
	private final Timer timer;

	/**
	 * Constructs a new BannerPanel with the specified dimensions.
	 * Initializes the layout, components, and auto-hide functionality.
	 * 
	 * @param width The width of the parent container
	 * @param height The height of the parent container
	 */
	public BannerPanel(int width, int height) {    
		this.width = width;
		this.height = height;
		this.messageLabel = this.createMessageLabel();
		this.timer = this.createAutoHideTimer();

		this.setupLayout();
		this.addComponents();
		this.setPositionBanner(this.width, this.height);
		this.setVisible(false);
	}

	/**
	 * Displays a normal banner message that tells the user what to do.
	 * Shows a small banner at the bottom of the screen with the specified text.
	 * The banner will automatically hide after the timer expires.
	 * 
	 * @param text The message to display to the user
	 */
	public void showMessage(String text) {
		this.resetToNormalMode();

		this.messageLabel.setText(text);

		this.showBannerWithTimer();
	}

	/**
	 * Displays a full-screen celebratory banner when a level is completed.
	 * Creates an elaborate medieval-themed display with parchment background,
	 * congratulatory text, and decorative elements. This method completely
	 * replaces the panel's contents with the celebration display.
	 */
	public void showFullScreenMessage() {
		this.resetToNormalMode();

		// Set full-screen dimensions
		this.setBounds(0, 0, this.width, this.height);

		// Temporarily remove all existing components
		this.removeAll();

		// Main panel with medieval background
		JPanel bannerPanel = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g.create();

				// Antialiasing for smooth edges
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				// Aged parchment background with gradient
				GradientPaint parchmentGradient = new GradientPaint(
						0, 0, new Color(240, 225, 180),
						0, getHeight(), new Color(210, 185, 130)
						);
				g2d.setPaint(parchmentGradient);
				g2d.fillRect(0, 0, getWidth(), getHeight());

				// Light parchment texture
				g2d.setColor(new Color(180, 155, 100, 20));
				Random rand = new Random(123);
				for (int i = 0; i < 100; i++) {
					int x = rand.nextInt(getWidth());
					int y = rand.nextInt(getHeight());
					int size = rand.nextInt(15) + 3;
					g2d.fillOval(x, y, size, size);
				}

				// Simple border
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

		// Add the banner to the main window
		this.add(bannerPanel);
		this.revalidate();
		this.repaint();

		this.showBannerWithTimer();
	}

	/**
	 * Creates the main message label for normal banner mode.
	 * Configures the label with default styling for user guidance messages.
	 * 
	 * @return A configured JLabel for displaying messages
	 */
	private JLabel createMessageLabel() {
		JLabel label = new JLabel("", SwingConstants.CENTER);
		label.setForeground(Color.BLACK);
		label.setFont(new Font("Arial", Font.BOLD, 14));
		return label;
	}

	/**
	 * Creates the auto-hide timer that hides the banner after 7 seconds.
	 * The timer is configured to run only once per activation.
	 * 
	 * @return A Timer configured to hide the banner automatically
	 */
	private Timer createAutoHideTimer() {
		Timer timer = new Timer(BannerPanel.timerFadeMS, e -> setVisible(false));
		timer.setRepeats(false);
		return timer;
	}

	/**
	 * Sets up the panel's layout manager and background styling.
	 * Configures the panel for normal banner mode display.
	 */
	private void setupLayout() {
		this.setLayout(new BorderLayout());
		this.setBackground(new Color(255, 200, 100)); // Light orange
	}

	/**
	 * Adds the message label to the panel's center region.
	 */
	private void addComponents() {
		this.add(messageLabel, BorderLayout.CENTER);
	}

	/**
	 * Positions the banner at the bottom of the parent container.
	 * Sets the banner to appear as a horizontal strip at the bottom of the screen.
	 * 
	 * @param width The width of the parent container
	 * @param frameHeight The height of the parent container
	 */
	private void setPositionBanner(int width, int frameHeight) {
		int bannerHeight = 30;
		this.setBounds(0, frameHeight - bannerHeight, width, bannerHeight);
	}

	/**
	 * Resets the panel to normal banner mode styling.
	 * Restores default colors, fonts, and layout for standard message display.
	 * This method is called before showing any new message to ensure clean state.
	 */
	private void resetToNormalMode() {        
		// Reset to normal styling
		this.setBackground(new Color(255, 200, 100));

		this.messageLabel.setFont(new Font("Arial", Font.BOLD, 14));     
		this.messageLabel.setForeground(Color.BLACK);
	}

	/**
	 * Makes the banner visible and starts the auto-hide timer.
	 * If the timer is already running, it restarts the countdown.
	 * This ensures the banner stays visible for the full duration even
	 * if multiple messages are shown in quick succession.
	 */
	private void showBannerWithTimer() {
		setVisible(true);

		if (timer.isRunning()) {
			timer.restart();
		} else {
			timer.start();
		}
	}
}