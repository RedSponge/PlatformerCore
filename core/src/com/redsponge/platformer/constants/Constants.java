package com.redsponge.platformer.constants;

import com.badlogic.gdx.math.Vector2;
import com.redsponge.platformer.systems.PhysicsSystem;

public class Constants {

    /**
     * Default gravity for {@link PhysicsSystem}
     */
    public static final Vector2 DEFAULT_GRAVITY = new Vector2(0, -10);

    public static final int PHYSICS_PRIORITY = 1;
    public static final int COLLISION_PRIORITY = 2;
    public static final float FRICTION_MULTIPLIER = 0.95f;

    public static final float MAX_HORIZ_VELOCITY = 200;
    public static final float MAX_VERT_VELOCITY = 200;

}
