package com.redsponge.platformer.input;

import com.redsponge.platformer.systems.PlayerSystem;

/**
 * A class which defines what an input system should include. the {@link PlayerSystem} uses an instance of a class which
 * inherits this one.
 */
public interface InputSystem {

    /**
     * Checks the current state of the horizontal input
     *
     * @return -1 for left, 0 for none or both, 1 for right
     */
    int getHorizontal();

    /**
     * Checks the current state of the vertical input
     *
     * @return -1 for down, 0 for none or both, 1 for up
     */
    int getVertical();

    /**
     * Checks if the jump button is held down
     * @return is the jump button held down?
     */
    boolean isJumping();

    /**
     * Checks if the jump button was just pressed down last tick
     * @return was the jump button pressed down last tick?
     */
    boolean isJustJumping();
}
