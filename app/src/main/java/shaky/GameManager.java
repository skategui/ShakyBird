package shaky;


import android.widget.Toast;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;

import java.util.ArrayList;

/**
 * Created by guillaumeagis on 29/03/15.
 */
public class GameManager {

    // main activity
    private MainActivity _activity;

    /**
     * user game state
     */
    private enum eStateGame {
        READY,
        PLAYING,
        DYING,
        DEAD
    }

    private TimerHandler _timer;
    private TimerHandler _timer2;

    protected float _position;

    // user current state
    private eStateGame _currentState = eStateGame.READY;
    // boolean too check if we have to change the background or not
    private boolean lastwithGravity = false;
    private Camera _camera;

    // user updateScore
    private int _score = 0;

    private ArrayList<PipePair> _pipesList = new ArrayList<PipePair>();

    private int _nbrPipesSpawn;
    private float prevX = 0;
    private float parallaxValueOffset = 0;
    private float mBirdXOffset;

    // click mode
    private int _nbrClick = 0;
    private boolean _canClick = false;
    private boolean shakeHand = false;

    public GameManager(MainActivity activity) {
        this._activity = activity;
    }

    /**
     * create camera Object
     */
    public void initializeCamera() {

        _camera = new Camera(0, 0, Config.CAMERA_WIDTH, Config.Game.CAMERA_HEIGHT) {

            @Override
            public void onUpdate(float pSecondsElapsed) {

                switch (_currentState) {
                    case READY:
                        ready();
                        break;
                    case PLAYING:
                        play();
                        break;

                    case DYING:
                        die();
                        break;
                }
                super.onUpdate(pSecondsElapsed);
            }
        };
    }

    // getter
    public Camera getCamera() {
        return _camera;
    }

    /**
     *  Make the bird jump, once the user shakes the phone during the game
     */
    public void makeItJump() {
        switch (_currentState) {
            case READY:
                startPlaying();
                break;
            case PLAYING:
                _activity.getSceneManager().getBird().jump();
                break;
        }
    }

    /**
     * Check a every moment what the state of the game is and update the view.
     *
     */
    public void updateScene() {

        switch(_currentState){
            case READY:
            case PLAYING:
                final float cameraCurrentX = _position;//_camera.getCenterX();

                if (prevX != cameraCurrentX) {

                    parallaxValueOffset +=  cameraCurrentX - prevX;
                    _activity.setParallax(parallaxValueOffset);
                    prevX = cameraCurrentX;
                }
                break;
        }
    }


    /**
     * User is read to play  : intro page
     */
    private void ready() {
        _position -= Config.Game.SCROLL_SPEED;
        _activity.getSceneManager().getBird().hover();

        if (!_activity.getResourceManager().getMusic().isPlaying()) {
            _activity.getResourceManager().getMusic().play();
        }

        this._activity.getScene().detachChild(_activity.getSceneManager().getGravityText());

        _activity.runOnUiThread( new Runnable() {
            @Override
            public void run() {
                if (_currentState == eStateGame.READY)
                {
                    makeAnimationShake();
                }
            }
        });
    }

    /**
     * The user just died
     */
    private void die() {
        float newY = _activity.getSceneManager().getBird().move(false); // get the bird to update itself
        if (newY >= Config.Game.FLOOR_BOUND) // out of screen
            dead();
    }

    /**
     * Check in function the updateScore if the user is on the space or on earth.
     * @return true, if the user is on space, false otherwise
     */
    private boolean isSpaceScene() {
        if ((_score >= 5 && _score <= 8) || (_score >= 13 && _score <= 18)  || (_score >= 23 && _score <= 28))
            return true;
        return false;
    }

    /**
     * Display the background in the space and make the bird jump with an invertion of gravity
     * @return true
     */
    private float getPositionInSpace() {
        if (lastwithGravity == false) {
            _activity.getSceneManager().changeBackground(true);
            lastwithGravity = true;
            displayChangeGravity();
        }
        return _activity.getSceneManager().getBird().move(true); // get the bird to update itself
    }

    /**
     * Display the background in the earths and make the bird jump with a normal gravity
     * Dont display the message when the user starts a game
     * @return false
     */
    private float getPositionHeart() {
        if (lastwithGravity == true) {
            _activity.getSceneManager().changeBackground(false);
            lastwithGravity = false;
            if (_score > 0)
            displayChangeGravity();
        }
        return _activity.getSceneManager().getBird().move(false); // get the bird to update itself
    }

    /**
     * the user is currently playing, check where he should be and update the scene.
     */
    private void play() {
        _position -= Config.Game.SCROLL_SPEED;

        float newY = isSpaceScene() == true ? getPositionInSpace() : getPositionHeart();

        if (newY >= Config.Game.FLOOR_BOUND)
            gameOver(); // check if it game over from twatting the floor

        // create one more pipe
        _nbrPipesSpawn++;

        if (_nbrPipesSpawn > Config.Game.PIPE_SPAWN_INTERVAL) {
            _nbrPipesSpawn = 0;
            spawnNewPipe();
        }

        renderPipesOnScreen();
    }

    /**
     * Render pipes on Screen and check if the bird has touched a pipes or not
     */
    private void renderPipesOnScreen()
    {
        // now render the _pipesList
        for (int i = 0; i < _pipesList.size(); i++) {
            PipePair pipe = _pipesList.get(i);
            if (pipe.isOnScreen()) {
                pipe.move(Config.Game.SCROLL_SPEED);
                if (pipe.collidesWith(_activity.getSceneManager().getBird().getSprite())) {
                    gameOver();
                }

                if (pipe.isCleared(mBirdXOffset)) {
                    updateScore();
                }
            } else {
                pipe.destroy();
                _pipesList.remove(pipe);
            }
        }
    }

