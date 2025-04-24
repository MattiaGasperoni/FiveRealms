package model.gameStatus;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import model.characters.*;
import model.characters.Character;
import model.point.Point;

import view.*;
import view.map.*;

import controller.*;

public class Game {
    private static final int TOTAL_LEVEL = 5; // Numero di livelli del gioco
    private static final int MAX_ALLIES_PER_ROUND = 3; // Numero di personaggi giocabili per round

    private List<Level> gameLevels; // Lista dei livelli del gioco
    private List<Character> availableAllies; // Lista di tutti i personaggi giocabili
    private List<Character> selectedAllies; // Lista dei personaggi con cui l'utente giocherà il livello

    private GameStateManager gameStateManager; // Oggetto che gestiste il caricamento di una partita
    private Controller controller;             
    private static final Random rand = new Random(); // ci inizializza i valori dei personaggi

    // Oggetti Grafici
    private MainMenu graphicsMenu;
    private TutorialMenu tutorialMenu;
    private CharacterSelectionMenu characterSelectionMenu; // Menu per la scelta dei personaggi

    public Game() 
    {
        this.gameLevels = new ArrayList<>();
        this.availableAllies = new ArrayList<>();
        this.selectedAllies = new ArrayList<>();
        this.gameStateManager = new GameStateManager();
        this.controller = new Controller(); 


        // Inizializzazione Oggetti Grafici
        this.graphicsMenu = new MainMenu();
        this.tutorialMenu = new TutorialMenu();
        this.characterSelectionMenu = new CharacterSelectionMenu();

        // Avvio il menù principale
        this.graphicsMenu.startMainMenu(this);
    }

    public void startNewGame() throws IOException {

        // Avvio il Tutorial Menu
        this.tutorialMenu.start(event -> {
            // Il flusso del codice rimane in attesa finche l'utente non sceglie se giocare
            // o saltare il tutorial
            if (this.tutorialMenu.isTutorialSelected()) {
                // Ha scelto di giocare il tutorial               
                if (this.startTutorial()) 
                {
                    System.out.println(" You completed the tutorial");
                    // Voglio un PopUp a schermo che dice "Tutorial completato, inizia il gioco"
                    this.startSelectionCharacterAndLevels();
                } 
                else {
                    System.out.println(" You failed the tutorial");
                    // Voglio un PopUp a schermo che dice "Tutorial fallito, e mi chiede se voglio
                    // riprovare o uscire"
                }
            } 
            else {
                System.out.println(" Tutorial skipped");
                this.startSelectionCharacterAndLevels();
            }
        });
    }

    private boolean startTutorial() {
        // Il tutorial prevede un gameplay statico, sempre uguale percio' non
        // permettiamo la scelta dei personaggi

        // Popolo la lista di nemici del tutorial
        List<Character> tutorialEnemies = new ArrayList<>();
        tutorialEnemies.add(new Barbarian());
        tutorialEnemies.add(new Archer());
        tutorialEnemies.add(new Knight());

        // Popolo la lista di personaggi con cui giocheremo il tutorial
        List<Character> tutorialAllies = new ArrayList<>();
        tutorialAllies.add(new Barbarian());
        tutorialAllies.add(new Archer());
        tutorialAllies.add(new Knight());
        
        for(Character ally : tutorialAllies) {
			ally.becomeHero();
		}

        // Creo e istanzio l'oggetto tutorial
        Tutorial tutorial = new Tutorial(new TutorialMap(tutorialEnemies, tutorialAllies), this.controller);

        // Gioco il Tutorial
        return tutorial.play();
    }

    private void startSelectionCharacterAndLevels() {
        // Inizializza la lista con tutti i personaggi giocabili
        this.createAllies();

        // Appare il menu per la selezione dei personaggi
        this.characterSelectionMenu.start(this.availableAllies, this.selectedAllies, event -> {
            // Inizializzo le liste dei nemici e dei livelli
            this.initializeGameLevels();

            // Gioca i livelli
            for (int i = 0; i < Game.TOTAL_LEVEL; i++) {
                // Gioca il livello, playLevel ritorna true se il livello è stato completato
                // sennò interrompe dal ciclo
                try {
                    if (!this.gameLevels.get(i).playLevel(this.gameStateManager, i)) {
                        System.out.println("Il livello non è stato completato, uscita dal ciclo.");
                        break;
                    }
                } catch (IOException e) {
                    System.err.println("Errore durante l'esecuzione del livello " + i + ": " + e.getMessage());
                    e.printStackTrace();
                    break;
                }

                // Sostituisci gli alleati morti con nuovi alleati
                this.checkAndReplaceDeadAllies();

                System.out.println("Passaggio al livello " + (i + 1));
            }
        });
    }

    private void createAllies() {
        // Popolo la lista di personaggi giocabili
        this.availableAllies.add(new Barbarian());
        this.availableAllies.add(new Archer());
        // ... Aggiungere altri personaggi
    }

    private void initializeGameLevels() {
        // Popolo le liste di nemici dei livelli principali
        List<Character> level1Enemies = new ArrayList<>();
        level1Enemies.add(new Barbarian());
        level1Enemies.add(new Archer());

        List<Character> level2Enemies = new ArrayList<>();
        level2Enemies.add(new Barbarian());
        level2Enemies.add(new Archer());

        List<Character> level3Enemies = new ArrayList<>();
        level3Enemies.add(new Barbarian());
        level3Enemies.add(new Archer());

        List<Character> level4Enemies = new ArrayList<>();
        level4Enemies.add(new Barbarian());
        level4Enemies.add(new Archer());

        List<Character> level5Enemies = new ArrayList<>();
        level5Enemies.add(new Barbarian());
        level5Enemies.add(new Archer());

        // Agggiungo tutti i livelli alla lista dei
        // PROBLEM : se qualche personaggio mi muore poi nei livelli prima poi la lista
        // dei selectedAllies viene aggiornata o tengono quella fornita originalmente
        // PROBLEM : LevelMap non funziona e quando chiamo il metodo
        // initializeGameLevels mi crea gia le istanze delle finestre
        // per capire in che livello siamo a leveMap gli passo un numeroche lo
        // rappresenta.
        
		// Inizializzo i livelli        
        this.gameLevels.add(new Level(new LevelMap(level1Enemies, this.selectedAllies, 1), this.controller));
        this.gameLevels.add(new Level(new LevelMap(level2Enemies, this.selectedAllies, 2), this.controller));
        this.gameLevels.add(new Level(new LevelMap(level3Enemies, this.selectedAllies, 3), this.controller));
        this.gameLevels.add(new Level(new LevelMap(level4Enemies, this.selectedAllies, 4), this.controller));
        this.gameLevels.add(new Level(new LevelMap(level5Enemies, this.selectedAllies, 5), this.controller));
    }

    // Metodo per sostituire gli alleati morti con nuovi personaggi scelti
    // dall'utente
    private void checkAndReplaceDeadAllies() {
        // Calcola quanti alleati sono morti
        int alliesToChange = MAX_ALLIES_PER_ROUND - this.selectedAllies.size();

        if (alliesToChange > 0) {
            System.out.println("Sostituzione di " + alliesToChange + " personaggi morti.");

            // Seleziona nuovi alleati
            // List<Character> newAllies =
            // CharacterSelectionMenu.replaceDeadAlliesMenu(this.availableAllies ,
            // alliesToChange);

            // Aggiungi i nuovi alleati
            // this.selectedAllies.addAll(newAllies);
        }
    }
}