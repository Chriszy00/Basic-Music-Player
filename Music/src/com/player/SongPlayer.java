package com.player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

class Song {
    private String title;
    private String[] lyrics;
    private String musicFilePath;

    public Song(String title, String[] lyrics, String musicFilePath) {
        this.title = title;
        this.lyrics = lyrics;
        this.musicFilePath = musicFilePath;
    }

    public String getTitle() {
        return title;
    }

    public String[] getLyrics() {
        return lyrics;
    }

    public String getMusicFilePath() {
        return musicFilePath;
    }
}

class MusicPlayer {
    private ArrayList<Song> playlist;
    private int currentSongIndex;
    private Clip clip;

    public MusicPlayer() {
        playlist = new ArrayList<>();
        currentSongIndex = -1; // No song initially
    }

    public void restart() {
        if (currentSongIndex != -1) {
            playSong(playlist.get(currentSongIndex));
        } else {
            System.out.println("No song is currently playing.");
        }
    }

    public void addSong(Song song) {
        playlist.add(song);
        System.out.println("Song \"" + song.getTitle() + "\" added to playlist.");
    }

    public void playSong(String title) {
        for (int i = 0; i < playlist.size(); i++) {
            if (playlist.get(i).getTitle().equals(title)) {
                currentSongIndex = i; // Update the current song index
                playSong(playlist.get(i));
                return;
            }
        }
        System.out.println("Song \"" + title + "\" not found in playlist.");
    }

    public void playSongByNumber(int songNumber) {
        if (songNumber >= 1 && songNumber <= playlist.size()) {
            currentSongIndex = songNumber - 1;
            playSong(playlist.get(currentSongIndex));
        } else {
            System.out.println("Invalid song number.");
        }
    }

    private void playSong(Song song) {
        System.out.println("Playing song: " + song.getTitle());
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(song.getMusicFilePath()));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);

            // Start playing the music
            clip.start();

            // Get the duration of each lyric line
            long totalDurationInMicroseconds = clip.getMicrosecondLength();
            long lyricDuration = totalDurationInMicroseconds / song.getLyrics().length;

            // Create a ScheduledExecutorService
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

            // Display lyrics synchronized with the music
            for (int i = 0; i < song.getLyrics().length; i++) {
                final int index = i;
                executor.scheduleAtFixedRate(() -> System.out.println(song.getLyrics()[index]),
                        i * lyricDuration,
                        song.getLyrics().length * lyricDuration,
                        TimeUnit.MICROSECONDS);
            }

            // Wait for the song to finish
            while (clip.getMicrosecondPosition() < totalDurationInMicroseconds) {
                Thread.sleep(100); // Sleep for a short period of time to reduce CPU usage
            }

            // Stop playing the music
            clip.stop();
            clip.close();

            // Shut down the executor
            executor.shutdown();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException e) {
            System.err.println("Error playing song: " + e.getMessage());
        }
    }

    public void next() {
        if (currentSongIndex < playlist.size() - 1) {
            currentSongIndex++;
            playSong(playlist.get(currentSongIndex));
        } else {
            System.out.println("End of playlist reached.");
        }
    }

    public void previous() {
        if (currentSongIndex > 0) {
            currentSongIndex--;
            playSong(playlist.get(currentSongIndex));
        } else {
            System.out.println("Beginning of playlist reached.");
        }
    }

    public void displayPlaylist() {
        System.out.println("Playlist:");
        for (Song song : playlist) {
            System.out.println("- " + song.getTitle());
        }
    }

    public void displayPlaylistWithNumbers() {
        System.out.println("Playlist:");
        for (int i = 0; i < playlist.size(); i++) {
            System.out.println((i+1) + ". " + playlist.get(i).getTitle());
        }
    }
}

