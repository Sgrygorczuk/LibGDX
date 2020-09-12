package com.packt.raumgame;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;


public class CollisionCell {

    private TiledMapTileLayer.Cell cell;
    private float cellX;
    private float cellY;

    private final float CELL_SIZE = 16;

    public CollisionCell(){}

    public CollisionCell(TiledMapTileLayer.Cell cell, int cellX, int cellY){
        this.cell = cell;
        this.cellX = cellX;
        this.cellY = cellY;
    }

    public boolean isEmpty(){
        return cell == null;
    }

    /*
        We are checking how many cells is Raum taking up
           By default we will always place him in the bottom left but he might
           also be in top right/left or bottom right.
                            |
                         -------
                            |

     */

    private Array<CollisionCell> whichCellsAreCovered(float x, float y, TiledMap tiledMap){
        Array<CollisionCell> cellsCovered = new Array<>();

        cellX = x / CELL_SIZE;
        cellY = y / CELL_SIZE;

        int bottomLeftCellX = MathUtils.floor(cellX);
        int bottomLeftCellY = MathUtils.floor(cellY);

        TiledMapTileLayer tiledMapTileLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);

        //He's in bottom x of his surrounding
        cellsCovered.add(new CollisionCell(
                tiledMapTileLayer.getCell(bottomLeftCellX, bottomLeftCellY),
                bottomLeftCellX,
                bottomLeftCellY));

        //Tip Right
        if(cellX % 1 != 0 && cellY % 1 != 0){
            int topRightCellX = bottomLeftCellX + 1;
            int topRightCellY = bottomLeftCellY + 1;
            cellsCovered.add(new CollisionCell(
                    tiledMapTileLayer.getCell(topRightCellX, topRightCellY),
                    topRightCellX,
                    topRightCellY));
        }

        //Bottom Right
        if(cellX % 1 != 0){
            int bottomRightX = bottomLeftCellX + 1;
            int bottomRightY = bottomLeftCellY;
            cellsCovered.add(new CollisionCell(
                    tiledMapTileLayer.getCell(bottomRightX, bottomRightY),
                    bottomRightX,
                    bottomRightY));
        }

        //Top Left
        if(cellY % 1 != 0){
            int topLeftX = bottomLeftCellX;
            int topLeftY = bottomLeftCellY + 1;
            cellsCovered.add(new CollisionCell(
                    tiledMapTileLayer.getCell(topLeftX, topLeftY),
                    topLeftX,
                    topLeftY));
        }

        //Returns all the cells raum is touching
        return cellsCovered;
    }

    /*
    Input: Array Of Collision Cells, all the cells on provided map
    Output: Array of Collision Cells; filler array
    Purpose: Remove any spots that don't have any cells provided by tiled
     */
    private Array<CollisionCell> filterOutNonTiledCells(Array<CollisionCell> cells) {
        for (Iterator<CollisionCell> iter = cells.iterator(); iter.hasNext(); ) {
            CollisionCell collisionCell = iter.next();
            if (collisionCell.isEmpty()) {
                iter.remove();
            }
        }
        return cells;
    }

    public void handleCollision(Raum raum, TiledMap tiledMap){
        Array<CollisionCell> coveredCells = whichCellsAreCovered(raum.getX(),raum.getY(), tiledMap);
        coveredCells = filterOutNonTiledCells(coveredCells);
        
        for(CollisionCell collisionCell : coveredCells){
            float cellLevelX = collisionCell.cellX * CELL_SIZE;
            float cellLevelY = collisionCell.cellY * CELL_SIZE;

            Rectangle intersection = new Rectangle();

            Intersector.intersectRectangles(
                    raum.getHitBox(),
                    new Rectangle(cellLevelX, cellLevelY, CELL_SIZE, CELL_SIZE),
                    intersection);

            if(intersection.getHeight() < intersection.getWidth()){
                raum.updatePosition(raum.getX(), intersection.getY() + intersection.getHeight());
                raum.landed();
            }
            else if(intersection.getWidth() < intersection.getHeight()){
                if(intersection.getX() == raum.getX()){
                    raum.updatePosition(intersection.getX() + intersection.width, raum.getY());
                }
                if(intersection.getX() > raum.getX()){
                    raum.updatePosition(intersection.getX() - raum.getWidth(), raum.getY());
                }
            }
        }


    }
}
