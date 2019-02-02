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
import com.redsponge.platformer.input.InputSystem;
import com.redsponge.platformer.input.SimpleInputSystem;

public class PlayerSystem extends IteratingSystem {


    private float jumpHeight;
    private float speed;
    private float maxSpeed;
    private float jumpMaxTime;
    private long jumpStartTime;
    private long wallJumpStartTime;
    private float pixelsPerMeter;


    private float fallAmplifier;
    // Flags
    private boolean onGround;
    private boolean jumping;
    private boolean holdingWall;

    // Show Debug Messages
    private static final boolean DEBUG = true;
    private float wallHoldVelocity;

    private InputSystem input;

    public PlayerSystem(float jumpHeight, float speed, float maxSpeed, float jumpMaxTime, float pixelsPerMeter, float fallAmplifier, float wallHoldVelocity, InputSystem inputSystem) {
        super(Family.all(PlayerComponent.class).get(), Constants.PLAYER_PRIORITY);
        this.jumpHeight = jumpHeight;
        this.speed = speed;
        this.maxSpeed = maxSpeed;
        this.jumpMaxTime = jumpMaxTime;
        this.pixelsPerMeter = pixelsPerMeter;
        this.fallAmplifier = fallAmplifier;
        this.wallHoldVelocity = wallHoldVelocity;
        this.input = inputSystem;
        this.jumpStartTime = 0;
        this.wallJumpStartTime = 0;
    }

    public PlayerSystem() {
        this(Constants.DEFAULT_JUMP_HEIGHT, Constants.DEFAULT_PLAYER_SPEED, Constants.DEFAULT_MAX_SPEED, 0.15f, Constants.DEFAULT_PPM, Constants.DEFAULT_FALL_AMPLIFIER, Constants.DEFAULT_WALL_HOLD_VELOCITY, new SimpleInputSystem());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Body body = Mappers.physics.get(entity).body;
        ColliderComponent collider = Mappers.collider.get(entity);

        updateFlags(collider);

        updateJumping(body, deltaTime);
        updateWallJumping(collider, body);

        updateStrafing(body, deltaTime);

        applyFriction(body);
        updateFallVelocity(body);

        clampSpeed(body);

        if(Gdx.input.isKeyJustPressed(Keys.R)) {
            body.setTransform(500 / pixelsPerMeter, 500 / pixelsPerMeter, 0);
            body.setLinearVelocity(0, 0);
        }
    }

    /**
     * Updates the flags of the player
     * @param collider - The {@link ColliderComponent} instance of the {@link Entity}
     */
    private void updateFlags(ColliderComponent collider) {
        onGround = collider.downTouches > 0;
        holdingWall = collider.rightTouches > 0 || collider.leftTouches > 0;
    }



    //region Input && Movement
    /**
     * Detects jumps and updates jump status
     * @param body - The player's {@link Body}
     * @param deltaTime The delta time since the last frame
     */
    private void updateJumping(Body body, float deltaTime) {
        if(input.isJumping()) {
            if(onGround && !jumping) {
                startJump(body);
            } else if(jumping) {
                continueJump(body, deltaTime);
            }
        } else {
            if(jumping) {
                endJump(body, true);
            }
        }
    }

    /**
     * Detects wall jumps and updates wall jump status
     * @param collider - The player's {@link ColliderComponent}
     * @param body - The player's {@link Body}
     */
    private void updateWallJumping(ColliderComponent collider, Body body) {
        if(input.isJustJumping() && !jumping && holdingWall) {
            int side = collider.rightTouches > 0 ? -1 : 1;
            if(input.getHorizontal() != 0) {
                wallJumpStartTime = TimeUtils.nanoTime(); // Block arrow use for x time
            }
            body.setLinearVelocity(5 * side, 10);
        }
    }

    /**
     * Detects strafing input and updates movement
     * @param body - The player's {@link Body}
     * @param deltaTime - The delta time since the last frame
     */
    private void updateStrafing(Body body, float deltaTime) {
        if(TimeUtils.timeSinceNanos(wallJumpStartTime) / 1000000000f > 0.2f) {
            int horiz = input.getHorizontal();
            if(horiz != 0) {
                if(horiz != Math.signum(body.getLinearVelocity().x)) {
                    body.applyLinearImpulse(new Vector2(horiz * speed * deltaTime * Constants.CHANGE_DIRECTION_MULTIPLIER, 0), body.getWorldCenter(), true);
                } else {
                    body.applyLinearImpulse(new Vector2(horiz * speed * deltaTime, 0), body.getWorldCenter(), true);
                }
            }
        }
    }
    //endregion

    //region Velocity tweaking
    /**
     * Applies friction to the player's character when on ground and not moving
     * @param body - The player's {@link Body}
     */
    private void applyFriction(Body body) {
        if(onGround && input.getHorizontal() == 0) {
            body.setLinearVelocity(body.getLinearVelocity().x * Constants.FRICTION_MULTIPLIER, body.getLinearVelocity().y);
        }
    }

    /**
     * Updates the player's y velocity:
     * if the player is air-born and isn't jumping: stronger gravity
     * if the player is holding a wall: softer gravity
     * @param body - The player's {@link Body}
     */
    private void updateFallVelocity(Body body) {
        if(!onGround && !jumping) {
            body.applyLinearImpulse(new Vector2(0, fallAmplifier), body.getWorldCenter(), true);
        }

        // Slow down fall when holding wall
        if(holdingWall) {
            if(body.getLinearVelocity().y < wallHoldVelocity) {
                body.setLinearVelocity(body.getLinearVelocity().x, wallHoldVelocity);
            }
        }
    }

    /**
     * Clamps the speed of the player to {@link PlayerSystem#maxSpeed}
     * @param body - The player's {@link Body}
     */
    private void clampSpeed(Body body) {
        float newVx = body.getLinearVelocity().x;
        float newVy = body.getLinearVelocity().y;

        if(Math.abs(newVx) > maxSpeed)
        {
            newVx = maxSpeed * Math.signum(newVx);
        }
        if(Math.abs(newVy) > maxSpeed)
        {
            newVy = maxSpeed * Math.signum(newVy);
        }

        body.setLinearVelocity(newVx, newVy);
    }
    //endregion

    //region Jumping
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
    //endregion

    /////////////////////////// Utility //////////////////////////////

    private void _DEBUG(String toDebug) {
        if(DEBUG) {
            Gdx.app.log("Player", toDebug);
        }
    }
}
