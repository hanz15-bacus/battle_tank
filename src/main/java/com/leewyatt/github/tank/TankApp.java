package com.leewyatt.github.tank;

import com.almasb.fxgl.app.CursorInfo;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.*;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.util.LazyValue;
import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.entity.level.tiled.TMXLevelLoader;
import com.almasb.fxgl.time.TimerAction;
import com.leewyatt.github.tank.collision.*;
import com.leewyatt.github.tank.components.PlayerComponent;
import com.leewyatt.github.tank.effects.HelmetEffect;
import com.leewyatt.github.tank.ui.*;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.almasb.fxgl.dsl.FXGL.*;


public class TankApp extends GameApplication {

    private Entity player;
    private PlayerComponent playerComponent;
    private Random random = new Random();
    public LazyValue<FailedScene> failedSceneLazyValue = new LazyValue<>(FailedScene::new);
    private LazyValue<SuccessScene> successSceneLazyValue = new LazyValue<>(SuccessScene::new);


    private int[] enemySpawnX = {30, 295 + 30, 589 + 20};


    private TimerAction spadeTimerAction;

    private TimerAction freezingTimerAction;

    private TimerAction spawnEnemyTimerAction;

    @Override
    protected void onPreInit() {
        getSettings().setGlobalSoundVolume(0.5);
        getSettings().setGlobalMusicVolume(0.5);
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(28 * 24 + 6 * 24);
        settings.setHeight(28 * 24);
        settings.setTitle("90 Tank");
        settings.setAppIcon("ui/icon.png");
        settings.setVersion("Version 0.3");
        settings.setMainMenuEnabled(true);
        settings.setGameMenuEnabled(true);
        settings.getCSSList().add("tankApp.css");
        settings.setDefaultCursor(new CursorInfo("ui/cursor.png", 0, 0));

        settings.setSceneFactory(new SceneFactory() {
            @Override
            public StartupScene newStartup(int width, int height) {

                return new GameStartupScene(width, height);
            }

            @Override
            public FXGLMenu newMainMenu() {

                return new GameMenu();
            }

            @Override
            public LoadingScene newLoadingScene() {

                return new GameLoadingScene();
            }
        });
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        if (getFileSystemService().exists(GameConfig.CUSTOM_LEVEL_PATH)) {
            vars.put("level", 0);
        }else {
            vars.put("level", 1);
        }
        vars.put("playerBulletLevel", 1);
        vars.put("freezingEnemy", false);
        vars.put("destroyedEnemy", 0);
        vars.put("spawnedEnemy", 0);
        vars.put("gameOver", false);
    }

    @Override
    protected void initInput() {
        onKey(KeyCode.W, this::moveUpAction);
        onKey(KeyCode.UP, this::moveUpAction);

        onKey(KeyCode.S, this::moveDownAction);
        onKey(KeyCode.DOWN, this::moveDownAction);

        onKey(KeyCode.A, this::moveLeftAction);
        onKey(KeyCode.LEFT, this::moveLeftAction);

        onKey(KeyCode.D, this::moveRightAction);
        onKey(KeyCode.RIGHT, this::moveRightAction);

        onKey(KeyCode.SPACE, this::shootAction);
        onKey(KeyCode.F, this::shootAction);
    }

    private boolean tankIsReady() {
        return player != null && playerComponent != null && !getb("gameOver") && player.isActive();
    }

    private void shootAction() {
        if (tankIsReady()) {
            playerComponent.shoot();
        }
    }

    private void moveRightAction() {
        if (tankIsReady()) {
            playerComponent.right();
        }
    }

    private void moveLeftAction() {
        if (tankIsReady()) {
            playerComponent.left();
        }
    }

    private void moveDownAction() {
        if (tankIsReady()) {
            playerComponent.down();
        }
    }

    private void moveUpAction() {
        if (tankIsReady()) {
            playerComponent.up();
        }
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.BLACK);
        getGameWorld().addEntityFactory(new GameEntityFactory());

