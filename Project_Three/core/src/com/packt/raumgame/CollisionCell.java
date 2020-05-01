package com.packt.raumgame;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

class CollisionCell {

    //Sizes of tiles in Tiled
    private static final float CELL_SIZE = 16;

    //
    private final TiledMapTileLayer.Cell cell;

    //Cell position
    private final int cellX;
    private final int cellY;

    /*
    Input: Cell, Position
    Output: Void
    Purpose: Creates a Cell
    */
    private CollisionCell(TiledMapTileLayer.Cell cell, int cellX, int cellY) {
        this.cell = cell;
        this.cellX = cellX;
        this.cellY = cellY;
    }

    /*
    Input: Player character, the tiles of the map
    Output: Void
    Purpose: Checks if Raum is touching any of the cells
    */
    void handleRaumCollision(Raum raum, TiledMap tiledMap) {
        //Determines where raum is
        Array<CollisionCell> raumCells = whichCellsDoesRaumCover(raum, tiledMap);
        //Check which of those cells are not used
        raumCells = filterOutNonTiledCells(raumCells);
        //For any remaining cells we check
        for (CollisionCell cell : raumCells) {
            //Gets their position in reference to the map
            float cellLevelX = cell.cellX * CELL_SIZE;
            float cellLevelY = cell.cellY * CELL_SIZE;
            //Make a rectangle to check for intersection
            Rectangle intersection = new Rectangle();
            //Checks for collision
            Intersector.intersectRectangles(raum.getCollisionRectangle(), new Rectangle(cellLevelX, cellLevelY, CELL_SIZE, CELL_SIZE), intersection);
            //Lands Raum ontop of the cell
            if (intersection.getHeight() < intersection.getWidth()) {
                raum.setPosition(raum.getX(), intersection.getY() + intersection.getHeight());raum.landed();
            }
            //Makes sure raum is not going through the intersection on left or right
            else if (intersection.getWidth() < intersection.getHeight())
            {
                if (intersection.getX() == raum.getX()) { raum.setPosition(intersection.getX() + intersection.getWidth(), raum.getY()); }
                if (intersection.getX() > raum.getX()) { raum.setPosition(intersection.getX() - raum.WIDTH, raum.getY()); }
            }
        }
    }

    /*
    Input: Player character, the tiles of the map
    Output: Array of Collision Cell points
    Purpose: Determines where raum is standing within a 2x2 box of area
            We minizme the are of the map to 4 points that he could be
            We find if he is standing in any of them
    */
    private Array<CollisionCell> whichCellsDoesRaumCover(Raum raum, TiledMap tiledMap) {
        //Get Raums position
        float x = raum.getX();
        float y = raum.getY();

        //Set up array for cells he's interacting with
        Array<CollisionCell> cellsCovered = new Array<CollisionCell>();

        //Finds the coordinate where Raum is on the map
        //So if Raum stands at  161 x 1 pixel tile would be a 10.1 x .1 x y coordinate system
        float cellX = x / CELL_SIZE;
        float cellY = y / CELL_SIZE;

        //Gets the closets tile to the decimal version so that the 10.1 x .1 would turn to 10 x 1
        int bottomLeftCellX = MathUtils.floor(cellX);
        int bottomLeftCellY = MathUtils.floor(cellY);

        //Grabs the first layer of the tile set
        TiledMapTileLayer tiledMapTileLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);

        //Adds the start point cell to the array
        cellsCovered.add(new CollisionCell(tiledMapTileLayer.getCell(bottomLeftCellX, bottomLeftCellY), bottomLeftCellX, bottomLeftCellY));

        //Raum is touching top right corner
        if (cellX % 1 != 0 && cellY % 1 != 0) {
            int topRightCellX = bottomLeftCellX + 1;
            int topRightCellY = bottomLeftCellY + 1;
            cellsCovered.add(new CollisionCell(tiledMapTileLayer.getCell(topRightCellX, topRightCellY), topRightCellX, topRightCellY));
        }

        //If Raum is touching the right bottom corner
        if (cellX % 1 != 0) {
            int bottomRightCellX = bottomLeftCellX + 1;
            int bottomRightCellY = bottomLeftCellY;
            cellsCovered.add(new CollisionCell(tiledMapTileLayer.getCell(bottomRightCellX, bottomRightCellY), bottomRightCellX, bottomRightCellY));
        }

        //If raum is touchign top left corner
        if (cellY % 1 != 0) {
            int topLeftCellX = bottomLeftCellX;
            int topLeftCellY = bottomLeftCellY + 1;
            cellsCovered.add(new CollisionCell(tiledMapTileLayer.getCell(topLeftCellX, topLeftCellY), topLeftCellX, topLeftCellY));
        }

        //Return the marked cells points
        return cellsCovered;
    }

    /*
  Input: Array of points
  Output: Void
  Purpose: Determines any of the found points are not tiled
  */
    private Array<CollisionCell> filterOutNonTiledCells(Array<CollisionCell> cells) {
        //Goes through each cell that raum has been determined to be in and check if they're empty,
        // if they are removes them from the array
        for (Iterator<CollisionCell> iter = cells.iterator(); iter.hasNext(); ) {
            CollisionCell collisionCell = iter.next();
            if (collisionCell.isEmpty()) { iter.remove(); }
        }
        return cells;
    }

    /*
    Input: Void
    Output: State of Cell
    Purpose: Check is cell is empty
    */
    private boolean isEmpty() { return cell == null; }

}
