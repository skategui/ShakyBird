package shaky;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.HorizontalAlign;

public class SceneManager {

	private SimpleBaseGameActivity mContext;
	private ResourceManager mResourceManager;
	private ParallaxBackground mParallaxBackground;

	// text objects
    private Text _scoreText;
    private Text _appNameText;
    private Text _makeItText;
    private Sprite _instructionSprite;
    private Text _failText;
    private Text _gravityText;

    private Sprite _spaceBackground;
    private Sprite _heartBackground;

    private Bird _player;

	public SceneManager(SimpleBaseGameActivity context, ResourceManager resourceManager, ParallaxBackground parallaxBackground){
		this.mContext = context;	
		this.mResourceManager = resourceManager;
		this.mParallaxBackground = parallaxBackground;
	}


	public Scene createScene(){

        Scene mScene = new Scene();
        VertexBufferObjectManager vbo = mContext.getVertexBufferObjectManager();

        _heartBackground = new Sprite(0, 0 , mResourceManager.getmBackgroundTextureRegion(), vbo);
        _spaceBackground = new Sprite(0, 0 , mResourceManager.getmBackgroundTextureSpace(), vbo);


        mParallaxBackground.attachParallaxEntity(new ParallaxEntity(1, _heartBackground));

        addItemOnscene(mScene);
		return mScene;
	}

    public boolean changeBackground(boolean isInSpace)
    {
        if (isInSpace == false)
            mParallaxBackground.attachParallaxEntity(new ParallaxEntity(1, _heartBackground));
        else
            mParallaxBackground.attachParallaxEntity(new ParallaxEntity(1, _spaceBackground));
        return isInSpace;
    }


    public void addItemOnscene(Scene mScene)
    {
        mScene.setBackground(mParallaxBackground);
        mScene.setBackgroundEnabled(true);

        // bird
        float birdStartXOffset = (Constants.CAMERA_WIDTH / 4) - (Constants.Bird.BIRD_WIDTH / 4);
        float birdYOffset = (Constants.Game.CAMERA_HEIGHT / 2) - (Constants.Bird.BIRD_HEIGHT / 4);
        _player = new Bird(birdStartXOffset, birdYOffset, mContext.getVertexBufferObjectManager(), mScene);

        //score
        _scoreText = new Text(0,720, mResourceManager.getScoreFont(), "        ", new TextOptions(HorizontalAlign.CENTER), mContext.getVertexBufferObjectManager());
        _scoreText.setZIndex(3);
        mScene.attachChild(_scoreText);

        // App Name
        _appNameText = new Text(0, 100, mResourceManager.getTitleFont(), Constants.SHAKYBIRD, new TextOptions(HorizontalAlign.CENTER), mContext.getVertexBufferObjectManager());
        _appNameText.setZIndex(3);
        mScene.attachChild(_appNameText);
        centerText(_appNameText);

        _makeItText = new Text(0, 170, mResourceManager.getGravityFont(), Constants.MAKEITSHAKE, new TextOptions(HorizontalAlign.CENTER), mContext.getVertexBufferObjectManager());
        _makeItText.setZIndex(3);
        mScene.attachChild(_makeItText);
        centerText(_makeItText);

        // instructions image
        _instructionSprite = new Sprite(0, 0, 200, 172, mResourceManager.getmInstructionsTexture(), mContext.getVertexBufferObjectManager());
        _instructionSprite.setZIndex(3);
        mScene.attachChild(_instructionSprite);
        centerSprite(_instructionSprite);
        _instructionSprite.setY(_instructionSprite.getY() + 20);

        // you failed
        _failText = new Text(0, Constants.Game.CAMERA_HEIGHT / 2 - 100, mResourceManager.getFailedFont()
                , Constants.FAILED, new TextOptions(HorizontalAlign.CENTER), mContext.getVertexBufferObjectManager());
        _failText.setZIndex(3);
        centerText(_failText);

          // create special font size
        _gravityText = new Text(0, Constants.Game.CAMERA_HEIGHT / 2 - 100, mResourceManager.getGravityFont()
                , Constants.INVGRAVITY, new TextOptions(HorizontalAlign.CENTER), mContext.getVertexBufferObjectManager());
        _gravityText.setZIndex(3);
        centerText(_gravityText);
    }
	
	public static void centerSprite(Sprite sprite){
		sprite.setX((Constants.CAMERA_WIDTH / 2) - (sprite.getWidth() / 2));
		sprite.setY((Constants.Game.CAMERA_HEIGHT / 2) - (sprite.getHeight() / 2));
	}
	
	public void displayCurrentScore(int score){		
			_scoreText.setText(String.valueOf(score));
			centerText(_scoreText);
	}
	
	public void displayBestScore(int score){
		_scoreText.setText(Constants.BESTSCORE + score);
		centerText(_scoreText);
	}

	private void centerText(Text text){
		text.setX((Constants.CAMERA_WIDTH / 2) - (text.getWidth() / 2));
	}

    public Bird getBird()
    {
        return _player;
    }


    public Text getAppText() {
        return _appNameText;
    }

    public Sprite getInstructionSprite() {
        return _instructionSprite;
    }

    public Text getFailText() {
        return _failText;
    }

    public Text getGravityText()
    {
        return _gravityText;
    }

    public Text getMakeItText()
    {
        return _makeItText;
    }
}
