package model.gameStatus;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MusicManager 
{

    private Clip clip;
    private Map<String, String> tracks = new HashMap<>();

    public MusicManager() 
    {
        // Mappa dei brani
        this.tracks.put("level1", "music/level1.wav");
        this.tracks.put("level2", "music/level2.wav");
        this.tracks.put("level3", "music/level3.wav");
        this.tracks.put("level4", "music/level4.wav");
        this.tracks.put("level5", "music/level5.wav");
        this.tracks.put("background", "music/background.wav");
        this.tracks.put("win",  "music/win.wav");
        this.tracks.put("lose", "music/lose.wav");
    }

    public void play(String trackName, boolean loop) 
    {
        this.stop(); // ferma musica attuale
        
        String path = tracks.get(trackName);
        
        if (path == null) 
        {
            System.out.println("Traccia non trovata: " + trackName);
            return;
        }
        
        try 
        {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(path));
            this.clip = AudioSystem.getClip();
            this.clip.open(audioStream);
            
            if (loop) 
            {
            	 this.clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
            
            this.clip.start();
            
        } 
        catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) 
        {
            e.printStackTrace();
        }
    }

    public void stop() 
    {
        if ( this.clip != null &&  this.clip.isRunning()) 
        {
        	 this.clip.stop();
        }
    }
}
