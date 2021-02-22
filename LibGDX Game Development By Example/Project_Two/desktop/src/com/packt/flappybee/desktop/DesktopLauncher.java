package com.packt.flappybee.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.packt.flappybee.FlappyBeeGame;

public class DesktopLauncher {
	public static void main(String[] arg) throws Exception {
		LwjglApplicationConfiguration config = new
				LwjglApplicationConfiguration();
		config.height = 320;
		config.width = 240;
		//Combines all of the textures into one big texture
		TexturePacker.process("/home/sebastian/Projects/LibGDX/Project_Two/desktop/build/resources/main", "/home/sebastian/Projects/LibGDX/Project_Two/desktop/build/resources/main", "flappy_bee_assets");
		new LwjglApplication(new FlappyBeeGame(), config);
	}
}
