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
        BATTLE, UPDATE_MAP, CHECK_END, DONE 
    }
    
    // Stati della fase BATTLE
    private enum BattleState 
    {
        INITIALIZING,           // Preparando la battaglia
        WAITING_FOR_MOVEMENT,   // Aspettando che l'utente scelga dove muoversi
        WAITING_FOR_TARGET,     // Aspettando che l'utente scelga il bersaglio
        PROCESSING_TURN,        // Elaborando il turno (AI o dopo input utente)
        TURN_COMPLETED          // Turno completato, pronto per il prossimo
    }
    
    private LevelPhase currentLevelPhase;
    private BattleState currentBattleState;
    
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

        
        this.currentLevelPhase  = LevelPhase.BATTLE;
        this.currentBattleState = BattleState.INITIALIZING;
    }

    // Metodo che aggiorna lo stato del livello
    public void update() 
    {
        switch (this.currentLevelPhase) 
        {
            case BATTLE:
            	
                this.handleBattlePhase();
                break;
            case UPDATE_MAP:
            	System.out.println("4.");
            	this.handleUpdateMap();
                break;
            case CHECK_END:
            	System.out.println("5.");
            	this.handleEndCheck();
                break;
            case DONE:
                // Livello completato, non fare nulla
            	System.out.println("6.");
            	this.levelMap.closeWindow();
                break;
            default:
                throw new IllegalStateException("Fase Sconosciuta: " + this.currentLevelPhase);
        }
    }
    
    // Metodo che aggiorna lo stato delle fase BATTLE
    private void handleBattlePhase() 
    {
    	
    	
    	if (this.currentBattleState == BattleState.WAITING_FOR_MOVEMENT ||
    	        this.currentBattleState == BattleState.WAITING_FOR_TARGET) 
    	{
    		return; // Non fare nulla, stai aspettando l'utente
    	}
    	System.out.println("1.");
        switch (this.currentBattleState) 
        {
            case INITIALIZING: 
            	//System.out.println("INITIALIZING: Nemici:"+this.enemiesList.size());
            	//System.out.println("Alleati:"+this.alliesList.size());
            	System.out.println("2.");
            	//Entriamo qua solo quando inizia un nuovo round
            	this.initializeBattleRound(); 
            	break;

            
            case TURN_COMPLETED:
            	//System.out.println("TURN_COMPLETED, Nemici:"+this.enemiesList.size());
            	//System.out.println("Alleati:"+this.alliesList.size());
            	System.out.println("3.");
            	this.startNextTurn();
            	break;
            
            default: throw new IllegalStateException("Fase Sconosciuta: " + this.currentBattleState);
        }
    }
    
    // Questo metodo viene chiamato quando inizia un nuovo round di movimento/attacco
    private void initializeBattleRound() 
    {
        System.out.println("\n\n=== FASE DI INIZIALIZZAZIONE DEL ROUND ===");
        
        //Otteniamo l'ordine di movimento/attacco del round 
        this.currentTurnOrder = this.getTurnOrder(this.alliesList, this.enemiesList);
        
        // Stampa a console l'ordine di movimento e attacco dei personaggi
        /*System.out.println("Ordine turni: " + this.currentTurnOrder.stream()
            .map(c -> c.getClass().getSimpleName() + "(speed:" + c.getSpeed() + ")")
            .collect(Collectors.joining(", ")));*/
         
        
        
        //Controllo inutile non dovrebbe fai esserci una coda dei turni vuota a inizio round
        if (this.hasRemainingAttackers(this.currentTurnOrder)) 
        {
        	this.startNextTurn();
        } 
        else 
        {
            System.out.println("Nessun attaccante rimasto, fine round");
            currentLevelPhase = LevelPhase.UPDATE_MAP;
        }
    }
    
    
    
    
    // Inizia il turno di un personaggio 
    private void startNextTurn() 
    {
    	// Controlla se tutti gli alleati sono morti o se tutti i nemici sono sconfitti
    	this.endTurnCheck();
    	
    	SwingUtilities.invokeLater(() -> {this.levelMap.updateMap();});
    	
        if (this.currentTurnOrder.isEmpty()) 
        {
            System.out.println("Round completato!");
            this.currentLevelPhase = LevelPhase.UPDATE_MAP;
            return;
        }
        
        // Trova il prossimo personaggio vivo
        Character nextAttacker = null;
        
        while (!currentTurnOrder.isEmpty()) 
        {
            Character candidate = currentTurnOrder.poll();
            
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
            currentLevelPhase = LevelPhase.UPDATE_MAP;
            return;
        }
        
        // Se ci sono personaggi vivi, impostiamo il nuovo attaccante e iniziamo il turno
        this.currentAttacker = nextAttacker;
        System.out.println("\n=== TURNO DI: " + currentAttacker.getClass().getSimpleName() + " ===");
        
        if (this.currentAttacker.isAllied())
        {
            startPlayerTurn();
        } 
        else
        {
            startAITurn();
        }
    }
    
    // Inizia il turno del giocatore alleato
    private void startPlayerTurn() 
    {
    	

        currentBattleState = BattleState.WAITING_FOR_MOVEMENT;
        System.out.print("In attesa del movimento di " + currentAttacker.getClass().getSimpleName()+" -> ");
        

    	SwingUtilities.invokeLater(() -> {this.levelMap.updateBannerMessage("In attesa del movimento di: "+ currentAttacker.getClass().getSimpleName());});
        
        // Configura il movimento con callback
        this.movementPhaseManager.movementPhase(currentAttacker, () -> 
        {


        	SwingUtilities.invokeLater(() -> {this.levelMap.updateBannerMessage("Movimento completato, "+currentAttacker.getClass().getSimpleName()+" scegli un  bersaglio");});
            
        	
            this.onMovementCompleted();
        });
    }
    
    // Questo metodo viene chiamato quando il movimento è completato
    private void onMovementCompleted() 
    {
        this.currentBattleState = BattleState.WAITING_FOR_TARGET;
        
        this.movementPhaseManager.chooseTarget(this.enemiesList, this.currentAttacker, () -> 
        {
            this.currentBattleState = BattleState.TURN_COMPLETED;
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
	        currentBattleState = BattleState.TURN_COMPLETED;
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
        
        currentBattleState = BattleState.TURN_COMPLETED;
    }
    
    private void handleUpdateMap() 
    {   
    	//Ridondante 
		System.out.println("=== AGGIORNAMENTO MAPPA ===");
		

    	SwingUtilities.invokeLater(() -> {this.levelMap.updateMap();});
        
        
        this.currentLevelPhase = LevelPhase.CHECK_END;
    }
    
    private void endTurnCheck()
    {
        System.out.print("\n\n=== CONTROLLO STATUS DEL TURNO  -> ");
        
        if (this.alliesList.isEmpty()) 
        {
            this.levelFailed = true;
            System.out.println("Tutti i tuoi personaggi sono morti. Game Over.\n");
            currentLevelPhase = LevelPhase.DONE;
            this.levelMap.closeWindow();
        } 
        else if (this.enemiesList.isEmpty()) 
        {
            this.levelCompleted = true;
            System.out.println("Hai sconfitto tutti i nemici. Livello completato!\n");
            this.currentLevelPhase = LevelPhase.DONE;
            this.levelMap.closeWindow();
        }

        // Nemici e alleati ancora in vita si continua 
        System.out.print("Tutto bene si continua\n");
        currentLevelPhase  = LevelPhase.BATTLE;
        currentBattleState = BattleState.INITIALIZING;
    }

    
    private void handleEndCheck()
    {
        System.out.println("\n=== CONTROLLO STATUS LIVELLO ===");
        
        if (this.alliesList.isEmpty()) 
        {
            this.levelFailed = true;
            System.out.println("Tutti i tuoi personaggi sono morti. Game Over.\n");
            currentLevelPhase = LevelPhase.DONE;
            return;
        } 
        else if (this.enemiesList.isEmpty()) 
        {
            this.levelCompleted = true;
            System.out.println("Hai sconfitto tutti i nemici. Livello completato!\n");
            this.currentLevelPhase = LevelPhase.DONE;
            return;
        }

        // Nemici e alleati ancora in vita si continua 
        System.out.println("Livello ancora in corso, inizia un nuovo round...\n");
        currentLevelPhase  = LevelPhase.BATTLE;
        currentBattleState = BattleState.INITIALIZING;
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
        System.out.println("Fase: " + currentLevelPhase + ", Stato Battaglia: " + currentBattleState);
        if (currentAttacker != null) {
            System.out.println("Attaccante corrente: " + currentAttacker.getClass().getSimpleName());
        }
    }
}