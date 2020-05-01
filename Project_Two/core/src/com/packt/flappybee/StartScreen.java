package com.packt.flappybee;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

class StartScreen extends ScreenAdapter {
    //Screen dimensions
    private static final float WORLD_WIDTH = 480;
    private static final float WORLD_HEIGHT = 640;

    //Stage that the buttons are on
    private Stage stage;

    //Textures
    private Texture backgroundTexture;
    private Texture playUpTexture;
    private Texture playDownTexture;
    private Texture titleTexture;

    //Game set up
    private final FlappyBeeGame game;
    StartScreen(FlappyBeeGame game) { this.game = game; }

    /*
    Input: Void
    Output: Void
    Purpose: Sets up the screen and buttons
    */
    public void show(){
        //Set up the stage and give it the input processing
        stage = new Stage(new FitViewport(WORLD_WIDTH,WORLD_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        //Set up the background image and add it to the stage
        backgroundTexture = new Texture(Gdx.files.internal("Space.png"));
        Image background = new Image(backgroundTexture);
        stage.addActor(background);

        //Set up the Button and add it to the stage
        playDownTexture = new Texture(Gdx.files.internal("PlayDown.png"));
        playUpTexture = new Texture(Gdx.files.internal("PlayUp.png"));
        ImageButton play = new ImageButton(new TextureRegionDrawable(new TextureRegion(playUpTexture)),
                new TextureRegionDrawable(playDownTexture));
        play.setPosition(WORLD_WIDTH/2, WORLD_HEIGHT/4, Align.center);
        stage.addActor(play);

        //Give the button click ability that starts the game
        play.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                game.setScreen(new LoadingScreen(game));
                dispose();
            }
        });

        //Give the button click ability that starts the game
        titleTexture = new Texture(Gdx.files.internal("Title.png"));
        Image title = new Image(titleTexture);
        title.setPosition(WORLD_WIDTH/2, 3*WORLD_HEIGHT/4,Align.center);
        stage.addActor(title);
    }

    /*
    Input: Dimensions
    Output: Void
    Purpose: Resize the screen depending on the the window size
    */
    public void resize(int width, int height){ stage.getViewport().update(width,height,true);}

    /*
    Input: Dimensions
    Output: Void
    Purpose: Resize the screen depending on the the window size
    */
    public void render(float delta){
        //Makes it run on delta time
        stage.act(delta);
        stage.draw();
    }

    /*
    Input: Void
    Output: Void
    Purpose: Gets rid of textures and stages
    */
    @Override
    public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();
        playUpTexture.dispose();
        playDownTexture.dispose();
        titleTexture.dispose();
    }
}
