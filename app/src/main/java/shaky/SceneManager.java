package shaky;

import com.shakybird.R;

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

	private SimpleBaseGameActivity _context;
	private ResourceManager _ressourceManager;
	private ParallaxBackground _parallax;

	// text objects
    private Text _scoreText;
    private Text _appNameText;
    private Text _makeItText;
    private Sprite _instructionSprite;
    private Text _failText;
    private Text _gravityText;

    private Sprite _spaceBackground;
    private Sprite _earthBackground;

    private Bird _player;


	public SceneManager(SimpleBaseGameActivity context, ResourceManager resourceManager, ParallaxBackground parallaxBackground){
		this._context = context;
		this._ressourceManager = resourceManager;
		this._parallax = parallaxBackground;
	}


	public Scene createScene(){

        Scene mScene = new Scene();
        VertexBufferObjectManager vbo = _context.getVertexBufferObjectManager();

        _earthBackground = new Sprite(0, 0 , _ressourceManager.getmBackgroundTextureRegion(), vbo);
        _spaceBackground = new Sprite(0, 0 , _ressourceManager.getmBackgroundTextureSpace(), vbo);


        _parallax.attachParallaxEntity(new ParallaxEntity(1, _earthBackground));

        addItemOnscene(mScene);
		return mScene;
	}

    /**
     * switch background between earth and space one
     * @param isInSpace boolean, if it should be the space background or not
     * @return isInSpace
     */
    public boolean changeBackground(boolean isInSpace)
    {
        if (isInSpace == false)
            _parallax.attachParallaxEntity(new ParallaxEntity(1, _earthBackground));
        else
            _parallax.attachParallaxEntity(new ParallaxEntity(1, _spaceBackground));
        return isInSpace;
    }


    public void addItemOnscene(Scene mScene)
    {
        mScene.setBackground(_parallax);
        mScene.setBackgroundEnabled(true);

        // bird
        float birdStartXOffset = (Config.CAMERA_WIDTH / 4) - (Config.Bird.BIRD_WIDTH / 4);
        float birdYOffset = (Config.Game.CAMERA_HEIGHT / 2) - (Config.Bird.BIRD_HEIGHT / 4);
        _player = new Bird(birdStartXOffset, birdYOffset, _context.getVertexBufferObjectManager(), mScene);

        //score
        _scoreText = new Text(0,720, _ressourceManager.getScoreFont(), "        ", new TextOptions(HorizontalAlign.CENTER), _context.getVertexBufferObjectManager());
        _scoreText.setZIndex(3);
        mScene.attachChild(_scoreText);

        // App Name
        _appNameText = new Text(0, 100, _ressourceManager.getTitleFont(), this._context.getResources().getString(R.string.app_name), new TextOptions(HorizontalAlign.CENTER), _context.getVertexBufferObjectManager());
        _appNameText.setZIndex(3);
        mScene.attachChild(_appNameText);
        centerText(_appNameText);

        // make it skake msg !
        _makeItText = new Text(0, 180, _ressourceManager.getGravityFont(), this._context.getResources().getString(R.string.makeitshake), new TextOptions(HorizontalAlign.CENTER), _context.getVertexBufferObjectManager());
        _makeItText.setZIndex(3);
        mScene.attachChild(_makeItText);
        centerText(_makeItText);

        // instructions image
        _instructionSprite = new Sprite(0, 0, 200, 172, _ressourceManager.getmInstructionsTexture(), _context.getVertexBufferObjectManager());
        _instructionSprite.setZIndex(3);
        mScene.attachChild(_instructionSprite);
        centerSprite(_instructionSprite);
        _instructionSprite.setY(_instructionSprite.getY() + 20);

        //  failed
        _failText = new Text(0, Config.Game.CAMERA_HEIGHT / 2 - 100, _ressourceManager.getFailedFont()
                , this._context.getResources().getString(R.string.failed), new TextOptions(HorizontalAlign.CENTER), _context.getVertexBufferObjectManager());
        _failText.setZIndex(3);
        centerText(_failText);

          // create special font size
        _gravityText = new Text(0, Config.Game.CAMERA_HEIGHT / 2 - 100, _ressourceManager.getGravityFont()
                , this._context.getResources().getString(R.string.grav_invertion), new TextOptions(HorizontalAlign.CENTER), _context.getVertexBufferObjectManager());
        _gravityText.setZIndex(3);
        centerText(_gravityText);
    }
	
	public static void centerSprite(Sprite sprite){
		sprite.setX((Config.CAMERA_WIDTH / 2) - (sprite.getWidth() / 2));
		sprite.setY((Config.Game.CAMERA_HEIGHT / 2) - (sprite.getHeight() / 2));
	}
	
	public void displayCurrentScore(int score){		
			_scoreText.setText(_context.getResources().getString(R.string.score) + " " + score);
			centerText(_scoreText);
	}
	
	public void displayBestScore(int score){
		_scoreText.setText(this._context.getResources().getString(R.string.bestscore) + " " + score);
		centerText(_scoreText);
	}

	private void centerText(Text text){
		text.setX((Config.CAMERA_WIDTH / 2) - (text.getWidth() / 2));
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
