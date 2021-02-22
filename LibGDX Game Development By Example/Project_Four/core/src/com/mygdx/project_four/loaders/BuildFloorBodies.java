package com.mygdx.project_four.loaders;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class BuildFloorBodies {

    private static final float PIXEL_PER_TILE = 16;
    private static final float HALF = 0.5f;

    /**
     * Creates the floor at which the objects in the world will rest on
     * @param tiledMap data from tiled map
     * @param world the world in which the data is drawn in
     */
    public static void buildFloorBodies(TiledMap tiledMap, World world) {
        MapObjects objects =
                tiledMap.getLayers().get("floor").getObjects();
        for (MapObject object : objects) {
            RectangleMapObject rectangleMapObject = (RectangleMapObject) object;
            PolygonShape rectangle = getRectangle((RectangleMapObject) object);
            BodyDef bd = new BodyDef();
            bd.type = BodyDef.BodyType.StaticBody;
            Body body = world.createBody(bd);
            body.createFixture(rectangle, 1);
            body.setUserData("floor");
            body.createFixture(rectangle, 1);
            body.setTransform(getTransformForRectangle
                    (rectangleMapObject.getRectangle()), 0);
            rectangle.dispose();
        }
    }

    /**
     * Purpose: Create a Shape object created the passed in data from Tiled
     * @param rectangleObject, data from Tiled
     * @return Shape object
     */
    private static PolygonShape getRectangle(RectangleMapObject rectangleObject) {
        Rectangle rectangle = rectangleObject.getRectangle();
        PolygonShape polygon = new PolygonShape();
        polygon.setAsBox(rectangle.width * HALF / PIXEL_PER_TILE,
                rectangle.height * HALF / PIXEL_PER_TILE);
        return polygon;
    }

    /**
     *Gets the data for the current position of object used for sprite work
     * @param rectangle the data from tiled about the rectangle
     * @return gives back the 2d vector of what the rectangle looks like now
     */
    private static Vector2 getTransformForRectangle(Rectangle rectangle) {
        return new Vector2((rectangle.x + (rectangle.width * HALF)) /
                PIXEL_PER_TILE, (rectangle.y + (rectangle.height * HALF)) /
                PIXEL_PER_TILE);
    }
}
