package model.gameStatus;

import java.io.IOException;
import java.util.*;

import javax.swing.SwingUtilities;
import controller.*;
import model.characters.AbstractCharacter;
import model.characters.Character;
import model.point.Point;
import view.*;
import view.map.LevelMap;

public class Level 
{
	// Stati del livello
    private enum LevelPhase 
    {
    	BATTLE_PHASE,          // È un round di combattimento
        CHECK_END_LEVEL,       // Controllo a fine round
        DONE 				   // Fine livello
    }
    
    // Stati della fase BATTLE_PHASE
    private enum RoundState 
    {
        INITIALIZING_TURN,      // Preparazione inizio round
        WAITING_FOR_MOVEMENT,   // Attesa movimento dell’utente
        WAITING_FOR_TARGET,     // Attesa scelta bersaglio
        UPDATE_MAP,             // Aggiorna mappa
        TURN_COMPLETED          // Turno completato
    }
    
    private LevelPhase currentLevelPhase;
    private RoundState currentTurnState;
    
    private List<Character> enemiesList;
    private List<Character> alliesList;
    
    private boolean levelCompleted;
    private boolean levelFailed;
    
    private PriorityQueue<Character> currentTurnOrder;
    private Character currentAttacker;
       
    private final LevelMap levelMap;
    private final BattlePhaseView movementPhaseManager;
    private final GameController controller;

    public Level(LevelMap map, GameController controller) 
    {
        this.levelMap = map;
        this.controller = controller;
        this.movementPhaseManager = new BattlePhaseView(this.levelMap, this.controller);

        this.enemiesList = this.levelMap.getEnemiesList();
        this.alliesList  = this.levelMap.getAlliesList();
        
        this.levelCompleted = false;
        this.levelFailed    = false;
    }

    public void play() throws IOException 
    {
    	
        this.levelMap.start();

        System.out.print(" Start level ->");
        
        this.levelMap.spawnCharacter(this.enemiesList);
        this.levelMap.spawnCharacter(this.alliesList);

        
        this.currentLevelPhase = LevelPhase.BATTLE_PHASE;
        this.currentTurnState  = RoundState.INITIALIZING_TURN;
    }

    // Metodo che aggiorna lo stato della fase BATTLE_PHASE
    public void update() 
    {
        switch (this.currentLevelPhase) 
        {
            case BATTLE_PHASE:
                this.handleBattlePhase();
                break;

            case CHECK_END_LEVEL:
                this.checkEndLevel();
                break;

            case DONE:
                //System.out.println("Chiusura Mappa");
                
                break;

            default:
                throw new IllegalStateException("Fase Sconosciuta: " + this.currentLevelPhase);
        }
    }
    
    // Metodo che aggiorna lo stato delle fase BATTLE
    private void handleBattlePhase() 
    {
        if (this.currentTurnState == RoundState.WAITING_FOR_MOVEMENT ||
            this.currentTurnState == RoundState.WAITING_FOR_TARGET) 
        {
            return; // In attesa dell'input utente
        }

        switch (this.currentTurnState) 
        {
            case INITIALIZING_TURN:
                this.initializeBattleRound();
                break;
        	
            case UPDATE_MAP:
            	this.handleUpdateMap();
                
            case TURN_COMPLETED:
                this.checkEndTurn();
                
                break;

            default:
                throw new IllegalStateException("Stato battaglia sconosciuto: " + this.currentTurnState);
        }
    }
    
    // Questo metodo viene SOLO chiamato quando inizia un nuovo round di movimento/attacco
    private void initializeBattleRound() 
    {
        System.out.println("\n\n=== INIZIA UN NUOVO ROUND ===");
        
        // Generiamo l'ordine di movimento/attacco dei personaggi per questo round 
        this.currentTurnOrder = this.getTurnOrder(this.alliesList, this.enemiesList);
        
        if (this.currentTurnOrder.isEmpty()) 
        {
            System.out.println("Coda del turno vuota, Round completato!");
            this.currentTurnState = RoundState.UPDATE_MAP;
            return;
        }
        
    	this.startNextTurn();
    }
    
