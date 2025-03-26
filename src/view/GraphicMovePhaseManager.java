package view;

import java.util.List;

import model.characters.Character;

public class GraphicMovePhaseManager {

	public void movementPhase(Character attacker, List<Character> alliesList, List<Character> enemiesList) {
		// TODO Auto-generated method stub
		/* Qui mostrare graficamente il movimento fattibile 
		 * da parte del personaggio che deve attaccare,
		 * 
		 * Implementare una struttura dati contenente i blocchi dove posso spostarmi,
		 * rendendo inacessibili i blocchi dove sono gia presenti alleati o nemici.
		 * 
		 * Usare la struttura dati con tutti i pulsanti del LevelMap, e fare una chiamata
		 * al metodo showGrid() che fara vedere graficamente i blocchi grigi dove mi posso
		 * spostare.
		 * 
		 * Quando clicchi su uno di questi blocchi grigi mi vado a spostare:
		 * attacker.moveTo(bottoneCliccato.getPosition()).
		 */
	}

	public Character chooseTarget(List<Character> alliesList, List<Character> enemiesList) {
		// TODO Auto-generated method stub
		/* Il taget è colui che è stato selezionato (premuto) 
		 * 
		 * Fa il fight su quel personaggio selezionato
		 * 
		 */
		return null;
	}

	/*E metodo showGrid che evidenzia i blocchi in cui puoi spostarti*/ 
	private void showGrid() {
		
	}

}
