package com.redsponge.platformer.components;

import com.badlogic.ashley.core.Component;

public class ChainComponent implements Component {

    public float[] vertices;

    public ChainComponent(float[] vertices) {
        this.vertices = vertices;
    }

    public ChainComponent() {}
}
