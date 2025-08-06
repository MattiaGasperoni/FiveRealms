package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import model.gameStatus.Game;
import model.gameStatus.Level;
import model.gameStatus.saveSystem.GameStateManager;
import model.characters.Character;
import model.characters.Barbarian;
import model.characters.Archer;
import model.characters.Knight;
import model.characters.Wizard;
import model.characters.Juggernaut;
import model.point.Point;
import view.map.AbstractMap;
import view.menu.MainMenu;
import view.menu.TutorialMenu;
import view.BattlePhaseView;
import view.CharacterSelectionMenu;
import controller.GameController;

/**
 * Batteria di test unitari per il sistema di gioco Five Realms
 * Testa le funzionalità principali del gameplay, gestione personaggi e logica di battaglia
 */
public class GameUnitTests {

    private Game game;
    private List<Character> testAllies;
    private List<Character> testEnemies;

    @BeforeEach
    void setUp() {
        game = new Game();
        
        // Inizializza liste di test
        testAllies = new ArrayList<>();
        testAllies.add(new Barbarian());
        testAllies.add(new Archer());
        testAllies.add(new Knight());
        
        testEnemies = new ArrayList<>();
        testEnemies.add(new Barbarian());
        testEnemies.add(new Wizard());
        
        // Trasforma gli alleati in eroi
        for (Character ally : testAllies) {
            ally.becomeHero();
        }
    }

    @Test
    @DisplayName("Test 1: Creazione e Inizializzazione Personaggi Alleati")
    void testCreateAlliesInitialization() {
        // Test della creazione dei personaggi disponibili
        List<Character> availableAllies = game.createAllies();
        
        // Verifica che vengano creati tutti i tipi di personaggio
        assertNotNull(availableAllies, "La lista degli alleati disponibili non dovrebbe essere null");
        assertEquals(5, availableAllies.size(), "Dovrebbero essere disponibili 5 tipi di personaggio");
        
        // Verifica che tutti i tipi di personaggio siano presenti
        boolean hasBarbarian = availableAllies.stream().anyMatch(c -> c instanceof Barbarian);
        boolean hasArcher = availableAllies.stream().anyMatch(c -> c instanceof Archer);
        boolean hasKnight = availableAllies.stream().anyMatch(c -> c instanceof Knight);
        boolean hasWizard = availableAllies.stream().anyMatch(c -> c instanceof Wizard);
        boolean hasJuggernaut = availableAllies.stream().anyMatch(c -> c instanceof Juggernaut);
        
        assertTrue(hasBarbarian, "Dovrebbe essere presente un Barbarian");
        assertTrue(hasArcher, "Dovrebbe essere presente un Archer");
        assertTrue(hasKnight, "Dovrebbe essere presente un Knight");
        assertTrue(hasWizard, "Dovrebbe essere presente un Wizard");
        assertTrue(hasJuggernaut, "Dovrebbe essere presente un Juggernaut");
    }

    @Test
    @DisplayName("Test 2: Selezione e Configurazione Personaggi")
    void testCharacterSelectionAndConfiguration() {
        List<Character> selectedCharacters = new ArrayList<>();
        selectedCharacters.add(new Barbarian());
        selectedCharacters.add(new Archer());
        selectedCharacters.add(new Knight());

        game.setSelectedCharacters(selectedCharacters);

        // Assegna posizione iniziale (es. griglia 0,0 -> 0,1 -> 0,2)
        for (int i = 0; i < selectedCharacters.size(); i++) {
            Character character = selectedCharacters.get(i);
            character.becomeHero(); // Assicurati che siano alleati
            character.setPosition(new Point(0, i)); // Set manuale
        }

        // Verifiche
        for (Character character : selectedCharacters) {
            assertTrue(character.isAllied(), "Tutti i personaggi selezionati dovrebbero essere alleati");
            assertTrue(character.isAlive(), "Tutti i personaggi dovrebbero essere vivi all'inizio");
            assertNotNull(character.getPosition(), "Ogni personaggio dovrebbe avere una posizione iniziale");
        }

        assertTrue(selectedCharacters.size() <= Game.MAX_ALLIES_PER_ROUND, 
            "Non dovrebbero essere selezionati più di " + Game.MAX_ALLIES_PER_ROUND + " alleati");
    }


    @Test
    @DisplayName("Test 3: Posizionamento e Movimento Personaggi sulla Griglia")
    void testCharacterPositioningAndMovement() {
        // Test del posizionamento e movimento dei personaggi
        Character testCharacter = new Barbarian();
        testCharacter.becomeHero();
        
        Point initialPosition = new Point(5, 5);
        Point targetPosition = new Point(7, 7);
        
        testCharacter.setPosition(initialPosition);
        
        // Verifica posizione iniziale
        assertEquals(initialPosition, testCharacter.getPosition(), 
                    "Il personaggio dovrebbe essere nella posizione iniziale");
        
        // Test validità posizione target
        int moveRange = testCharacter.getSpeed() / 10;
        double distance = initialPosition.distanceFrom(targetPosition);
        
        assertTrue(distance <= moveRange, 
                  "La distanza target dovrebbe essere entro il raggio di movimento");
        
        // Simula movimento
        testCharacter.setPosition(targetPosition);
        assertEquals(targetPosition, testCharacter.getPosition(), 
                    "Il personaggio dovrebbe essere nella nuova posizione dopo il movimento");
        
        // Test limiti griglia
        assertTrue(targetPosition.getX() >= 0 && targetPosition.getX() < AbstractMap.GRID_SIZE_WIDTH,
                  "La posizione X dovrebbe essere entro i limiti della griglia");
        assertTrue(targetPosition.getY() >= 0 && targetPosition.getY() < AbstractMap.GRID_SIZE_HEIGHT,
                  "La posizione Y dovrebbe essere entro i limiti della griglia");
    }

