package view.menu.selectionMenu;

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
    public void show() 
    {
        this.maxSelectableCharacters = Game.MAX_ALLIES_PER_ROUND;
        super.show();
    }
    
    /** Menu type and title text specific to character selection */
    @Override
    protected String getMenuType() 
    {
        return "Characters Selection";
    }
    
    /** Title text indicating the number of characters to select */
    @Override
    protected String getTitleText() 
    {
        return "SELECT "+Game.MAX_ALLIES_PER_ROUND+" CHARACTERS";
    }
    
    /** Character selection menu requires special title styling */
    @Override
    protected boolean requiresSpecialTitleStyling() 
    {
        return true; // CharacterSelectionMenu has the special background styling
    }

    

}