    /**
     * update updateScore
     */
    public void updateScore(){
        _score++;
        _activity.getResourceManager().getScore().play();
        displayScore();
    }

    /**
     * display updateScore
     */
    public void displayScore(){

        if(_currentState == eStateGame.READY){
            _activity.getSceneManager().displayBestScore(ScoreManager.GetBestScore(_activity));
        }else{
            _activity.getSceneManager().displayCurrentScore(_score);
        }
    }

    /**
     * Restart game
     */
    public void restartGame(){
        _currentState = eStateGame.READY;
        _activity.getResourceManager().getMusic().resume();
        _activity.getSceneManager().getBird().restart();
        _score = 0;
        displayScore();
        removeAllPipes();

        PipePair.resetGame();
        this._activity.getSceneManager().changeBackground(false);

        this._activity.getScene().attachChild(_activity.getSceneManager().getAppText());
        this._activity.getScene().attachChild(_activity.getSceneManager().getMakeItText());
        this._activity.getScene().attachChild(_activity.getSceneManager().getInstructionText());
        this._activity.getScene().attachChild(_activity.getSceneManager().getInstructionSprite());
    }



    private void makeAnimationShake()
    {
        _activity.getSceneManager().getGravityText().detachSelf();
        _activity.getSceneManager().getInstructionSprite2().detachSelf();
        _activity.getSceneManager().getInstructionSprite().detachSelf();
        if (shakeHand == false)
        {
            this._activity.getScene().detachChild(_activity.getSceneManager().getInstructionSprite());
            this._activity.getScene().attachChild(_activity.getSceneManager().getInstructionSprite2());
        }
        else
        {
            this._activity.getScene().detachChild(_activity.getSceneManager().getInstructionSprite2());
            this._activity.getScene().attachChild(_activity.getSceneManager().getInstructionSprite());
        }
        shakeHand = shakeHand ? false : true;
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * remove all pipes from the list and recycle them
     */
    private void removeAllPipes()
    {
        for (int i = 0; i< _pipesList.size(); i++){
            PipePair pipe = _pipesList.get(i);
            pipe.destroy();
        }
        _pipesList.clear();
    }


    /**
     * New game
     */
    public void startPlaying(){

        this._currentState = eStateGame.PLAYING;

            this._activity.getResourceManager().getMusic().pause();
            this._activity.getResourceManager().getMusic().seekTo(0);
        _activity.getSceneManager().getGravityText().detachSelf();
        this._activity.getScene().detachChild(_activity.getSceneManager().getAppText());
        this._activity.getScene().detachChild(_activity.getSceneManager().getInstructionSprite());
        this._activity.getScene().detachChild(_activity.getSceneManager().getInstructionSprite2());
        this._activity.getScene().detachChild(_activity.getSceneManager().getMakeItText());
        this._activity.getScene().detachChild(_activity.getSceneManager().getInstructionText());
        _activity.getSceneManager().getGravityText().detachSelf();

        displayScore();
        _activity.getSceneManager().getBird().jump();
    }

    /**
     * Game over view
     */
    public void gameOver(){

        _currentState = eStateGame.DYING;

        _activity.getResourceManager().getDie().play();
        this._activity.getScene().detachChild(_activity.getSceneManager().getGravityText());
        this._activity.getScene().attachChild(_activity.getSceneManager().getFailText());
        _activity.getSceneManager().getBird().getSprite().stopAnimation();
        ScoreManager.SetBestScore(_activity, _score);
    }

    /**
     *  dead view
     */
    public void dead(){

        _currentState = eStateGame.DEAD;

        _timer = new TimerHandler(1.6f, false, new ITimerCallback() {
            @Override
            public void onTimePassed(final TimerHandler pTimerHandler) {
                _activity.getScene().detachChild(_activity.getSceneManager().getFailText());
                restartGame();
                _activity.getScene().unregisterUpdateHandler(_timer);
            }
        });

        this._activity.getScene().registerUpdateHandler(_timer);

    }

    /**
     * Display change Gravity message on screen
     */
    public void displayChangeGravity()
    {
        this._activity.getScene().attachChild(_activity.getSceneManager().getGravityText());

        _timer2 =  new TimerHandler(1.6f, false, new ITimerCallback() {
            @Override
            public void onTimePassed(final TimerHandler pTimerHandler) {
                _activity.getScene().detachChild(_activity.getSceneManager().getGravityText());
                _activity.getSceneManager().getGravityText().detachSelf();
                _activity.getScene().unregisterUpdateHandler(_timer2);
            }
        });
        this._activity.getScene().registerUpdateHandler(_timer2);
    }

    /**
     * Set listener on the current scene
     *  return false if the user cant click on the scene
     */
    public boolean setListenerOnTouch()
    {
        clickModActivated();
        if (_canClick == false)
            return false;
        switch (_currentState) {

            case READY:
                startPlaying();
                break;

            case PLAYING:
                _activity.getSceneManager().getBird().jump();
                break;
        }
        return true;
    }

    /**
     * activated mode when the user can click
     */
    private void clickModActivated()
    {
        _nbrClick++;
        if (_nbrClick == 10)
        {
            _canClick = true;
            _activity.runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(_activity.getApplicationContext(), "Click mode activated",  Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    /**
     * span new pipe on the screen, with random size
     */
    protected void spawnNewPipe() {
        int Min = 250;
        int Max = 450;
        int spawn = Min + (int)(Math.random() * ((Max - Min) + 1));
        PipePair newPipes = new PipePair(spawn, _activity.getVertexBufferObjectManager(), this._activity.getScene());
        this._pipesList.add(newPipes);
    }
}
