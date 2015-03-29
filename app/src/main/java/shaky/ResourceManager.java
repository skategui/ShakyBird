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

import com.shakybird.R;

public class ResourceManager {

	// fonts
	private Font _scoreFont;
    private Font _titleFont;
    private Font mCopyFont;
    private StrokeFont _failFont;
    private StrokeFont _gravityFont;

	// sounds
    private Sound _score;
    private Sound _die;
    private Music _music;

	//textures
    private BitmapTextureAtlas mBackgroundBitmapTextureAtlas;
    private BitmapTextureAtlas mBackgroundBitmapTextureAtlas2;
    private ITextureRegion mBackgroundTextureRegion;
    private ITextureRegion mBackgroundTextureSpace;
    private TextureRegion mInstructionsTexture;



    private SimpleBaseGameActivity _context;
	
	public ResourceManager(SimpleBaseGameActivity _context){
		this._context = _context;
	}
	
	public void createResources(){
		SoundFactory.setAssetBasePath(Config.SOUND_PATH);
		MusicFactory.setAssetBasePath(Config.SOUND_PATH);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath(Config.IMG_PATH);

		// background
		mBackgroundBitmapTextureAtlas = new BitmapTextureAtlas(_context.getTextureManager(), 718, 1184,
				TextureOptions.NEAREST_PREMULTIPLYALPHA);
        mBackgroundBitmapTextureAtlas2 = new BitmapTextureAtlas(_context.getTextureManager(), 718, 1184,
                TextureOptions.NEAREST_PREMULTIPLYALPHA);
		mBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mBackgroundBitmapTextureAtlas, _context.getAssets(), Config.Textures.BACKGROUND_EARTH, 0, 0);

        mBackgroundTextureSpace = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(mBackgroundBitmapTextureAtlas2, _context.getAssets(), Config.Textures.BACKGROUND_SPACE, 0, 0);

		mBackgroundBitmapTextureAtlas.load();
        mBackgroundBitmapTextureAtlas2.load();

        // instructions img
		BitmapTextureAtlas instructionsTextureAtlas = new BitmapTextureAtlas(_context.getTextureManager(), 285, 245, TextureOptions.BILINEAR);
		mInstructionsTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(instructionsTextureAtlas, _context, Config.Textures.INSTRUCTION, 0, 0);
		instructionsTextureAtlas.load();

		PipePair.onCreateResources(_context); // let it sort its own resources out
		Bird.onCreateResources(_context);

		Typeface typeFace = Typeface.createFromAsset(_context.getAssets(), Config.Textures.FONTNAME);

		// score board		
		final ITexture scoreFontTexture = new BitmapTextureAtlas(_context.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		_scoreFont = new StrokeFont(_context.getFontManager(), scoreFontTexture, typeFace, 45, true, Color.WHITE, 2, Color.BLACK);
		_scoreFont.load();

		// title texture
		final ITexture titleTexture = new BitmapTextureAtlas(_context.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		_titleFont = new StrokeFont(_context.getFontManager(), titleTexture, typeFace, 80, true,
                _context.getResources().getColor(R.color.custom_blue_trans), 2, Color.BLACK);
		_titleFont.load();


		final ITexture copyFontTexture = new BitmapTextureAtlas(_context.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		mCopyFont = new StrokeFont(_context.getFontManager(), copyFontTexture, typeFace, 40, true, Color.WHITE, 2, Color.BLACK);
		mCopyFont.load();

		// Fail message
		final ITexture FailedTexture = new BitmapTextureAtlas(_context.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		_failFont = new StrokeFont(_context.getFontManager(), FailedTexture, typeFace, 100, true, _context.getResources().getColor(R.color.custom_red_trans), 2, Color.BLACK);
		_failFont.load();

        // inv Gravity message
        final ITexture GravityTexture = new BitmapTextureAtlas(_context.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
        _gravityFont = new StrokeFont(_context.getFontManager(), GravityTexture, typeFace, 50, true, Color.WHITE, 2, Color.BLACK);
        _gravityFont.load();

		// sounds
		try {			
			_score = SoundFactory.createSoundFromAsset(_context.getSoundManager(), _context, Config.Songs.SCOREMUSIC);
			_die = SoundFactory.createSoundFromAsset(_context.getSoundManager(), _context, Config.Songs.GAMEOVERMUSHC);
		} catch (final IOException e) {
			Debug.e(e);
		}	

		// music

		try {
			_music = MusicFactory.createMusicFromAsset(_context.getMusicManager(), _context, Config.Songs.MUSIC);
			_music.setVolume(0.1f);
			_music.setLooping(true);
		} catch (final IOException e) {
			Debug.e("Error", e);
		}
	}

    public Sound get_score() {
        return _score;
    }

    public Sound get_die() {
        return _die;
    }

    public Music getMusic() {
        return _music;
    }



    public ITextureRegion getmBackgroundTextureRegion() {
        return mBackgroundTextureRegion;
    }

    public ITextureRegion getmBackgroundTextureSpace() {
        return mBackgroundTextureSpace;
    }

    public Font getScoreFont() {
        return _scoreFont;
    }

    public Font getTitleFont() {
        return _titleFont;
    }


    public StrokeFont getGravityFont() {
        return _gravityFont;
    }

    public StrokeFont getFailedFont() {
        return _failFont;
    }

    public TextureRegion getmInstructionsTexture() {
        return mInstructionsTexture;
    }
}
