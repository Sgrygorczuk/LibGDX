package com.packt.raumgame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Skull {

    private Rectangle hitBox;   //Hit box
    private Texture texture;    //Texture

    /*
    Input: Texture, Position
    Output: Void
    Purpose: Constructor create a object and places it on the screen
    */
    public Skull(Texture texture, float x, float y){
        this.texture = texture;
        float width = 16;
        float height = 16;
        hitBox = new Rectangle(x, y, width, height);
    }

    /*
    Input: Void
    Output: Returns hit Box
    Purpose: Returns hit Box
    */
    public Rectangle getHitBox(){ return hitBox; }

    /*
    Input: SpriteBatch
    Output: Void
    Purpose: Draws the skull
    */
    public void draw(SpriteBatch batch){ batch.draw(texture, hitBox.getX(), hitBox.getY()); }
}
