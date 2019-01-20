package com.redsponge.platformer.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.TimeUtils;
import com.redsponge.platformer.components.ColliderComponent;
import com.redsponge.platformer.components.Mappers;
import com.redsponge.platformer.components.PlayerComponent;
import com.redsponge.platformer.constants.Constants;

public class PlayerSystem extends IteratingSystem {


    private float jumpHeight;
    private float speed;
    private float jumpMaxTime;
    private long jumpStartTime;
    private float pixelsPerMeter;


    private float fallAmplifier;
    private float jumpStartMultiplier;

    // Flags
    private boolean onGround;
    private boolean jumping;
    private boolean moving;
    private boolean releasedJumpButton;
    private boolean holdingWall;

    // Show Debug Messages
    private static final boolean DEBUG = false;

    public PlayerSystem(float jumpHeight, float speed, float jumpMaxTime, float pixelsPerMeter, float fallAmplifier, float jumpStartMultiplier) {
        super(Family.all(PlayerComponent.class).get(), Constants.PLAYER_PRIORITY);
        this.jumpHeight = jumpHeight;
        this.speed = speed;
        this.jumpMaxTime = jumpMaxTime;
        this.pixelsPerMeter = pixelsPerMeter;
        this.fallAmplifier = fallAmplifier;
        this.jumpStartMultiplier = jumpStartMultiplier;
        this.jumpStartTime = 0;
    }

    public PlayerSystem() {
        this(Constants.DEFAULT_JUMP_HEIGHT, Constants.DEFAULT_PLAYER_SPEED, 0.15f, Constants.DEFAULT_PPM, Constants.DEFAULT_FALL_AMPLIFIER, Constants.DEFAULT_JUMP_START_MULTIPLIER);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Body body = Mappers.physics.get(entity).body;
        ColliderComponent collider = Mappers.collider.get(entity);

        updateFlags(collider);
        moving = false;

        if(Gdx.input.isKeyPressed(Keys.SPACE)) {
            if(onGround && !jumping) {
                startJump(body);
            } else if(collider.rightTouches > 0 && Gdx.input.isKeyJustPressed(Keys.SPACE)) {
                body.setLinearVelocity(-20, 15);
            } else if(collider.leftTouches > 0 && Gdx.input.isKeyJustPressed(Keys.SPACE)) {
                body.setLinearVelocity(20, 15);
            } else if(jumping) {
                continueJump(body, deltaTime);
            }
        } else {
            releasedJumpButton = true;
            if(jumping) {
                endJump(body, true);
            }
        }

        if(!jumping && !onGround && !holdingWall) {
            body.applyLinearImpulse(new Vector2(0, fallAmplifier), body.getWorldCenter(), true);
        }



        if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
            if(body.getLinearVelocity().x < 0) {
                _DEBUG("Changing Direction To Right!");

                body.applyLinearImpulse(new Vector2(speed * deltaTime * Constants.CHANGE_DIRECTION_MULTIPLIER, 0), body.getWorldCenter(), true);
            } else {
                body.applyLinearImpulse(new Vector2(speed * deltaTime, 0), body.getWorldCenter(), true);
            }
            moving = true;
        }

        if(Gdx.input.isKeyPressed(Keys.LEFT)) {
            if(body.getLinearVelocity().x > 0) {
                _DEBUG("Changing Direction To Left!");

                body.applyLinearImpulse(new Vector2(-speed * deltaTime * Constants.CHANGE_DIRECTION_MULTIPLIER, 0), body.getWorldCenter(), true);
            } else {
                body.applyLinearImpulse(new Vector2(-speed * deltaTime, 0), body.getWorldCenter(), true);
            }
            moving = true;
        }

        if(collider.downTouches > 0) {
            if(!moving) {
                body.setLinearVelocity(body.getLinearVelocity().x * Constants.FRICTION_MULTIPLIER, body.getLinearVelocity().y);
            }
        }

        if(Gdx.input.isKeyJustPressed(Keys.R)) {
            body.setTransform(0 / pixelsPerMeter, 200 / pixelsPerMeter, 0);
            body.setLinearVelocity(0, 0);
        }
    }

    /**
     * Updates the flags of the player
     * @param collider - The {@link ColliderComponent} instance of the {@link Entity}
     */
    private void updateFlags(ColliderComponent collider) {
        moving = false;
        onGround = collider.downTouches > 0;
        holdingWall = collider.rightTouches > 0 || collider.leftTouches > 0;
    }



    /////////////////////// JUMP METHODS ////////////////////////////////
    /**
     * Begins a jump with a small boost
     * @param body - The box2d {@link Body} of the player
     */
    private void startJump(Body body) {
        jumping = true;
        jumpStartTime = TimeUtils.nanoTime();
        body.applyLinearImpulse(new Vector2(0, jumpHeight), body.getWorldCenter(), true);
    }

    /**
     * Continues a jump if the player is jumping
     * @param body - The box2d {@link Body} of the player
     * @param delta - The delta time
     */
    private void continueJump(Body body, float delta) {
        if(!jumping) {
            return;
        }

        float timeSince = TimeUtils.timeSinceNanos(jumpStartTime) / 1000000000f;
        if(timeSince > jumpMaxTime) {
            endJump(body, false);
            return;
        }
        body.applyLinearImpulse(new Vector2(0, jumpHeight * delta), body.getWorldCenter(), true);
    }

    /**
     * Ends a jump if the player is currently jumping
     * @param body - The player's body
     * @param forceDown - Whether or not to cancel all upwards momentum (to control the jump height more)
     */
    private void endJump(Body body, boolean forceDown) {
        if(!jumping) {
            return;
        }
        jumping = false;
        if(forceDown) {
            body.setLinearVelocity(body.getLinearVelocity().x, 0.5f);
        }
    }

    /////////////////////////// Utility //////////////////////////////

    private void _DEBUG(String toDebug) {
        if(DEBUG) {
            Gdx.app.log("Player", toDebug);
        }
    }
}
