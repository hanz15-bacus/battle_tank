//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.leewyatt.github.tank.ui;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.Texture;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class GameMenu extends FXGLMenu {
    private final TranslateTransition tt;
    private final Pane defaultPane;

    public GameMenu() {
        super(MenuType.MAIN_MENU);
        Texture texture = FXGL.texture("ui/mbbc_start.png");
        texture.setLayoutX(144.0);
        texture.setLayoutY(160.0);
        MainMenuButton newGameBtn = new MainMenuButton("START GAME", () -> {
            this.fireNewGame();
        });
        ToggleGroup tg = new ToggleGroup();
        tg.getToggles().add(newGameBtn);
        newGameBtn.setSelected(true);
        VBox menuBox = new VBox(5.0, newGameBtn);
        menuBox.setAlignment(Pos.CENTER_LEFT);
        menuBox.setLayoutX(240.0);
        menuBox.setLayoutY(360.0);
        menuBox.setVisible(false);
        Texture tankTexture = FXGL.texture("ui/tankLoading.png");
        this.tt = new TranslateTransition(Duration.seconds(2.0), tankTexture);
        this.tt.setInterpolator(Interpolators.ELASTIC.EASE_OUT());
        this.tt.setFromX(172.0);
        this.tt.setFromY(252.0);
        this.tt.setToX(374.0);
        this.tt.setToY(252.0);
        this.tt.setOnFinished((e) -> {
            menuBox.setVisible(true);
        });
        Rectangle bgRect = new Rectangle((double)this.getAppWidth(), (double)this.getAppHeight());
        Line line = new Line(30.0, 580.0, 770.0, 580.0);
        line.setStroke(Color.web("#B9340D"));
        line.setStrokeWidth(2.0);
        Texture textureWall = FXGL.texture("ui/mbbc_logo.png");
        textureWall.setLayoutX(310.0);
        textureWall.setLayoutY(600.0);
        this.defaultPane = new Pane(bgRect, texture, tankTexture, menuBox, line, textureWall);
        this.getContentRoot().getChildren().setAll(this.defaultPane);
    }

    public void onCreate() {
        this.getContentRoot().getChildren().setAll(this.defaultPane);
        FXGL.play("mainMenuLoad.wav");
        this.tt.play();
    }
}
