package com.leewyatt.github.tank.ui;

import com.almasb.fxgl.app.scene.StartupScene;
import javafx.animation.PauseTransition;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;


public class GameStartupScene extends StartupScene {
    public GameStartupScene(int appWidth, int appHeight) {
        super(appWidth, appHeight);
        StackPane pane = new StackPane(new ImageView(getClass().getResource("/assets/textures/ui/tank_yey.png").toExternalForm()));
        PauseTransition delay = new PauseTransition(Duration.seconds(5));
        pane.setPrefSize(appWidth, appHeight);
        pane.setStyle("-fx-background-color: black");
        getContentRoot().getChildren().addAll(pane);
        delay.setOnFinished(event -> new GameMainMenu());

        // Start the delay timer
        delay.play();
    }
}
