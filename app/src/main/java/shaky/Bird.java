package shaky;

import java.io.IOException;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;


public class Bird {


    private static float WRAPAROUND_POINT = (float) (2 * Math.PI);

    private float mHoverStep = 0;


    private AnimatedSprite mSprite;

    private float _macceleration = Config.Bird.GRAVITY;
    private float mVerticalSpeed;
    private float _currentBirdAngle = Config.Bird.BIRD_MAX_FLAP_ANGLE;


	//bird
	private static BuildableBitmapTextureAtlas mBirdBitmapTextureAtlas;
	private static TiledTextureRegion mBirdTextureRegion;

    private float mBirdYOffset, mBirdXOffset;

    // sounds
	private static Sound mJumpSound;	



	

	public Bird(float birdXOffset, float birdYOffset, VertexBufferObjectManager mVertexBufferObjectManager, Scene mScene) {

		this.mBirdXOffset = birdXOffset;
		this.mBirdYOffset = birdYOffset;		
		
		mSprite = new AnimatedSprite(mBirdXOffset, mBirdYOffset, 55.8f, 40, mBirdTextureRegion, mVertexBufferObjectManager);
		mSprite.animate(25);
		mSprite.setZIndex(2);
		mScene.attachChild(mSprite);
		
	}
	
	public void restart(){
		mSprite.animate(25);
		mSprite.setY(mBirdYOffset);
		mSprite.setX(mBirdXOffset);
		_currentBirdAngle = 0;
		mSprite.setRotation(_currentBirdAngle);
	}

	public float move(boolean isInSpace){

        int coeficient = isInSpace ? -1 : 1;


		float newY = mSprite.getY() + mVerticalSpeed * coeficient; // calculate the birds new height based on the current vertical speed
		newY = Math.max(newY, 0); // don't allow through the ceiling
		newY = Math.min(newY, Config.Game.FLOOR_BOUND); // don't allow through the floor
		mSprite.setY(newY); //apply the new position

		// now calculate the new speed
		_macceleration += Config.Bird.GRAVITY * coeficient; // always applying gravity to current acceleration
		mVerticalSpeed += _macceleration * coeficient; // always applying the current acceleration tp the current speed
		mVerticalSpeed = Math.min(mVerticalSpeed, Config.Bird.MAX_DROP_SPEED); // but capping it to a terminal velocity (science bitch)

		if(mVerticalSpeed <= (Config.Bird.FLAP_POWER)){
			_currentBirdAngle -= Config.Bird.BIRD_FLAP_ANGLE_POWER;
		}else{
			_currentBirdAngle += Config.Bird.FLAP_ANGLE_DRAG;
		}

		_currentBirdAngle = Math.max(_currentBirdAngle, Config.Bird.BIRD_MAX_FLAP_ANGLE);
		_currentBirdAngle = Math.min(_currentBirdAngle, Config.Bird.BIRD_MAX_DROP_ANGLE);

		// now apply bird angle based on current speed
		mSprite.setRotation(_currentBirdAngle);

		return newY;
	}


    /**
     * make the bird jump !
     */
	public void jump(){
		mVerticalSpeed = (-Config.Bird.FLAP_POWER);
		_macceleration = 0;
		mJumpSound.play();
	}	

	public void hover(){
		mHoverStep += 0.13f;
		if(mHoverStep > WRAPAROUND_POINT) mHoverStep = 0;
		
		float newY = mBirdYOffset + ((float) (7 * Math.sin(mHoverStep)));		
		mSprite.setY(newY);			
		
	}

    public static void onCreateResources(SimpleBaseGameActivity activity){
        // bird
        mBirdBitmapTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), (int) Config.Bird.BITMAP_WIDTH, (int) Config.Bird.BITMAP_HEIGHT, TextureOptions.NEAREST);
        mBirdTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mBirdBitmapTextureAtlas, activity, Config.Bird.BIRDFILENAME, 3, 3);
        try {
            mBirdBitmapTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
            mBirdBitmapTextureAtlas.load();
        } catch (TextureAtlasBuilderException e) {
            e.printStackTrace();
        }

        try {
            mJumpSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(), activity, Config.Songs.JUMPMUSIC);
        } catch (final IOException e) {
            Debug.e(e);
        }

    }

	public AnimatedSprite getSprite() {
		return mSprite;
	}

}