        buildAndStartLevel();
        getip("destroyedEnemy").addListener((ob, ov, nv) -> {
            if (nv.intValue() == GameConfig.ENEMY_AMOUNT) {
                set("gameOver", true);
                play("Win.wav");
                showWinnerSplashScreen();
            }
        });
    }

    private void showWinnerSplashScreen() {
        Pane splashPane = new Pane();
        splashPane.setPrefSize(getAppWidth(), getAppHeight());
        splashPane.setStyle("-fx-background-color: transparent;");

        Rectangle blackScreen = new Rectangle(getAppWidth(), getAppHeight(), Color.BLACK);
        blackScreen.setOpacity(0);

        Text winnerText = new Text("WINNER!!!");
        winnerText.setFill(Color.YELLOW);
        winnerText.setFont(new Font(50));
        winnerText.setOpacity(0);
        winnerText.setLayoutX(getAppWidth() / 2.0 - 100);
        winnerText.setLayoutY(getAppHeight() / 2.0 - 25);

        splashPane.getChildren().addAll(blackScreen, winnerText);
        getGameScene().addUINode(splashPane);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), new KeyValue(blackScreen.opacityProperty(), 0)),
                new KeyFrame(Duration.seconds(1), new KeyValue(blackScreen.opacityProperty(), 1)),
                new KeyFrame(Duration.seconds(2), new KeyValue(winnerText.opacityProperty(), 1)),
                new KeyFrame(Duration.seconds(6), new KeyValue(winnerText.opacityProperty(), 1)),
                new KeyFrame(Duration.seconds(7), new KeyValue(winnerText.opacityProperty(), 0))
        );

        timeline.setOnFinished(e -> {
            getGameScene().removeUINode(splashPane);
            // Switch back to the main menu after the splash screen
            getGameController().gotoMainMenu();
        });
        timeline.play();
    }

    public void buildAndStartLevel() {
        getGameWorld().getEntitiesByType(
                GameType.BULLET, GameType.ENEMY, GameType.PLAYER
        ).forEach(Entity::removeFromWorld);

        Rectangle rect1 = new Rectangle(getAppWidth(), getAppHeight() / 2.0, Color.web("#333333"));
        Rectangle rect2 = new Rectangle(getAppWidth(), getAppHeight() / 2.0, Color.web("#333333"));
        rect2.setLayoutY(getAppHeight() / 2.0);

        Text text = new Text("Ready?");
        text.setFill(Color.WHITE);
        text.setFont(new Font(35));
        text.setLayoutX(getAppWidth() / 2.0 - 70);
        text.setLayoutY(getAppHeight() / 2.0 - 5);

        Pane p1 = new Pane(rect1, rect2, text);

        addUINode(p1);

        Timeline tl = new Timeline(
                new KeyFrame(Duration.seconds(1.2),
                        new KeyValue(rect1.translateYProperty(), -getAppHeight() / 2.0),
                        new KeyValue(rect2.translateYProperty(), getAppHeight() / 2.0)
                ));
        tl.setOnFinished(e -> removeUINode(p1));

        PauseTransition pt = new PauseTransition(Duration.seconds(1.5));
        pt.setOnFinished(e -> {
            text.setVisible(false);
            tl.play();

            startLevel();
        });
        pt.play();
    }

    private void startLevel() {
        if (spawnEnemyTimerAction != null) {
            spawnEnemyTimerAction.expire();
            spawnEnemyTimerAction = null;
        }
        set("gameOver", false);
        set("freezingEnemy", false);
        set("destroyedEnemy", 0);
        set("spawnedEnemy", 0);
        expireAction(freezingTimerAction);
        expireAction(spadeTimerAction);

       if (geti("level")==0){
           Level level;
           try {
               level = new TMXLevelLoader()
                       .load(new File(GameConfig.CUSTOM_LEVEL_PATH).toURI().toURL(), getGameWorld());
               getGameWorld().setLevel(level);
           } catch (MalformedURLException e) {
               throw new RuntimeException(e);
           }
       }else {
           setLevelFromMap("level" + geti("level") + ".tmx");
       }
        play("start.wav");
        player = null;
        player = spawn("player", 9 * 24 + 3, 25 * 24);

        player.getComponent(EffectComponent.class).startEffect(new HelmetEffect());
        playerComponent = player.getComponent(PlayerComponent.class);

        getGameScene().addGameView(new GameView(new InfoPane(), 100));

        for (int i = 0; i < enemySpawnX.length; i++) {
            spawn("enemy",
                    new SpawnData(enemySpawnX[i], 30).put("assentName", "tank/E" + FXGLMath.random(1, 12) + "U.png"));
            inc("spawnedEnemy", 1);
        }
        spawnEnemy();
    }

    private void spawnEnemy() {
        if (spawnEnemyTimerAction != null) {
            spawnEnemyTimerAction.expire();
            spawnEnemyTimerAction = null;
        }

        Entity spawnBox = spawn("spawnBox", new SpawnData(-100, -100));

        spawnEnemyTimerAction = run(() -> {

            int testTimes = FXGLMath.random(2, 3);
            for (int i = 0; i < testTimes; i++) {
                if (geti("spawnedEnemy") < GameConfig.ENEMY_AMOUNT) {
                    boolean canGenerate = true;

                    int x = enemySpawnX[random.nextInt(3)];
                    int y = 30;
                    spawnBox.setPosition(x, y);
                    List<Entity> tankList = getGameWorld().getEntitiesByType(GameType.ENEMY, GameType.PLAYER);

                    for (Entity tank : tankList) {
                        if (tank.isActive() && spawnBox.isColliding(tank)) {
                            canGenerate = false;
                            break;
                        }
                    }

                    if (canGenerate) {
                        inc("spawnedEnemy", 1);
                        spawn("enemy",
                                new SpawnData(x, y).put("assentName", "tank/E" + FXGLMath.random(1, 12) + "U.png"));
                    }

                    spawnBox.setPosition(-100, -100);

                } else {
                    if (spawnEnemyTimerAction != null) {
                        spawnEnemyTimerAction.expire();
                    }
                }
            }
        }, GameConfig.SPAWN_ENEMY_TIME);
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new BulletEnemyHandler());
        getPhysicsWorld().addCollisionHandler(new BulletPlayerHandler());
        BulletBrickHandler bulletBrickHandler = new BulletBrickHandler();
        getPhysicsWorld().addCollisionHandler(bulletBrickHandler);
        getPhysicsWorld().addCollisionHandler(bulletBrickHandler.copyFor(GameType.BULLET, GameType.STONE));
        getPhysicsWorld().addCollisionHandler(bulletBrickHandler.copyFor(GameType.BULLET, GameType.GREENS));
        getPhysicsWorld().addCollisionHandler(new BulletFlagHandler());
        getPhysicsWorld().addCollisionHandler(new BulletBorderHandler());
        getPhysicsWorld().addCollisionHandler(new BulletBulletHandler());
        getPhysicsWorld().addCollisionHandler(new PlayerItemHandler());
    }

    public void freezingEnemy() {
        expireAction(freezingTimerAction);
        set("freezingEnemy", true);
        freezingTimerAction = runOnce(() -> {
            set("freezingEnemy", false);
        }, GameConfig.STOP_MOVE_TIME);
    }

    public void spadeBackUpBase() {
        expireAction(spadeTimerAction);

        updateWall(true);
        spadeTimerAction = runOnce(() -> {
            //基地周围的墙,还原成砖头墙
            updateWall(false);
        }, GameConfig.SPADE_TIME);
    }

    private void updateWall(boolean isStone) {

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 4; col++) {
                if (row != 0 && (col == 1 || col == 2)) {
                    continue;
                }

                List<Entity> entityTempList = getGameWorld().getEntitiesAt(new Point2D(288 + col * 24, 576 + row * 24));
                for (Entity entityTemp : entityTempList) {
                    Serializable type = entityTemp.getType();

                    if (type == GameType.STONE || type == GameType.BRICK || type == GameType.SNOW || type == GameType.SEA || type == GameType.GREENS) {
                        if (entityTemp.isActive()) {
                            entityTemp.removeFromWorld();
                        }
                    }
                }

                if (isStone) {
                    spawn("itemStone", new SpawnData(288 + col * 24, 576 + row * 24));
                } else {
                    spawn("brick", new SpawnData(288 + col * 24, 576 + row * 24));
                }
            }
        }
    }

    public void expireAction(TimerAction action) {
        if (action == null) {
            return;
        }
        if (!action.isExpired()) {
            action.expire();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
