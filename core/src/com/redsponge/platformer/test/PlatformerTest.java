package com.redsponge.platformer.test;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.redsponge.platformer.PlatformerCore;

public class PlatformerTest implements Disposable {

    private SpriteBatch batch;
    private ShapeRenderer renderer;
    private PlatformerCore game;

    public PlatformerTest(PlatformerCore game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.renderer = new ShapeRenderer();
        this.game.setScreen(new TestScreen(batch, renderer));
    }

    @Override
    public void dispose() {
        this.batch.dispose();
        this.renderer.dispose();
    }
}
