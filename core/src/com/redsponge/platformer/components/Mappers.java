package com.redsponge.platformer.components;

import com.badlogic.ashley.core.ComponentMapper;

import java.lang.ref.PhantomReference;

public class Mappers {

    public static final ComponentMapper<PositionComponent> position = ComponentMapper.getFor(PositionComponent.class);
    public static final ComponentMapper<VelocityComponent> velocity = ComponentMapper.getFor(VelocityComponent.class);
    public static final ComponentMapper<SizeComponent> size = ComponentMapper.getFor(SizeComponent.class);
    public static final ComponentMapper<PhysicsComponent> physics = ComponentMapper.getFor(PhysicsComponent.class);
    public static final ComponentMapper<ColliderComponent> collider = ComponentMapper.getFor(ColliderComponent.class);
    public static final ComponentMapper<ChainComponent> chain = ComponentMapper.getFor(ChainComponent.class);
}
