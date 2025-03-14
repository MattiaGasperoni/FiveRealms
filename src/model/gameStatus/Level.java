package model.gameStatus;

import java.util.*;
import java.util.stream.Stream;


import controller.StaticCombatManager;
import model.characters.Character;
import view.LevelMap;

public class Level 
{
    private final LevelMap LevelMap;              // Mappa del livello
    private List<Character> enemiesList;  // Lista dei nemici del livello
    private List<Character> alliesList;   // Lista dei personaggi giocabili
    private boolean doorIsOpen;                   // Flag che indica se la porta per il prossimo livello è aperta

    private int initialAlliesCount;              // Numero di alleati a inizio livello

    public Level(LevelMap map, List<Character> enemies, List<Character> allies) 
    {
        this.LevelMap     = map;
        this.enemiesList  = enemies;
        this.alliesList   = allies;
        this.doorIsOpen   = false;
        this.initialAlliesCount = allies.size();
    }

    public List<Character> getEnemies() {
        return this.enemiesList;
    }

    public List<Character> getAllies() {
        return this.alliesList;
    }

    public boolean isDoorOpen() {
        return this.doorIsOpen;
    }

    public int getAlliesDeathCount() {
        return this.initialAlliesCount - this.alliesList.size();
    }

    //Metodo Pubblico per giocare il livello, restituisce true se il livello è stato completato, false altrimenti
    public boolean playLevel(EnemyManager enemyManager) 
    {
        // Spawn dei nemici
        enemyManager.spawnEnemies(this.enemiesList);

        // Metodo per lo spawn degli alleati
        this.spawnAllies(this.alliesList);

        // Il livello continua finche non apri la porta per il prossimo livello(sconfiggi tutti i nemici) o muoiono tutti e tre i tuoi personaggi 
        while (!this.doorIsOpen || getAlliesDeathCount() < 3) 
        {
            // INIZIO FASE DI ATTACCO

            // Otteniamo l'ordine di attacco dei personaggi
            PriorityQueue<Character> attackTurnOrder = this.getTurnOrder(this.alliesList, this.enemiesList);

            // Fase di attacco, continua finche la coda del turno non è vuota
            while (!attackTurnOrder.isEmpty()) 
            {
                // Estrai il personaggio più veloce e lo rimuove dalla coda
                Character attacker = attackTurnOrder.poll(); 
                
                // Se è morto, salta il turno e passa al prossimo
                if (!attacker.isAlive()) continue; 
                            
                // Il personaggio attacca METODO TEMPORANEO DA SOSTITUIRE FINCHE NON SVILUPPIAMO LA SCELTA DEL TARGET
                StaticCombatManager.fight(attacker, this.enemiesList.get(0), this.alliesList, this.enemiesList);

                // Ricostruisci la coda senza i personaggi morti
                attackTurnOrder = new PriorityQueue<>(attackTurnOrder.comparator());
                attackTurnOrder.addAll(
                    Stream.concat(alliesList.stream(), enemiesList.stream())
                          .filter(Character::isAlive)
                          .toList()
                );
            }
                             
            // FINE FASE DI ATTACCO

            // Controlla se tutti gli alleati sono morti
            if (getAlliesDeathCount() == 3)
            {
                System.out.println("Tutti i tuoi personaggi sono morti. Game Over.");
                return false;
            }

            // Controlla se tutti i nemici sono stati sconfitti
            if (enemyManager.allEnemiesDefeated()) 
            {
                // Attiva la porta per il prossimo livello
                this.doorIsOpen = true;
                System.out.println("Hai sconfitto tutti i nemici. La porta per il prossimo livello e' aperta.");
            }
        }
        return true;
    }

    private void spawnAllies(List<Character> alliesList) 
    {
        // Metodo per lo spawn degli alleati
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