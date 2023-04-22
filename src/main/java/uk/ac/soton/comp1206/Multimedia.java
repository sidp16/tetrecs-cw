package uk.ac.soton.comp1206;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;

public class Multimedia {
  private MediaPlayer audioPlayer;
  private MediaPlayer musicPlayer;

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
