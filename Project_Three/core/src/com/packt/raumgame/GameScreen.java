package com.packt.raumgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
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
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.awt.Color;
import java.util.Iterator;

public class GameScreen extends ScreenAdapter {

    private static final float WORLD_WIDTH = 640;
    private static final float WORLD_HEIGHT = 480;

    private Viewport viewport;
    private Camera camera;

    private SpriteBatch batch = new SpriteBatch();
    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    private final RaumGame raumGame;

    private Raum raum;
    private CollisionCell collisionCell;
    private Array<Skull> skulls = new Array<>();

    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;

    public GameScreen(RaumGame raumGame){this.raumGame = raumGame;}

    @Override
    public void resize(int width, int height){viewport.update(width, height);}

    @Override
    public void show(){
        showCamera();
        showObjects();
    }

    public void showCamera(){
        camera = new OrthographicCamera();
        camera.position.set(WORLD_WIDTH/2, WORLD_HEIGHT/2f, 0);
        camera.update();

        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply(true);
    }

    public void showObjects(){
        //Gets the ti
        tiledMap = raumGame.getAssetManager().get("map.tmx");
        orthogonalTiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, batch);
        orthogonalTiledMapRenderer.setView((OrthographicCamera) camera);

        raum = new Raum((Texture) raumGame.getAssetManager().get("RaumSprite.png"));
        collisionCell = new CollisionCell();

        populateSkulls();
    }

    private void populateSkulls(){
        Texture skullTexture = new Texture(Gdx.files.internal("Skull.png"));

        MapLayer mapLayer = tiledMap.getLayers().get("Skulls");
        for(MapObject mapObject : mapLayer.getObjects()){
            skulls.add(new Skull(skullTexture,
                    mapObject.getProperties().get("x",Float.class),
                    mapObject.getProperties().get("y",Float.class)));
        }
    }

    @Override
    public void render(float delta){
        update(delta);
        clearScreen();
        draw();
        drawDebug();
    }

    public void update(float delta){
        raum.update(delta);
        collisionCell.handleCollision(raum, tiledMap);
        playerCollectibleCollision();
    }

    private void playerCollectibleCollision(){
        for(Iterator<Skull> iter = skulls.iterator(); iter.hasNext();){
            Skull skull = iter.next();
            if(raum.getHitBox().overlaps(skull.getHitBox())){iter.remove();}
        }
    }

    public void clearScreen(){
        Gdx.gl.glClearColor(Color.BLACK.getRed(), Color.BLACK.getGreen(), Color.BLACK.getBlue(), Color.BLACK.getAlpha());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public void draw(){
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);

        orthogonalTiledMapRenderer.render();

        batch.begin();
        for(Skull skull : skulls){skull.draw(batch);}
        raum.draw(batch);
        batch.end();
    }

    public void drawDebug(){
        shapeRenderer.setProjectionMatrix(camera.projection);
        shapeRenderer.setTransformMatrix(camera.view);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.end();
    }

}