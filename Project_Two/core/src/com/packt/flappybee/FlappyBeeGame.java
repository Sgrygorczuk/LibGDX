/*
FlappyBee.java by Sebastian Grygorczuk
March 2020
sgrygorczuk@gmail.com

This Project Covers the LibGDX Game Development By Example Chapter 3 and 4 of constructing a
basic flappy bird
 */

package com.packt.flappybee;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import javax.xml.soap.Text;

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

	/*
	Textures
 	*/
	private Texture background;
	private Texture topTower;
	private Texture bottomTower;
	private Texture flappeeTexture;

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
	int score = 0;

	/*
	Bitmap and GlyphLayout
	 */
	private BitmapFont bitmapFont;
	private GlyphLayout glyphLayout;

	/*
	Flags
	 */
	boolean debugMode = false;

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
		//Textures
		background = new Texture(Gdx.files.internal("Space.png"));
		topTower = new Texture(Gdx.files.internal("T11.png"));
		bottomTower = new Texture(Gdx.files.internal("T22.png"));
		flappeeTexture = new Texture(Gdx.files.internal("Skulls.png"));

		//Flappee's initial position
		flappee = new Flappee(flappeeTexture);
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

	private void setDebugMode() {
		if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1) && !debugMode) {debugMode = true;}
		else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1) && debugMode) {debugMode = false;}
	}

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
	Purpose: Goes through each flower in the array and updates the poposition	*/
	private void updateFlowers(float delta){ for(Flower flower : flowers){ flower.update(delta); } }

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

	void restart(){
		flappee.setPosition(WORLD_WIDTH/4,WORLD_HEIGHT/2);
		flowers.clear();
		score = 0;
	}

	private boolean checkForCollision(){
		for (Flower flower : flowers){
			if(flower.isFlappeeColliding(flappee)){return true;}
		}
		return false;
	}

	private void draw() {
		//Viewport/Camera projection
		batch.setProjectionMatrix(camera.projection);
		batch.setTransformMatrix(camera.view);
		//Batch setting up texture
		batch.begin();
		batch.draw(background, 0 ,0);
		flappee.draw(batch);
		drawScore();
		drawFlower();
		batch.end();
	}

	private void drawFlower(){
		for(Flower flower : flowers){
			flower.draw(batch);
		}
	}

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

	/*
	Texture
	 */
	private final TextureRegion [][] flappeeTextures;
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
	Flappee(Texture flappeeTexture) {
		this.flappeeTextures = new TextureRegion(flappeeTexture).split(TILE_WIDTH,TILE_HEIGHT);

		animation = new Animation(FRAME_DURATION, flappeeTextures[0][0],
				flappeeTextures[0][1],flappeeTextures[0][2],flappeeTextures[0][3]);
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
	private float y = 0;

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
	private final Texture floorTexture;
	private final Texture ceilingTexture;

	/*
	Input: Delta
	Output: Void
	Purpose: Flower constructor, creates the rectangle and circle on top, and places it at -400 to 0 y
	*/
	Flower(Texture floorTexture, Texture ceilingTexture){
		this.ceilingTexture = ceilingTexture;
		this.floorTexture = floorTexture;

		this.y = MathUtils.random(HEIGHT_OFFSET);
		this.floorCollisionRectangle = new Rectangle(x,y,COLLISION_RECTANGLE_WIDTH, COLLISION_RECTANGLE_HEIGHT);
		this.floorCollisionCircle = new Circle((x + COLLISION_RECTANGLE_WIDTH)/2,y + COLLISION_RECTANGLE_HEIGHT ,COLLISION_CIRCLE_RADIUS);

		this.ceilingCollisionRectangle = new Rectangle(x,y + COLLISION_RECTANGLE_HEIGHT + DISTANCE_BETWEEN_FLOOR_AND_CEILING,COLLISION_RECTANGLE_WIDTH, COLLISION_RECTANGLE_HEIGHT);
		this.ceilingCollisionCircle = new Circle((x + COLLISION_RECTANGLE_WIDTH)/2,y + COLLISION_RECTANGLE_HEIGHT + DISTANCE_BETWEEN_FLOOR_AND_CEILING,COLLISION_CIRCLE_RADIUS);
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

	private void updateCollisionCircle(){
		floorCollisionCircle.setX(x + floorCollisionRectangle.width/2);
		ceilingCollisionCircle.setX(x + floorCollisionRectangle.width/2);
	}

	private void updateCollisionRectangle(){
		floorCollisionRectangle.setX(x);
		ceilingCollisionRectangle.setX(x);
	}

	public boolean isFlappeeColliding(Flappee flapee) {
		Circle flappeeCollisionCircle = flapee.getCollisionCircle();
		return Intersector.overlaps(flappeeCollisionCircle, floorCollisionCircle) ||
				Intersector.overlaps(flappeeCollisionCircle, ceilingCollisionCircle) ||
				Intersector.overlaps(flappeeCollisionCircle, ceilingCollisionRectangle) ||
				Intersector.overlaps(flappeeCollisionCircle, floorCollisionRectangle);
	}

	void update(float delta){ setPosition(x-(MAX_SPEED_PER_SECOND * delta)); }

	boolean isPointClaimed(){return pointClaimed;}

	void markPointClaimed(){ pointClaimed = true;}

	void draw(SpriteBatch batch){
		drawFloor(batch);
		drawCeiling(batch);
	}

	void drawFloor(SpriteBatch batch){
		float textureX = floorCollisionCircle.x - floorTexture.getWidth()/2;
		float textureY = floorCollisionRectangle.getY() - COLLISION_CIRCLE_RADIUS;
		batch.draw(floorTexture, textureX, textureY);
	}

	void drawCeiling(SpriteBatch batch){
		float textureX = ceilingCollisionCircle.x - ceilingTexture.getWidth()/2;
		float textureY = ceilingCollisionRectangle.getY() - COLLISION_CIRCLE_RADIUS;
		batch.draw(ceilingTexture, textureX, textureY);
	}


	void drawDebug(ShapeRenderer shapeRenderer) {
		shapeRenderer.circle(floorCollisionCircle.x, floorCollisionCircle.y, floorCollisionCircle.radius);
		shapeRenderer.rect(floorCollisionRectangle.x, floorCollisionRectangle.y, floorCollisionRectangle.width, floorCollisionRectangle.height);
		shapeRenderer.circle(ceilingCollisionCircle.x, ceilingCollisionCircle.y, ceilingCollisionCircle.radius);
		shapeRenderer.rect(ceilingCollisionRectangle.x, ceilingCollisionRectangle.y, ceilingCollisionRectangle.width, ceilingCollisionRectangle.height);
	}
}

class StartScreen extends ScreenAdapter{
	private static final float WORLD_WIDTH = 480;
	private static final float WORLD_HEIGHT = 640;

	private Stage stage;

	private Texture backgroundTexture;
	private Texture playUpTexture;
	private Texture playDownTexture;
	private Texture titleTexture;

	private final Game game;
	public StartScreen(Game game) { this.game = game; }

	public void show(){
		stage = new Stage(new FitViewport(WORLD_WIDTH,WORLD_HEIGHT));
		Gdx.input.setInputProcessor(stage);

		backgroundTexture = new Texture(Gdx.files.internal("Space.png"));
		Image background = new Image(backgroundTexture);
		stage.addActor(background);

		playDownTexture = new Texture(Gdx.files.internal("PlayDown.png"));
		playUpTexture = new Texture(Gdx.files.internal("PlayUp.png"));
		ImageButton play = new ImageButton(new TextureRegionDrawable(new TextureRegion(playUpTexture)),
				new TextureRegionDrawable(playDownTexture));
		play.setPosition(WORLD_WIDTH/2, WORLD_HEIGHT/4, Align.center);
		stage.addActor(play);

		play.addListener(new ActorGestureListener() {
			@Override
			public void tap(InputEvent event, float x, float y, int count,
							int button) {
				super.tap(event, x, y, count, button);
				game.setScreen(new GameScreen());
				dispose();
			}
		});


		titleTexture = new Texture(Gdx.files.internal("Title.png"));
		Image title = new Image(titleTexture);
		title.setPosition(WORLD_WIDTH/2, 3*WORLD_HEIGHT/4,Align.center);
		stage.addActor(title);
	}

	public void resize(int width, int height){ stage.getViewport().update(width,height,true);}

	public void render(float delta){
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void dispose() {
		stage.dispose();
		backgroundTexture.dispose();
		playUpTexture.dispose();
		playDownTexture.dispose();
		titleTexture.dispose();
	}

}


public class FlappyBeeGame extends Game {
	@Override
	public void create () {
		//Calls game screen
		setScreen(new StartScreen(this));
	}
}