    // Inizia il turno del personaggio i-esimo del round
    private void startNextTurn() 
    {
    	// Controlla se tutti gli alleati sono morti o se tutti i nemici sono sconfitti
    	//this.endTurnCheck();
    	
    	//SwingUtilities.invokeLater(() -> {this.levelMap.updateMap();});
    	
        if (this.currentTurnOrder.isEmpty()) 
        {
            System.out.println("Tutti i personaggi hanno giocato, Round completato!");
            this.currentTurnState = RoundState.TURN_COMPLETED;
            return;
        }
        
        // Trova il prossimo personaggio vivo
        Character nextAttacker = null;
        
        while (!this.currentTurnOrder.isEmpty()) 
        {
            Character candidate = this.currentTurnOrder.poll();
            
            if (candidate.isAlive()) 
            {
                nextAttacker = candidate;
                break;
            }
            else 
            {
                System.out.println("Il personaggio "+candidate.getClass().getSimpleName()+" non è vivo, salto al prossimo.");
            }
        }
        
        // Se non ci sono personaggi vivi, termina il round
        if (nextAttacker == null) 
        {
            System.out.println("Nessun personaggio vivo rimasto, fine round");
            this.currentTurnState = RoundState.UPDATE_MAP;
            return;
        }
        
        // Se ci sono personaggi vivi, impostiamo il nuovo attaccante e iniziamo il turno
        this.currentAttacker = nextAttacker;
        System.out.println("\n=== TURNO DI: " + currentAttacker.getClass().getSimpleName() + " ===");
        
        if (this.currentAttacker.isAllied())
        {
            this.startPlayerTurn();
        } 
        else
        {
            this.startAITurn();
        }
    }
    
    // Inizia il turno del giocatore alleato
    private void startPlayerTurn() 
    {
        this.currentTurnState = RoundState.WAITING_FOR_MOVEMENT;
        System.out.print("In attesa del movimento di " + currentAttacker.getClass().getSimpleName()+" -> ");
        
    	this.levelMap.updateBannerMessage("In attesa del movimento di: "+ currentAttacker.getClass().getSimpleName(), false);
        
        // Configura il movimento con callback
        this.movementPhaseManager.movementPhase(currentAttacker, () -> 
        {
        	this.levelMap.updateBannerMessage("Movimento completato, "+currentAttacker.getClass().getSimpleName()+" scegli un  bersaglio", false);
            
            this.onMovementCompleted();
        });
    }
    
    // Questo metodo viene chiamato quando il movimento è completato
    private void onMovementCompleted() 
    {
        this.currentTurnState = RoundState.WAITING_FOR_TARGET;
        
        this.movementPhaseManager.chooseTarget(this.enemiesList, this.currentAttacker, () -> 
        {
            this.currentTurnState = RoundState.UPDATE_MAP;
        });
    }
    
    // Metod che gestisce il turno dell'AI
    private void startAITurn()
    {
    	try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}System.out.println("Turno AI");
		Character victim = alliesList.stream()
				   .min(Comparator.comparing(charac -> charac.getDistanceInSquares(currentAttacker.getPosition()))) //NOTE: If that's the wrong order (hard to test right now), put .reversed() on it. Picks closest enemy.
				   .orElse(null);
		
	    if (victim == null) {
	    	this.currentTurnState = RoundState.UPDATE_MAP;
	        return;
	    }
		
        List<Point> availablePositions = new ArrayList<>();

        for(int i = 0; i < 20; i++) { //AbstractMap.GRID_SIZE_WIDTH
        	for(int j = 0; j < 15; j++) {  //AbstractMap.GRID_SIZE_HEIGHT
        		availablePositions.add(new Point(i,j));
        	}
        }
        
        Set<Point> occupiedPositions = new HashSet<>();
        alliesList.stream().forEach(character -> occupiedPositions.add(character.getPosition()));
        enemiesList.stream().forEach(character -> occupiedPositions.add(character.getPosition()));
        
        movementPhaseManager.graphicMovementCharacterToPoint(currentAttacker, availablePositions.stream()
				.filter(point -> currentAttacker.getDistanceInSquares(point) <= (currentAttacker.getSpeed() / AbstractCharacter.SPEED_TO_MOVEMENT))
				.filter(point -> !occupiedPositions.contains(point)) //supposed to filter out already-occupied positions...
				.min(Comparator.comparing(point -> victim.getDistanceInSquares(point)))
				.orElse(currentAttacker.getPosition()));
        
