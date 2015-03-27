package shaky;

import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.input.touch.TouchEvent;
import org.andengine.ui.activity.SimpleBaseGameActivity;

public class MainActivity extends SimpleBaseGameActivity implements SensorListener {

	public static float CAMERA_WIDTH = 485; // this is not final because we dynamically set it at runtime based on the device aspect ratio
	public static final float CAMERA_HEIGHT = 800;
	private static final float SCROLL_SPEED = 4.5f;	// game speed
	public static final float FLOOR_BOUND = 601; // 
	protected static final int PIPE_SPAWN_INTERVAL = 100; // distance between pipe obstacles


    private enum eState
    {
        READY,
        PLAYING,
        DYING,
        DEAD
    }

	private eState GAME_STATE = eState.READY;

	// objects
	private TimerHandler mTimer;
	private SceneManager mSceneManager;
	private ResourceManager mResourceManager;	
	private Scene mSceneHeart;
    private Scene mSceneSpace;
    private boolean withGravity = false;
    private Camera mCamera;

	// sprites
	private ParallaxBackground mBackground;
	private ArrayList<PipePair> pipes = new ArrayList<PipePair>();

	// game variables
	private int _score = 0;
	protected float mCurrentWorldPosition;
	private float mBirdXOffset;

    private SensorManager sensorMgr;
    private long lastUpdate;
    private float x,y,z, last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 400;

    boolean lastwithGravity = false;
	
	@Override
	public EngineOptions onCreateEngineOptions() {


        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorMgr.registerListener(this,  SensorManager.SENSOR_ACCELEROMETER, SensorManager.SENSOR_DELAY_GAME);

		CAMERA_WIDTH = ScreenSizeHelper.calculateScreenWidth(this, CAMERA_HEIGHT);

		mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT){

			private int mPipeSpawnCounter;

			@Override
			public void onUpdate(float pSecondsElapsed) {

				switch(GAME_STATE){

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

			private void ready(){
				
				mCurrentWorldPosition -= SCROLL_SPEED;	
				mSceneManager.getBird().hover();
				
				if(!mResourceManager.mMusic.isPlaying()){
					mResourceManager.mMusic.play();
				}
			}

			private void die(){
				float newY = mSceneManager.getBird().moveWithGravity(); // get the bird to update itself
				if(newY >= FLOOR_BOUND) dead();
			}

			private void play(){

				mCurrentWorldPosition -= SCROLL_SPEED;
                float newY = 0;

                if(_score >= 5 && _score <= 8)
                    withGravity = true;
                else if(_score >= 13 && _score <= 18)
                    withGravity = true;
                 else
                withGravity = false;
                if (withGravity == true)
                {
                    newY = mSceneManager.getBird().moveWithoutGravitiy(); // get the bird to update itself
                }
                else
                {

                    newY = mSceneManager.getBird().moveWithGravity(); // get the bird to update itself
                }
				if(newY >= FLOOR_BOUND) gameOver(); // check if it game over from twatting the floor

				// now create pipes
				mPipeSpawnCounter++;

				if(mPipeSpawnCounter > PIPE_SPAWN_INTERVAL){
					mPipeSpawnCounter = 0;
					spawnNewPipe();						
				}

				// now render the pipes
				for (int i = 0; i<pipes.size(); i++){
					PipePair pipe = pipes.get(i);
					if(pipe.isOnScreen()){
						pipe.move(SCROLL_SPEED);
						if(pipe.collidesWith(mSceneManager.getBird().getSprite())){
							gameOver();
						}

						if(pipe.isCleared(mBirdXOffset)){							
							score();
						}
					}else{
						pipe.destroy();
						pipes.remove(pipe);							
					}					
				}	
			}
		};

		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, 
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);

		engineOptions.getAudioOptions().setNeedsSound(true);	
		engineOptions.getAudioOptions().setNeedsMusic(true);

		return engineOptions;				
	}



