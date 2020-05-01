package com.packt.flappybee;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

class Flower{

    /*
    Dimensions
     */
    private static final float DISTANCE_BETWEEN_FLOOR_AND_CEILING = 225F;
    private static final float COLLISION_RECTANGLE_WIDTH = 13f;
    private static final float COLLISION_RECTANGLE_HEIGHT = 447f;
    private static final float HEIGHT_OFFSET = -400f;
    private static final float COLLISION_CIRCLE_RADIUS = 33f;

    /*
    Objects
     */
    private final Circle floorCollisionCircle;
    private final Rectangle floorCollisionRectangle;
    private final Circle ceilingCollisionCircle;
    private final Rectangle ceilingCollisionRectangle;

    //Position
    private float x = 0;

    /*
    Movment
     */
    private static final float MAX_SPEED_PER_SECOND = 100f;

    /*
    Flags
     */
    private boolean pointClaimed = false;

    /*
    Textures
     */
    private final TextureRegion floorTexture;
    private final TextureRegion ceilingTexture;

    /*
    Input: Delta
    Output: Void
    Purpose: Flower constructor, creates the rectangle and circle on top, and places it at -400 to 0 y
    */
    Flower(TextureRegion floorTexture, TextureRegion ceilingTexture){
        this.ceilingTexture = ceilingTexture;
        this.floorTexture = floorTexture;

        float y = MathUtils.random(HEIGHT_OFFSET);
        this.floorCollisionRectangle = new Rectangle(x, y,COLLISION_RECTANGLE_WIDTH, COLLISION_RECTANGLE_HEIGHT);
        this.floorCollisionCircle = new Circle((x + COLLISION_RECTANGLE_WIDTH)/2, y + COLLISION_RECTANGLE_HEIGHT ,COLLISION_CIRCLE_RADIUS);

        this.ceilingCollisionRectangle = new Rectangle(x, y + COLLISION_RECTANGLE_HEIGHT + DISTANCE_BETWEEN_FLOOR_AND_CEILING,COLLISION_RECTANGLE_WIDTH, COLLISION_RECTANGLE_HEIGHT);
        this.ceilingCollisionCircle = new Circle((x + COLLISION_RECTANGLE_WIDTH)/2, y + COLLISION_RECTANGLE_HEIGHT + DISTANCE_BETWEEN_FLOOR_AND_CEILING,COLLISION_CIRCLE_RADIUS);
    }

    /*
    Input: Void
    Output: Returns X
    Purpose: Gives the Screen X coordinate
    */
    float getX(){ return x; }

    /*
    Input: Void
    Output: Returns X
    Purpose: Gives the width of rectangle
    */
    float getWidth(){ return COLLISION_RECTANGLE_WIDTH;}

    /*
    Input: Void
    Output: Returns X
    Purpose: Gives the Screen X coordinate
    */
    void setPosition(float x){
        this.x = x;
        updateCollisionCircle();
        updateCollisionRectangle();
    }

    /*
    Input: Void
    Output: Void
    Purpose: Updates the position of the circles of the flower
    */
    private void updateCollisionCircle(){
        floorCollisionCircle.setX(x + floorCollisionRectangle.width/2);
        ceilingCollisionCircle.setX(x + floorCollisionRectangle.width/2);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Updates the position of the rectangles of the flower
    */
    private void updateCollisionRectangle(){
        floorCollisionRectangle.setX(x);
        ceilingCollisionRectangle.setX(x);
    }

    /*
    Input: Flapee circle
    Output: Returs flag that tells us if we intercepts between any of the flower parts and flappe
    Purpose: Checks if flappe hits any of the flower parts
    */
    boolean isFlappeeColliding(Flappee flapee) {
        Circle flappeeCollisionCircle = flapee.getCollisionCircle();
        return Intersector.overlaps(flappeeCollisionCircle, floorCollisionCircle) ||
                Intersector.overlaps(flappeeCollisionCircle, ceilingCollisionCircle) ||
                Intersector.overlaps(flappeeCollisionCircle, ceilingCollisionRectangle) ||
                Intersector.overlaps(flappeeCollisionCircle, floorCollisionRectangle);
    }

    /*
    Input: Delta for timing,
    Output: Void
    Purpose: Calculates the new position of the flower
    */
    void update(float delta){ setPosition(x-(MAX_SPEED_PER_SECOND * delta)); }

    /*
    Input: Void
    Output: Flag that tells us if the point has been passed
    Purpose: Returns a flag that tells us if the point has been passed
    */
    boolean isPointClaimed(){return pointClaimed;}

    /*
    Input: Void
    Output: Void
    Purpose: Set the flag true if we passed the point to stop counting it afterwards
    */
    void markPointClaimed(){ pointClaimed = true;}

    /*
    Input: SpriteBatch
    Output: Void
    Purpose: Central drawing function
    */
    void draw(SpriteBatch batch){
        drawFloor(batch);
        drawCeiling(batch);
    }

    /*
    Input: SpriteBatch
    Output: Void
    Purpose: Draws the top textures
    */
    private void drawFloor(SpriteBatch batch){
        float textureX = floorCollisionCircle.x - (float) floorTexture.getRegionWidth()/2;
        float textureY = floorCollisionRectangle.getY() - COLLISION_CIRCLE_RADIUS;
        batch.draw(floorTexture, textureX, textureY);
    }

    /*
    Input: SpriteBatch
    Output: Void
    Purpose: Draws the floor textures
    */
    private void drawCeiling(SpriteBatch batch){
        float textureX = ceilingCollisionCircle.x - (float) ceilingTexture.getRegionWidth()/2;
        float textureY = ceilingCollisionRectangle.getY() - COLLISION_CIRCLE_RADIUS;
        batch.draw(ceilingTexture, textureX, textureY);
    }

    /*
    Input: ShapeRenderer
    Output: Void
    Purpose: Draws the wire frame
    */
    void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.circle(floorCollisionCircle.x, floorCollisionCircle.y, floorCollisionCircle.radius);
        shapeRenderer.rect(floorCollisionRectangle.x, floorCollisionRectangle.y, floorCollisionRectangle.width, floorCollisionRectangle.height);
        shapeRenderer.circle(ceilingCollisionCircle.x, ceilingCollisionCircle.y, ceilingCollisionCircle.radius);
        shapeRenderer.rect(ceilingCollisionRectangle.x, ceilingCollisionRectangle.y, ceilingCollisionRectangle.width, ceilingCollisionRectangle.height);
    }
}