package com.redsponge.platformer.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class PositionComponent implements Component {

    public float x = 0;
    public float y = 0;

    public PositionComponent(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public PositionComponent() {}

    public PositionComponent(Vector2 pos) {
        this(pos.x, pos.y);
    }
}
