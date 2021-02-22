package com.mygdx.project_four;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;

public class SpriteGenerator {
    public static Sprite generateSpriteForBody(AssetManager assetManager, Body body) {
        if ("horizontal".equals(body.getUserData())) {
            return createSprite(assetManager, "Block.png");
        }
        if ("vertical".equals(body.getUserData())) {
            return createSprite(assetManager, "Block.png");
        }
        if ("enemy".equals(body.getUserData())) {
            return createSprite(assetManager, "RaumSprite.png");
        }
        return null;
    }
    private static Sprite createSprite(AssetManager assetManager, String textureName) {
        Sprite sprite = new Sprite(assetManager.get(textureName, Texture.class));
        sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
        return sprite;
    }
}