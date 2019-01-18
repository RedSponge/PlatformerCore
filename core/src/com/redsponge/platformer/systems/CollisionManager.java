package com.redsponge.platformer.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.redsponge.platformer.components.ColliderComponent;
import com.redsponge.platformer.components.Mappers;
import com.redsponge.platformer.constants.Constants;

public class CollisionManager implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        if(fixA.getUserData().equals(Constants.SENSOR_DATA_ID) || fixB.getUserData().equals(Constants.SENSOR_DATA_ID)) {
            Fixture sensor = (fixA.getUserData().equals(Constants.SENSOR_DATA_ID) ? fixA : fixB);
            Fixture other = (sensor == fixA) ? fixB : fixA;

            handleSensorCollision(sensor, true);
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        if(fixA.getUserData().equals(Constants.SENSOR_DATA_ID) || fixB.getUserData().equals(Constants.SENSOR_DATA_ID)) {
            Fixture sensor = (fixA.getUserData().equals(Constants.SENSOR_DATA_ID) ? fixA : fixB);

            handleSensorCollision(sensor, false);
        }
    }

    public void handleSensorCollision(Fixture sensor, boolean collisionStart) {
        Entity entity = (Entity) sensor.getBody().getUserData();
        ColliderComponent collider = Mappers.collider.get(entity);

        final int adder = collisionStart ? 1 : -1;

        if(sensor == collider.right) {
            Gdx.app.log("Collision", "Right Sensor Collision" + (collisionStart ? "Start" : "End") + "d!");
            collider.rightTouches += adder;
        }
        if(sensor == collider.left) {
            Gdx.app.log("Collision", "Left Sensor Collision " + (collisionStart ? "Start" : "End") + "!");
            collider.leftTouches += adder;
        }
        if(sensor == collider.up) {
            Gdx.app.log("Collision", "Up Sensor Collision " + (collisionStart ? "Start" : "End") + "!");
            collider.upTouches += adder;
        }
        if(sensor == collider.down) {
            Gdx.app.log("Collision", "Down Sensor Collision " + (collisionStart ? "Start" : "End") + "!");
            collider.downTouches += adder;
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
