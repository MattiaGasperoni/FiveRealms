package model.gameStatus;

import java.util.ArrayList;
import java.util.List;

public class Game {

	private List<Level> levels;
	private static final int NUMLIVELLI = 5;
	
	public Game() {
		this.levels = new ArrayList<>();
	}

	public List<Level> getLevels() {
		return levels;
	}

	//test github
}
