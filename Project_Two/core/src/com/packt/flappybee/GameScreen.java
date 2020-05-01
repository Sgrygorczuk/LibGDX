package com.packt.flappybee;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

class GameScreen extends ScreenAdapter {

    /*
    Dimensions -- Units the screen has
     */
    private static final float WORLD_WIDTH = 480;
    private static final float WORLD_HEIGHT = 640;

    /*
    Image processing -- Objects that modify the view and textures
     */
    private ShapeRenderer shapeRenderer; //Creates the wire frames
    private Viewport viewport;			 //The screen where we display things
    private Camera camera;				 //The camera viewing the viewport
    private SpriteBatch batch;			 //Batch that holds all of the textures

    //Asset Manager helps us to just keep the one used textures instead of reloading and disposing of them
    private final AssetManager assetManager = new AssetManager();

    /*
    Textures
     */
    private TextureRegion background;
    private TextureRegion topTower;
    private TextureRegion bottomTower;
    private TextureRegion flappedTexture;

    /*
    Flappee Bee Object -- Object that deals with Flappee Bee's operations
     */
    private Flappee flappee;

    /*
    Array of Flowers -- Array of flowers that act as our obstacles and the distance between each pair
     */
    private Array<Flower> flowers = new Array<>();
    private static final float GAP_BETWEEN_FLOWERS = 200F;

    /*
    User Info
     */
    private int score = 0;

    /*
    Bitmap and GlyphLayout
     */
    private BitmapFont bitmapFont;
    private GlyphLayout glyphLayout;

    /*
    Flags
     */
    private boolean debugMode = false;


    FlappyBeeGame flappyBeeGame;

    GameScreen(FlappyBeeGame flappyBeeGame){
        this.flappyBeeGame = flappyBeeGame;
    }