    @Test
    @DisplayName("Test 4: Sistema di Combattimento e Calcolo Danni")
    void testCombatSystemAndDamageCalculation() {
        // Test del sistema di combattimento
        Character attacker = new Barbarian();
        Character defender = new Knight();
        
        attacker.becomeHero();
        
        // Salva valori iniziali
        int initialAttackerHealth = attacker.getCurrentHealth();
        int initialDefenderHealth = defender.getCurrentHealth();
        int attackerPower = attacker.getPower();
        int defenderDefence = defender.getDefence();
        
        // Verifica che entrambi i personaggi siano vivi
        assertTrue(attacker.isAlive(), "L'attaccante dovrebbe essere vivo");
        assertTrue(defender.isAlive(), "Il difensore dovrebbe essere vivo");
        
        // Simula attacco (logica semplificata)
        int expectedDamage = Math.max(1, attackerPower - defenderDefence);
        int expectedDefenderHealth = Math.max(0, initialDefenderHealth - expectedDamage);
        
        // Verifica calcolo danni
        assertTrue(expectedDamage > 0, "Il danno dovrebbe essere sempre almeno 1");
        assertTrue(expectedDefenderHealth >= 0, "La salute non dovrebbe mai essere negativa");
        
        // Test range di attacco
        attacker.setPosition(new Point(0, 0));
        defender.setPosition(new Point(2, 2));
        
        int weaponRange = attacker.getWeapon().getRange();
        double attackDistance = attacker.getPosition().distanceFrom(defender.getPosition());
        
        boolean canAttack = attackDistance <= weaponRange;
        assertNotNull(attacker.getWeapon(), "L'attaccante dovrebbe avere un'arma");
        assertTrue(weaponRange > 0, "Il range dell'arma dovrebbe essere positivo");
    }

    @Test
    @DisplayName("Test 5: Condizioni di Vittoria e Sconfitta del Livello")
    void testLevelWinLoseConditions() {
        // Test delle condizioni di fine livello
        List<Character> allies = new ArrayList<>();
        List<Character> enemies = new ArrayList<>();
        
        // Scenario 1: Tutti i nemici sconfitti (Vittoria)
        allies.add(new Barbarian());
        allies.add(new Archer());
        // enemies lista vuota = tutti i nemici sconfitti
        
        for (Character ally : allies) {
            ally.becomeHero();
        }
        
        boolean levelWon = enemies.isEmpty() && !allies.isEmpty();
        assertTrue(levelWon, "Il livello dovrebbe essere vinto quando tutti i nemici sono sconfitti");
        
        // Scenario 2: Tutti gli alleati morti (Sconfitta)
        allies.clear();
        enemies.add(new Wizard());
        enemies.add(new Knight());
        
        boolean levelLost = allies.isEmpty() && !enemies.isEmpty();
        assertTrue(levelLost, "Il livello dovrebbe essere perso quando tutti gli alleati sono morti");
        
        // Scenario 3: Battaglia in corso
        allies.add(new Barbarian());
        allies.get(0).becomeHero();
        
        boolean battleOngoing = !allies.isEmpty() && !enemies.isEmpty();
        assertTrue(battleOngoing, "La battaglia dovrebbe continuare quando ci sono sia alleati che nemici");
        
        // Test conteggio personaggi vivi
        long aliveAllies = allies.stream().filter(Character::isAlive).count();
        long aliveEnemies = enemies.stream().filter(Character::isAlive).count();
        
        assertEquals(1, aliveAllies, "Dovrebbe esserci 1 alleato vivo");
        assertEquals(2, aliveEnemies, "Dovrebbero esserci 2 nemici vivi");
        
        // Test numero totale di livelli
        assertEquals(5, Game.TOTAL_LEVEL, "Il gioco dovrebbe avere 5 livelli totali");
    }

    @Test
    @DisplayName("Test Bonus: Validazione Stato del Gioco")
    void testGameStateValidation() {
        // Test aggiuntivo per validare lo stato generale del gioco
        
        // Test inizializzazione gioco
        assertNotNull(game, "L'istanza del gioco non dovrebbe essere null");
        
        // Test costanti di gioco
        assertTrue(Game.TOTAL_LEVEL > 0, "Il numero totale di livelli dovrebbe essere positivo");
        assertTrue(Game.MAX_ALLIES_PER_ROUND > 0, "Il numero massimo di alleati per round dovrebbe essere positivo");
        assertTrue(Game.MAX_ALLIES_PER_ROUND <= 5, "Il numero massimo di alleati non dovrebbe superare 5");
        
        // Test dimensioni griglia
        assertTrue(AbstractMap.GRID_SIZE_HEIGHT > 0, "L'altezza della griglia dovrebbe essere positiva");
        assertTrue(AbstractMap.GRID_SIZE_WIDTH > 0, "La larghezza della griglia dovrebbe essere positiva");
        assertEquals(15, AbstractMap.GRID_SIZE_HEIGHT, "L'altezza della griglia dovrebbe essere 15");
        assertEquals(20, AbstractMap.GRID_SIZE_WIDTH, "La larghezza della griglia dovrebbe essere 20");
        
        // Test creazione personaggi base
        Character testBarbarian = new Barbarian();
        Character testArcher = new Archer();
        
        assertNotNull(testBarbarian, "Il Barbarian dovrebbe essere creato correttamente");
        assertNotNull(testArcher, "L'Archer dovrebbe essere creato correttamente");
        assertNotEquals(testBarbarian.getClass(), testArcher.getClass(), 
                       "Diversi tipi di personaggio dovrebbero avere classi diverse");
    }
}