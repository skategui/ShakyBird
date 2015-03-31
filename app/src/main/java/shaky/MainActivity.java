package shaky;

import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.util.Log;

import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.input.touch.TouchEvent;
import org.andengine.ui.activity.SimpleBaseGameActivity;

public class MainActivity extends SimpleBaseGameActivity implements SensorListener {


	// objects
	private SceneManager _sceneManager;
	private ResourceManager _resourceManager;
	private Scene _scene;
    private GameManager _gameManager;

	// sprites
	private ParallaxBackground _background;

    private SensorManager _sensor;
    private long lastUpdate;
    private float x,y,z, last_x, last_y, last_z;


	@Override
	public EngineOptions onCreateEngineOptions() {


        _sensor = (SensorManager) getSystemService(SENSOR_SERVICE);
        _sensor.registerListener(this, SensorManager.SENSOR_ACCELEROMETER, SensorManager.SENSOR_DELAY_GAME);

		Config.CAMERA_WIDTH = ScreenSizeHelper.calculateScreenWidth(this, Config.Game.CAMERA_HEIGHT);

        _gameManager = new GameManager(this);
        _gameManager.initializeCamera();

        EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED,
                new RatioResolutionPolicy(Config.CAMERA_WIDTH, Config.Game.CAMERA_HEIGHT), _gameManager.getCamera());

        engineOptions.getAudioOptions().setNeedsSound(true);
        engineOptions.getAudioOptions().setNeedsMusic(true);
        return engineOptions;
        }


    /**
     * call when the mobile phone is shaked
     * @param sensor sensor from the phone ?
     * @param values power of the shake
     */
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
                if (speed > Config.SHAKE_POWER) {
                    Log.e("sensor", "shake detected w/ speed: " + speed);
                    _gameManager.makeItJump();

                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }




    @Override
    public void onAccuracyChanged(int sensor, int accuracy) {

    }


		
	@Override
	protected void onCreateResources() {
		_resourceManager = new ResourceManager(this);
		_resourceManager.createResources();
	}

	@Override
	protected Scene onCreateScene() {				

		_background = new ParallaxBackground(82/255f, 190/255f, 206/255f){

			@Override
			public void onUpdate(float pSecondsElapsed) {

                _gameManager.updateScene();
				super.onUpdate(pSecondsElapsed);
			}
		};

		_sceneManager = new SceneManager(this, _resourceManager, _background);
        _scene = _sceneManager.createScene();

        defineListener(_scene);
		_gameManager.displayScore();
		return _scene;
	}

    private void defineListener(Scene scene)
    {
        scene.setOnSceneTouchListener(new IOnSceneTouchListener() {

            @Override
            public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {

                if (pSceneTouchEvent.isActionDown()) {
                    _gameManager.setListenerOnTouch();
                }
                return false;
            }
        });
    }

	@Override
	public final void onPause() {
		super.onPause();
        if (_resourceManager != null && _resourceManager.getMusic() != null)
		_resourceManager.getMusic().pause();
	}

    public SceneManager getSceneManager() {
        return _sceneManager;
    }

    public ResourceManager getResourceManager() {
        return _resourceManager;
    }

    public Scene getScene()
    {
        return _scene;
    }

    public void setParallax(float value)
    {
        _background.setParallaxValue(value);
    }
}