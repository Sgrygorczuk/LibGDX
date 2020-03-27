package com.packt.flappybee.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.packt.flappybee.FlappyBeeGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height = 320;
		config.width = 240;
		new LwjglApplication(new FlappyBeeGame(), config);
	}
}
