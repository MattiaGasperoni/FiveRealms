package model.gameStatus.level;

import java.util.List;

import controller.GameController;
import model.characters.Character;
import view.map.TutorialMap;

public class GameTutorial implements Level
{
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
    
    public GameTutorial(TutorialMap map, GameController controller)
    {
        this.tutorialCompleted = false;
        this.tutorialMap       = map;
        this.enemiesList       = this.tutorialMap.getEnemiesList();
        this.alliesList        = this.tutorialMap.getAlliesList();
        this.currentStep       = 0;
        this.controller        = controller;
    }
    
    public boolean play() 
    {
    	
        this.tutorialMap.show();
        
        this.tutorialMap.spawnCharacter(this.alliesList);
        this.tutorialMap.spawnCharacter(this.enemiesList);
        
        this.startTutorialSequence();
        
        return this.tutorialCompleted;
    }
    
    private void startTutorialSequence() 
    {
    	this.currentStep = 0;
        this.showNextStep();
    }
    
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
    
    private void nextStep() 
    {
    	this.currentStep++;
        this.showNextStep();
    }
    
    private void completeTutorial() 
    {
    	this.tutorialCompleted = true;
        this.tutorialMap.close();
        this.controller.onTutorialPopupsCompleted();
    }
    
    public boolean isCompleted() 
    {
        return this.tutorialCompleted;
    }
}
