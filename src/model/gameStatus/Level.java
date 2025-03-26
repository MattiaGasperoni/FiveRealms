package model.gameStatus;

import java.util.*;
import java.util.stream.Stream;


import model.characters.Character;
import view.*;
import controller.*;

public class Level 
{
    private final LevelMap LevelMap;              // Mappa del livello
    private List<Character> enemiesList;          // Lista dei nemici del livello
    private List<Character> alliesList;           // Lista dei personaggi giocabili

    private boolean isDoorOpen;                   // Flag che indica se la porta per il prossimo livello è aperta


    public Level(LevelMap map, List<Character> enemies, List<Character> allies) 
    {
        this.LevelMap     = map;
        this.enemiesList  = enemies;
        this.alliesList   = allies;
        this.isDoorOpen   = false;       
    }

    public List<Character> getEnemies() {
        return this.enemiesList;
    }

    public List<Character> getAllies() {
        return this.alliesList;
    }
    
    public boolean playTutorial() 
    {
        System.out.println("Start Tutorial !!!");
		return true;
    }

    //Metodo Pubblico per giocare il livello, restituisce true se il livello è stato completato, false altrimenti
    public boolean playLevel(GameStateManager gameStateManager, int levelNumber) 
    {
        // Inizializzazione della mappa del livello e spawn dei personaggi
        GraphicsMenu.spawnCharacters(this.enemiesList, this.alliesList);

        // Il livello continua finche non muoiono tutti e tre i tuoi personaggi 
        while (!this.alliesList.isEmpty()) 
        {
            // INIZIO FASE DI ATTACCO

            // Otteniamo l'ordine di attacco dei personaggi per questo turno
            PriorityQueue<Character> attackTurnOrder = this.getTurnOrder(this.alliesList, this.enemiesList);

            // La fase di attacco, continua finche la coda del turno non è vuota
            while (!attackTurnOrder.isEmpty()) 
            {
                // Estrai il personaggio più veloce e lo rimuove dalla coda essedo il primo che attacca
                Character attacker = attackTurnOrder.poll(); 
                
                // Se è morto, salta il turno e passa al prossimo
                if (!attacker.isAlive()) continue; 

                // Fase di spostamento del personaggio, se attacker e' un alleato chiede all'utente dove vuole spostarsi, 
                // se e' un nemico l'A.I. decide dove spostarlo  CAMBIARE NOME METODO!!!!
                CharacterMovementMenu.movementFace(attacker, this.alliesList, this.enemiesList); 

                // Fase di scelta de target da attaccare, se attacker e' un alleato chiede all'utente chi vuole attaccare,
                // se e' un nemico l'A.I. decide chi attaccare
                CharacterMovementMenu.chooseTarget(this.alliesList, this.enemiesList);
                Character target = CharacterMovementMenu.getTarget();
                
                // Il personaggio attacca il target
                StaticCombatManager.fight(attacker, target, this.alliesList, this.enemiesList);

                // Ricostruisci la coda dell'ordine di attacco senza i personaggi morti
                // Combat manager non flagga i personaggi morti, quindi devo controllare io dalle liste!!!!
                attackTurnOrder = new PriorityQueue<>(attackTurnOrder.comparator());
                attackTurnOrder.addAll(
                    Stream.concat(alliesList.stream(), enemiesList.stream())
                          .filter(Character::isAlive)
                          .toList()
                );
            }
                             
            // FINE FASE DI ATTACCO

            // Salva lo stato della partita
            gameStateManager.saveStatus(this.alliesList, this.enemiesList, levelNumber);

            // Controlla se tutti gli alleati sono morti
            if (this.alliesList.isEmpty())
            {
                System.out.println("Tutti i tuoi personaggi sono morti. Game Over.");
                return false;
            }

            // Controlla se tutti i nemici sono stati sconfitti
            if (this.enemiesList.isEmpty()) 
            {
                // Attiva la porta per il prossimo livello
                this.isDoorOpen = true;
                System.out.println("Hai sconfitto tutti i nemici. La porta per il prossimo livello e' aperta.");
                GraphicsMenu.nextLevelDoorMenu();
            }
        }
        return true;
    }

    // Metodo per verificare l'ordine di attacco dei personaggi in un turno
    private PriorityQueue<Character> getTurnOrder(List<Character> allies, List<Character> enemies) 
    {
        PriorityQueue<Character> queue = new PriorityQueue<>((a, b) -> Integer.compare(b.getSpeed(), a.getSpeed()));
    
        queue.addAll(allies);
        queue.addAll(enemies);
    
        return queue;
    }
}