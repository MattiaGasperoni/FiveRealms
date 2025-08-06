package model.gameStatus;

import java.util.List;

import controller.GameController;
import model.characters.Character;
import view.map.TutorialMap;

public class Tutorial 
{
    // Messaggi dl Tutorial
    private static final String[] TUTORIAL_MESSAGES = 
    {
        "Welcome, soldier! Listen carefully.",
        "The enemies are above you. Defeat them!",
        "Your allies are down here!",
        "Your mission is to defeat all enemies!",
        "Good luck, soldier!"
    };
    
    private boolean tutorialCompleted;
    private TutorialMap tutorialMap;
    private List<Character> enemiesList;
    private List<Character> alliesList;
    private int currentStep;
	private GameController controller;
    
    public Tutorial(TutorialMap map, GameController controller)
    {
        this.tutorialCompleted = false;
        this.tutorialMap       = map;
        this.enemiesList       = this.tutorialMap.getEnemiesList();
        this.alliesList        = this.tutorialMap.getAlliesList();
        this.currentStep       = 0;
        this.controller        = controller;
    }
    
    /**
     * Avvia il tutorial completo
     */
    public boolean play() 
    {
        // Mostra la mappa del tutorial
        this.tutorialMap.start();
        
        // Spawna i personaggi
        this.tutorialMap.spawnCharacter(this.alliesList);
        this.tutorialMap.spawnCharacter(this.enemiesList);
        
        // Avvia la sequenza di popup tutorial
        this.startTutorialSequence();
        
        return this.tutorialCompleted;
    }
    
    /**
     * Inizia la sequenza dei popup tutorial
     */
    
    private void startTutorialSequence() 
    {
    	this.currentStep = 0;
        this.showNextStep();
    }
    
    /**
     * Mostra il prossimo step del tutorial
     */
    private void showNextStep() 
    {
        if (this.currentStep >= TUTORIAL_MESSAGES.length) 
        {
            this.completeTutorial();
            return;
        }
        
        String message = TUTORIAL_MESSAGES[this.currentStep];
        
        switch (this.currentStep) 
        {
            case 0: // Welcome + highlight allies
            	this.tutorialMap.showTutorialPopup(message, () -> 
                {
                	this.tutorialMap.highlightAlliesArea(this::nextStep);
                });
                break;
                
            case 1: // Enemy warning + highlight enemies
            	this.tutorialMap.showTutorialPopup(message, () -> 
                {
                	this.tutorialMap.highlightEnemiesArea(this::nextStep);
                });
                break;
                
            case 2: // Ally info
            case 3: // Mission info
            case 4: // Final message
            	this.tutorialMap.showTutorialPopup(message, this::nextStep);
                break;
        }
    }
    
    /**
     * Passa al prossimo step
     */
    private void nextStep() 
    {
    	this.currentStep++;
        this.showNextStep();
    }
    
    /**
     * Completa il tutorial
     */
    private void completeTutorial() 
    {
    	this.tutorialCompleted = true;
        System.out.println("Tutorial completed!");
        this.tutorialMap.closeWindow();
        this.controller.onTutorialPopupsCompleted();
    }
    
    /**
     * Controlla se il tutorial è completato
     */
    public boolean isCompleted() 
    {
        return this.tutorialCompleted;
    }
}
