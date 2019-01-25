package com.redsponge.platformer.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.redsponge.platformer.components.Mappers;
import com.redsponge.platformer.components.PositionComponent;
import com.redsponge.platformer.constants.Constants;

public class MapRenderSystem extends EntitySystem {

    private SpriteBatch batch;
    private Viewport viewport;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    private float x, y;

    public MapRenderSystem(SpriteBatch batch, Viewport viewport, TiledMap map) {
        super(Constants.MAP_RENDERING_PRIORITIES);
        this.batch = batch;
        this.viewport = viewport;
        this.map = map;
        this.mapRenderer = new OrthogonalTiledMapRenderer(this.map, this.batch);

        x = 0;
        y = 0;

    }

    @Override
    public void update(float deltaTime) {
        if(Gdx.input.isKeyPressed(Keys.UP)) {
            y++;
        }
        if(Gdx.input.isKeyPressed(Keys.DOWN)) {
            y--;
        }
        if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
            x++;
        }
        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            x--;
        }

        viewport.getCamera().position.set(x, y, 0);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        mapRenderer.setView((OrthographicCamera) viewport.getCamera());
        mapRenderer.render(new int[] {0});
    }

    @Override
    public void removedFromEngine(Engine engine) {
        mapRenderer.dispose();
    }
}
