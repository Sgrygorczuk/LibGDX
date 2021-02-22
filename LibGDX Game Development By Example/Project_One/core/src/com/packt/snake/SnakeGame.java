/*
SnakeGame.java by Sebastian Grygorczuk
March 2020
sgrygorczuk@gmail.com

This Project Covers the LibGDX Game Development By Example Chapter 1 and 2 of constructing a
basic snake game
 */

package com.packt.snake;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/*
Input: Void
Output: Void
Purpose: This class manages all of the image processing that is occurring in the game,
rendering the screen and then sending the final product to the SnakeGame class
*/
class GameScreen extends ScreenAdapter {

	/*
	Flags
	 */
	private  boolean directionSet = false;	//A flag to keep track of if the user has input a direction

	/*
	ViewPorts
	 */
	private static final float WORLD_WIDTH = 640;
	private static final float WORLD_HEIGHT = 480;
	private Viewport viewport;
	private Camera camera;

	/*
	Games States dictate how the game should be behaving, we always start in Playing state here
	 */
	private enum STATE {PLAYING, GAME_OVER}
	private STATE state = STATE.PLAYING;

	/*
	Bitmap font variable that store the text output 
	 */
	private BitmapFont bitmapFont;					//A variable of font
	private GlyphLayout layout = new GlyphLayout(); //Layout that will define the size of the font
	private static final String GAME_OVER_TEXT = "Game Over... Tap space to restart!";
	private Integer Score = 0;
	private Integer POINTS_PER_APPLE = 20;

	/*
	Creates the shape renderer object that will draw rectangles on the screen with cell size of 32x32 pixel
	 */
	private ShapeRenderer shapeRenderer;
	private static final int GRID_CELL = 32;

	/*
	The batch section that creates all of the different texture our images will be attached to
	 */
	private SpriteBatch batch; //Batch is the thing connects all of the textures and images
	private Texture snakeHead; //The snakehead texture
	private Texture snakeBody; //The snakebody texture
	private Texture apple;     //The apple texture

	/*
	Variables pertain to the apples existence and position
	 */
	private boolean appleAvailable = false; //Tells us if the apple is displayed or not
	private int appleX, appleY;             //Tells us where the apple exists on the screen


	/*
	Variables pertain to the snakes movement distance in pixels and position of the head and the position of where the head was before
	 */
	private static final int SNAKE_MOVEMENT = 32; //Sets that it can only move 32 pixel at a time
	private int snakeX = 0, snakeY = 0; //Sets the initial position
	private int snakeXBeforeUpdate = 0, snakeYBeforeUpdate = 0; //Sets where the head used to be to 0,0

	/*
	Direction variables, defines what is Left,Right, Up and Down, and presets the initial movement to Right
	 */
	private static final int RIGHT = 0;
	private static final int LEFT = 1;
	private static final int UP = 2;
	private static final int DOWN = 3;
	private int snakeDirection = RIGHT; 	//User can change this to move around

	/*
	Time variables, counts down how much time has passed between the frames
	 */
	private static final float MOVE_TIME = 0.3F; //The amount of time we want to pass between frames
	private float timer = MOVE_TIME;			 //Keeps track of how much time has passed, timer-delta if timer < 0 update

	/*
	Holds all of the instances of the body part
	 */
	private Array<BodyPart> bodyParts = new Array<>();

	/*
	Input: Void
	Output: Void
	Purpose: The class stores all of the information that a body part object would need
	*/
	private class BodyPart {
		private int x, y;										//Initiates the x and y variables
		private Texture texture;								//Initiates texture variable
		BodyPart(Texture texture) { this.texture = texture; }   //Constructor initializes the texture variable

		/*
		Input: Void
		Output: Void
		Purpose: Updates the position of the body part
		*/
		void updateBodyPosition(int x, int y) {
			this.x = x;
			this.y = y;
		}

		/*
		Input: Batch
		Output: Void
		Purpose: Adds the body texture to the object unless the head is onto of it, then doesn't draw anything
		*/
		void draw(Batch batch) { if (!(x == snakeX && y == snakeY)) batch.draw(texture, x, y); }
	}

	/*
	Input: Void
	Output: Void
	Purpose: Updates the the position of the snake body part if they pass the boarders of the screen
	*/
	private void checkForOutOfBounds() {
		if (snakeX >= Gdx.graphics.getWidth()) { snakeX = 0; }                  //If snake goes past right edge shows up on left side
		if (snakeX < 0) { snakeX = (int) viewport.getWorldHeight() - SNAKE_MOVEMENT; }  //If snake goes past left edge shows up on right side
		if (snakeY >= Gdx.graphics.getHeight()) { snakeY = 0; }                 //If snake goes past up edge shows up on the down side
		if (snakeY < 0) { snakeY = (int) viewport.getWorldWidth() - SNAKE_MOVEMENT; } //If snakes goes past down edge shows up on the up side
	}

