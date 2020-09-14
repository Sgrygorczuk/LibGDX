package com.packt.raumgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
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
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.Iterator;

public class GameScreen extends ScreenAdapter {

    //Set up screen dimensions of the screen
    private static final float WORLD_WIDTH = 640;
    private static final float WORLD_HEIGHT = 480;

    //Set up what we look through
    private Viewport viewport;
    private Camera camera;

    //Set up items used for drawing
    private SpriteBatch batch = new SpriteBatch();
    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    //The wrapper we use to keep track of things
    private final RaumGame raumGame;

    //Objects we keep track of
    private Raum raum;                           //Player
    private CollisionCell collisionCell;         //All the blocks
    private Array<Skull> skulls = new Array<>(); //Collectibles

    //Music and SFX
    private Music music;
    private Sound laserSound;

    //Tiled
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
    private float levelWidth;

    /*
    Input: RaumGame
    Output: Void
    Purpose: Gets the wrapper data
    */
    public GameScreen(RaumGame raumGame){this.raumGame = raumGame;}

    /*
    Input: width, height
    Output: Void
    Purpose: Resizes the game to fit the screen
    */
    @Override
    public void resize(int width, int height){viewport.update(width, height);}

    /*
    Input: Void
    Output: Void
    Purpose: Sets up all the items before the screen starts
    */
    @Override
    public void show(){
        showCamera();       //Set up camera
        showObjects();      //Set up everything else
    }

    /*
    Input: Void
    Output: Void
    Purpose: Used to set up the camera
    */
    public void showCamera(){
        camera = new OrthographicCamera();
        camera.position.set(WORLD_WIDTH/2, WORLD_HEIGHT/2f, 0);
        camera.update();

        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply(true);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets up the map, player, collectibles and sounds
    */
    public void showObjects(){
        //Gets the map
        tiledMap = raumGame.getAssetManager().get("map.tmx");
        //Makes it into a drawing that we can call
        orthogonalTiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, batch);
        //Center the drawing based on the camera
        orthogonalTiledMapRenderer.setView((OrthographicCamera) camera);

        //Creates the skull collectibles
        populateSkulls();
        //Sets up all the cells that raum can interact with
        collisionCell = new CollisionCell();

        //Gets the data from tiledmap
        TiledMapTileLayer tiledMapTileLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        levelWidth = tiledMapTileLayer.getWidth() * tiledMapTileLayer.getTileWidth();

        //Creates player character
        raum = new Raum((Texture) raumGame.getAssetManager().get("RaumSprite.png"), raumGame.getAssetManager().get("PunchOne.wav", Sound.class));

        //Sets up the music
        music = raumGame.getAssetManager().get("VsMusic.wav", Music.class);
        music.setLooping(true);
        music.play();

        //Set up sound
        laserSound = raumGame.getAssetManager().get("Laser.wav", Sound.class);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Gets the skull layer from the tiled map and generates skull collectibles from it
    */
    private void populateSkulls(){
        //Grab the skull texture
        Texture skullTexture = new Texture(Gdx.files.internal("Skull.png"));

        //Grab the layer from tiled map
        MapLayer mapLayer = tiledMap.getLayers().get("Skulls");
        //For each instance of that in the layered map create a skull collectible at it's position
        for(MapObject mapObject : mapLayer.getObjects()){
            skulls.add(new Skull(skullTexture,
                    mapObject.getProperties().get("x",Float.class),
                    mapObject.getProperties().get("y",Float.class)));
        }
    }

    /*
    Input: Delta
    Output: Void
    Purpose: Central function from which everything is done from
    */
    @Override
    public void render(float delta){
        update(delta);          //Update all objects
        clearScreen();          //Clear screen
        draw();                 //Draw
        drawDebug();            //Draw wireframe
    }

    /*
    Input: Void
    Output: Void
    Purpose: Central update function, updates player, camera, and collisons
    */
    public void update(float delta){
        //Update raum's action and position
        raum.update(delta, levelWidth);
        //Updates raum's collision with environment
        collisionCell.handleCollision(raum, tiledMap);
        //Checks raum's collision for any collectible
        playerCollectibleCollision();
        //Update camera based on raum's position
        updateCamera();
    }

    /*
    Input: Void
    Output: Void
    Purpose: Checks if player hit any collectibles
    */
    private void playerCollectibleCollision(){
        //Goes through all the collectibles
        for(Iterator<Skull> iter = skulls.iterator(); iter.hasNext();){
            Skull skull = iter.next();
            //If the hit boxes overlap then remove collectible and play sound
            if(raum.getHitBox().overlaps(skull.getHitBox())){
                laserSound.play();
                iter.remove();
            }
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Updates the camera position
    */
    public void updateCamera(){
        //Update if raum's postion is bigger or less than 240 or level widht - 240
        if((raum.getX() > WORLD_WIDTH/2f) && (raum.getX() < levelWidth - WORLD_WIDTH/2f)) {
            camera.position.set(raum.getX(), camera.position.y, camera.position.z);
            camera.update();
            orthogonalTiledMapRenderer.setView((OrthographicCamera) camera);
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Clears screen black
    */
    public void clearScreen(){
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.b, Color.BLUE.g, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws everything
    */
    public void draw(){
        //Set up camera
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);

        //Draws tiled map
        orthogonalTiledMapRenderer.render();

        batch.begin();
        for(Skull skull : skulls){skull.draw(batch);} //Draws skulls
        raum.draw(batch);                             //Draws player
        batch.end();
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws wireframe
    */
    public void drawDebug(){
        shapeRenderer.setProjectionMatrix(camera.projection);
        shapeRenderer.setTransformMatrix(camera.view);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        //raum.drawDebug(shapeRenderer);
        shapeRenderer.end();
    }

}