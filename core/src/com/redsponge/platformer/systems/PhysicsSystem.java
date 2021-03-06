package com.redsponge.platformer.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Array.ArrayIterator;
import com.redsponge.platformer.components.ChainComponent;
import com.redsponge.platformer.components.ColliderComponent;
import com.redsponge.platformer.components.Mappers;
import com.redsponge.platformer.components.PhysicsComponent;
import com.redsponge.platformer.components.PositionComponent;
import com.redsponge.platformer.components.SizeComponent;
import com.redsponge.platformer.components.VelocityComponent;
import com.redsponge.platformer.constants.Constants;
import com.redsponge.platformer.utils.GeneralUtils;
import com.redsponge.platformer.utils.SensorFactory;

import java.util.Iterator;

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
     * Create the platforms for the world
     * @param map - The world map
     */
    public void createWorldObjects(TiledMap map) {
        MapLayer layer = map.getLayers().get("Collidables");

        for (PolylineMapObject obj : new ArrayIterator<PolylineMapObject>(layer.getObjects().getByType(PolylineMapObject.class))) {
            Entity platform = PlatformFactory.createChainFloor(obj.getPolyline().getTransformedVertices());
            this.getEngine().addEntity(platform);
        }
        for (PolygonMapObject obj : new ArrayIterator<PolygonMapObject>(layer.getObjects().getByType(PolygonMapObject.class))) {
            Entity platform = PlatformFactory.createChainFloor(obj.getPolygon().getTransformedVertices());
            this.getEngine().addEntity(platform);
        }
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
        ChainComponent chain = Mappers.chain.get(entity);


        // Body Creation
        BodyDef bdef = new BodyDef();
        bdef.type = physics.type;
        bdef.position.set(pos.x / pixelsPerMeter, pos.y / pixelsPerMeter);

        Body body = world.createBody(bdef);
        body.setUserData(entity);
        physics.body = body;

        FixtureDef collider = new FixtureDef();

        Shape shape = null;
        if(size != null) {
            shape = new PolygonShape();
            ((PolygonShape) shape).setAsBox((size.width / 2 - pixelsPerMeter * 0.01f) / pixelsPerMeter, (size.height / 2 - pixelsPerMeter * 0.01f) / pixelsPerMeter);
        } else if(chain != null){
            Gdx.app.log("PhysicsSystem", "CHAIN!");
            shape = new ChainShape();
            ((ChainShape)shape).createChain(GeneralUtils.divideAll(chain.vertices, pixelsPerMeter));
        } else {
            Gdx.app.error("PhysicsSystem", "Platform Type Isn't Recognized!", new RuntimeException("Error!"));
            return;
        }
        collider.shape = shape;
        collider.friction = 0;

        body.createFixture(collider).setUserData(Constants.BODY_USER_DATA);
        shape.dispose();

        // Sensors Creation
        if(colliderComp == null || size == null) {
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
