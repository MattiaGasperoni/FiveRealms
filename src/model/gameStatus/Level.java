package model.gameStatus;

import java.io.IOException;

/**
 * Interface representing a generic game level.
 * Implementing classes should provide functionality to start the level
 * and check whether it has been completed successfully.
 */
public interface Level 
{
    /**
     * Starts the level and initializes all necessary components.
     * 
     * @throws IOException if an error occurs during level initialization or setup
     */
	boolean play() throws IOException;

    /**
     * Checks whether the level has been successfully completed.
     * 
     * @return true if the level is completed, false otherwise
     */
    boolean isCompleted();
}
