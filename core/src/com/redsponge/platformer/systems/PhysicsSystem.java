package com.redsponge.platformer.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.redsponge.platformer.components.BodyType;
import com.redsponge.platformer.components.Mappers;
import com.redsponge.platformer.components.PhysicsComponent;
import com.redsponge.platformer.components.PositionComponent;
import com.redsponge.platformer.components.SizeComponent;
import com.redsponge.platformer.components.VelocityComponent;
import com.redsponge.platformer.constants.Constants;

/**
 * Handles gravity and movement
 */
public class PhysicsSystem extends IteratingSystem {

    private Vector2 gravity;

    public PhysicsSystem(Vector2 gravity) {
        super(Family.all(PositionComponent.class, SizeComponent.class, VelocityComponent.class, PhysicsComponent.class).get(), Constants.PHYSICS_PRIORITY);
        this.gravity = gravity;
    }

    public PhysicsSystem() {
        this(Constants.DEFAULT_GRAVITY);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PhysicsComponent physicsComponent = Mappers.physics.get(entity);
        VelocityComponent velocity = Mappers.velocity.get(entity);
        SizeComponent size = Mappers.size.get(entity);
        PositionComponent position = Mappers.position.get(entity);

        // Apply gravity
        if(physicsComponent.type == BodyType.DYNAMIC) {
            velocity.x += gravity.x;
            velocity.y += gravity.y;
        }

        // Apply friction if needed
        if(physicsComponent.touchingDown) {
            velocity.x *= Constants.FRICTION_MULTIPLIER;
        }

        // Apply max bounds
        if(Math.abs(velocity.x) > Constants.MAX_HORIZ_VELOCITY) {
            velocity.x = Constants.MAX_HORIZ_VELOCITY * Math.signum(velocity.x);
        }

        // Apply velocity
        if(physicsComponent.type != BodyType.STATIC) {
            position.x += velocity.x * deltaTime;
            position.y += velocity.y * deltaTime;
        }

        physicsComponent.rectangle.set(position.x, position.y, size.width, size.height);

        physicsComponent.up.set(position.x, position.y+size.height-1, size.width, 1);
        physicsComponent.down.set(position.x, position.y, size.width, 1);

        physicsComponent.right.set(position.x+size.width-1, position.y, 1, size.height);
        physicsComponent.left.set(position.x, position.y, 1, size.height);
    }

    /**
     * Sets the gravity of the engine.
     * @param gravity The new gravity
     */
    public void setGravity(Vector2 gravity) {
        this.gravity = gravity;
    }

    /**
     * Returns the system's gravity
     * @return The gravity
     */
    public Vector2 getGravity() {
        return gravity;
    }
}
