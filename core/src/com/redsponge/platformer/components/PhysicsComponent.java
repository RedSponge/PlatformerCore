package com.redsponge.platformer.components;


import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;

/**
 * Must be added in order for the body to be in physics and collision calculations
 */
public class PhysicsComponent implements Component {

    /**
     * The type of the body
     */
    public BodyType type = BodyType.DYNAMIC;
    public Rectangle rectangle = new Rectangle();

    /* Collision Checkers*/
    public Rectangle left = new Rectangle();
    public Rectangle right = new Rectangle();
    public Rectangle up = new Rectangle();
    public Rectangle down = new Rectangle();

    /* Touching flags */
    public boolean touchingDown;
    public boolean touchingUp;
    public boolean touchingLeft;
    public boolean touchingRight;

    public PhysicsComponent(BodyType type) {
        this.type = type;
    }

    public PhysicsComponent() {}
}
