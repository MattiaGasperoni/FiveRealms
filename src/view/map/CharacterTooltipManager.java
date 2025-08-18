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

public class CharacterTooltipManager {

	/** Map to keep track of listeners associated with tooltips for each button */
	private final Map<JButton, TooltipListenerData> tooltipListeners = new ConcurrentHashMap<>();

	/**
	 * Inner class to contain listener data and tooltip window information.
	 * This data structure allows proper cleanup of both the mouse listener
	 * and the associated tooltip window when removing tooltips.
	 */
	private static class TooltipListenerData {
		final MouseListener mouseListener;
		final JWindow tooltipWindow;

		/**
		 * Creates a new TooltipListenerData instance.
		 * 
		 * @param listener The mouse listener associated with this tooltip
		 * @param window The tooltip window to be displayed
		 */
		TooltipListenerData(MouseListener listener, JWindow window) {
			this.mouseListener = listener;
			this.tooltipWindow = window;
		}
	}

	/**
	 * Creates and shows a character tooltip for the specified button.
	 * The tooltip displays comprehensive character information including stats,
	 * weapon, and range in a medieval-themed design with parchment styling.
	 * 
	 * If a tooltip already exists for the button, it will be removed before
	 * creating the new one to prevent duplicates.
	 * 
	 * @param character The character whose information will be displayed in the tooltip
	 * @param button The button that will trigger the tooltip on mouse hover
	 */
	public void showCharacterTooltip(Character character, JButton button) {
		// If a tooltip already exists for this button, remove it first
		this.removeCharacterTooltip(button);

		JWindow tooltipWindow = new JWindow();

		// Main panel with rounded border
		JPanel mainPanel = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				// Parchment background with gradient
				GradientPaint gradient = new GradientPaint(
						0, 0, new Color(244, 228, 188),
						getWidth(), getHeight(), new Color(230, 211, 163)
						);
				g2d.setPaint(gradient);
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

				// Medieval border
				g2d.setStroke(new BasicStroke(3));
				g2d.setColor(new Color(139, 69, 19));
				g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 20, 20);

				// Decorative inner border
				g2d.setStroke(new BasicStroke(1));
				g2d.setColor(new Color(139, 69, 19, 80));
				g2d.drawRoundRect(8, 8, getWidth()-17, getHeight()-17, 15, 15);

				// Corner ornaments
				drawCornerOrnaments(g2d);

