package com.redsponge.platformer.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.redsponge.platformer.components.BodyType;
import com.redsponge.platformer.components.Mappers;
import com.redsponge.platformer.components.PhysicsComponent;
import com.redsponge.platformer.components.PositionComponent;
import com.redsponge.platformer.components.SizeComponent;
import com.redsponge.platformer.components.VelocityComponent;
import com.redsponge.platformer.constants.Constants;

public class CollisionSystem extends IteratingSystem {

    public CollisionSystem() {
        super(Family.all(PositionComponent.class, SizeComponent.class, PhysicsComponent.class).get(), Constants.COLLISION_PRIORITY);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PhysicsComponent physics = Mappers.physics.get(entity);
        PositionComponent position = Mappers.position.get(entity);
        SizeComponent size = Mappers.size.get(entity);
        VelocityComponent velocity = Mappers.velocity.get(entity);

        if(physics.type == BodyType.STATIC) return;
        for(int i = 0; i < getEntities().size(); i++) {
            Entity e = getEntities().get(i);
            if(e != entity) {
                PhysicsComponent checkPhysics = Mappers.physics.get(e);

                // If falling
                if(velocity.y < 0) {
                    if(physics.down.overlaps(checkPhysics.rectangle)) {
                        velocity.y = 0;
                        position.y = checkPhysics.up.y+1;
                        physics.touchingDown = true;
                    }
                }


            }
        }
    }
}
