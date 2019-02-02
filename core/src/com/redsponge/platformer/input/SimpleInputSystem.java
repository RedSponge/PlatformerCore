package com.redsponge.platformer.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

/**
 * A simple example which inherits from {@link InputSystem}
 */
public class SimpleInputSystem implements InputSystem {

    private final int rightButton;
    private final int leftButton;
    private final int upButton;
    private final int downButton;
    private final int jumpButton;

    public SimpleInputSystem(int rightButton, int leftButton, int upButton, int downButton, int jumpButton) {
        this.rightButton = rightButton;
        this.leftButton = leftButton;
        this.upButton = upButton;
        this.downButton = downButton;
        this.jumpButton = jumpButton;
    }

    public SimpleInputSystem() {
        this(Keys.RIGHT, Keys.LEFT, Keys.UP, Keys.DOWN, Keys.SPACE);
    }

    @Override
    public int getHorizontal() {
        int right = Gdx.input.isKeyPressed(rightButton) ? 1 : 0;
        int left = Gdx.input.isKeyPressed(leftButton) ? -1 : 0;
        return right + left;
    }

    @Override
    public int getVertical() {
        int up = Gdx.input.isKeyPressed(upButton) ? 1 : 0;
        int down = Gdx.input.isKeyPressed(downButton) ? -1 : 0;
        return up + down;
    }

    @Override
    public boolean isJumping() {
        return Gdx.input.isKeyPressed(jumpButton);
    }

    @Override
    public boolean isJustJumping() {
        return Gdx.input.isKeyJustPressed(jumpButton);
    }
}
