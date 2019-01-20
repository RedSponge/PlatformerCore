package com.redsponge.platformer.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.redsponge.platformer.components.ColliderComponent;
import com.redsponge.platformer.components.Mappers;
import com.redsponge.platformer.components.PhysicsComponent;
import com.redsponge.platformer.components.PositionComponent;
import com.redsponge.platformer.components.SizeComponent;
import com.redsponge.platformer.components.VelocityComponent;
import com.redsponge.platformer.constants.Constants;
import com.redsponge.platformer.utils.SensorFactory;

/**
 * Handles gravity and movement
 */
public class PhysicsSystem extends IteratingSystem implements EntityListener {

    private Vector2 gravity;
    private World world;
    private float pixelsPerMeter;

    public PhysicsSystem(Vector2 gravity, float pixelsPerMeter) {
        super(Family.all(PositionComponent.class, SizeComponent.class, VelocityComponent.class, PhysicsComponent.class).get(), Constants.PHYSICS_PRIORITY);
        this.gravity = gravity;
        this.pixelsPerMeter = pixelsPerMeter;
        this.world = new World(this.gravity, true);
        this.world.setContactListener(new CollisionManager());
    }

    public PhysicsSystem() {
        this(Constants.DEFAULT_GRAVITY, Constants.DEFAULT_PPM);
    }

    @Override
    public void update(float deltaTime) {
        world.step(deltaTime, Constants.PHYSICS_VELOCITY_ITERATIONS, Constants.PHYSICS_POSITION_ITERATIONS);
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent pos = Mappers.position.get(entity);
        PhysicsComponent physics = Mappers.physics.get(entity);

        pos.x = physics.body.getPosition().x * pixelsPerMeter;
        pos.y = physics.body.getPosition().y * pixelsPerMeter;
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

    /**
     * Builds the new entity's body
     * @param entity
     */
    @Override
    public void entityAdded(Entity entity) {
        PhysicsComponent physics = Mappers.physics.get(entity);
        SizeComponent size = Mappers.size.get(entity);
        PositionComponent pos = Mappers.position.get(entity);
        ColliderComponent colliderComp = Mappers.collider.get(entity);


        // Body Creation
        BodyDef bdef = new BodyDef();
        bdef.type = physics.type;
        bdef.position.set(pos.x / pixelsPerMeter, pos.y / pixelsPerMeter);

        Body body = world.createBody(bdef);
        body.setUserData(entity);
        physics.body = body;

        FixtureDef collider = new FixtureDef();

        PolygonShape shape = new PolygonShape();
        shape.setAsBox((size.width / 2) / pixelsPerMeter, (size.height / 2) / pixelsPerMeter);
        collider.shape = shape;
        collider.friction = 0;

        body.createFixture(collider).setUserData(Constants.BODY_USER_DATA);
        shape.dispose();

        // Sensors Creation
        if(colliderComp == null) {
            return;
        }

        colliderComp.down = SensorFactory.createCollideFixture(physics.body, size.width, size.height, new Vector2(0, -size.height / 2), false, pixelsPerMeter);
        colliderComp.up = SensorFactory.createCollideFixture(physics.body, size.width, size.height, new Vector2(0, size.height / 2), false, pixelsPerMeter);
        colliderComp.left = SensorFactory.createCollideFixture(physics.body, size.width, size.height, new Vector2(-size.width / 2, 0), true, pixelsPerMeter);
        colliderComp.right = SensorFactory.createCollideFixture(physics.body, size.width, size.height, new Vector2(size.width / 2, 0), true, pixelsPerMeter);
    }

    @Override
    public void entityRemoved(Entity entity) {
        world.destroyBody(Mappers.physics.get(entity).body);
    }

    public World getWorld() {
        return this.world;
    }
}
