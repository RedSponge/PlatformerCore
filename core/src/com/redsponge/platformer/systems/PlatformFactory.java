package com.redsponge.platformer.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.redsponge.platformer.components.ChainComponent;
import com.redsponge.platformer.components.PhysicsComponent;
import com.redsponge.platformer.components.PositionComponent;
import com.redsponge.platformer.components.SizeComponent;
import com.redsponge.platformer.components.VelocityComponent;

import java.util.Arrays;

public class PlatformFactory {

    public static Entity createRectanglePlatform(float x, float y, float width, float height) {
        Entity e = new Entity();
        e.add(new PositionComponent(x, y));
        e.add(new SizeComponent(width, height));
        e.add(new PhysicsComponent(BodyType.StaticBody));
        e.add(new VelocityComponent());
        return e;
    }

    public static Entity createChainFloor(float[] vertex) {
        assert vertex.length >= 4;

        Entity e = new Entity();
        e.add(new PositionComponent(vertex[0], vertex[1]));
        e.add(new PhysicsComponent(BodyType.StaticBody));
        e.add(new VelocityComponent());
        e.add(new ChainComponent(vertex));

        for(float f : vertex) {
            System.out.print(f + ",");
        }
        System.out.println();

        return e;
    }

}
