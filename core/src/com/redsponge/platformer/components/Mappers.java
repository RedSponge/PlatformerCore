package com.redsponge.platformer.components;

import com.badlogic.ashley.core.ComponentMapper;

public class Mappers {

    public static final ComponentMapper<PositionComponent> position = ComponentMapper.getFor(PositionComponent.class);
    public static final ComponentMapper<VelocityComponent> velocity = ComponentMapper.getFor(VelocityComponent.class);
    public static final ComponentMapper<SizeComponent> size = ComponentMapper.getFor(SizeComponent.class);
    public static final ComponentMapper<PhysicsComponent> physics = ComponentMapper.getFor(PhysicsComponent.class);

}
