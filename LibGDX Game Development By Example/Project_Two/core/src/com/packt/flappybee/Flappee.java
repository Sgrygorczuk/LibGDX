package com.packt.flappybee;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;

class Flappee {
    //The circle class that will draw a circle on screen
    private final Circle collisionCircle;

    //Sets radius, and initial position
    private static final float COLLISION_RADIUS = 24f;
    private static final int TILE_WIDTH = 48;
    private static final int TILE_HEIGHT = 48;
    private float x = 0;
    private float y = 0;

    /*
    Movement
     */
    private static final float FLY_ACCELERATION = 5f;
    private static final float DIVE_ACCELERATION = 0.30f;
    private float ySpeed = 0;

    private static final float FRAME_DURATION = 0.25f;
    private float animationTime = 0;
    private final Animation animation;

    /*
    Input: Void
    Output: Returns X
    Purpose: Gives the Screen X coordinate
    */
    float getX(){ return x; }

    /*
    Input: Void
    Output: Returns collisionCircle
    Purpose: Gives user access to the collisionCircle
    */
    Circle getCollisionCircle(){ return collisionCircle;}

    /*
    Input: Void
    Output: Returns y
    Purpose: Gives the Screen y coordinate
    */
    float getY(){ return y; }

    /*
    Input: Void
    Output: Returns X
    Purpose: Gives the Screen X coordinate
    */
    float getRadius(){ return COLLISION_RADIUS; }

    /*
    Input: Void
    Output: Void
    Purpose: Constructed initializes a copy of the Flappee Bee
    */
    Flappee(TextureRegion flappeeTexture) {
		/*
	Texture
	 */
        TextureRegion[][] flappedTextures = new TextureRegion(flappeeTexture).split(TILE_WIDTH, TILE_HEIGHT);

        animation = new Animation(FRAME_DURATION, flappedTextures[0][0], flappedTextures[0][1], flappedTextures[0][2], flappedTextures[0][3]);
        animation.setPlayMode(Animation.PlayMode.LOOP);

        collisionCircle = new Circle(x,y, COLLISION_RADIUS);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Updates the position variables of the flappee bee
    */
    void setPosition(float x, float y){
        this.x = x;				 //Changes x variable in class
        this.y = y;				 //Changes y variable in class
        updateCollisionCircle();
    }

    /*
    Input: Void
    Output: Void
    Purpose: updates the
    */
    void update(float delta){
        animationTime += delta;
        ySpeed -= DIVE_ACCELERATION;
        setPosition(x, y + ySpeed);
    }

    /*
    Input: Void
    Output: Void
    Purpose: updates the
    */
    void flyUp(){
        ySpeed = FLY_ACCELERATION;
        setPosition(x, y + ySpeed);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Inputs the variables into the circle to be redrawn
    */
    private void updateCollisionCircle(){
        collisionCircle.setX(x);
        collisionCircle.setY(y);
    }

    /*
    Input: SpriteBatch
    Output: Void
    Purpose: Draws textures
    */
    void draw(SpriteBatch batch){
        TextureRegion flappeeTexture = (TextureRegion) animation.getKeyFrame(animationTime);
        batch.draw(flappeeTexture, collisionCircle.x-COLLISION_RADIUS, collisionCircle.y-COLLISION_RADIUS);
    }

    /*
    Input: Shaperenderd
    Output: Void
    Purpose: Draws the circle on the screen using render
    */
    void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.circle(collisionCircle.x, collisionCircle.y, collisionCircle.radius);
    }
}