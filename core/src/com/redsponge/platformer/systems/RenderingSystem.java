package com.redsponge.platformer.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.redsponge.platformer.components.Mappers;
import com.redsponge.platformer.components.PositionComponent;
import com.redsponge.platformer.components.SizeComponent;
import com.redsponge.platformer.constants.Constants;

public class RenderingSystem extends SortedIteratingSystem {

    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;

    private Entity player;
    private Viewport viewport;

    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    private int[] background;
    private int[] foreground;

    public RenderingSystem(ShapeRenderer shapeRenderer, SpriteBatch batch, Viewport viewport, Entity player, TiledMap map) {
        super(Family.all(PositionComponent.class, SizeComponent.class).get(), new ZComparator(), Constants.RENDERING_PRIORITY);
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.player = player;
        this.viewport = viewport;
        this.map = map;
        this.mapRenderer = new OrthogonalTiledMapRenderer(map, this.batch);
    }


    @Override
    public void update(float deltaTime) {
        PositionComponent pos = Mappers.position.get(player);

        viewport.getCamera().position.set(pos.x, pos.y, 0);
        viewport.apply();
        mapRenderer.setView((OrthographicCamera) viewport.getCamera());

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        batch.setProjectionMatrix(viewport.getCamera().combined);

        mapRenderer.render();

        shapeRenderer.begin(ShapeType.Filled);
        super.update(deltaTime);
        shapeRenderer.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent pos = Mappers.position.get(entity);
        SizeComponent size = Mappers.size.get(entity);

        shapeRenderer.setColor(Color.GRAY);
        shapeRenderer.rect(pos.x-size.width/2, pos.y-size.height/2, size.width, size.height);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        mapRenderer.dispose();
    }

    public void setPlayer(Entity player) {
        this.player = player;
    }
}
