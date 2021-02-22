package com.mygdx.project_four;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.project_four.loaders.BuildFloorBodies;
import com.mygdx.project_four.loaders.TiledObjectBodyBuilder;
import com.mygdx.project_four.loaders.buildBird;

public class GameScreen extends ScreenAdapter {

    private Array<Body> toRemove = new Array<>();

    //Set up screen dimensions of the screen
    private static final float WORLD_WIDTH = 960;
    private static final float WORLD_HEIGHT = 544;
    private static final float UNITS_PER_METER = 16;
    private static float UNIT_WIDTH = WORLD_WIDTH / UNITS_PER_METER;
    private static float UNIT_HEIGHT = WORLD_HEIGHT / UNITS_PER_METER;

    //================================= Data for Arc ===============================================
    //Sets bound on how far it can be pulled
    private static final float MAX_DISTANCE = 100;
    //Sets bounds of how far the player can move the angle
    private static final float UPPER_ANGLE = 3 * MathUtils.PI / 2f;
    private static final float LOWER_ANGLE = MathUtils.PI / 2f;

    //Holds the position of the firing point
    private final Vector2 anchor = new Vector2(convertMetresToUnits(10), convertMetresToUnits(12));
    private final Vector2 firingPosition = anchor.cpy();
    //Holds the distance and angle user has selected
    private float distance;
    private float angle;

    //==============================================================================================


    private ObjectMap<Body, Sprite> sprites = new ObjectMap<>();
    private Sprite squirrel;
    private Sprite staticAcorn;

    //World from the Box2D engine
    private World world;
    //Draws wireframe in the Box2D engine
    private Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
    private NuttyContactListener nuttyContactListener = new NuttyContactListener();
    //
    private Body body;

    //Set up what we look through
    private Viewport viewport;
    private Camera camera;
    private OrthographicCamera box2dCam;


    //Set up items used for drawing
    private SpriteBatch batch = new SpriteBatch();
    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    //The wrapper we use to keep track of things
    private final ProjectFour projectFour;

    //Music and SFX
    private Music music;
    private Sound laserSound;

    //Tiled
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;

    /*
    Input: RaumGame
    Output: Void
    Purpose: Gets the wrapper data
    */
    public GameScreen(ProjectFour projectFour){this.projectFour = projectFour;}

    /*
    Input: width, height
    Output: Void
    Purpose: Resizes the game to fit the screen
    */
    @Override
    public void resize(int width, int height){viewport.update(width, height);}

    /*
    Input: Void
    Output: Void
    Purpose: Sets up all the items before the screen starts
    */
    @Override
    public void show(){
        showCamera();       //Set up camera
        showBox2D();
        showObjects();      //Set up everything else
        showInput();
        showTextures();
    }

