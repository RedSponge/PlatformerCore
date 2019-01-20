package com.redsponge.platformer.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.redsponge.platformer.components.Mappers;
import com.redsponge.platformer.components.PositionComponent;
import com.redsponge.platformer.components.SizeComponent;
import com.redsponge.platformer.constants.Constants;

public class RenderingSystem extends SortedIteratingSystem {

    private ShapeRenderer shapeRenderer;
    private Entity player;
    private Viewport viewport;

    public RenderingSystem(ShapeRenderer shapeRenderer, Viewport viewport, Entity player) {
        super(Family.all(PositionComponent.class, SizeComponent.class).get(), new ZComparator(), Constants.RENDERING_PRIORITY);
        this.shapeRenderer = shapeRenderer;
        this.player = player;
        this.viewport = viewport;
    }


    @Override
    public void update(float deltaTime) {

        PositionComponent position = Mappers.position.get(player);
        viewport.getCamera().position.lerp(new Vector3(position.x, position.y, 0), 1);
        viewport.apply();

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

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

    public void setPlayer(Entity player) {
        this.player = player;
    }
}