    public void onSensorChanged(int sensor, float[] values) {
        if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            // only allow one update every 100ms.
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                x = values[SensorManager.DATA_X];
                y = values[SensorManager.DATA_Y];
                z = values[SensorManager.DATA_Z];

                float add = x + y +z;
                float rem =  last_x  + last_y + last_z;
                float res = add - rem;
                float speed = Math.abs(res) / diffTime * 10000;
                if (speed > SHAKE_THRESHOLD) {
                    Log.e("sensor", "shake detected w/ speed: " + speed);

                    if (speed > 2 * SHAKE_THRESHOLD)
                        makeItJump();
                    makeItJump();

                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    private void makeItJump()
    {
        switch(GAME_STATE){

            case PLAYING:
                mSceneManager.getBird().flap();
                break;
        }
    }


    @Override
    public void onAccuracyChanged(int sensor, int accuracy) {

    }

    protected void spawnNewPipe() {
		int Min = 250; // 150
		int Max = 450; // 450
		int spawn = Min + (int)(Math.random() * ((Max - Min) + 1));
		PipePair newPipes = new PipePair(spawn, this.getVertexBufferObjectManager(), getSCene());
		pipes.add(newPipes);		
	}
		
	@Override
	protected void onCreateResources() {
		mResourceManager = new ResourceManager(this);
		mResourceManager.createResources();
	}

	@Override
	protected Scene onCreateScene() {				

		mBackground = new ParallaxBackground(82/255f, 190/255f, 206/255f){

			float prevX = 0;
			float parallaxValueOffset = 0;

			@Override
			public void onUpdate(float pSecondsElapsed) {

				switch(GAME_STATE){

				case READY:
				case PLAYING:
					final float cameraCurrentX = mCurrentWorldPosition;//mCamera.getCenterX();

					if (prevX != cameraCurrentX) {

						parallaxValueOffset +=  cameraCurrentX - prevX;
						this.setParallaxValue(parallaxValueOffset);
						prevX = cameraCurrentX;
					}
					break;
				}		

				super.onUpdate(pSecondsElapsed);
			}
		};

		mSceneManager = new SceneManager(this, mResourceManager, mBackground);

        mSceneHeart= mSceneManager.createSceneHeart();
        //mSceneSpace = mSceneManager.createSceneSpace();

        defineListener(mSceneHeart);
        //defineListener(mSceneSpace);


		updateScore();

		return getSCene();
	}

    private void defineListener(Scene scene)
    {
        scene.setOnSceneTouchListener(new IOnSceneTouchListener() {

            @Override
            public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {

                if (pSceneTouchEvent.isActionDown()) {

                    switch (GAME_STATE) {

                        case READY:
                            // PipePair.resetPosition();
                            startPlaying();
                            break;

                        case PLAYING:
                            mSceneManager.getBird().flap();
                            break;

                        case DEAD:
                            //restartGame();
                            break;
                    }
                }
                return false;
            }
        });
    }

	private void score(){
		_score++;
		mResourceManager.mScoreSound.play();
		updateScore();
	}

	private void updateScore(){

		if(GAME_STATE == eState.READY){
			mSceneManager.displayBestScore(ScoreManager.GetBestScore(this));
		}else{
			mSceneManager.displayCurrentScore(_score);
		}		
	}
	
	// STATE SWITCHES

    private void restartGame(){
        GAME_STATE = eState.READY;
        mResourceManager.mMusic.resume();
        mSceneManager.getBird().restart();
        _score = 0;
        updateScore();

        for (int i = 0; i<pipes.size(); i++){
            PipePair pipe = pipes.get(i);
            pipe.destroy();
        }
        pipes.clear();
        PipePair.clearScore();

        getSCene().attachChild(mSceneManager.getmGetReadyText());
        getSCene().attachChild(mSceneManager.getmInstructionsSprite());
        //getSCene().attachChild(mSceneManager.mCopyText);
    }

	private void startPlaying(){
		
		GAME_STATE = eState.PLAYING;	
		
		mResourceManager.mMusic.pause();
		mResourceManager.mMusic.seekTo(0);
		getSCene().detachChild(mSceneManager.getmGetReadyText());
		getSCene().detachChild(mSceneManager.getmInstructionsSprite());
		//getSCene().detachChild(mSceneManager.mCopyText);
		updateScore();
		mSceneManager.getBird().flap();
	}

    private Scene getSCene()
    {
      //  if (withGravity == true)
        //    return mSceneSpace;
        return mSceneHeart;
    }

	private void gameOver(){
		
		GAME_STATE = eState.DYING;
		
		mResourceManager.mDieSound.play();
		getSCene().attachChild(mSceneManager.getmYouSuckText());
		mSceneManager.getBird().getSprite().stopAnimation();
		ScoreManager.SetBestScore(this, _score);
	}

	private void dead(){

		GAME_STATE = eState.DEAD;	

		mTimer = new TimerHandler(1.6f, false, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				getSCene().detachChild(mSceneManager.getmYouSuckText());
				restartGame();
				getSCene().unregisterUpdateHandler(mTimer);
			}
		});

		getSCene().registerUpdateHandler(mTimer);
	}



	@Override
	public final void onPause() {
		super.onPause();
		mResourceManager.mMusic.pause();		
	}

}