public class SongPlayer {
    public static void main(String[] args) {
        MusicPlayer player = new MusicPlayer();

        // Load properties from the configuration file
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("config.properties"));
        } catch (IOException e) {
            System.err.println("Error reading configuration file: " + e.getMessage());
            return;
        }

        // Get file paths from the configuration file
        String beautiful_thingsLyrics = prop.getProperty("beautiful_thingsLyrics");
        String beautiful_thingsFile = prop.getProperty("beautiful_thingsFile");
        String lose_controlLyrics = prop.getProperty("lose_controlLyrics");
        String lose_controlFile = prop.getProperty("lose_controlFile");
        String dandelionsLyrics = prop.getProperty("dandelionsLyrics");
        String dandelionsFile = prop.getProperty("dandelionsFile");
        String hold_me_while_you_waitLyrics = prop.getProperty("hold_me_while_you_waitLyrics");
        String hold_me_while_you_waitFile = prop.getProperty("hold_me_while_you_waitFile");
        String before_you_goLyrics = prop.getProperty("before_you_goLyrics");
        String before_you_goFile = prop.getProperty("before_you_goFile");
        String irisFIle = prop.getProperty("iris");
        String irisLyrics = prop.getProperty("irisLyrics");
        String cant_stopFile = prop.getProperty("cant_stopFile");
        String cant_stopLyrics = prop.getProperty("cant_stopLyrics");
        String slipknotFile = prop.getProperty("slipknotFile");
        String slipknotLyrics = prop.getProperty("slipknotLyrics");

        // Read lyrics from text file
        String[] beautifulThingsLyrics = readLyricsFromFile(beautiful_thingsLyrics);
        String[] loseControlLyrics = readLyricsFromFile(lose_controlLyrics);
        String[] dandelions_Lyrics = readLyricsFromFile(dandelionsLyrics);
        String[] hold_me_while_you_wait_Lyrics = readLyricsFromFile(hold_me_while_you_waitLyrics);
        String [] before_you_go_Lyrics = readLyricsFromFile(before_you_goLyrics);
        String [] iris_Lyrics = readLyricsFromFile(irisLyrics);
        String [] cant_stop_Lyrics = readLyricsFromFile(cant_stopLyrics);
        String [] slipknot_Lyrics = readLyricsFromFile(slipknotLyrics);

        // Adding the songs to the playlist
        Song beautifulThings = new Song("Beautiful Things", beautifulThingsLyrics, beautiful_thingsFile);
        player.addSong(beautifulThings);

        Song loseControl = new Song("Lose Control", loseControlLyrics, lose_controlFile);
        player.addSong(loseControl);

        Song dandelions = new Song("Dandelions", dandelions_Lyrics, dandelionsFile);
        player.addSong(dandelions);

        Song holdMeWhileYouWait = new Song("Hold Me While You Wait", hold_me_while_you_wait_Lyrics, hold_me_while_you_waitFile);
        player.addSong(holdMeWhileYouWait);

        Song beforeYouGo = new Song("Before You Go", before_you_go_Lyrics, before_you_goFile);
        player.addSong(beforeYouGo);

        Song iris = new Song("Iris w/out Lyrics", iris_Lyrics, irisFIle);
        player.addSong(iris);

        Song cantStop = new Song("Can't Stop w/out Lyrics", cant_stop_Lyrics, cant_stopFile);
        player.addSong(cantStop);

        Song slipknot = new Song("Slipknot w/out Lyrics", slipknot_Lyrics, slipknotFile);
        player.addSong(slipknot);

        // Simulating user interaction
        Scanner scanner = new Scanner(System.in);
        String choice;
        do {
            System.out.println("\n--------MENU--------\n");
            System.out.println("1. Play a song");
            System.out.println("2. Display playlist");
            System.out.println("3. Next");
            System.out.println("4. Previous");
            System.out.println("5. Restart");
            System.out.println("6. Exit");
            System.out.print("\nEnter your choice: ");
            choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    player.displayPlaylistWithNumbers(); // Display playlist with numbers
                    System.out.print("\nEnter the number of the song you want to play: ");
                    int songNumber = scanner.nextInt();
                    scanner.nextLine(); // Consume newline character
                    player.playSongByNumber(songNumber);
                    break;
                case "2":
                    player.displayPlaylist();
                    break;
                case "3":
                    player.next();
                    break;
                case "4":
                    player.previous();
                    break;
                case "5":
                    player.restart();
                    break;
                case "6":
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number from 1 to 6 .");
            }

        } while (!choice.equals("6"));

        scanner.close();
    }

    // Method to read lyrics from a text file
    private static String[] readLyricsFromFile(String filename) {
        ArrayList<String> lyricsList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                lyricsList.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading lyrics file: " + e.getMessage());
        }
        return lyricsList.toArray(new String[0]);
    }
}