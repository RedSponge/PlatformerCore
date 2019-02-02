package com.redsponge.platformer.comparators;

import com.badlogic.ashley.core.Entity;
import com.redsponge.platformer.components.Mappers;

import java.util.Comparator;

public class ZComparator implements Comparator<Entity> {

    @Override
    public int compare(Entity o1, Entity o2) {
        return (int) Math.signum(Mappers.position.get(o1).z - Mappers.position.get(o2).z);
    }
}