    /*
    Input: The width and height of the screen
    Output: Void
    Purpose: Updates the dimensions of the screen
    */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Displays the screen to the user
    */
    @Override
    public void show() {


        //Calls in the big texture pack
        TextureAtlas textureAtlas = flappyBeeGame.getAssetManager().get("flappy_bee_assets.atlas");
        //Connects the images, no need for image extensions anymore
        background = textureAtlas.findRegion("Space");
        bottomTower = textureAtlas.findRegion("T22");
        topTower = textureAtlas.findRegion("T11");
        flappedTexture = textureAtlas.findRegion("Skulls");

        //Flappee's initial position
        flappee = new Flappee(flappedTexture);
        flappee.setPosition(WORLD_WIDTH/4, WORLD_HEIGHT/2);

        //Camera and View display
        camera = new OrthographicCamera();									//Sets a 2D view
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);	//Places the camera in the center of the view port
        camera.update();													//Updates the camera
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);		//

        //Sets up the wireframe render
        shapeRenderer = new ShapeRenderer();

        //Sets up the texture with the images
        batch = new SpriteBatch();

        //BitmapFont and GlyphLayout
        bitmapFont = new BitmapFont();
        bitmapFont = flappyBeeGame.getAssetManager().get("font.fnt");
        glyphLayout = new GlyphLayout();

    }

    /*
    Input: Void
    Output: Void
    Purpose: Updates all the variables on the screen
    */
    @Override
    public void render(float delta) {
        //Wipes screen
        clearScreen();

        draw();

        setDebugMode();

        //ShapeRender, drawing lines
        if(debugMode) {
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.setProjectionMatrix(camera.projection);                    //Screen set up
            shapeRenderer.setTransformMatrix(camera.view);                            //Screen set up
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);                        //Sets up to draw lines
            flappee.drawDebug(shapeRenderer);                                        //Draws flappee
            for (Flower flower : flowers) {
                flower.drawDebug(shapeRenderer);
            }        //Draws flowers
            shapeRenderer.end();
        }

        //Creation and destruction of new flowers
        checkIfNewFlowerIsNeeded();
        removeFlowerIfPassed();

        //Updates position of Flappee and Flowers
        updateFlappee(delta);
        updateFlowers(delta);
        updateScore();
        if(checkForCollision()){ restart();}
    }

    /*
    Input: Void
    Output: Void
    Purpose: Turns on and off the debug mode
    */
    private void setDebugMode() { if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)){debugMode = !debugMode;} }

    /*
    Input: Void
    Output: Void
    Purpose: Creates a new flower and adds it to the array
    */
    private void createNewFlower(){
        Flower newFlower = new Flower(topTower, bottomTower);
        newFlower.setPosition(WORLD_WIDTH + newFlower.getWidth());
        flowers.add(newFlower);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Checks if there is need to create a new flower if does calls createNewFlower()
    */
    private void checkIfNewFlowerIsNeeded(){
        //If no flower exits
        if(flowers.size == 0){
            createNewFlower();
        }
        //If flowers are GAP_BETWEEN_FLOWERS apart
        else{
            Flower flower = flowers.peek();
            if(flower.getX() < WORLD_WIDTH - GAP_BETWEEN_FLOWERS){ createNewFlower(); }
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Removes flower from array if its off the screen
    */
    private void removeFlowerIfPassed(){
        if(flowers.size > 0){																					//Checks if we have more than 0 flowers
            Flower firstFlower = flowers.first();																//Grabs the first flower
            if(firstFlower.getX() < - firstFlower.getWidth()){ flowers.removeValue(firstFlower,true); }	//If x is off screen remove from array
        }
    }

    /*
    Input: Delta
    Output: Void
    Purpose: Updates the position of the bee
    */
    private void updateFlappee(float delta){
        flappee.update(delta);
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {flappee.flyUp();}
        blockFLappeeLeavingTheWorld();
    }

    /*
    Input: Delta
    Output: Void
    Purpose: Goes through each flower in the array and updates the position
    */
    private void updateFlowers(float delta){ for(Flower flower : flowers){ flower.update(delta); } }

    /*
    Input: Void
    Output: Void
    Purpose: Claims a flower if flappe passes over it and adds it to the score
    */
    private void updateScore(){
        Flower flower = flowers.first();
        if(flower.getX() < flappee.getX() && !flower.isPointClaimed()) {
            flower.markPointClaimed();
            score++;
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Stops the Circle from going past the bottom of the screen
    */
    private void blockFLappeeLeavingTheWorld(){
        //Gets the Y, sets the min: Radius away from bottom, max: World Height - Radius
        flappee.setPosition(flappee.getX(), MathUtils.clamp(flappee.getY(), flappee.getRadius(), WORLD_HEIGHT - flappee.getRadius()) );
    }


    /*
    Input: Void
    Output: Void
    Purpose: Sets flappee at start of screen, clears all flowers from scree nad resets score to zero
    */
    private void restart(){
        flappee.setPosition(WORLD_WIDTH/4,WORLD_HEIGHT/2);
        flowers.clear();
        score = 0;
    }

    /*
    Input: Void
    Output: Void
    Purpose: Checks if flappe hit any of the flowers
    */
    private boolean checkForCollision(){
        for (Flower flower : flowers){
            if(flower.isFlappeeColliding(flappee)){return true;}
        }
        return false;
    }

    /*
    Input: Void
    Output: Void
    Purpose: Main function that draws everything
    */
    private void draw() {
        //Restart the count to be 0 each time
        batch.totalRenderCalls = 0;
        //Viewport/Camera projection
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);
        //Batch setting up texture
        batch.begin();
        batch.draw(background, 0 ,0);
        flappee.draw(batch);
        drawFlower();
        drawScore();
        batch.end();
        //Checks how much memory we take up with these images
        Gdx.app.log("Debug", "" + batch.totalRenderCalls);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Goes through the flower array and draws all of them
    */
    private void drawFlower(){
        for(Flower flower : flowers){
            flower.draw(batch);
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws the text that shows the score
    */
    private void drawScore(){
        String scoreAsString = Integer.toString(score);
        glyphLayout.setText(bitmapFont, scoreAsString);
        bitmapFont.draw(batch, scoreAsString,
                viewport.getWorldWidth()/2 - glyphLayout.width/2,
                (4*viewport.getWorldHeight()/ 5) - glyphLayout.height/2);
    }


    /*
    Input: Void
    Output: Void
    Purpose: Updates all the variables on the screen
    */
    private void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a); //Sets color to black
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);										 //Sends it to the buffer
    }
}