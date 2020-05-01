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
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Iterator;

public class GameScreen extends ScreenAdapter {
    private static final float WORLD_WIDTH = 640;
    private static final float WORLD_HEIGHT = 480;

    private static final float CELL_SIZE = 16;

    private ShapeRenderer shapeRenderer;
    private Viewport viewport;
    private Camera camera;
    private SpriteBatch batch;

    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;

    private TiledMap tiledMap;

    private Raum raum;
    private Array<Skull> skulls = new Array<Skull>();

    private final RaumGame raumGame;
    GameScreen(RaumGame raumGame) {
        this.raumGame = raumGame;
    }

    @Override
    public void resize(int width, int height) { viewport.update(width, height); }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply(true);

        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        tiledMap = raumGame.getAssetManager().get("RuamPlatformer.tmx");
        orthogonalTiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, batch);
        orthogonalTiledMapRenderer.setView((OrthographicCamera) camera);
        raum = new Raum((Texture) raumGame.getAssetManager().get("RaumSprite.png"));
        populateSkulls();

    }

    @Override
    public void render(float delta) {
        update(delta);
        clearScreen();
        draw();
        drawDebug();
    }

    private void update(float delta) {
        raum.update(delta);
        stopPeteLeavingTheScreen();
        handleRaumCollision();
    }

    private void stopPeteLeavingTheScreen() {
        if (raum.getY() < 0) {
            raum.setPosition(raum.getX(), 0);
            raum.landed();
        }
        if (raum.getX() < 0) { raum.setPosition(0, raum.getY()); }
        if (raum.getX() + raum.WIDTH > WORLD_WIDTH) { raum.setPosition(WORLD_WIDTH - raum.WIDTH, raum.getY()); }
    }

    private void populateSkulls() {
        MapLayer mapLayer = tiledMap.getLayers().get("Collectibles");
        for (MapObject mapObject : mapLayer.getObjects()) {
            skulls.add(
                    new Skull(raumGame.getAssetManager().get("Skull.png", Texture.class),
                            mapObject.getProperties().get("x", Float.class),
                            mapObject.getProperties().get("y", Float.class)
                    )
            );
        }
    }

    private class CollisionCell {
        private final TiledMapTileLayer.Cell cell;
        private final int cellX;
        private final int cellY;
        public CollisionCell(TiledMapTileLayer.Cell cell, int cellX, int cellY) {
            this.cell = cell;
            this.cellX = cellX;
            this.cellY = cellY;
        }
        public boolean isEmpty() { return cell == null; }
    }

    private Array<CollisionCell> whichCellsDoesPeteCover() {
        float x = raum.getX();
        float y = raum.getY();
        Array<CollisionCell> cellsCovered = new Array<CollisionCell>();
        float cellX = x / CELL_SIZE;
        float cellY = y / CELL_SIZE;
        int bottomLeftCellX = MathUtils.floor(cellX);
        int bottomLeftCellY = MathUtils.floor(cellY);
        TiledMapTileLayer tiledMapTileLayer = (TiledMapTileLayer)
                tiledMap.getLayers().get(0);
        cellsCovered.add(new
                CollisionCell(tiledMapTileLayer.getCell(bottomLeftCellX,
                bottomLeftCellY), bottomLeftCellX, bottomLeftCellY));
        if (cellX % 1 != 0 && cellY % 1 != 0) {
            int topRightCellX = bottomLeftCellX + 1;
            int topRightCellY = bottomLeftCellY + 1;
            cellsCovered.add(new
                    CollisionCell(tiledMapTileLayer.getCell(topRightCellX,
                    topRightCellY), topRightCellX, topRightCellY));
        }
        if (cellX % 1 != 0) {
            int bottomRightCellX = bottomLeftCellX + 1;
            int bottomRightCellY = bottomLeftCellY;
            cellsCovered.add(new
                    CollisionCell(tiledMapTileLayer.getCell(bottomRightCellX,
                    bottomRightCellY), bottomRightCellX, bottomRightCellY));
        }
        if (cellY % 1 != 0) {
            int topLeftCellX = bottomLeftCellX;
            int topLeftCellY = bottomLeftCellY + 1;
            cellsCovered.add(new
                    CollisionCell(tiledMapTileLayer.getCell(topLeftCellX,
                    topLeftCellY), topLeftCellX, topLeftCellY));
        }
        return cellsCovered;
    }

    private Array<CollisionCell> filterOutNonTiledCells(Array<CollisionCell> cells) {
        for (Iterator<CollisionCell> iter = cells.iterator();
             iter.hasNext(); ) {
            CollisionCell collisionCell = iter.next();
            if (collisionCell.isEmpty()) { iter.remove(); }
        }
        return cells;
    }

    private void handleRaumCollision() {
        Array<CollisionCell> raumCells = whichCellsDoesPeteCover();
        raumCells = filterOutNonTiledCells(raumCells);
        for (CollisionCell cell : raumCells) {
            float cellLevelX = cell.cellX * CELL_SIZE;
            float cellLevelY = cell.cellY * CELL_SIZE;
            Rectangle intersection = new Rectangle();
            Intersector.intersectRectangles(raum.getCollisionRectangle(), new Rectangle(cellLevelX, cellLevelY, CELL_SIZE, CELL_SIZE), intersection);
            if (intersection.getHeight() < intersection.getWidth()) {
                raum.setPosition(raum.getX(), intersection.getY() + intersection.getHeight());raum.landed();
            }
            else if (intersection.getWidth() < intersection.getHeight())
            {
                if (intersection.getX() == raum.getX()) { raum.setPosition(intersection.getX() + intersection.getWidth(), raum.getY()); }
                if (intersection.getX() > raum.getX()) { raum.setPosition(intersection.getX() - raum.WIDTH, raum.getY()); }
            }
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