    private void showInput(){
        //Listens for the user touching the screen and calculates the movement
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDragged(int screenX, int screenY, int
                    pointer) {
                calculateAngleAndDistanceForBullet(screenX, screenY);
                return true;
            }

            //Listen for the user letting go of the screen
            @Override
            public boolean touchUp(int screenX, int screenY, int pointer,
                                   int button) {
                new createBullet(world, distance, angle, firingPosition, projectFour, sprites);
                firingPosition.set(anchor.cpy());
                return true;
            }
        });

        world.setContactListener(new NuttyContactListener());
    }

    /*
    Input: Void
    Output: Void
    Purpose: Used to set up the camera
    */
    public void showCamera(){
        camera = new OrthographicCamera();
        camera.position.set(WORLD_WIDTH/2, WORLD_HEIGHT/2f, 0);
        camera.update();

        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply(true);

        box2dCam = new OrthographicCamera(UNIT_WIDTH, UNIT_HEIGHT);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets up all the showBox2D object
    */
    private void showBox2D(){
        //The vector describes the gravity, so we have -10f going in the y axis and there is no x
        //gravity
        world = new World(new Vector2(0, -10f), true);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets up the map, player, collectibles and sounds
    */
    public void showObjects(){
        tiledMap = projectFour.getAssetManager().get("map.tmx");
        orthogonalTiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, batch);
        orthogonalTiledMapRenderer.setView((OrthographicCamera) camera);

        //Creates the objects from tiled map to be draw and interacted in the world
        TiledObjectBodyBuilder.buildBuildingBodies(tiledMap, world);
        BuildFloorBodies.buildFloorBodies(tiledMap, world);
        buildBird.buildBirdBodies(tiledMap, world);

    }

    public void showTextures(){
        Array<Body> bodies = new Array<>();
        world.getBodies(bodies);
        //Textures all the items gotten from tiled
        for (Body body : bodies) {
            Sprite sprite = SpriteGenerator.generateSpriteForBody(projectFour.getAssetManager(), body);
            if (sprite != null) sprites.put(body, sprite);
        }

        squirrel = new Sprite(projectFour.getAssetManager().get("RaumSprite.png", Texture.class));
        squirrel.setPosition(32, 64);
        staticAcorn = new Sprite(projectFour.getAssetManager().get("Skull.png", Texture.class));
    }

    /*
    Input: Delta
    Output: Void
    Purpose: Central function from which everything is done from
    */
    @Override
    public void render(float delta){
        update(delta);          //Update all objects
        clearScreen();          //Clear screen
        draw();                 //Draw
        drawDebug();            //Draw wireframe
    }

    /**
     * Gives Meters to Pixel units
     * @param metres gives meters
     * @return gives back the pixels traveled
     */
    private float convertMetresToUnits(float metres) { return metres * UNITS_PER_METER; }

    /**
     * Gives the angle between the anchor point and current finger point, to determine direction
     * of the bullet
     * @return the angle between the anchor and the finger
     */
    private float angleBetweenTwoPoints() {
        float angle = MathUtils.atan2(anchor.y - firingPosition.y,
                anchor.x - firingPosition.x);
        angle %= 2 * MathUtils.PI;
        if (angle < 0) angle += 2 * MathUtils .PI2;
        return angle;
    }

    /**Calculate the distance between the anchor and the finger to set the power of the bullet's speed
     * @return distance between anchor and finger
     */
    private float distanceBetweenTwoPoints() {
        return (float) Math.sqrt(((anchor.x - firingPosition.x) *
                (anchor.x - firingPosition.x)) + ((anchor.y - firingPosition.y)
                * (anchor.y - firingPosition.y)));
    }

    /**
     * Takes the finger and anchor postions and translates them into angle and power for bullet
     * @param screenX
     * @param screenY
     */
    private void calculateAngleAndDistanceForBullet(int screenX, int
            screenY) {
        firingPosition.set(screenX, screenY);
        viewport.unproject(firingPosition);
        distance = distanceBetweenTwoPoints();
        angle = angleBetweenTwoPoints();
        if (distance > MAX_DISTANCE) {
            distance = MAX_DISTANCE;
        }
        if (angle > LOWER_ANGLE) {
            if (angle > UPPER_ANGLE) {
                angle = 0;
            } else {angle = LOWER_ANGLE;
            }
        }
        firingPosition.set(anchor.x + (distance * -
                MathUtils.cos(angle)), anchor.y + (distance * -
                MathUtils.sin(angle)));
    }

    /*
    Input: Void
    Output: Void
    Purpose: Central update function, updates player, camera, and collisons
    */
    public void update(float delta){
        updateBody(delta);
        updateCamera();
        clearDeadBodies();
        updateSpritePositions();
    }

    /**
     * Updates the rotation and positions of the fired skulls
     */
    private void updateSpritePositions() {
        for (Body body : sprites.keys()) {
            Sprite sprite = sprites.get(body);
            sprite.setPosition(
                    convertMetresToUnits(body.getPosition().x) -
                            sprite.getWidth() / 2f,
                    convertMetresToUnits(body.getPosition().y) -
                            sprite.getHeight() / 2f);
            sprite.setRotation(MathUtils.radiansToDegrees *
                    body.getAngle());
        }
        staticAcorn.setPosition(firingPosition.x -
                staticAcorn.getWidth() / 2f, firingPosition.y -
                staticAcorn.getHeight() / 2f);
    }

    private void clearDeadBodies() {
        for (Body body : toRemove) {
            sprites.remove(body);
            world.destroyBody(body);
        }
        toRemove.clear();
    }

    /*
    Input: Void
    Output: Void
    Purpose: Updates the body
    */
    public void updateBody(float delta){
        world.step(delta, 6, 2);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Updates the camera position
    */
    public void updateCamera(){
        //Updates the camera that is looking at the tiled in data
        box2dCam.position.set(UNIT_WIDTH / 2, UNIT_HEIGHT / 2, 0);
        box2dCam.update();
    }

    /*
    Input: Void
    Output: Void
    Purpose: Clears screen black
    */
    public void clearScreen(){
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.b, Color.BLUE.g, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws everything
    */
    public void draw(){
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);
        orthogonalTiledMapRenderer.render();
        batch.begin();
        for (Sprite sprite : sprites.values()) {
            sprite.draw(batch);
        }
        squirrel.draw(batch);
        staticAcorn.draw(batch);
        batch.end();
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws wireframe
    */
    public void drawDebug(){
        debugRenderer.render(world, box2dCam.combined);
        shapeRenderer.setProjectionMatrix(camera.projection);
        shapeRenderer.setTransformMatrix(camera.view);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.rect(anchor.x - 5, anchor.y - 5, 10, 10);
        shapeRenderer.rect(firingPosition.x - 5, firingPosition.y - 5,
                10, 10);
        shapeRenderer.line(anchor.x, anchor.y, firingPosition.x,
                firingPosition.y);
        shapeRenderer.end();
    }

    public class NuttyContactListener implements ContactListener{

        /**
         *Implements the contact listen to see if any of the bird enemies are touched
         * @param contact provided by the world when it's connected
         */
        public void beginContact(Contact contact){
            if (contact.isTouching()) {
                //Looks at two fixtures that are touching
                Fixture attacker = contact.getFixtureA();
                Fixture defender = contact.getFixtureB();
                WorldManifold worldManifold = contact.getWorldManifold();
                //Checks user data to see if one of them being touched has enemy tag on them
                if ("enemy".equals(defender.getUserData())) {
                    Vector2 vel1 = attacker.getBody().
                            getLinearVelocityFromWorldPoint(worldManifold.getPoints()[0]);
                    Vector2 vel2 = defender.getBody().
                            getLinearVelocityFromWorldPoint(worldManifold.getPoints()[0]);
                    Vector2 impactVelocity = vel1.sub(vel2);
                    //If the speed at which it hit is enough make it go away
                    if (Math.abs(impactVelocity.x) > 1 ||
                            Math.abs(impactVelocity.y) > 1) {
                        toRemove.add(defender.getBody());
                    }
                }
            }
        }

        @Override
        public void endContact(Contact contact) {

        }

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {

        }

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {

        }
    }
}
