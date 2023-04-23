package uk.ac.soton.comp1206;

import java.nio.file.Paths;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Multimedia {
  private MediaPlayer audioPlayer;
  private MediaPlayer musicPlayer;
  private static final Logger logger = LogManager.getLogger(Multimedia.class);
  public void playMusic(String pathToFile) {
    String specificPath = Paths.get("src/main/resources/music/").toUri().toString();
    Media audio = new Media(specificPath + pathToFile);
    musicPlayer = new MediaPlayer(audio);
    musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
    musicPlayer.play();
  }

  public void playAudioFile(String pathToFile) {
    String specificPath = Paths.get("src/main/resources/sounds/").toUri().toString();
    Media music = new Media(specificPath + pathToFile);
    audioPlayer = new MediaPlayer(music);

    audioPlayer.play();
  }
  public void stopMusic() {
    if (musicPlayer != null && musicPlayer.getStatus() == Status.PLAYING) {
      musicPlayer.stop();
    }
  }
  public void stopAudio() {
    if (audioPlayer != null && audioPlayer.getStatus() == Status.PLAYING) {
      audioPlayer.stop();
    }
  }
}
