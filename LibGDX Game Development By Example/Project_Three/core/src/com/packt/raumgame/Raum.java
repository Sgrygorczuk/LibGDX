package com.packt.raumgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Raum {

    //How fast the character will move if clicked
    private static final float MAX_SPEED_X = 2;
    private static final float MAX_SPEED_Y = 2;

    //The hit box
    private final Rectangle hitBox = new Rectangle(0,0, 16, 15);

    //Their current speed
    private float xSpeed = 0;
    private float ySpeed = 0;

    private boolean jumpedFlag = false; //Tells us if character is in air
    private float jumpDistance = 0;     //How far from ground is the character

    private Sound jumpSound;    //Sound effect

    //Animation and textures
    private float animationTimer = 0;
    private final Animation walking;
    private final TextureRegion standing;
    private final TextureRegion jump;

    /*
    Input: Texture and sound,
    Output: Void
    Purpose: Constructor sets up the textures used and sound
    */
    public Raum(Texture texture, Sound sound){
        //Brake the texture down into regions
        TextureRegion[] regions = TextureRegion.split(texture, 16, 15)[0];
        //Fires two are for walking
        walking = new Animation(0.25f, regions[1], regions[2]);
        walking.setPlayMode(Animation.PlayMode.LOOP);
        //This one is for statnding
        standing = regions[0];
        //This one is for jumping
        jump = regions[3];

        //Sound that plays when player jumps
        jumpSound = sound;
    }

    /*
    Input: Delta for animation timing, float for how big the level is
    Output: Void
    Purpose: Update function, updates movement action and animation
    */
    public void update(float delta, float levelWidth){
        animationTimer += delta;            //Update animation time
        updateMovement();                   //Update movement actions
        checkIfWorldBound(levelWidth);      //Keep player in level bounds
    }

    /*
    Input: Void
    Output: Void
    Purpose: Update the movement functions
    */
    private void updateMovement(){
        //Get the input
        Input input = Gdx.input;

        //Update x movement
        if(input.isKeyPressed(Input.Keys.D)){ xSpeed = MAX_SPEED_X;}
        else if(input.isKeyPressed(Input.Keys.A)){xSpeed = -MAX_SPEED_X;}
        else {xSpeed = 0;}

        //Updates the Y movement, if jumped and is low enough make y movement higher
        if (input.isKeyPressed(Input.Keys.SPACE) && !jumpedFlag) {
            if(ySpeed != MAX_SPEED_Y){jumpSound.play();}
            ySpeed = MAX_SPEED_Y;
            jumpDistance += ySpeed;
            jumpedFlag = jumpDistance > 45;
        }
        //Can no longer go higher and slowly falls down
        else{
            ySpeed = -MAX_SPEED_Y;
            jumpedFlag = jumpDistance > 0;
        }

        //Update hitBox postilion
        hitBox.x += xSpeed;
        hitBox.y += ySpeed;
    }

    /*
    Input: New float X and Y
    Output: Void
    Purpose: Update the position outside of the raum movement
    */
    public void updatePosition(float x, float y){
        hitBox.setX(x);
        hitBox.setY(y);
    }

    /*
    Input: Float levelWidth
    Output: Void
    Purpose: Keeps raum between 0 and levelWidth and make sure it stops when it hit the ground
    */
    private void checkIfWorldBound(float levelWidth){
        //Makes sure we're bound by x
        if(hitBox.x < 0){hitBox.x = 0;}
        else if(hitBox.x + hitBox.width > levelWidth){hitBox.x = (int) (levelWidth - hitBox.getWidth());}

        //Makes sure that we stop moving down when we hit the ground
        if(hitBox.y < 0){
            hitBox.y = 0;
            landed();}
    }

    /*
    Input: Void
    Output: Void
    Purpose: Makes sure we no longer move down
    */
    public void landed(){
        jumpedFlag = false;
        jumpDistance = 0;
        ySpeed = 0;
    }

    /*
    Input: Void
    Output: Return x
    Purpose: Returns X
    */
    public float getX(){return hitBox.x;}

    /*
    Input: Void
    Output: Return Y
    Purpose: Returns Y
    */
    public float getY(){return hitBox.y;}

    /*
    Input: Void
    Output: Return Width
    Purpose: Returns Width
    */
    public float getWidth(){return hitBox.getWidth();}

    /*
    Input: Void
    Output: Return hitBox
    Purpose: Returns hitBox
    */
    public Rectangle getHitBox(){ return hitBox;}

    /*
    Input: SpriteBatch
    Output: Void
    Purpose: Draws the player
    */
    public void draw(SpriteBatch batch){
        TextureRegion drawTexture = standing; //Preset to draw standing
        if(ySpeed > 0){drawTexture = jump;}  //Draw falling if y is being enough
        //Draw the movement animation
        else if(xSpeed != 0){
            drawTexture = (TextureRegion) walking.getKeyFrame(animationTimer);
            if(xSpeed < 0 && !drawTexture.isFlipX()){drawTexture.flip(true, false);}
            else if(xSpeed > 0 && drawTexture.isFlipX()){drawTexture.flip(true, false);}
        }
        //Draw the image
        batch.draw(drawTexture, hitBox.x, hitBox.y);
    }

    /*
    Input: ShapeRenderer
    Output: Void
    Purpose: Draws the wireframe
    */
    public void drawDebug(ShapeRenderer shapeRenderer){
        shapeRenderer.rect(hitBox.x, hitBox.y, hitBox.width, hitBox.height);
    }

}
