/*
FlappyBee.java by Sebastian Grygorczuk
March 2020
sgrygorczuk@gmail.com

This Project Covers the LibGDX Game Development By Example Chapter 3 and 4 of constructing a
basic flappy bird
 */

package com.packt.flappybee;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;

public class FlappyBeeGame extends Game {

	private final AssetManager assetManager = new AssetManager();

	AssetManager getAssetManager() { return assetManager; }

	@Override
	public void create () {
		//Calls game screen
		setScreen(new StartScreen(this));
	}
}
