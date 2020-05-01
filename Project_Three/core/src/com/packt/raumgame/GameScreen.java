package com.packt.raumgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen extends ScreenAdapter {

    //Screen dimension
    private static final float WORLD_WIDTH = 640;
    private static final float WORLD_HEIGHT = 480;

    //Image rendering objects
    private ShapeRenderer shapeRenderer;
    private Viewport viewport;
    private Camera camera;
    private SpriteBatch batch;

    //Tiled objects
    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
    private TiledMap tiledMap;
    private CollisionCell collisionCell;

    //Costumed Objects
    private Raum raum;
    private Array<Skull> skulls = new Array<>();

    /*
    Input: RaumGame
    Output: Void
    Purpose: Gets the assetManger from the start of the program
    */
    private final RaumGame raumGame;
    GameScreen(RaumGame raumGame) { this.raumGame = raumGame; }

    /*
    Input: Dimensions
    Output: Void
    Purpose: Resize the screen with changing window
    */
    @Override
    public void resize(int width, int height) { viewport.update(width, height); }

    /*
    Input: Void
    Output: Void
    Purpose: Set up camera and object that will be used in drawing
    */
    @Override
    public void show() {
        //Camera
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply(true);

        //Draws objects
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        //Background
        tiledMap = raumGame.getAssetManager().get("RuamPlatformer.tmx");
        orthogonalTiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, batch);
        orthogonalTiledMapRenderer.setView((OrthographicCamera) camera);
        //Player
        raum = new Raum((Texture) raumGame.getAssetManager().get("RaumSprite.png"));
        //Collectible
        populateSkulls();
    }

    /*
    Input: Delta timing
    Output: Void
    Purpose: Central function that draws everything
    */
    @Override
    public void render(float delta) {
        update(delta);
        clearScreen();
        draw();
        drawDebug();
    }

    /*
    Input: Delta, timing
    Output: Void
    Purpose: Central function that updates everything
    */
    private void update(float delta) {
        raum.update(delta);         //Update raums position
        stopPeteLeavingTheScreen(); //Makes sure raum doesn't leaves the screen
        collisionCell.handleRaumCollision(raum, tiledMap);      //Check if raum has collided with any of the platforms
    }

    /*
    Input: Void
    Output: Void
    Purpose: Keeps raum on screen
    */
    private void stopPeteLeavingTheScreen() {
        //Makes sure he doesn't end up below the screen
        if (raum.getY() < 0) {
            raum.setPosition(raum.getX(), 0);
            raum.landed();
        }
        //Make sure he can't leave from the left
        if (raum.getX() < 0) { raum.setPosition(0, raum.getY()); }
        //Makes sure he can't leave from the right
        if (raum.getX() + Raum.WIDTH > WORLD_WIDTH) { raum.setPosition(WORLD_WIDTH - Raum.WIDTH, raum.getY()); }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Goes through the tiledMap and check for all of the skulls that exist on it, if found add
            to the array
    */
    private void populateSkulls() {
        //Looks for all objects on the map
        MapLayer mapLayer = tiledMap.getLayers().get("Collectibles");
        //For each object found on the map creates a skull object and adds it to the array
        for (MapObject mapObject : mapLayer.getObjects()) {
            skulls.add(
                    new Skull(raumGame.getAssetManager().get("Skull.png", Texture.class),
                            mapObject.getProperties().get("x", Float.class),
                            mapObject.getProperties().get("y", Float.class)));
        }
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(Color.TEAL.r, Color.TEAL.g, Color.TEAL.b, Color.TEAL.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
    private void draw() {
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);
        orthogonalTiledMapRenderer.render();

        batch.begin();
        for (Skull skull : skulls) { skull.draw(batch); }
        raum.draw(batch);
        batch.end();
    }
    private void drawDebug() {
        shapeRenderer.setProjectionMatrix(camera.projection);
        shapeRenderer.setTransformMatrix(camera.view);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        //raum.drawDebug(shapeRenderer);
        shapeRenderer.end();
    }
}
