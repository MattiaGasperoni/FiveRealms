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
        START, MOVEMENT, ATTACK, CHECK_END, DONE
    }
    
    private LevelPhase currentPhase;

	
    private List<Character> enemiesList;          // Lista dei nemici del livello
    private List<Character> alliesList;           // Lista dei personaggi con cui giochiamo il livello

        
    private final LevelMap levelMap;                            // Mappa del livello
    private final GraphicMovePhaseManager movementPhaseManager; // Oggetto che gestisce la fase di movimento dei personaggi
    
    private boolean levelCompleted;               // Flag che indica se il livello e' completato
    private boolean levelFailed;                  // Flag che indica se il livello e' fallito

    public Level(LevelMap map, GameController controller) 
    {
        // Inizializzazione Oggetti Grafici
        this.levelMap = map;
        this.movementPhaseManager = new GraphicMovePhaseManager(this.levelMap, controller);

        // Inizializzazione delle liste di personaggi
        this.enemiesList      = this.levelMap.getEnemiesList();
        this.alliesList       = this.levelMap.getAlliesList();
        
        this.levelCompleted = false;
        this.levelFailed = false;
    }

    
   /* //Metodo Pubblico per giocare il livello, restituisce true se il livello è stato completato, false altrimenti
    public boolean play(GameStateManager gameStateManager, int levelNumber) throws IOException 
    {
        // Faccio comparire la mappa del livello
        this.levelMap.start();
       
    	System.out.print(" Start level ->");
       
    	// Per  provare scommentare
    	 
        // Il livello continua finche non muoiono tutti e tre i tuoi personaggi 
    	while (!areAllAlliesDefeated())
        {
            // INIZIO FASE DI ATTACCO

            // Otteniamo l'ordine di attacco dei personaggi per questo turno
            PriorityQueue<Character> attackTurnOrder = this.getTurnOrder(this.alliesList, this.enemiesList);

            // La fase di attacco, continua finche la coda del turno non è vuota
            while (hasRemainingAttackers(attackTurnOrder)) 
            {
                // INIZIO TURNO

                // Estrai il personaggio più veloce e lo rimuove dalla coda essedo il primo che attacca
                Character attacker = attackTurnOrder.poll(); 
                
                // Se è morto, salta il turno e passa al prossimo
                if (!attacker.isAlive()) continue; 

                // Fase di spostamento del personaggio nella mappa e scelta del nemico da attaccare
                // se attacker e' un alleato chiede all'utente dove vuole spostarsi e chi vuole attaccare, 
                // se e' un nemico l'A.I. decide dove spostarlo e chi attaccare
                if(attacker.isAllied()) 
                {
                	this.movementPhaseManager.movementPhase(attacker, alliesList, enemiesList);  
                	
                	this.movementPhaseManager.chooseTarget(this.enemiesList, target -> 
	                {
	                	// Il personaggio attacca il target
	                    StaticCombatManager.fight(attacker, target, this.alliesList, this.enemiesList);
	                });
                }
                else {
                	//..
                }           
                                 
                // Ricostruisci la coda dell'ordine di attacco senza i personaggi morti
                // Combat manager non flagga i personaggi morti, quindi devo controllare io dalle liste!!!!
                attackTurnOrder = new PriorityQueue<>(attackTurnOrder.comparator());
                attackTurnOrder.addAll(
                    Stream.concat(alliesList.stream(), enemiesList.stream())
                          .filter(Character::isAlive)
                          .toList()
                );
                
                // FINE FASE DI ATTACCO
                System.out.print(" End attack phase ->");

                // Controlla se tutti gli alleati sono morti
                if (this.alliesList.isEmpty())
                {
                    // Salva lo stato della partita
                    gameStateManager.saveStatus(this.alliesList, this.enemiesList, levelNumber);

                    System.out.println("Tutti i tuoi personaggi sono morti. Game Over.");
                    return this.levelCompleted;
                }

                // Controlla se tutti i nemici sono stati sconfitti
                if (this.enemiesList.isEmpty()) 
                {
                    // Salva lo stato della partita
                    gameStateManager.saveStatus(this.alliesList, this.enemiesList, levelNumber);

                    // Attiva la porta per il prossimo livello
                    this.levelCompleted = true;
                    System.out.println("Hai sconfitto tutti i nemici. La porta per il prossimo livello e' aperta.");
                    //MainMenu.nextLevelDoorMenu();
                    return this.levelCompleted;
                }
                
                // FINE TURNO
        
            }
                             

        }
        
    	System.out.println(" End Level");
    	this.levelMap.closeWindow();
        return true;
    }*/

    
    public void start(int index) throws IOException 
    {
        // inizializzazioni e setup della mappa, dei nemici, ecc.
    	
    	//stampa di debug
    	System.out.println("Allies:");
	    alliesList.forEach(ally -> System.out.println(ally.getClass().getSimpleName()));

	    System.out.println("Enemies:");
	    enemiesList.forEach(enemy -> System.out.println(enemy.getClass().getSimpleName()));
       
	    // Faccio comparire la mappa del livello
        this.levelMap.start();
       
    	System.out.print(" Start level ->");
    	
    	// Spawn dei personaggi
    	this.levelMap.spawnCharacter(this.enemiesList);
    	this.levelMap.spawnCharacter(this.alliesList);
    	
    	// Partiamo dalla fase di movimento
        this.currentPhase = LevelPhase.MOVEMENT;  

    }

    // Metodo che viene invocato dal Timer
    public void update() {
        switch (currentPhase) {
            case MOVEMENT:
                handleMovementPhase();
                break;
            case ATTACK:
                handleAttackPhase();
                break;
            case CHECK_END:
                handleEndCheck();
                break;
            case DONE:
                break;
            default:
                throw new IllegalStateException("Unknown phase: " + currentPhase);
        }
    }
    
    private void handleMovementPhase() 
    {
        // Gestisce la fase di movimento per gli alleati
        if (hasRemainingAllies()) 
        {
            for (Character ally : alliesList) 
            {
                if (ally.isAlive()) 
                {
                    this.movementPhaseManager.movementPhase(ally, alliesList, enemiesList);
                }
            }
            currentPhase = LevelPhase.ATTACK;  // Passa alla fase di attacco
        }
    }

    private void handleAttackPhase() 
    {
        // Ottieni l'ordine di attacco
    	PriorityQueue<Character> attackTurnOrder = this.getTurnOrder(alliesList, enemiesList);

        // La fase di attacco continua finché c'è almeno un attaccante
        if (hasRemainingAttackers(attackTurnOrder)) 
        {
            // Estrai il personaggio più veloce
            Character attacker = attackTurnOrder.poll();

            if (attacker.isAllied()) 
            {
                // Fase di attacco per gli alleati
                this.movementPhaseManager.chooseTarget(enemiesList, target -> 
                {
                	attacker.fight(target, alliesList, enemiesList);
                });
            }
            
        }
        else 
        {
            // Dopo aver eseguito tutti gli attacchi, passiamo alla fase di controllo
            currentPhase = LevelPhase.CHECK_END;
        }
    }

    private void handleEndCheck()
    {
        // Controlla se il livello è stato completato o se è fallito
        if (this.alliesList.isEmpty()) 
        {
            this.levelFailed = true;
            System.out.println("Tutti i tuoi personaggi sono morti. Game Over.");
        } 
        else if (this.enemiesList.isEmpty()) 
        {
            this.levelCompleted = true;
            System.out.println("Hai sconfitto tutti i nemici. La porta per il prossimo livello è aperta.");
        }

        // Passiamo alla fase finale
        currentPhase = LevelPhase.DONE;
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
    

    // Verifica se ci sono ancora alleati da muovere
    private boolean hasRemainingAllies() {
        return alliesList.stream().anyMatch(Character::isAlive);
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