	/*
	Input: Void
	Output: Void
	Purpose: Checks if the new direction is not opposite of the old direction
	*/
	private void updateIfNotOppositeDirection(int newSnakeDirection, int oppositeDirection) {
		if (snakeDirection != oppositeDirection || bodyParts.size == 0) {snakeDirection = newSnakeDirection;}
	}

	/*
	Input: Void
	Output: Void
	Purpose: Goes through all possible choice for the input given in and see if such action is allowed
	*/
	private void updateDirection(int newSnakeDirection) {
		if (!directionSet && snakeDirection != newSnakeDirection) {
			directionSet = true;		//Says that the user has made a choice
			switch (newSnakeDirection) {
				case LEFT: {
					updateIfNotOppositeDirection(newSnakeDirection, RIGHT);
				}
				break;
				case RIGHT: {
					updateIfNotOppositeDirection(newSnakeDirection, LEFT);
				}
				break;
				case UP: {
					updateIfNotOppositeDirection(newSnakeDirection, DOWN);
				}
				break;
				case DOWN: {
					updateIfNotOppositeDirection(newSnakeDirection, UP);
				}
				break;
			}
		}
	}

	/*
	Input: Void
	Output: Void
	Purpose: Listens to the user keyboard inputs and changes the direction variable accordingly
	User can use WASD or Arrow keys to change direction
	*/
	private void queryInput(){
		//Listens for a key press, if pressed the variable is true, if not pressed variable is false
		boolean lPressed = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
		boolean rPressed = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);
		boolean dPressed = Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN);
		boolean uPressed = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP);

		//If one of the buttons was pressed updates the direction
		if (lPressed) updateDirection(LEFT);
		if (rPressed) updateDirection(RIGHT);
		if (dPressed) updateDirection(DOWN);
		if (uPressed) updateDirection(UP);
	}

	/*
	Input: Void
	Output: Void
	Purpose: Checks for the click of the space key, if so restarts the game
	*/
	private void checkForRestart() {
		if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) { doRestart(); }
	}

	/*
	Input: Void
	Output: Void
	Purpose: Restarts all the variables to their initial state
	*/
	private void doRestart() {
		state = STATE.PLAYING;
		bodyParts.clear();
		snakeDirection = RIGHT;
		directionSet = false;
		timer = MOVE_TIME;
		Score = 0;
		snakeX = 0;
		snakeY = 0;
		snakeXBeforeUpdate = 0;
		snakeYBeforeUpdate = 0;
		appleAvailable = false;
	}

	/*
	Input: Void
	Output: Void
	Purpose: Saves current position of the snake and then updates the position of the snake based on the current set direction
	*/
	private void moveSnake() {
		//Saves the position of the snake head
		snakeXBeforeUpdate = snakeX;
		snakeYBeforeUpdate = snakeY;

		//Updates the position of the snake to new position
		switch (snakeDirection) {
			case RIGHT: {
				snakeX += SNAKE_MOVEMENT;
				return;
			}
			case LEFT: {
				snakeX -= SNAKE_MOVEMENT;
				return;
			}
			case UP: {
				snakeY += SNAKE_MOVEMENT;
				return;
			}
			case DOWN: {
				snakeY -= SNAKE_MOVEMENT;
			}
		}
	}

	/*
	Input: Void
	Output: Void
	Purpose: Only updates one body the rest don't need to be redrawn

	*/
	private void updateBodyPartsPosition() {
		//Checks if the array is empty
		if (bodyParts.size > 0) {
			//If not removes the body first body part from the array an initializes it to a local variable
			BodyPart bodyPart = bodyParts.removeIndex(0);
			//Gives the local variable the new position
			bodyPart.updateBodyPosition(snakeXBeforeUpdate, snakeYBeforeUpdate);
			//Puts the local valuable at the end of the array
			bodyParts.add(bodyPart);
		}
	}


	/*
	Input: Void
	Output: Void
	Purpose: Changes the position of the apple away from the snake
	*/
	private void checkAndPlaceApple() {
		//Checks if the apple is available
		if (!appleAvailable) {
			do {
				//Randomize the location of the apple
				appleX = MathUtils.random((int) (viewport.getWorldWidth() / SNAKE_MOVEMENT) - 1) * SNAKE_MOVEMENT;
				appleY = MathUtils.random((int) (viewport.getWorldHeight() / SNAKE_MOVEMENT) - 1) * SNAKE_MOVEMENT;
				appleAvailable = true;
			} while (appleX == snakeX && appleY == snakeY);
		}
	}

	private void addToScore(){
		Score += POINTS_PER_APPLE;
	}

	/*
	Input: Void
	Output: Void
	Purpose: Checks for if the snake passes over the apple
	*/
	private void checkAppleCollision() {
		if (appleAvailable && appleX == snakeX && appleY == snakeY) {
			BodyPart bodyPart = new BodyPart(snakeBody);
			bodyPart.updateBodyPosition(snakeX, snakeY);
			bodyParts.insert(0,bodyPart);
			addToScore();
			appleAvailable = false;
		}
	}

	/*
	Input: Void
	Output: Void
	Purpose: Checks if the head has touched any of the body parts, if the snake does it's game over
	*/
	private void checkSnakeBodyCollision() {
		for (BodyPart bodyPart : bodyParts) {
			if (bodyPart.x == snakeX && bodyPart.y == snakeY) {
				state = STATE.GAME_OVER;
				break;
			}
		}
	}

	/*
	Input: Void
	Output: Void
	Purpose: Deals with updating all of the variables of the snake:
		Position,
		Body Parts Position
		If Collided with Body Part
		If Chosen an action
	*/
	private void updateSnake(float delta) {
		if (state == STATE.PLAYING) {
			timer -= delta;                  //Sees if the frame has updated
			if (timer <= 0) {
				timer = MOVE_TIME;           //Sets the timer back to the 0.3
				moveSnake();               	 //Updates position of the snake on the screen
				checkForOutOfBounds();       //Checks if the snakes position moved past the screen if so corrects it
				updateBodyPartsPosition();   //Updates position of all of the bodyparts
				checkSnakeBodyCollision();   //Checks if head touched any of the body parts
				directionSet = false;        //Resets user's ability to input a direction
			}
		}
	}

	/*
	Input: Void
	Output: Void
	Purpose: Turns the screen black
	*/
	private void clearScreen() {
		Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	/*
	Input: Void
	Output: Void
	Purpose: Creates 32x32 pixel grids across the screen
	*/
	private void drawGrid() {
		shapeRenderer.setProjectionMatrix(camera.projection);
		shapeRenderer.setTransformMatrix(camera.view);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		for (int x = 0; x < Gdx.graphics.getWidth(); x += GRID_CELL) {
			for (int y = 0; y < Gdx.graphics.getHeight(); y += GRID_CELL) {
				shapeRenderer.rect(x,y, GRID_CELL, GRID_CELL);
			}
		}
		shapeRenderer.end();
	}

	private  void drawScore(){
		if (state == STATE.PLAYING) {
			layout.setText(bitmapFont, Score.toString());   //Connects the bitmap font and text to a layout
			bitmapFont.draw(batch,  Score.toString(), 0, 480 - layout.height); //Draws in top of left screen
		}
	}


	/*
	Input: Void
	Output: Void
	Purpose: Draws the texture onto the screen
	*/
	private void draw() {
		batch.setProjectionMatrix(camera.projection);
		batch.setTransformMatrix(camera.view);

		batch.begin();
		//Draws the snake head
		batch.draw(snakeHead, snakeX, snakeY);
		//Draws the texture for each body part in the array
		for (BodyPart bodyPart : bodyParts) { bodyPart.draw(batch); }
		//If apple is available draws apple
		if (appleAvailable) { batch.draw(apple, appleX, appleY); }
		//Adds the font
		if (state == STATE.GAME_OVER) {
			String gameOver = GAME_OVER_TEXT + "\nFinal Score:" + Score.toString();
			layout.setText(bitmapFont, gameOver);   //Connects the bitmap font and text to a layout
			bitmapFont.draw(batch, gameOver, (640 - layout.width) / 2, (480 - layout.height) / 2); //draws it in the center of the screen
		}
		drawScore();
		batch.end();
	}

	/*
	Input: Void
	Output: Void
	Purpose: Does the general calculation regarding all of the variables then updates and redraws the screen
	*/
	@Override
	public void render(float delta) {
		/*
		Now we have games states, depending on what state the game is in different things will be rendered onto the screen
		 */
		switch(state) {
			case PLAYING: {
				queryInput();			//Checks for user input to change direction
				updateSnake(delta);		//Updates Snakes position and status
				checkAppleCollision();	//Checks if the apple and snake collied
				checkAndPlaceApple();	//Places an apple on the screen
			}
			break;
			case GAME_OVER: {
				checkForRestart();
			}
			break;
		}
		clearScreen();								//Wipes the screens
		drawGrid();									//Uses ShapeRender to make a grid
		draw();										//Draws the screen
	}

	/*
	Input: Void
	Output: Void
	Purpose: Displays the new image to the user, and attaches the texture to the images.
	*/
	@Override
	public void show() {
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
		camera.update();
		viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

		shapeRenderer = new ShapeRenderer();		//Initiates shape renderer
		bitmapFont = new BitmapFont();				//Add bitmap font
		//Creates the batch
		batch = new SpriteBatch();
		//Attaches the textures to the images
		snakeHead = new Texture(Gdx.files.internal("snakehead.png"));
		snakeBody = new Texture(Gdx.files.internal("snakebody.png"));
		apple = new Texture(Gdx.files.internal("apple.png"));
	}

}


public class SnakeGame extends Game {
	@Override
	public void create() {
		//Calls game screen
		setScreen(new GameScreen());
	}
}