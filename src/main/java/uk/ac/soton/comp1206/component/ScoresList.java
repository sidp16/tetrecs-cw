package uk.ac.soton.comp1206.component;

import java.util.ArrayList;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Display a list a of names and scores
 */
public class ScoresList extends VBox {
  private static final Logger logger = LogManager.getLogger(ScoresList.class);

  public final SimpleListProperty<Pair<String, Integer>> scores;

  public final ArrayList<VBox> displayingScores = new ArrayList<>();

  public StringProperty nameProperty = null;

  public ScoresList() {
    getStyleClass().add("scorelist");
    scores = new SimpleListProperty<>();
    scores.addListener((ListChangeListener<? super Pair<String, Integer>>) e -> {
      displayingScores.clear();
      getChildren().clear();
      int counter = 0;
      for (Pair<String, Integer> score : scores) {
        counter++;
        if (counter > 10) {
          break;
        }
        // Center box showing scores
        var scoreBox = new VBox();
        scoreBox.setAlignment(Pos.CENTER);

        var name = new Text(score.getKey() + ":" + score.getValue());
        name.setFill(GameBlock.COLOURS[counter]);
        scoreBox.getChildren().add(name);
        displayingScores.add(scoreBox);
        getChildren().add(scoreBox);

        reveal();
      }
    });
    nameProperty = new SimpleStringProperty();
    logger.info("Creating list of scores");
  }

  public void reveal() {
    ArrayList<Transition> transitionArrayList = new ArrayList<>();
    for (var score : displayingScores) {
      FadeTransition fade = new FadeTransition(new Duration(100), score);
      fade.setFromValue(0);
      fade.setToValue(1);
      fade.setCycleCount(2);
      transitionArrayList.add(fade);
    }
    SequentialTransition transition = new SequentialTransition(transitionArrayList.toArray(
        Animation[]::new));
    transition.play();
    logger.info("Score revealed");
  }
}
