package com.packt.raumgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;


public class Raum {

    private static final float MAX_SPEED_X = 2;
    private static final float MAX_SPEED_Y = 2;

    private final Rectangle hitBox = new Rectangle(0,0, 16, 15);

    private float xSpeed = 0;
    private float ySpeed = 0;

    private boolean jumpedFlag = false;
    private float jumpDistance = 0;

    private float animationTimer = 0;
    private final Animation walking;
    private final TextureRegion standing;
    private final TextureRegion jump;

    public Raum(Texture texture){
        TextureRegion[] regions = TextureRegion.split(texture, 16, 15)[0];
        walking = new Animation(0.25f, regions[1], regions[2]);
        walking.setPlayMode(Animation.PlayMode.LOOP);
        standing = regions[0];
        jump = regions[3];
    }

    public void update(float delta){
        animationTimer += delta;
        updateMovement();
        checkIfWorldBound();
    }

    private void updateMovement(){
        Input input = Gdx.input;
        if(input.isKeyPressed(Input.Keys.D)){ xSpeed = MAX_SPEED_X;}
        else if(input.isKeyPressed(Input.Keys.A)){xSpeed = -MAX_SPEED_X;}
        else {xSpeed = 0;}

        if (input.isKeyPressed(Input.Keys.SPACE) && !jumpedFlag) {
            ySpeed = MAX_SPEED_Y;
            jumpDistance += ySpeed;
            jumpedFlag = jumpDistance > 45;
        }
        else{
            ySpeed = -MAX_SPEED_Y;
            jumpedFlag = jumpDistance > 0;
        }

        hitBox.x += xSpeed;
        hitBox.y += ySpeed;
    }

    public void updatePosition(float x, float y){
        hitBox.setX(x);
        hitBox.setY(y);
    }

    private void checkIfWorldBound(){
        if(hitBox.x < 0){hitBox.x = 0;}
        else if(hitBox.x + hitBox.height > 640){hitBox.x = (int) (640 - hitBox.getWidth());}

        if(hitBox.y < 0){
            hitBox.y = 0;
            landed();}
    }

    public void landed(){
        jumpedFlag = false;
        jumpDistance = 0;
        ySpeed = 0;
    }

    public float getX(){return hitBox.x;}

    public float getY(){return hitBox.y;}

    public float getWidth(){return hitBox.getWidth();}

    public com.badlogic.gdx.math.Rectangle getHitBox(){ return hitBox;}

    public void draw(SpriteBatch batch){
        TextureRegion drawTexture = standing;
        if(ySpeed > 0){drawTexture = jump;}
        else if(xSpeed != 0){
            drawTexture = (TextureRegion) walking.getKeyFrame(animationTimer);
            if(xSpeed < 0 && !drawTexture.isFlipX()){drawTexture.flip(true, false);}
            else if(xSpeed > 0 && drawTexture.isFlipX()){drawTexture.flip(true, false);}
        }

        batch.draw(drawTexture, hitBox.x, hitBox.y);
    }

    public void drawDebug(ShapeRenderer shapeRenderer){
        shapeRenderer.rect(hitBox.x, hitBox.y, hitBox.width, hitBox.height);
    }

}
