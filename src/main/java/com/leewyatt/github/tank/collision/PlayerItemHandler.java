package com.leewyatt.github.tank.collision;

import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.leewyatt.github.tank.GameConfig;
import com.leewyatt.github.tank.GameType;
import com.leewyatt.github.tank.ItemType;
import com.leewyatt.github.tank.TankApp;
import com.leewyatt.github.tank.effects.HelmetEffect;
import com.leewyatt.github.tank.effects.ShipEffect;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;


public class PlayerItemHandler extends CollisionHandler {

    public PlayerItemHandler() {
        super(GameType.PLAYER, GameType.ITEM);
    }

    protected void onCollisionBegin(Entity player, Entity item) {
        TankApp app = getAppCast();
        ItemType itemType = item.getObject("itemType");
        play("item.wav");
        item.removeFromWorld();
        switch (itemType) {
            case BOMB -> collisionBomb();
            case TANK -> collisionTank(player);
            case SHIP -> collisionShip(player);
            case STAR -> collisionStar();
            case SPADE -> app.spadeBackUpBase();
            case HEART -> collisionHeart(player);
            case TIME -> app.freezingEnemy();
            case GUN -> set("playerBulletLevel", GameConfig.PLAYER_BULLET_MAX_LEVEL);
            case HELMET -> player.getComponent(EffectComponent.class)
                    .startEffect(new HelmetEffect());
            default -> {
            }
        }
    }

    private void collisionHeart(Entity player) {
        HealthIntComponent hp = player.getComponent(HealthIntComponent.class);
        hp.setValue(hp.getMaxValue());
    }

    private void collisionStar() {
        if (geti("playerBulletLevel") < GameConfig.PLAYER_BULLET_MAX_LEVEL) {
            inc("playerBulletLevel", 1);
        }
    }

    private void collisionShip(Entity player) {
        if (!player.getComponent(EffectComponent.class).hasEffect(ShipEffect.class)) {
            player.getComponent(EffectComponent.class).startEffect(new ShipEffect());
        }
    }

    private void collisionTank(Entity player) {
        HealthIntComponent hp = player.getComponent(HealthIntComponent.class);
        if (hp.getValue() < hp.getMaxValue()) {
            hp.damage(-1);
        }
    }

    private void collisionBomb() {
        List<Entity> enemyList = getGameWorld().getEntitiesByType(GameType.ENEMY);
        play("rocketBomb.wav");
        for (Entity enemy : enemyList) {
            spawn("explode", enemy.getCenter().getX() - 25, enemy.getCenter().getY() - 20);
            enemy.removeFromWorld();
            inc("destroyedEnemy", 1);
        }
    }

}
