package model.gameStatus;
import model.characters.Character;
import java.io.*;
import java.util.List;

public class GameStateManager 
{
    private static final String FILE_NAME = "game_state.dat";

    public void saveStatus(List<Character> allies, List<Character> enemies, int level)
    {
        GameState gameState = new GameState(level, allies, enemies);

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) 
        {
            out.writeObject(gameState);
            System.out.println("Stato di gioco salvato con successo.");
        } 
        catch (IOException e) 
        {
            System.err.println("Errore durante il salvataggio dello stato di gioco: " + e.getMessage());
        }
    }

    public GameState loadStatus() 
    {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) 
        {
            return (GameState) in.readObject();
        } 
        catch (IOException | ClassNotFoundException e) 
        {
            System.err.println("Errore durante il caricamento dello stato di gioco: " + e.getMessage());
            return null;
        }
    }
}
