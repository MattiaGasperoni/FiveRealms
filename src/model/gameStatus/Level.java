package model.gameStatus;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import model.characters.Character;
import model.gameStatus.saveSystem.GameStateManager;
import view.*;
import view.map.LevelMap;
import controller.*;

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

    public Level(LevelMap map, GameController controller) 
    {
        this.levelMap = map;
        this.movementPhaseManager = new BattlePhaseView(this.levelMap, controller);

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
            	this.handleUpdateMap();
                break;
            case CHECK_END:
            	this.handleEndCheck();
                break;
            case DONE:
                // Livello completato, non fare nulla
                break;
            default:
                throw new IllegalStateException("Fase Sconosciuta: " + this.currentLevelPhase);
        }
    }
    
    // Metodo che aggiorna lo stato delle fase BATTLE
    private void handleBattlePhase() 
    {
        switch (this.currentBattleState) 
        {
            case INITIALIZING:
                this.initializeBattleRound();
                break;
            case WAITING_FOR_MOVEMENT:
            case WAITING_FOR_TARGET:
                // Non fare nulla, aspetta input dell'utente
                // Gli ActionListener gestiranno il resto
                break;
            case TURN_COMPLETED:
                this.moveToNextTurn();
                break;
            default:
                throw new IllegalStateException("Fase Sconosciuta: " + this.currentBattleState);
        }
    }
    
    // Inizializza un nuovo round
    private void initializeBattleRound() 
    {
        System.out.println("\n=== INIZIALIZZANDO UN NUOVO ROUND ===");
        
        this.currentTurnOrder = getTurnOrder(this.alliesList, this.enemiesList);
        
        System.out.println("Ordine turni: " + currentTurnOrder.stream()
            .map(c -> c.getClass().getSimpleName() + "(speed:" + c.getSpeed() + ")")
            .collect(Collectors.joining(", ")));
            
        if (this.hasRemainingAttackers(currentTurnOrder)) 
        {
        	this.moveToNextTurn();
        } 
        else 
        {
            System.out.println("Nessun attaccante rimasto, fine round");
            currentLevelPhase = LevelPhase.UPDATE_MAP;
        }
    }
    
    
    
    
    // Il turno del personaggio corrente è finito, passiamo al prossimo
    private void moveToNextTurn() 
    {
    	// Controlla se questo era l'ultimo personaggio che doveva attaccare, se sì, il round è finito
        if (currentTurnOrder.isEmpty()) 
        {
            System.out.println("Round completato!");
            currentLevelPhase = LevelPhase.UPDATE_MAP;
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
        
        if (currentAttacker.isAllied())
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
        System.out.println("In attesa del movimento di " + currentAttacker.getClass().getSimpleName());
        // Configura il movimento con callback
        this.movementPhaseManager.movementPhase(currentAttacker, alliesList, enemiesList, () -> 
        {
            System.out.println("Movimento completato, passando alla scelta bersaglio");
            this.onMovementCompleted();
        });
    }
    
    // Questo metodo viene chiamato quando il movimento è completato
    private void onMovementCompleted() 
    {
        currentBattleState = BattleState.WAITING_FOR_TARGET;
        
        this.movementPhaseManager.chooseTarget(enemiesList, currentAttacker, () -> {
            System.out.println("Attacco completato!");
            this.onAttackCompleted();
        });
    }
    
    // Questo metodo viene chiamato quando l'attacco è completato
    private void onAttackCompleted() 
    {
        System.out.println("Turno giocatore completato");
        currentBattleState = BattleState.TURN_COMPLETED;
        
        // Rimuovi personaggi morti dalle liste
        this.cleanupDeadCharacters();
    }
    
    // Metod che gestisce il turno dell'AI
    private void startAITurn()
    {
        System.out.println("Turno AI - non ancora implementato");
        currentBattleState = BattleState.TURN_COMPLETED;
    }
    
    // Rimuove personaggi morti
    private void cleanupDeadCharacters() 
    {
        int deadAllies  = (int) alliesList.stream().filter(c -> !c.isAlive()).count();
        int deadEnemies = (int) enemiesList.stream().filter(c -> !c.isAlive()).count();
        
        alliesList.removeIf(c -> !c.isAlive());
        enemiesList.removeIf(c -> !c.isAlive());
        
        if (deadAllies > 0 || deadEnemies > 0) {
            System.out.println("Rimossi " + deadAllies + " alleati e " + deadEnemies + " nemici morti");
        }
    }
    
    private void handleUpdateMap() 
    {
        System.out.println("=== AGGIORNAMENTO MAPPA ===");
        this.levelMap.updateMap();
        currentLevelPhase = LevelPhase.CHECK_END;
    }

    private void handleEndCheck()
    {
        System.out.println("=== CONTROLLO FINE LIVELLO ===");
        
        if (this.alliesList.isEmpty()) 
        {
            this.levelFailed = true;
            System.out.println("Tutti i tuoi personaggi sono morti. Game Over.");
            currentLevelPhase = LevelPhase.DONE;
            return;
        } 
        else if (this.enemiesList.isEmpty()) 
        {
            this.levelCompleted = true;
            System.out.println("Hai sconfitto tutti i nemici. Livello completato!");
            currentLevelPhase = LevelPhase.DONE;
            return;
        }

        // Continua la battaglia
        System.out.println("Livello in corso, nuovo round...");
        currentLevelPhase = LevelPhase.BATTLE;
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
    
    // ✅ Metodo per debug dello stato
    public void printCurrentState() {
        System.out.println("Fase: " + currentLevelPhase + ", Stato Battaglia: " + currentBattleState);
        if (currentAttacker != null) {
            System.out.println("Attaccante corrente: " + currentAttacker.getClass().getSimpleName());
        }
    }
}