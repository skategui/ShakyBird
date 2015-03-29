package shaky;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.StrokeFont;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;

import android.graphics.Color;
import android.graphics.Typeface;

public class ResourceManager {

	// fonts
	private Font mScoreFont;
    private Font mGetReadyFont;
    private Font mCopyFont;
    private StrokeFont mYouSuckFont;

	// sounds
    private Sound mScoreSound;
    private Sound mDieSound;
    private Music mMusic;

	//textures
    private BitmapTextureAtlas mBackgroundBitmapTextureAtlas;
    private BitmapTextureAtlas mBackgroundBitmapTextureAtlas2;
    private ITextureRegion mBackgroundTextureRegion;
    private ITextureRegion mBackgroundTextureSpace;
    private TextureRegion mInstructionsTexture;



    private SimpleBaseGameActivity context;
	
	public ResourceManager(SimpleBaseGameActivity context){
		this.context = context;
	}
	
	public void createResources(){
		SoundFactory.setAssetBasePath("sound/");
		MusicFactory.setAssetBasePath("sound/");
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("img/");		

		// background
		mBackgroundBitmapTextureAtlas = new BitmapTextureAtlas(context.getTextureManager(), 718, 1184, 
				TextureOptions.NEAREST_PREMULTIPLYALPHA);
        mBackgroundBitmapTextureAtlas2 = new BitmapTextureAtlas(context.getTextureManager(), 718, 1184,
                TextureOptions.NEAREST_PREMULTIPLYALPHA);
		mBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mBackgroundBitmapTextureAtlas, context.getAssets(), "background480.png", 0, 0);

        mBackgroundTextureSpace = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(mBackgroundBitmapTextureAtlas2, context.getAssets(), "background_space2.png", 0, 0);

		mBackgroundBitmapTextureAtlas.load();
        mBackgroundBitmapTextureAtlas2.load();

        // instructions img
		BitmapTextureAtlas instructionsTextureAtlas = new BitmapTextureAtlas(context.getTextureManager(), 285, 245, TextureOptions.BILINEAR);
		mInstructionsTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(instructionsTextureAtlas, context, "instructions.png", 0, 0);
		instructionsTextureAtlas.load();

		PipePair.onCreateResources(context); // let it sort its own resources out
		Bird.onCreateResources(context);

		Typeface typeFace = Typeface.createFromAsset(context.getAssets(), "flappy.ttf");

		// score board		
		final ITexture scoreFontTexture = new BitmapTextureAtlas(context.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		mScoreFont = new StrokeFont(context.getFontManager(), scoreFontTexture, typeFace, 60, true, Color.WHITE, 2, Color.BLACK);
		mScoreFont.load();

		// get ready text	
		final ITexture getReadyFontTexture = new BitmapTextureAtlas(context.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		mGetReadyFont = new StrokeFont(context.getFontManager(), getReadyFontTexture, typeFace, 60, true, Color.WHITE, 2, Color.BLACK);
		mGetReadyFont.load();


		// (c) text
		final ITexture copyFontTexture = new BitmapTextureAtlas(context.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		mCopyFont = new StrokeFont(context.getFontManager(), copyFontTexture, typeFace, 20, true, Color.WHITE, 2, Color.BLACK);
		mCopyFont.load();

		// (c) you suck text
		final ITexture youSuckTexture = new BitmapTextureAtlas(context.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		mYouSuckFont = new StrokeFont(context.getFontManager(), youSuckTexture, typeFace, 80, true, Color.WHITE, 2, Color.BLACK);
		mYouSuckFont.load();	

		// sounds
		try {			
			mScoreSound = SoundFactory.createSoundFromAsset(context.getSoundManager(), context, "score.ogg");
			mDieSound = SoundFactory.createSoundFromAsset(context.getSoundManager(), context, "gameover.ogg");			
		} catch (final IOException e) {
			Debug.e(e);
		}	

		// music

		try {
			mMusic = MusicFactory.createMusicFromAsset(context.getMusicManager(), context, "song.ogg");
			mMusic.setVolume(0.1f);
			mMusic.setLooping(true);
		} catch (final IOException e) {
			Debug.e("Error", e);
		}
	}

    public Sound getmScoreSound() {
        return mScoreSound;
    }

    public Sound getmDieSound() {
        return mDieSound;
    }

    public Music getmMusic() {
        return mMusic;
    }



    public ITextureRegion getmBackgroundTextureRegion() {
        return mBackgroundTextureRegion;
    }

    public ITextureRegion getmBackgroundTextureSpace() {
        return mBackgroundTextureSpace;
    }

    public Font getmScoreFont() {
        return mScoreFont;
    }

    public Font getmGetReadyFont() {
        return mGetReadyFont;
    }


    public StrokeFont getmYouSuckFont() {
        return mYouSuckFont;
    }

    public TextureRegion getmInstructionsTexture() {
        return mInstructionsTexture;
    }
}
