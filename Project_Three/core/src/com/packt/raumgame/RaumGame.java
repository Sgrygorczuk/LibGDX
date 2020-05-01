package com.packt.raumgame;

/*

 */

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class RaumGame extends Game {

	private final AssetManager assetManager = new AssetManager();

	/*
	Returns the asset manager to store and retrieve textures
	 */
	AssetManager getAssetManager() { return assetManager; }

	@Override
	public void create () {
		//Calls game screen
		assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
		setScreen(new LoadingScreen(this));
	}
}

