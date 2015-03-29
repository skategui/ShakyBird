package shaky;


import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;


public class PipePair {

    private Scene _scene;

    private Sprite _upperPipe;
    private Sprite _upperPipeSection;
    private Sprite _lowerPipe;
    private Sprite _lowerPipeSection;
    private boolean counted = false;


    private static int _nbrJump = 0;
    private static int _distance = 220;
    private static final float PIPE_Y_OFFSET = Constants.CAMERA_WIDTH + 200; // make sure they always spawn way off screen



    // upper pipe
	private static TextureRegion _upperPipeTexture;
	private static TextureRegion _upperPipeSectionTexture;

	//lower pipe
	private static TextureRegion _lowerPipeTexture;
	private static TextureRegion _lowerPipeSectionTexture;

	public static void onCreateResources(SimpleBaseGameActivity activity){

		// upper pipe		
		BitmapTextureAtlas upperPipeTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 130, 60, TextureOptions.BILINEAR);
		_upperPipeTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(upperPipeTextureAtlas, activity, Constants.PIPEUPPER, 0, 0);
		upperPipeTextureAtlas.load();

		// upper pipe section	
		BitmapTextureAtlas upperPipeSectionTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 120, 1, TextureOptions.BILINEAR);
		_upperPipeSectionTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(upperPipeSectionTextureAtlas, activity, Constants.PIPEUPPERSECTION, 0, 0);
		upperPipeSectionTextureAtlas.load();


		// lower pipe		
		BitmapTextureAtlas lowerPipeTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 130, 60, TextureOptions.BILINEAR);
		_lowerPipeTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(lowerPipeTextureAtlas, activity, Constants.PIPELOWER, 0, 0);
		lowerPipeTextureAtlas.load();

		// lower pipe section	
		BitmapTextureAtlas lowerPipeSectionTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 120, 1, TextureOptions.BILINEAR);
		_lowerPipeSectionTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(lowerPipeSectionTextureAtlas, activity, Constants.PIPELOWERSECTION, 0, 0);
		lowerPipeSectionTextureAtlas.load();
	}


    /**
     * reset variables  once starting a new game
     */
    public static void resetGame()
    {
        _nbrJump = 0;
        _distance = 220;
    }


	public PipePair(int mOpeningHeight,
			VertexBufferObjectManager mVertexBufferObjectManager, Scene _scene) {
		super();
		this._scene = _scene;

		// upper pipe
		_upperPipe = new Sprite(PIPE_Y_OFFSET, mOpeningHeight- _distance, 88, 41, _upperPipeTexture, mVertexBufferObjectManager);
		_upperPipe.setZIndex(1);
		_scene.attachChild(_upperPipe);

		_upperPipeSection = new Sprite(PIPE_Y_OFFSET + 3, 0, 82, mOpeningHeight- _distance, _upperPipeSectionTexture, mVertexBufferObjectManager);
		_upperPipeSection.setZIndex(1);
		_scene.attachChild(_upperPipeSection);

		//lower pipe		
		_lowerPipe = new Sprite(PIPE_Y_OFFSET, mOpeningHeight+ _distance - 41, 88, 41, _lowerPipeTexture, mVertexBufferObjectManager);
		_lowerPipe.setZIndex(1);
		_scene.attachChild(_lowerPipe);

		_lowerPipeSection = new Sprite(PIPE_Y_OFFSET + 3, mOpeningHeight+ _distance, 82, (644-(mOpeningHeight+ _distance)), _lowerPipeSectionTexture, mVertexBufferObjectManager);
		_lowerPipeSection.setZIndex(1);
		_scene.attachChild(_lowerPipeSection);
		_scene.sortChildren();


        changeDifficulty();
    }

    private void changeDifficulty()
    {
        _nbrJump++;
        if (_nbrJump % 4 == 0)
        {
            _distance -= 20;
        }
    }


	public void move(float offset){
		_upperPipe.setPosition(_upperPipe.getX() - offset, _upperPipe.getY());
		_upperPipeSection.setPosition(_upperPipeSection.getX() - offset, _upperPipeSection.getY());

		_lowerPipe.setPosition(_lowerPipe.getX() - offset, _lowerPipe.getY());
		_lowerPipeSection.setPosition(_lowerPipeSection.getX() - offset, _lowerPipeSection.getY());

	}

	public boolean isOnScreen(){

		if(_upperPipe.getX() < -200){
			return false;
		}

		return true;		
	}
	

	public boolean isCleared(float birdXOffset){
		
		if(!counted){
			if(_upperPipe.getX()<(birdXOffset - (Constants.BIRD_WIDTH / 2))){
				counted = true; // make sure we don't count this again
				return true;
			}
		}		
		
		return false;
	
	}


    /**
     * clean scene
     */
	public void destroy(){
		_scene.detachChild(_upperPipe);
		_scene.detachChild(_upperPipeSection);
		_scene.detachChild(_lowerPipe);
		_scene.detachChild(_lowerPipeSection);

	}

	public boolean collidesWith(Sprite bird){

		if(_upperPipe.collidesWith(bird))
            return true;
		if(_upperPipeSection.collidesWith(bird))
            return true;
		if(_lowerPipe.collidesWith(bird))
            return true;
		if(_lowerPipeSection.collidesWith(bird))
            return true;
		return false;

	}

}
