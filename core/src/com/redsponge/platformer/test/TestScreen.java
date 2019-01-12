package com.redsponge.platformer.test;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.redsponge.platformer.components.BodyType;
import com.redsponge.platformer.components.Mappers;
import com.redsponge.platformer.components.PhysicsComponent;
import com.redsponge.platformer.components.PositionComponent;
import com.redsponge.platformer.components.SizeComponent;
import com.redsponge.platformer.components.VelocityComponent;
import com.redsponge.platformer.systems.CollisionSystem;
import com.redsponge.platformer.systems.PhysicsSystem;

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
        this.gameViewport = new FitViewport(312, 256);
        this.entityEngine = new Engine();
        this.test = new Entity();
        this.test.add(new PositionComponent(200, 200));
        this.test.add(new VelocityComponent(0, -10));
        this.test.add(new SizeComponent(20, 20));
        this.test.add(new PhysicsComponent(BodyType.DYNAMIC));

        Entity platform = new Entity();
        platform.add(new PositionComponent(0, 0));
        platform.add(new SizeComponent(300, 100));
        platform.add(new VelocityComponent(0, 0));
        platform.add(new PhysicsComponent(BodyType.STATIC));

        entityEngine.addEntity(test);
        entityEngine.addEntity(platform);

        entityEngine.addSystem(new PhysicsSystem(new Vector2(0, -5)));
        entityEngine.addSystem(new CollisionSystem());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        VelocityComponent velocityComponent = Mappers.velocity.get(test);
        PhysicsComponent physicsComponent = Mappers.physics.get(test);
        if(Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            velocityComponent.y = 200;
            physicsComponent.touchingDown = false;
        }
        if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
            velocityComponent.x += 10;
        }
        if(Gdx.input.isKeyPressed(Keys.LEFT)) {
            velocityComponent.x += -10;
        }

        entityEngine.update(delta);



        batch.setProjectionMatrix(gameViewport.getCamera().combined);
        shapeRenderer.setProjectionMatrix(gameViewport.getCamera().combined);

        shapeRenderer.begin(ShapeType.Filled);

        shapeRenderer.setColor(Color.LIGHT_GRAY);
        shapeRenderer.rect(0, 0, 300, 100);

        shapeRenderer.setColor(Color.GRAY);
        PositionComponent pos = test.getComponent(PositionComponent.class);
        shapeRenderer.rect(pos.x, pos.y, 20, 20);
        shapeRenderer.end();

    }




    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height, true);
    }
}
