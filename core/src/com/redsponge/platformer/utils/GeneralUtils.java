package com.redsponge.platformer.utils;

import com.badlogic.gdx.utils.TimeUtils;

public class GeneralUtils {

    public static float secondsSince(long nanos) {
        return TimeUtils.timeSinceNanos(nanos) / 1000000000f;
    }

}
