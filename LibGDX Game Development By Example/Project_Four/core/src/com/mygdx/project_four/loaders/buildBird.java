package com.mygdx.project_four.loaders;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;

public class buildBird {
    private static final float PIXEL_PER_TILE = 16;
    private static final float HALF = 0.5f;

    /**
     * The bird enemy that we will be shooting t
     * @param tiledMap the tiled data for the bird objects
     * @param world the world that will be displayed
     */
    public static void buildBirdBodies(TiledMap tiledMap, World world)
    {
        MapObjects objects = tiledMap.getLayers().
                get("bird").getObjects();
        for (MapObject object : objects) {
            EllipseMapObject ellipseMapObject = (EllipseMapObject) object;
            CircleShape circle = getCircle(ellipseMapObject);
            BodyDef bd = new BodyDef();
            bd.type = BodyDef.BodyType.DynamicBody;
            Body body = world.createBody(bd);
            Fixture fixture = body.createFixture(circle, 1);
            fixture.setUserData("enemy");
            body.setUserData("enemy");
            Ellipse ellipse = ellipseMapObject.getEllipse();
            body.setTransform(new Vector2((ellipse.x + ellipse.width *
                    HALF) / PIXEL_PER_TILE, (ellipse.y + ellipse.height * HALF) /
                    PIXEL_PER_TILE), 0);
            circle.dispose();
        }
    }

    /**
     * The circle shape that will define our bird enemy
     * @param ellipseObject data from tiled about our circle object
     * @return a circle object ready to be added to the world
     */
    private static CircleShape getCircle(EllipseMapObject
                                                 ellipseObject) {
        Ellipse ellipse = ellipseObject.getEllipse();
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(ellipse.width * HALF / PIXEL_PER_TILE);
        return circleShape;
    }

}
