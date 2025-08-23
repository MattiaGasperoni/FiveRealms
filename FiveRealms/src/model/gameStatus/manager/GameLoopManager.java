package model.gameStatus.manager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Manages the game loop execution including starting, stopping,
 * and handling the main game update cycle.
 */
public class GameLoopManager 
{
    /** Executor service for managing the game loop thread */
    private ScheduledExecutorService gameExecutor;
    
    /** Flag indicating if the game loop is currently running */
    private boolean isRunning;
    
    /** The game update callback */
    private Runnable updateCallback;
    
    /**
     * Constructs a new GameLoopManager.
     */
    public GameLoopManager() 
    {
        this.isRunning = false;
    }
    
    /**
     * Sets the update callback that will be called during each game loop iteration.
     * 
     * @param updateCallback the callback to execute during updates
     */
    public void setUpdateCallback(Runnable updateCallback) 
    {
        this.updateCallback = updateCallback;
    }
    
    /**
     * Starts the game loop with the default update interval (100ms).
     * 
     * @throws IllegalStateException if no update callback is set
     */
    public void startGameLoop() 
    {
        this.startGameLoop(100);
    }
    
    /**
     * Starts the game loop with a specified update interval.
     * 
     * @param intervalMs the update interval in milliseconds
     * @throws IllegalStateException if no update callback is set
     */
    public void startGameLoop(long intervalMs) 
    {
        if (this.updateCallback == null) 
        {
            throw new IllegalStateException("Update callback must be set before starting the game loop");
        }
        
        if (this.isRunning) 
        {
            return;
        }
        
        this.gameExecutor = Executors.newSingleThreadScheduledExecutor();
        this.isRunning = true;
        
        this.gameExecutor.scheduleAtFixedRate(() -> 
        {
            try 
            {
                if (this.updateCallback != null) 
                {
                    this.updateCallback.run();
                }
            } 
            catch (Exception e) 
            {
                System.err.println("Error in game loop: " + e.getMessage());
                e.printStackTrace();
            }
        }, 0, intervalMs, TimeUnit.MILLISECONDS);
        
    }
    
    /**
     * Starts the game loop for loading a saved game (uses thread pool).
     * This variant is used when multiple threads might be needed.
     * 
     * @param intervalMs the update interval in milliseconds
     */
    public void startGameLoopForLoad(long intervalMs) 
    {
        if (this.updateCallback == null) 
        {
            throw new IllegalStateException("Update callback must be set before starting the game loop");
        }
        
        if (this.isRunning) 
        {
            return;
        }
        
        if (this.gameExecutor == null) 
        {
            this.gameExecutor = Executors.newScheduledThreadPool(1);
        }
        
        this.isRunning = true;
        
        this.gameExecutor.scheduleAtFixedRate(() -> 
        {
            try 
            {
                if (this.updateCallback != null) 
                {
                    this.updateCallback.run();
                }
            } 
            catch (Exception e) 
            {
                System.err.println("Error in game loop: " + e.getMessage());
                e.printStackTrace();
            }
        }, 0, intervalMs, TimeUnit.MILLISECONDS);
        
    }
    
    /**
     * Stops the game loop by shutting down the executor service.
     */
    public void stopGameLoop() 
    {
        if (this.gameExecutor != null && !this.gameExecutor.isShutdown()) 
        {
            this.gameExecutor.shutdownNow();
        }
        this.isRunning = false;
    }
    
    /**
     * Checks if the game loop is currently running.
     * 
     * @return true if the game loop is running, false otherwise
     */
    public boolean isRunning() 
    {
        return this.isRunning && this.gameExecutor != null && !this.gameExecutor.isShutdown();
    }
    
    /**
     * Pauses the game loop execution.
     * Note: This doesn't actually pause the scheduled executor,
     * but sets a flag that can be checked in the update callback.
     */
    public void pauseGameLoop() 
    {
        this.isRunning = false;
    }
    
    /**
     * Resumes the game loop execution.
     */
    public void resumeGameLoop() 
    {
        if (this.gameExecutor != null && !this.gameExecutor.isShutdown()) 
        {
            this.isRunning = true;
        } 
        else 
        {
            System.out.println("Cannot resume - game loop was not properly initialized");
        }
    }
    
    /**
     * Checks if the executor service exists and is not shutdown.
     * 
     * @return true if executor is available
     */
    public boolean isExecutorAvailable() 
    {
        return this.gameExecutor != null && !this.gameExecutor.isShutdown();
    }
}