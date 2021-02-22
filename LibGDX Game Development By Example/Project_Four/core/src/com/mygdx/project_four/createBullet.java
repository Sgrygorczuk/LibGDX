package com.mygdx.project_four;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ObjectMap;

public class createBullet {

    private static final float MAX_STRENGTH = 15;
    private static final float UNITS_PER_METER = 16;

    /**
     * Creates a bullet that's launched at the enemy
     * @param world the world that the bullet resides in
     * @param angle the angle that the user chose for the bullet to fire at
     * @param distance the distance or power at which the bullet should shoot at
     * @param firingPosition the position where we start
     */
    public createBullet(World world, float distance, float angle, Vector2 firingPosition, ProjectFour projectFour, ObjectMap<Body, Sprite> sprites) {
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(1f);
        //Gets the positions from firingPosition
        circleShape.setPosition(new
                Vector2(convertUnitsToMetres(firingPosition.x),
                convertUnitsToMetres(firingPosition.y)));
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        Body bullet = world.createBody(bd);
        bullet.createFixture(circleShape, 1);
        circleShape.dispose();
        //Deaneries the speed of the bullet
        float velX = Math.abs( (MAX_STRENGTH * -MathUtils.cos(angle) *
                (2 * distance / 100f)));
        float velY = Math.abs( (MAX_STRENGTH * -MathUtils.sin(angle) *
                (2 * distance / 100f)));
        bullet.setLinearVelocity(velX, velY);

        Sprite sprite = new Sprite(projectFour.getAssetManager().get("Skull.png", Texture.class));
        sprites.put(bullet, sprite);
    }

    /**
     * Convers the pixels to meters
     * @param pixels gives the amount of pixels we gone
     * @return gives it back in meters
     */
    private float convertUnitsToMetres(float pixels) { return pixels / UNITS_PER_METER; }


}
