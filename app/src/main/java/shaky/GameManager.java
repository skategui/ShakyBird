package shaky;


import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;

import java.util.ArrayList;

/**
 * Created by guillaumeagis on 29/03/15.
 */
public class GameManager {

    private MainActivity _activity;

    private enum eStateGame {
        READY,
        PLAYING,
        DYING,
        DEAD
    }

    private TimerHandler _timer;

    protected float mCurrentWorldPosition;

    private eStateGame _currentState = eStateGame.READY;
    private boolean withGravity = false;
    private boolean lastwithGravity = false;
    private Camera _camera;
    // game variables
    private int _score = 0;

    private ArrayList<PipePair> _pipesList = new ArrayList<>();
    private float mBirdXOffset;

    private int mPipeSpawnCounter;
    float prevX = 0;
    float parallaxValueOffset = 0;


    public GameManager(MainActivity activity) {
        this._activity = activity;
    }

    public void initializeCamera() {

        _camera = new Camera(0, 0, Constants.CAMERA_WIDTH, Constants.Game.CAMERA_HEIGHT) {


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

    public Camera getCamera() {
        return _camera;
    }


    public void makeItJump() {
        switch (_currentState) {

            case PLAYING:
                _activity.getSceneManager().getBird().flap();
                break;
        }
    }

    public void updateScene() {


        switch(_currentState){

            case READY:
            case PLAYING:
                final float cameraCurrentX = mCurrentWorldPosition;//_camera.getCenterX();

                if (prevX != cameraCurrentX) {

                    parallaxValueOffset +=  cameraCurrentX - prevX;
                    _activity.setParallax(parallaxValueOffset);
                    prevX = cameraCurrentX;
                }
                break;
        }
    }


    private void ready() {

        mCurrentWorldPosition -= Constants.Game.SCROLL_SPEED;
        _activity.getSceneManager().getBird().hover();

        if (!_activity.getResourceManager().getMusic().isPlaying()) {
            _activity.getResourceManager().getMusic().play();
        }
    }


    private void die() {
        float newY = _activity.getSceneManager().getBird().move(false); // get the bird to update itself
        if (newY >= Constants.Game.FLOOR_BOUND)
            dead();
    }

    private boolean isSpaceScene() {
        if ((_score >= 5 && _score <= 8) || (_score >= 13 && _score <= 18))
            return true;
        return false;
    }

    private float getPositionInSpace() {
        if (lastwithGravity == false) {
            _activity.getSceneManager().changeBackground(true);
            lastwithGravity = true;
            displayChangeGravity();
        }
        return _activity.getSceneManager().getBird().move(true); // get the bird to update itself
    }

    private float getPositionHeart() {
        if (lastwithGravity == true) {
            _activity.getSceneManager().changeBackground(false);
            lastwithGravity = false;
            displayChangeGravity();
        }
        return _activity.getSceneManager().getBird().move(false); // get the bird to update itself
    }

    private void play() {

        mCurrentWorldPosition -= Constants.Game.SCROLL_SPEED;


        float newY = isSpaceScene() == true ? getPositionInSpace() : getPositionHeart();


        if (newY >= Constants.Game.FLOOR_BOUND)
            gameOver(); // check if it game over from twatting the floor

        // now create _pipesList
        mPipeSpawnCounter++;

        if (mPipeSpawnCounter > Constants.Game.PIPE_SPAWN_INTERVAL) {
            mPipeSpawnCounter = 0;
            spawnNewPipe();
        }

        // now render the _pipesList
        for (int i = 0; i < _pipesList.size(); i++) {
            PipePair pipe = _pipesList.get(i);
            if (pipe.isOnScreen()) {
                pipe.move(Constants.Game.SCROLL_SPEED);
                if (pipe.collidesWith(_activity.getSceneManager().getBird().getSprite())) {
                    gameOver();
                }

                if (pipe.isCleared(mBirdXOffset)) {
                    score();
                }
            } else {
                pipe.destroy();
                _pipesList.remove(pipe);
            }
        }
    }

    public void score(){
        _score++;
        _activity.getResourceManager().get_score().play();
        updateScore();
    }

    public void updateScore(){

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
        updateScore();
        removeAllPipes();

        PipePair.resetGame();

        this._activity.getScene().attachChild(_activity.getSceneManager().getAppText());
        this._activity.getScene().attachChild(_activity.getSceneManager().getMakeItText());
        this._activity.getScene().attachChild(_activity.getSceneManager().getInstructionSprite());
    }

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
        this._activity.getScene().detachChild(_activity.getSceneManager().getAppText());
        this._activity.getScene().detachChild(_activity.getSceneManager().getInstructionSprite());
        this._activity.getScene().detachChild(_activity.getSceneManager().getMakeItText());
        this._activity.getScene().detachChild(_activity.getSceneManager().getGravityText());
        updateScore();
        _activity.getSceneManager().getBird().flap();
    }


    /**
     * Game over view
     */
    public void gameOver(){

        _currentState = eStateGame.DYING;

        _activity.getResourceManager().get_die().play();
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

        TimerHandler a=  new TimerHandler(1.6f, false, new ITimerCallback() {
            @Override
            public void onTimePassed(final TimerHandler pTimerHandler) {
                _activity.getScene().detachChild(_activity.getSceneManager().getGravityText());
            }
        });

        this._activity.getScene().registerUpdateHandler(a);

    }

    public void setListenerOnTouch()
    {
        switch (_currentState) {

            case READY:
                startPlaying();

                break;

            case PLAYING:
                _activity.getSceneManager().getBird().flap();
                break;
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
