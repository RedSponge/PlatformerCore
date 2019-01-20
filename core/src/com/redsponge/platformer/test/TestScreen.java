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

    public TestScreen(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        this.batch = batch;
        this.shapeRenderer = shapeRenderer;
    }

    @Override
    public void show() {
        this.gameViewport = new FitViewport(1600, 900);
        this.entityEngine = new Engine();
        this.test = new Entity();
        this.test.add(new PositionComponent(0, 0));
        this.test.add(new VelocityComponent(0, -10));
        this.test.add(new SizeComponent(20, 30));
        this.test.add(new PhysicsComponent(BodyType.DynamicBody));
        this.test.add(new ColliderComponent());
        this.test.add(new PlayerComponent());

        PhysicsSystem physicsSystem = new PhysicsSystem(new Vector2(0, -10), Constants.DEFAULT_PPM);
        entityEngine.addSystem(physicsSystem);
        entityEngine.addEntityListener(Family.all(SizeComponent.class, PositionComponent.class, PhysicsComponent.class).get(), physicsSystem);
        entityEngine.addSystem(new PlayerSystem());
        entityEngine.addSystem(new PhysicsDebugSystem(physicsSystem.getWorld(), gameViewport));
        entityEngine.addSystem(new RenderingSystem(shapeRenderer, gameViewport, test));

        entityEngine.addEntity(test);
        entityEngine.addEntity(PlatformFactory.createPlatform(0, 0,300, 50));
        entityEngine.addEntity(PlatformFactory.createPlatform(350, 60,300, 50));
        entityEngine.addEntity(PlatformFactory.createPlatform(0, 150,300, 50));
        entityEngine.addEntity(PlatformFactory.createPlatform(0, 0,300, 50));

        shapeRenderer.setAutoShapeType(true);
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
    public void resize(int width, int height) {
        gameViewport.update(width, height);
    }
}
