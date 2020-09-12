package com.packt.raumgame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Skull {

    private float width = 16;
    private float height = 16;

    private Rectangle hitBox;
    private Texture texture;

    public Skull(Texture texture, float x, float y){
        this.texture = texture;
        hitBox = new Rectangle(x, y, width, height);
    }

    public Rectangle getHitBox(){ return hitBox; }

    public void draw(SpriteBatch batch){ batch.draw(texture, hitBox.getX(), hitBox.getY()); }
}
