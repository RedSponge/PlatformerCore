package com.redsponge.platformer;

import com.badlogic.gdx.Game;
import com.redsponge.platformer.test.PlatformerTest;

public class PlatformerCore extends Game {

	private PlatformerTest test;

	@Override
	public void create () {
		test = new PlatformerTest(this);
	}
	
	@Override
	public void dispose () {
		test.dispose();
	}
}
