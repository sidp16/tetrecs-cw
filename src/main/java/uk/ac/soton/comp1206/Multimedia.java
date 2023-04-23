package uk.ac.soton.comp1206;

import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.AbstractLogger;

public class Multimedia {
  private MediaPlayer audioPlayer;
  private MediaPlayer musicPlayer;
  private static final Logger logger = LogManager.getLogger(Multimedia.class);

  public static Image qgetImage(String file) {
    logger.info("Image " + file + "displayed");
    return new Image(Multimedia.class.getResource("/images/" + file).toExternalForm());
  }

  public void playBackgroundMusic(String pathToFile) {
    Media audio = new Media(pathToFile);
    musicPlayer = new MediaPlayer(audio);
    musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
    musicPlayer.play();
  }

  public void playAudioFile(String pathToFile) {
    Media music = new Media(pathToFile);
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
