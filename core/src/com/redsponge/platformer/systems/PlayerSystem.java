package com.redsponge.platformer.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.redsponge.platformer.components.ColliderComponent;
import com.redsponge.platformer.components.Mappers;
import com.redsponge.platformer.components.PlayerComponent;
import com.redsponge.platformer.constants.Constants;

public class PlayerSystem extends IteratingSystem {

    public PlayerSystem() {
        super(Family.all(PlayerComponent.class).get(), Constants.PLAYER_PRIORITY);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Body body = Mappers.physics.get(entity).body;
        ColliderComponent collider = Mappers.collider.get(entity);

        if(Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            if(collider.downTouches > 0) {
                body.setLinearVelocity(0, 200);
            } else {
                if(collider.rightTouches > 0) {
                    body.applyLinearImpulse(new Vector2(-100, 100), body.getWorldCenter(), true);
                } else if(collider.leftTouches > 0) {
                    body.applyLinearImpulse(new Vector2(100, 100), body.getWorldCenter(), true);
                }
            }
        }

        if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
            body.setLinearVelocity(50, body.getLinearVelocity().y);
        }
        if(Gdx.input.isKeyPressed(Keys.LEFT)) {
            body.setLinearVelocity(-50, body.getLinearVelocity().y);
        }
    }
}
