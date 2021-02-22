package com.mygdx.project_four;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.physics.box2d.Box2D;

public class ProjectFour extends Game {

	private final AssetManager assetManager = new AssetManager();
	/*
	Returns the asset manager to store and retrieve textures
	 */
	AssetManager getAssetManager() { return assetManager; }

	@Override
	public void create () {
		//Initialize the Box2D engine
		Box2D.init();
		//Tells us that if we pass in a tmx file it should go into the Tiled Class
		assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
		setScreen(new LoadingScreen(this));
	}
}
