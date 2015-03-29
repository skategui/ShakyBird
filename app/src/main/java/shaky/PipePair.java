package shaky;

import android.util.Log;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;


public class PipePair {


	float PIPE_WIDTH = MainActivity.CAMERA_WIDTH * 0.18f;

	// upper pipe
	private static TextureRegion mUpperPipeTexture;
	private static TextureRegion mUpperPipeSectionTexture;

	//lower pipe
	private static TextureRegion mLowerPipeTexture;
	private static TextureRegion mLowerPipeSectionTexture;

	public static void onCreateResources(SimpleBaseGameActivity activity){

		// upper pipe		
		BitmapTextureAtlas upperPipeTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 130, 60, TextureOptions.BILINEAR);
		mUpperPipeTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(upperPipeTextureAtlas, activity, "pipeupper.png", 0, 0);
		upperPipeTextureAtlas.load();

		// upper pipe section	
		BitmapTextureAtlas upperPipeSectionTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 120, 1, TextureOptions.BILINEAR);
		mUpperPipeSectionTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(upperPipeSectionTextureAtlas, activity, "pipesectionupper.png", 0, 0);
		upperPipeSectionTextureAtlas.load();


		// lower pipe		
		BitmapTextureAtlas lowerPipeTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 130, 60, TextureOptions.BILINEAR);
		mLowerPipeTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(lowerPipeTextureAtlas, activity, "pipelower.png", 0, 0);
		lowerPipeTextureAtlas.load();

		// lower pipe section	
		BitmapTextureAtlas lowerPipeSectionTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 120, 1, TextureOptions.BILINEAR);
		mLowerPipeSectionTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(lowerPipeSectionTextureAtlas, activity, "pipesectionlower.png", 0, 0);
		lowerPipeSectionTextureAtlas.load();
	}


	private Scene mScene;

	private Sprite mUpperPipe;
	private Sprite mUpperPipeSection;
	private Sprite mLowerPipe;
	private Sprite mLowerPipeSection;
    private static int score = 0;
    private static int gap = 220;


    public static void clearScore()
    {
        score = 0;
        gap  = 220;
    }

	private static final float PIPE_Y_OFFSET = MainActivity.CAMERA_WIDTH + 200; // make sure they always spawn way off screen
	
	public PipePair(int mOpeningHeight,
			VertexBufferObjectManager mVertexBufferObjectManager, Scene mScene) {
		super();
		this.mScene = mScene;

		// upper pipe
		mUpperPipe = new Sprite(PIPE_Y_OFFSET, mOpeningHeight-gap, 88, 41, mUpperPipeTexture, mVertexBufferObjectManager);
		mUpperPipe.setZIndex(1);
		mScene.attachChild(mUpperPipe);

		mUpperPipeSection = new Sprite(PIPE_Y_OFFSET + 3, 0, 82, mOpeningHeight-gap, mUpperPipeSectionTexture, mVertexBufferObjectManager);
		mUpperPipeSection.setZIndex(1);
		mScene.attachChild(mUpperPipeSection);

		//lower pipe		
		mLowerPipe = new Sprite(PIPE_Y_OFFSET, mOpeningHeight+gap - 41, 88, 41, mLowerPipeTexture, mVertexBufferObjectManager);
		mLowerPipe.setZIndex(1);
		mScene.attachChild(mLowerPipe);

		mLowerPipeSection = new Sprite(PIPE_Y_OFFSET + 3, mOpeningHeight+gap, 82, (644-(mOpeningHeight+gap)), mLowerPipeSectionTexture, mVertexBufferObjectManager);
		mLowerPipeSection.setZIndex(1);
		mScene.attachChild(mLowerPipeSection);
		mScene.sortChildren();
        score++;
        if (score % 4 == 0)
        {
            gap -= 20;
        }
    }


	public void move(float offset){
		mUpperPipe.setPosition(mUpperPipe.getX() - offset, mUpperPipe.getY());
		mUpperPipeSection.setPosition(mUpperPipeSection.getX() - offset, mUpperPipeSection.getY());

		mLowerPipe.setPosition(mLowerPipe.getX() - offset, mLowerPipe.getY());
		mLowerPipeSection.setPosition(mLowerPipeSection.getX() - offset, mLowerPipeSection.getY());	

	}

	public boolean isOnScreen(){

		if(mUpperPipe.getX() < -200){
			return false;
		}

		return true;		
	}
	
	boolean counted = false;
	
	public boolean isCleared(float birdXOffset){
		
		if(!counted){
			if(mUpperPipe.getX()<(birdXOffset - (Bird.BIRD_WIDTH/2))){
				counted = true; // make sure we don't count this again
				return true;
			}
		}		
		
		return false;
	
	}


	public void destroy(){
		mScene.detachChild(mUpperPipe);
		mScene.detachChild(mUpperPipeSection);
		mScene.detachChild(mLowerPipe);
		mScene.detachChild(mLowerPipeSection);

	}

	public boolean collidesWith(Sprite bird){

		if(mUpperPipe.collidesWith(bird))
            return true;
		if(mUpperPipeSection.collidesWith(bird))
            return true;
		if(mLowerPipe.collidesWith(bird))
            return true;
		if(mLowerPipeSection.collidesWith(bird))
            return true;
		return false;

	}

}
