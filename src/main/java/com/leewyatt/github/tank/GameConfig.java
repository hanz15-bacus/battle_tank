package com.leewyatt.github.tank;

import com.almasb.fxgl.core.collection.PropertyMap;
import com.almasb.fxgl.dsl.FXGL;
import javafx.util.Duration;


public class GameConfig {

    private GameConfig() {
    }

    private static final PropertyMap map;


    static {
        map = FXGL.getAssetLoader().loadPropertyMap("properties/game.properties");
    }

    public static final int MAX_LEVEL = map.getInt("maxLevel");


    public static final int PLAYER_BULLET_MAX_LEVEL = map.getInt("bulletMaxLevel");

    public static final int ENEMY_AMOUNT = map.getInt("enemyAmount");

    public static final int PLAYER_HEALTH = map.getInt("playerHealth");

    public static final int PLAYER_BULLET_SPEED = map.getInt("playerBulletSpeed");

    public static final int ENEMY_BULLET_SPEED = map.getInt("enemyBulletSpeed");

    public static final Duration PLAYER_SHOOT_DELAY = Duration.seconds(map.getDouble("playerShootDelay"));
    /**
     * 敌人射击间隔
     */
    public static final Duration ENEMY_SHOOT_DELAY = Duration.seconds(map.getDouble("enemyShootDelay"));
    /**
     * 保护罩保护的无敌时间
     */
    public static final Duration HELMET_TIME = Duration.seconds(map.getDouble("helmetTime"));
    /**
     * 定时道具.敌人停止行动的时间
     */
    public static final Duration STOP_MOVE_TIME = Duration.seconds(map.getDouble("stopMoveTime"));
    /**
     * 道具出现的时间
     */
    public static final Duration ITEM_SHOW_TIME = Duration.seconds(map.getDouble("itemShowTime"));
    /**
     * 道具从出现到闪烁提醒的时间
     */
    public static final Duration ITEM_NORMAL_SHOW_TIME = Duration.seconds(map.getDouble("itemNormalShowTime"));
    /**
     * spade 铁锨保护基地的时间
     */
    public static final Duration SPADE_TIME = Duration.seconds(map.getDouble("spadeTime"));

    /**
     * 铁锨即将结束,闪烁提示15秒-12秒=3秒, 在最后的3秒,基地四周进行闪烁提示
     */
    public static final Duration SPADE_NORMAL_TIME = Duration.seconds(map.getDouble("spadeNormalTime"));

    /**
     * 产生地方坦克的间隔时间
     */
    public static final Duration SPAWN_ENEMY_TIME = Duration.seconds(map.getDouble("spawnEnemyTime"));

    /**
     * 产生道具的比例
     */
    public static final double SPAWN_ITEM_PRO = map.getDouble("spawnItemPro");
    /**
     * 玩家移动速度
     */
    public static final int PLAYER_SPEED = map.getInt("playerSpeed");
    /**
     * 敌人的移动速度
     */
    public static final int ENEMY_SPEED = map.getInt("enemySpeed");
    /**
     * 自定义地图tmx文件路径
     */
    public static final String CUSTOM_LEVEL_PATH = map.getString("customLevelPath");
    /**
     * 自定义地图data文件路径
     */
    public static final String CUSTOM_LEVEL_DATA = map.getString("customLevelData");

}
