package com.mygdx.project_four;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class LoadingScreen extends ScreenAdapter {

    //Sets up the visual
    private static final float WORLD_WIDTH = 640;
    private static final float WORLD_HEIGHT = 480;

    private Viewport viewport; //Sets up the way the screen is stretched
    private Camera camera;     //Sets at where the camera is looking

    //Used to create lines
    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    //Keeps track of all the data and game wrapper
    private final ProjectFour projectFour;

    //Keeps track of the progress bar
    private static  final float PROGRESS_BAR_WIDTH = 100;
    private static  final float PROGRESS_BAR_HEIGHT = 25;
    private float progress = 0;

    /*
    Input: RaumGame
    Output: Void
    Purpose: Loads this screen and keeps the wrapper data
    */
    public LoadingScreen(ProjectFour projectFour) {
        this.projectFour = projectFour;
    }


    /*
    Input: width, height
    Output: Void
    Purpose: Updates the size of the screen used
    */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Set up all the objects that are using/Load assets to asset manager
    */
    @Override
    public void show() {
        showCamera();       //Set up camera
        showObjects();      //Sets up render and loads stuff into asset manager
    }

    /*
    Input: Void
    Output: Void
    Purpose: Used to set up the camera
    */
    public void showCamera() {
        camera = new OrthographicCamera();
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2f, 0);
        camera.update();

        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
    }

    /*
    Input: Void
    Output: Void
    Purpose: USed to set up progress bar and load the asset manager
    */
    public void showObjects(){
        shapeRenderer.setColor(Color.WHITE);

        projectFour.getAssetManager().load("map.tmx", TiledMap.class);            //Loads the map
        projectFour.getAssetManager().load("RaumSprite.png", Texture.class);      //Loads Raum
        projectFour.getAssetManager().load("Skull.png", Texture.class);
        projectFour.getAssetManager().load("Block.png", Texture.class);

        //Loads Music and SFX
        projectFour.getAssetManager().load("PunchOne.wav", Sound.class);
        projectFour.getAssetManager().load("Laser.wav", Sound.class);
        projectFour.getAssetManager().load("VsMusic.wav", Music.class);
    }

    /*
    Input: Delta
    Output: Void
    Purpose: Main function from which everything runs
    */
    @Override
    public void render(float delta) {
        update();           //Updates the loading process
        clearScreen();      //Clears screen
        draw();             //Draws the progress bar
    }

    /*
    Input: Void
    Output: Void
    Purpose: Updates the state of loading and sends us to main screen when loading is done
    */
    public void update() {
        if(projectFour.getAssetManager().update()){ projectFour.setScreen(new GameScreen(projectFour)); }
        else{ progress = projectFour.getAssetManager().getProgress(); }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Clears the screen black
    */
    public void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }


    /*
    Input: Void
    Output: Void
    Purpose: Draws the progress bar
    */
    public void draw() {
        shapeRenderer.setProjectionMatrix(camera.projection);
        shapeRenderer.setTransformMatrix(camera.view);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect((WORLD_WIDTH - PROGRESS_BAR_WIDTH)/2, WORLD_HEIGHT/2 - PROGRESS_BAR_HEIGHT/2,
                progress * PROGRESS_BAR_WIDTH, PROGRESS_BAR_HEIGHT);
        shapeRenderer.end();
    }
}
