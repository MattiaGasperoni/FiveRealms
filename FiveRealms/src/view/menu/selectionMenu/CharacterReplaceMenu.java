package view.menu.selectionMenu;

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
    public void show(int alliesToChange) 
    {
        this.alliesToChange          = alliesToChange;
        this.maxSelectableCharacters = alliesToChange;
        super.show();
    }
    
    /** Menu type and title text specific to character replacement */
    @Override
    protected String getMenuType() 
    {
        return "Characters Replace";
    }
    
    /** Title text indicating the number of characters to replace */
    @Override
    protected String getTitleText() 
    {
        return "REPLACE " + this.alliesToChange + " CHARACTERS";
    }
    
    /** Character replacement menu does not require special title styling */
    @Override
    protected boolean requiresSpecialTitleStyling() 
    {
        return true; // CharacterReplaceMenu doesn't have the special background styling
    }
}