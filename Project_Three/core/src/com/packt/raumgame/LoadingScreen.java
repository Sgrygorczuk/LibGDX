package com.packt.raumgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.awt.TextArea;

public class LoadingScreen extends ScreenAdapter {

    private static final float WORLD_WIDTH = 640;
    private static final float WORLD_HEIGHT = 480;

    private Viewport viewport;
    private Camera camera;

    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    private final RaumGame raumGame;

    private static  final float PROGRESS_BAR_WIDTH = 100;
    private static  final float PROGRESS_BAR_HEIGHT = 25;
    private float progress = 0;

    public LoadingScreen(RaumGame raumGame) {
        this.raumGame = raumGame;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void show() {
        showCamera();
        showObjects();
    }

    public void showCamera() {
        camera = new OrthographicCamera();
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2f, 0);
        camera.update();

        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
    }

    public void showObjects(){
        shapeRenderer.setColor(Color.WHITE);

        raumGame.getAssetManager().load("map.tmx", TiledMap.class);
        raumGame.getAssetManager().load("RaumSprite.png", Texture.class);
    }

    @Override
    public void render(float delta) {
        update();
        clearScreen();
        draw();
    }

    public void update() {
        if(raumGame.getAssetManager().update()){ raumGame.setScreen(new GameScreen(raumGame)); }
        else{ progress = raumGame.getAssetManager().getProgress(); }
    }

    public void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public void draw() {
        shapeRenderer.setProjectionMatrix(camera.projection);
        shapeRenderer.setTransformMatrix(camera.view);


        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.rect((WORLD_WIDTH - PROGRESS_BAR_WIDTH)/2, WORLD_HEIGHT/2 - PROGRESS_BAR_HEIGHT/2,
                    progress * PROGRESS_BAR_WIDTH, PROGRESS_BAR_HEIGHT);
        shapeRenderer.end();
    }
}