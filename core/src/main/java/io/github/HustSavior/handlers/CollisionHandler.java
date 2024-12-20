package io.github.HustSavior.handlers;

import java.util.List;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import io.github.HustSavior.entities.AbstractMonster;
import io.github.HustSavior.entities.Player;

public class CollisionHandler {
    public void checkCollisions(Player player, List<AbstractMonster> monsters) {
        Rectangle playerBounds = player.getBounds();
        
        for (AbstractMonster monster : monsters) {
            if (playerBounds.overlaps(monster.getBounds())) {
                handleCollision(player, monster);
            }
        }
    }

    private void handleCollision(Player player, AbstractMonster monster) {
        Vector2 knockbackDir = new Vector2(
            player.getPosition().x - monster.getPosition().x,
            player.getPosition().y - monster.getPosition().y
        ).nor();
        
        if (!player.isKnockedBack()) {
            player.applyKnockback(knockbackDir);
            player.takeDamage(monster.getAttack());
        }
        
        monster.handlePush(player.getVelocity());
    }
} 