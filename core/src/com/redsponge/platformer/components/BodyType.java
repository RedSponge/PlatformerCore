package com.redsponge.platformer.components;

/**
 * How the body is affected by forces?
 *
 * Static - No effect
 * Kinematic - Only code-inflicted
 * Dynamic - Gravity and code-inflicted
 */
public enum BodyType {
    STATIC,
    DYNAMIC,
    KINEMATIC
}
