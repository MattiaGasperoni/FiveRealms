package view.selectionMenu;

import model.characters.Character;
import java.util.List;

/**
 * Character replacement menu for changing team members.
 * Extends AbstractSelectionMenu to inherit common functionality.
 */
public class CharacterReplaceMenu extends AbstractSelectionMenu 
{
    
    private int alliesToChange;
    
    /**
     * Initializes and displays the character replacement menu with all available allies.
     * 
     * @param allAllies List of all available characters that can be selected
     * @param alliesToChange Number of characters that need to be selected for replacement
     */
    public void start(List<Character> allAllies, int alliesToChange) 
    {
        this.alliesToChange = alliesToChange;
        this.maxSelectableCharacters = alliesToChange;
        super.start(allAllies);
    }
    
    @Override
    protected String getMenuType() 
    {
        return "Characters Replace";
    }
    
    @Override
    protected String getTitleText() 
    {
        return "SELECT " + alliesToChange + " CHARACTERS";
    }
    
    @Override
    protected boolean requiresSpecialTitleStyling() 
    {
        return false; // CharacterReplaceMenu doesn't have the special background styling
    }
}