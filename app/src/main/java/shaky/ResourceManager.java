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
    private StrokeFont _makeItshakeFont;
    private StrokeFont _instructionTextfont;
	// sounds
    private Sound _score;
    private Sound _die;
    private Music _music;

	//textures
    private BitmapTextureAtlas _textureEarth;
    private BitmapTextureAtlas _textureSpace;
    private ITextureRegion _earthBack;
    private ITextureRegion _spaceBack;
    private TextureRegion _instructionTexture;



    private SimpleBaseGameActivity _context;

	public ResourceManager(SimpleBaseGameActivity _context){
		this._context = _context;
	}

	public void createResources(){
		SoundFactory.setAssetBasePath(Config.SOUND_PATH);
		MusicFactory.setAssetBasePath(Config.SOUND_PATH);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath(Config.IMG_PATH);

		// background

        initialiseBackground();

        PipePair.onCreateResources(_context); // let it sort its own resources out
        Bird.onCreateResources(_context);

        initialiseTextures();
        initialiseSongs();
	}

    /**
     * initialise different backgrounds
     */
    private void initialiseBackground()
    {
        this._textureEarth = new BitmapTextureAtlas(_context.getTextureManager(), 718, 1184, TextureOptions.NEAREST_PREMULTIPLYALPHA);
        this._textureSpace = new BitmapTextureAtlas(_context.getTextureManager(), 718, 1184, TextureOptions.NEAREST_PREMULTIPLYALPHA);

        this._earthBack = BitmapTextureAtlasTextureRegionFactory.createFromAsset(_textureEarth, _context.getAssets(), Config.Textures.BACKGROUND_EARTH, 0, 0);
        this._spaceBack = BitmapTextureAtlasTextureRegionFactory.createFromAsset(_textureSpace, _context.getAssets(), Config.Textures.BACKGROUND_SPACE, 0, 0);

        this._textureEarth.load();
        this._textureSpace.load();
    }

    /**
     * initialise songs in games
     */
    private void initialiseSongs()
    {
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

    /**
     * initialise texture for all the displayed texts
     */
    private void initialiseTextures()
    {
        Typeface typeFace = Typeface.createFromAsset(_context.getAssets(), Config.Textures.FONTNAME);

        // updateScore board
        final ITexture scoreFontTexture = new BitmapTextureAtlas(_context.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
        _scoreFont = new StrokeFont(_context.getFontManager(), scoreFontTexture, typeFace, 45, true, Color.WHITE, 2, Color.BLACK);
        _scoreFont.load();

        // title texture
        final ITexture titleTexture = new BitmapTextureAtlas(_context.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
        _titleFont = new StrokeFont(_context.getFontManager(), titleTexture, typeFace, 90, true,
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
        _gravityFont = new StrokeFont(_context.getFontManager(), GravityTexture, typeFace, 50, true, _context.getResources().getColor(R.color.custom_green_trans), 2, Color.BLACK);
        _gravityFont.load();

        // inv Make It shake message
        final ITexture MakeItShakeTexture = new BitmapTextureAtlas(_context.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
        _makeItshakeFont = new StrokeFont(_context.getFontManager(), MakeItShakeTexture, typeFace, 50, true, Color.WHITE, 2, Color.BLACK);
        _makeItshakeFont.load();


        // inv Make It shake message
        final ITexture instructionText = new BitmapTextureAtlas(_context.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
        _instructionTextfont = new StrokeFont(_context.getFontManager(), instructionText, typeFace, 40, true, Color.WHITE, 2, Color.BLACK);
        _instructionTextfont.load();

        // instructions img
        BitmapTextureAtlas inastruction = new BitmapTextureAtlas(_context.getTextureManager(), 285, 245, TextureOptions.BILINEAR);
        _instructionTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(inastruction, _context, Config.Textures.INSTRUCTION, 0, 0);
        inastruction.load();

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

    public ITextureRegion get_earthBack() {
        return _earthBack;
    }

    public ITextureRegion get_spaceBack() {
        return _spaceBack;
    }

    public Font getScoreFont() {
        return _scoreFont;
    }

    public Font getTitleFont() {
        return _titleFont;
    }

    public Font getMakeItshakeFont() {
        return _makeItshakeFont;
    }

    public Font getIntructionTextFont() {
        return _instructionTextfont;
    }

    public StrokeFont getGravityFont() {
        return _gravityFont;
    }

    public StrokeFont getFailedFont() {
        return _failFont;
    }

    public TextureRegion get_instructionTexture() {
        return _instructionTexture;
    }
}
