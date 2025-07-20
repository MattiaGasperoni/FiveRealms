package model.gameStatus;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;


import model.characters.Character;
import model.gameStatus.saveSystem.GameStateManager;
import view.*;
import view.map.LevelMap;
import controller.*;

public class Level 
{
    private enum LevelPhase 
    {
        BATTLE, UPDATE_MAP, CHECK_END, DONE 
    }
    
    private LevelPhase currentPhase;              // Status corrente del livello
    private List<Character> enemiesList;          // Lista dei nemici del livello
    private List<Character> alliesList;           // Lista dei personaggi con cui giochiamo il livello
    private boolean levelCompleted;               // Flag che indica se il livello è completato
    private boolean levelFailed;                  // Flag che indica se il livello è fallito
    private boolean combatPhaseInitiated;		  // Flag che indica se la fase di combattimento è già iniziata
        
    private final LevelMap levelMap;                    // Mappa del livello
    private final BattlePhaseView movementPhaseManager; // Gestisce l'interazione con la mappa, i personaggi e l'utente nella fase BATTLE
    
    

    public Level(LevelMap map, GameController controller) 
    {
        // Inizializzazione Oggetti Grafici
        this.levelMap = map;
        this.movementPhaseManager = new BattlePhaseView(this.levelMap, controller);

        // Inizializzazione delle liste di personaggi
        this.enemiesList      = this.levelMap.getEnemiesList();
        this.alliesList       = this.levelMap.getAlliesList();
        
        this.levelCompleted = false;
        this.levelFailed = false;
        this.combatPhaseInitiated = false;
    }

    
    public void play() throws IOException 
    {
	    // Faccio comparire la mappa del livello
        this.levelMap.start();
       
    	System.out.print(" Start level ->");
    	
    	// Spawn dei personaggi
    	this.levelMap.spawnCharacter(this.enemiesList);

    	this.levelMap.spawnCharacter(this.alliesList);
    	
    	// Partiamo dalla fase di movimento
        this.currentPhase = LevelPhase.BATTLE;  

    }

    // Metodo che viene invocato dal Timer
    public void update() 
    {
        switch (this.currentPhase) 
        {
            case BATTLE:
            	if(!combatPhaseInitiated)
            		handleMovementAttackPhase();
                break;
            case UPDATE_MAP:
                handleUpdateMap();
            case CHECK_END:
                handleEndCheck();
                break;
            case DONE:
                break;
            default:
                throw new IllegalStateException("Unknown phase: " + currentPhase);
        }
    }
    
    



	private void handleMovementAttackPhase() 
    {   
		// Bisogna sbiscenare questo metodo privato in piu metodi, finche non selezioni un blocco dove spostarti ti continua
		// a rimandare in quella fase del gioco 
    	// Ottieni l'ordine del round
    	PriorityQueue<Character> characterTurnOrder = this.getTurnOrder(this.alliesList, this.enemiesList);
    	
    	if (this.hasRemainingAttackers(characterTurnOrder))
    	{
    		this.combatPhaseInitiated = true;
        	// Estrai il personaggio più veloce e lo rimuove dalla coda essedo il primo che attacca
            Character attacker = characterTurnOrder.poll(); 
            
            // Se è morto, salta il turno e passa al prossimo
            if (!attacker.isAlive()) 
            {
            	characterTurnOrder.poll();
            }

            // Fase di spostamento del personaggio nella mappa e scelta del nemico da attaccare
            // se attacker e' un alleato chiede all'utente dove vuole spostarsi e chi vuole attaccare, 
            // se e' un nemico l'A.I. decide dove spostarlo e chi attaccare
            if(attacker.isAllied()) 
            {
            	System.out.println("Fase di movimento");
            	this.movementPhaseManager.movementPhase(attacker, alliesList, enemiesList, () -> 
            	{
            	    this.movementPhaseManager.chooseTarget(this.enemiesList, attacker);
            	});

            }
            else 
            {
            	System.out.print("Bot non implementato");
            }           
                             
            // Ricostruisci la coda dell'ordine di attacco senza i personaggi morti
            // Combat manager non flagga i personaggi morti, quindi devo controllare io dalle liste!!!!
            characterTurnOrder = new PriorityQueue<>(characterTurnOrder.comparator());
            characterTurnOrder.addAll(
                Stream.concat(alliesList.stream(), enemiesList.stream())
                      .filter(Character::isAlive)
                      .toList()
            );   		
    	}
        else 
        {
            //Finito il round, passiamo alla fase di aggiornamento della mappa
            currentPhase = LevelPhase.UPDATE_MAP;
        }
    }
	
    private void handleUpdateMap() 
    {
    	this.levelMap.updateMap();
    	// Passiamo alla fase di controllo 
    	currentPhase = LevelPhase.CHECK_END;
	}
    
    

    private void handleEndCheck()
    {
        // Controlla se il livello è stato completato o se è fallito
        if (this.alliesList.isEmpty()) 
        {
            this.levelFailed = true;
            System.out.println("Tutti i tuoi personaggi sono morti. Game Over.");
            return;
        } 
        else if (this.enemiesList.isEmpty()) 
        {
            this.levelCompleted = true;
            System.out.println("Hai sconfitto tutti i nemici. La porta per il prossimo livello è aperta.");
            currentPhase = LevelPhase.DONE;
            return;

        }

        // Se il livello non è completato e non è fallito, torniamo alla fase di combattimento
        currentPhase = LevelPhase.BATTLE;
    }

    
    
    // Metodo per verificare l'ordine di attacco dei personaggi in un turno
    private PriorityQueue<Character> getTurnOrder(List<Character> allies, List<Character> enemies) 
    {
        PriorityQueue<Character> queue = new PriorityQueue<>((a, b) -> Integer.compare(b.getSpeed(), a.getSpeed()));
    
        queue.addAll(allies);
        queue.addAll(enemies);
    
        return queue;
    }
    

    public List<Character> getEnemies() {
        return this.enemiesList;
    }

    public List<Character> getAllies() {
        return this.alliesList;
    }
    

    // Verifica se ci sono attaccanti rimanenti
    private boolean hasRemainingAttackers(PriorityQueue<Character> attackTurnOrder) {
        return !attackTurnOrder.isEmpty();
    }
    
    public boolean isCompleted() {
        return this.levelCompleted;
    }

    public boolean isFailed() {
        return this.levelFailed;
    }


}