        try 
        {
        	this.controller.fight(currentAttacker, victim, alliesList, enemiesList, levelMap);
        	
		} catch (IllegalArgumentException e) { //to handle the case where: even the closest enemy is still out of attack range after movement
			//System.out.println("Character " + currentAttacker.getClass().getSimpleName() + " cannot find an enemy to fight, ERROR: " + e); //for debug purposes
			//System.out.println("Attempted to attack " + victim.getClass().getSimpleName() + " in position: " + victim.getPosition() + " but self was in position: " + currentAttacker.getPosition()); //for debug purposes
			System.out.print(" Nessun nemico nel raggio d'attacco. Fase di attacco annullata.");
		}
        
        this.currentTurnState = RoundState.UPDATE_MAP;
    }
    
    
    private void handleUpdateMap() 
    {   
    	this.levelMap.updateMap();
    	
    	this.currentTurnState = RoundState.TURN_COMPLETED;
    }
    
    
    private void checkEndTurn()
    {
        System.out.print("\n\n=== CONTROLLO DI FINE TURNO DI UN PERSONAGGIO -> ");

        
        if (this.alliesList.isEmpty() || this.enemiesList.isEmpty())
        {
        	// Se uno dei due team non c'e' piu passiamo i controlli a chech end level
        	this.currentLevelPhase = LevelPhase.CHECK_END_LEVEL;
        	return;
        } 
        else if(this.currentTurnOrder.isEmpty())
        {
        	// Se non c'e' piu nessuno a dover giocare dopo di noi inizia un nuovo tutno
        	this.currentLevelPhase = LevelPhase.BATTLE_PHASE;
        	this.currentTurnState  = RoundState.INITIALIZING_TURN;
        }
        else 
        {
            // Nemici e alleati ancora in vita e ci sono ancora personaggi che devo giocare si continua 
            System.out.print("Tutto bene si continua con il prossimo personaggio\n");
            this.startNextTurn();
        }
    }


    
    private void checkEndLevel()
    {
        System.out.println("\n=== CONTROLLO DI FINE ROUND ===");
        
        if (this.alliesList.isEmpty()) 
        {
            this.levelFailed = true;
            System.out.println("Tutti i tuoi personaggi sono morti. Game Over.\n");
            //this.levelMap.updateBannerMessage("Tutti i tuoi personaggi sono morti. Game Over.", true);
            this.currentLevelPhase = LevelPhase.DONE;
            this.levelMap.closeWindow();
            return;
        } 
        else if (this.enemiesList.isEmpty()) 
        {
            this.levelCompleted = true;
            System.out.println("Hai sconfitto tutti i nemici. Livello completato!\n");
            //this.levelMap.updateBannerMessage("Hai sconfitto tutti i nemici. Livello Completato", true);
            this.currentLevelPhase = LevelPhase.DONE;
            this.levelMap.closeWindow();
            return;
        }

        // Nemici e alleati ancora in vita si continua 
        System.out.println("Livello ancora in corso, inizia un nuovo round...\n");
        currentLevelPhase  = LevelPhase.BATTLE_PHASE;
        currentTurnState   = RoundState.INITIALIZING_TURN;
    }

    private PriorityQueue<Character> getTurnOrder(List<Character> allies, List<Character> enemies) 
    {
        PriorityQueue<Character> queue = new PriorityQueue<>((a, b) -> Integer.compare(b.getSpeed(), a.getSpeed()));
        queue.addAll(allies.stream().filter(Character::isAlive).toList());
        queue.addAll(enemies.stream().filter(Character::isAlive).toList());
        return queue;
    }

    private boolean hasRemainingAttackers(PriorityQueue<Character> attackTurnOrder) {
        return attackTurnOrder.stream().anyMatch(Character::isAlive);
    }

    // Getters
    public List<Character> getEnemies() { return this.enemiesList; }
    public List<Character> getAllies() { return this.alliesList; }
    public boolean isCompleted() { return this.levelCompleted; }
    public boolean isFailed() { return this.levelFailed; }
    
    // Metodo per debug dello stato
    public void printCurrentState() {
        System.out.println("Fase: " + currentLevelPhase + ", Stato Battaglia: " + currentTurnState);
        if (currentAttacker != null) {
            System.out.println("Attaccante corrente: " + currentAttacker.getClass().getSimpleName());
        }
    }
    
    public LevelMap getLevelMap() {
        return this.levelMap;
    }

}