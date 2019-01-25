package com.redsponge.platformer.test;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.redsponge.platformer.components.ColliderComponent;
import com.redsponge.platformer.components.PhysicsComponent;
import com.redsponge.platformer.components.PlayerComponent;
import com.redsponge.platformer.components.PositionComponent;
import com.redsponge.platformer.components.SizeComponent;
import com.redsponge.platformer.components.VelocityComponent;
import com.redsponge.platformer.constants.Constants;
import com.redsponge.platformer.systems.MapRenderSystem;
import com.redsponge.platformer.systems.PhysicsDebugSystem;
import com.redsponge.platformer.systems.PhysicsSystem;
import com.redsponge.platformer.systems.PlayerSystem;
import com.redsponge.platformer.systems.RenderingSystem;

public class TestScreen extends ScreenAdapter {

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    private FitViewport gameViewport;
    private Engine entityEngine;

    private Entity test;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    public TestScreen(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        this.batch = batch;
        this.shapeRenderer = shapeRenderer;
    }

    @Override
    public void show() {
        this.gameViewport = new FitViewport(1600, 900);
        this.entityEngine = new Engine();
        this.test = new Entity();
        this.test.add(new PositionComponent(300, 300));
        this.test.add(new VelocityComponent(0, 0));
        this.test.add(new SizeComponent(20, 30));
        this.test.add(new PhysicsComponent(BodyType.DynamicBody));
        this.test.add(new ColliderComponent());
        this.test.add(new PlayerComponent());

        Entity test2 = new Entity();
        test2.add(new PositionComponent(300, 300));
        test2.add(new SizeComponent(20, 10));
        test2.add(new VelocityComponent(0,0));
        test2.add(new PhysicsComponent(BodyType.DynamicBody));

        TmxMapLoader mapLoader = new TmxMapLoader();
        map = mapLoader.load("levels/level00/level00.tmx");

        PhysicsSystem physicsSystem = new PhysicsSystem(new Vector2(0, -10), Constants.DEFAULT_PPM);
        entityEngine.addSystem(physicsSystem);
        entityEngine.addEntityListener(Family.all(SizeComponent.class, PositionComponent.class, PhysicsComponent.class).get(), physicsSystem);
        entityEngine.addSystem(new PlayerSystem());
        entityEngine.addSystem(new PhysicsDebugSystem(physicsSystem.getWorld(), gameViewport));

        entityEngine.addSystem(new RenderingSystem(shapeRenderer, batch, gameViewport, test, map));

        entityEngine.addEntity(test);
        entityEngine.addEntity(test2);
        physicsSystem.createWorldObjects(map);

        shapeRenderer.setAutoShapeType(true);

        mapRenderer = new OrthogonalTiledMapRenderer(map, batch);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(Gdx.input.isKeyPressed(Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        entityEngine.update(delta);
    }

    @Override
    public void dispose() {
        map.dispose();
    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height, true);
    }
}
