package model.gameStatus;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;


import model.characters.Character;
import view.*;
import view.map.LevelMap;
import controller.*;

public class Level 
{
    private List<Character> enemiesList;          // Lista dei nemici del livello
    private List<Character> alliesList;           // Lista dei personaggi con cui giochiamo il livello

    private boolean levelCompleted;               // Flag che indica se il livello e' completato
        
    private final LevelMap levelMap;                            // Mappa del livello
    private final GraphicMovePhaseManager movementPhaseManager; // Oggetto che gestisce la fase di movimento dei personaggi

    public Level(LevelMap map) 
    {
        // Inizializzazione Oggetti Grafici
        this.levelMap = map;
        this.movementPhaseManager = new GraphicMovePhaseManager(this.levelMap);

        // Inizializzazione delle liste di personaggi
        this.enemiesList      = this.levelMap.getEnemiesList();
        this.alliesList       = this.levelMap.getAlliesList();
        
        this.levelCompleted   = false;     
    }

    
    //Metodo Pubblico per giocare il livello, restituisce true se il livello è stato completato, false altrimenti
    public boolean playLevel(GameStateManager gameStateManager, int levelNumber) throws IOException 
    {
        // Faccio comparire la mappa del livello
        this.levelMap.start();
       
    	System.out.print(" Start level ->");

        // Spawn dei personaggi
        //this.levelMap.spawnCharacters();

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
        return this.levelCompleted;
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
    
    // Metodi privati per migliorare la leggibilita del codice
    
    private boolean areAllAlliesDefeated() {
        return this.alliesList.isEmpty();
    }
    
    private boolean hasRemainingAttackers(PriorityQueue<Character> turnOrder) {
        return !turnOrder.isEmpty();
    }


}