				g2d.dispose();
			}

			/**
			 * Draws decorative ornaments at the four corners of the tooltip.
			 * 
			 * @param g2d The Graphics2D context for drawing
			 */
			private void drawCornerOrnaments(Graphics2D g2d) {
				g2d.setColor(new Color(139, 69, 19));
				int ornamentSize = 8;

				// Top left corner
				g2d.fillOval(5, 5, ornamentSize, ornamentSize);
				// Top right corner
				g2d.fillOval(getWidth() - ornamentSize - 5, 5, ornamentSize, ornamentSize);
				// Bottom left corner
				g2d.fillOval(5, getHeight() - ornamentSize - 5, ornamentSize, ornamentSize);
				// Bottom right corner
				g2d.fillOval(getWidth() - ornamentSize - 5, getHeight() - ornamentSize - 5, ornamentSize, ornamentSize);
			}
		};

		mainPanel.setLayout(new BorderLayout());
		mainPanel.setOpaque(false);

		// Header with character name
		JLabel nameLabel = new JLabel(character.getClass().getSimpleName(), JLabel.CENTER);
		nameLabel.setFont(new Font("Serif", Font.BOLD, 16));
		nameLabel.setForeground(new Color(139, 69, 19));
		nameLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 5, 8));

		// Panel for statistics
		JPanel statsPanel = new JPanel();
		statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
		statsPanel.setOpaque(false);
		statsPanel.setBorder(BorderFactory.createEmptyBorder(3, 10, 8, 10));

		// Decorative separator
		JSeparator separator = new JSeparator();
		separator.setForeground(new Color(139, 69, 19));
		separator.setBackground(new Color(139, 69, 19));

		// Adding statistics with icons and colors
		statsPanel.add(createStatRow("❤", "HP", character.getCurrentHealth() + "/" + character.getMaxHealth(), new Color(220, 20, 60)));
		statsPanel.add(Box.createVerticalStrut(2));
		statsPanel.add(createStatRow("⚔", "ATT", String.valueOf(character.getPower()), new Color(255, 69, 0)));
		statsPanel.add(Box.createVerticalStrut(2));
		statsPanel.add(createStatRow("🛡", "DEF", String.valueOf(character.getDefence()), new Color(70, 130, 180)));
		statsPanel.add(Box.createVerticalStrut(2));
		statsPanel.add(createStatRow("⚡", "SPD", String.valueOf(character.getSpeed()), new Color(50, 205, 50)));
		statsPanel.add(Box.createVerticalStrut(2));
		statsPanel.add(createStatRow("🗡", "Weapon", character.getWeapon().getClass().getSimpleName(), new Color(184, 134, 11)));
		statsPanel.add(Box.createVerticalStrut(2));
		statsPanel.add(createStatRow("🏹", "Range", String.valueOf(character.getRange()), new Color(184, 134, 11)));

		mainPanel.add(nameLabel, BorderLayout.NORTH);
		mainPanel.add(separator, BorderLayout.CENTER);
		mainPanel.add(statsPanel, BorderLayout.SOUTH);

		tooltipWindow.add(mainPanel);
		tooltipWindow.pack();

		// Set the rounded shape of the window
		tooltipWindow.setShape(new RoundRectangle2D.Double(0, 0, tooltipWindow.getWidth(), tooltipWindow.getHeight(), 20, 20));

		// Create the dedicated MouseListener for this tooltip
		MouseListener tooltipMouseListener = new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				// Check if the button is still valid
				if (!button.isShowing() || !button.isDisplayable()) {
					tooltipWindow.setVisible(false);
					return;
				}
				Point locationOnScreen = button.getLocationOnScreen();
				int x = locationOnScreen.x + button.getWidth() + 10;
				int y = locationOnScreen.y;

				// Check if tooltip goes off screen
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
			public void mouseExited(MouseEvent e) {
				if (tooltipWindow != null && tooltipWindow.isVisible()) {
					tooltipWindow.setVisible(false);
				}
			}
		};

		// Add the listener to the button
		button.addMouseListener(tooltipMouseListener);

		// Save the tooltip data in the map
		this.tooltipListeners.put(button, new TooltipListenerData(tooltipMouseListener, tooltipWindow));
	}

	/**
	 * Removes the character tooltip associated with the specified button.
	 * This method properly cleans up the mouse listener, hides the tooltip window,
	 * and removes all references to prevent memory leaks.
	 * 
	 * @param button The button whose tooltip should be removed
	 */
	public void removeCharacterTooltip(JButton button) {
		// Retrieve tooltip data for this button
		TooltipListenerData tooltipData = tooltipListeners.get(button);

		if (tooltipData != null) {
			// Hide the tooltip if it's visible
			if (tooltipData.tooltipWindow.isVisible()) {
				tooltipData.tooltipWindow.setVisible(false);
			}

			// Remove only the specific MouseListener for the tooltip
			button.removeMouseListener(tooltipData.mouseListener);

			// Clean up the tooltip window
			tooltipData.tooltipWindow.dispose();
			tooltipData.tooltipWindow.getContentPane().removeAll();

			// Remove from map
			tooltipListeners.remove(button);
		}
	}

	/**
	 * Checks if a button has an active tooltip.
	 * 
	 * @param button The button to check
	 * @return true if the button has an active tooltip, false otherwise
	 */
	public boolean hasTooltip(JButton button) {
		return tooltipListeners.containsKey(button);
	}

	/**
	 * Gets the number of currently active tooltips.
	 * 
	 * @return The count of active tooltips being managed
	 */
	public int getActiveTooltipCount() {
		return tooltipListeners.size();
	}

	/**
	 * Helper method to create statistic rows for character information display.
	 * Each row contains an icon, label, and colored value with medieval styling.
	 * 
	 * @param icon The emoji or symbol to display as an icon
	 * @param label The text label for the statistic
	 * @param value The value to display for the statistic
	 * @param valueColor The color to use for the value text
	 * @return A JPanel containing the formatted statistic row
	 */
	private JPanel createStatRow(String icon, String label, String value, Color valueColor) {
		JPanel row = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				// Semi-transparent background for the row
				g2d.setColor(new Color(139, 69, 19, 20));
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

				// Decorative left border
				g2d.setColor(new Color(139, 69, 19));
				g2d.fillRoundRect(0, 1, 3, getHeight()-2, 2, 2);

				g2d.dispose();
			}
		};

		row.setLayout(new BorderLayout());
		row.setOpaque(false);
		row.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

		// Panel for icon and label
		JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
		leftPanel.setOpaque(false);

		JLabel iconLabel = new JLabel(icon);
		iconLabel.setFont(new Font("Dialog", Font.PLAIN, 14));

		JLabel labelText = new JLabel(label);
		labelText.setFont(new Font("Serif", Font.BOLD, 12));
		labelText.setForeground(new Color(101, 67, 33));

		leftPanel.add(iconLabel);
		leftPanel.add(labelText);

		// Label for the value
		JLabel valueLabel = new JLabel(value);
		valueLabel.setFont(new Font("Serif", Font.BOLD, 12));
		valueLabel.setForeground(valueColor);
		valueLabel.setOpaque(true);
		valueLabel.setBackground(new Color(255, 255, 255, 60));
		valueLabel.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));

		row.add(leftPanel, BorderLayout.WEST);
		row.add(valueLabel, BorderLayout.EAST);

		// Hover effect
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

	/**
	 * Removes all active tooltips and clears the manager's internal state.
	 * This method should be called when cleaning up the entire tooltip system,
	 * such as when closing a level or resetting the game state.
	 */
	public void removeAllTooltips() {
		for (TooltipListenerData data : tooltipListeners.values()) {
			if (data.tooltipWindow != null && data.tooltipWindow.isVisible()) {
				data.tooltipWindow.setVisible(false);
			}
		}
		tooltipListeners.clear();
	}
}