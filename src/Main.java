
import javax.swing.JFrame;

import model.*;
import model.gameStatus.Game;
import view.GrapichsMenu;

public class Main {
	
	public static void main(String[] args) {
		Game g = new Game();
		JFrame f = new JFrame("Partita");
		f.setLocationRelativeTo(null);
		//f.setContentPane(new GrapichsMenu());
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setBounds(0, 0, 600, 600);
		f.setVisible(true);
	}

}
