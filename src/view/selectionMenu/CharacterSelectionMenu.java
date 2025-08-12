package view.selectionMenu;

import model.gameStatus.Game;

/**
 * Character selection menu for initial team selection.
 * Extends AbstractSelectionMenu to inherit common functionality.
 */
public class CharacterSelectionMenu extends AbstractSelectionMenu 
{
    
    /**
     * Initializes and displays the character selection menu with all available allies.
     * 
     * @param allAllies List of all available characters that can be selected
     */
    @Override
    public void start() 
    {
        this.maxSelectableCharacters = Game.MAX_ALLIES_PER_ROUND;
        super.start();
    }
    
    @Override
    protected String getMenuType() 
    {
        return "Characters Selection";
    }
    
    @Override
    protected String getTitleText() 
    {
        return "SELECT 3 CHARACTERS";
    }
    
    @Override
    protected boolean requiresSpecialTitleStyling() 
    {
        return true; // CharacterSelectionMenu has the special background styling
    }

    

}