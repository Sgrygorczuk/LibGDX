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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
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

	/*
	Flappee Bee Object -- Object that deals with Flappee Bee's operations
	 */
	private Flappee flappee = new Flappee();

	/*
	Array of Flowers -- Array of flowers that act as our obstacles and the distance between each pair
	 */
	private Array<Flower> flowers = new Array<>();
	private static final float GAP_BETWEEN_FLOWERS = 200F;

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
		//Flappee's initial position
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

		//Viewport/Camera projection
		batch.setProjectionMatrix(camera.projection);
		batch.setTransformMatrix(camera.view);

		//ShapeRender, drawing lines
		shapeRenderer.setProjectionMatrix(camera.projection);					//Screen set up
		shapeRenderer.setTransformMatrix(camera.view);							//Screen set up
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);						//Sets up to draw lines
		flappee.drawDebug(shapeRenderer);										//Draws flappee
		for (Flower flower : flowers) {flower.drawDebug(shapeRenderer);}		//Draws flowers
		shapeRenderer.end();

		//Batch setting up texture
		batch.begin();
		batch.end();

		//Creation and destruction of new flowers
		checkIfNewFlowerIsNeeded();
		removeFlowerIfPassed();

		//Updates position of Flappe and Flowers
		updateFlappe(delta);
		updateFlowers(delta);
	}

	/*
	Input: Void
	Output: Void
	Purpose: Creates a new flower and adds it to the array
	*/
	private void createNewFlower(){
		Flower newFlower = new Flower();
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
	private void updateFlappe(float delta){
		flappee.update();
		if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {flappee.flyUp();}
		blockFLappeeLeavingTheWorld();
	}

	/*
	Input: Delta
	Output: Void
	Purpose: Goes through each flower in the array and updates the poposition	*/
	private void updateFlowers(float delta){ for(Flower flower : flowers){ flower.update(delta); } }

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
	private float x = 0;
	private float y = 0;

	/*
	Movement
	 */
	private static final float FLY_ACCELERATION = 5f;
	private static final float DIVE_ACCELERATION = 0.30f;
	private float ySpeed = 0;

	/*
	Input: Void
	Output: Returns X
	Purpose: Gives the Screen X coordinate
	*/
	float getX(){ return x; }

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
	Flappee() { collisionCircle = new Circle(x,y, COLLISION_RADIUS); }

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
	void update(){
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
	private static final float COLLISION_RECTANGLE_WIDTH = 13f;
	private static final float COLLISION_RECTANGLE_HEIGHT = 447f;
	private static final float HEIGHT_OFFSET = -400f;
	private static final float COLLISION_CIRCLE_RADIUS = 33f;

	/*
	Objects
	 */
	private final Circle collisionCircle;
	private final Rectangle collisionRectangle;

	//Position
	private float x = 0;
	private float y = 0;

	/*
	Movment
	 */
	private static final float MAX_SPEED_PER_SECOND = 100f;

	/*
	Input: Delta
	Output: Void
	Purpose: Flower constructor, creates the rectangle and circle on top, and places it at -400 to 0 y
	*/
	Flower(){
		this.y = MathUtils.random(HEIGHT_OFFSET);
		this.collisionRectangle = new Rectangle(x,y,COLLISION_RECTANGLE_WIDTH, COLLISION_RECTANGLE_HEIGHT);
		this.collisionCircle = new Circle((x + COLLISION_RECTANGLE_WIDTH)/2,y + COLLISION_RECTANGLE_HEIGHT ,COLLISION_CIRCLE_RADIUS);
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

	private void updateCollisionCircle(){ collisionCircle.setX(x + collisionRectangle.width/2); }

	private void updateCollisionRectangle(){ collisionRectangle.setX(x); }

	void update(float delta){ setPosition(x-(MAX_SPEED_PER_SECOND * delta)); }

	void drawDebug(ShapeRenderer shapeRenderer) {
		shapeRenderer.circle(collisionCircle.x, collisionCircle.y, collisionCircle.radius);
		shapeRenderer.rect(collisionRectangle.x, collisionRectangle.y, collisionRectangle.width, collisionRectangle.height);
	}
}

public class FlappyBeeGame extends Game {
	@Override
	public void create () {
		//Calls game screen
		setScreen(new GameScreen());
	}
}
