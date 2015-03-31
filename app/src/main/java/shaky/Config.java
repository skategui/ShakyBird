package shaky;

/**
 * Created by guillaumeagis on 29/03/15.
 * Variables related to the configuration of the game.
 * ie . Camera, pipes, positions, gravity, bird...
 */
public class Config {

    // this is not final because we dynamically set it at runtime based on the device aspect ratio
    public static float CAMERA_WIDTH = 485;
    // shake  power
    public static final int SHAKE_POWER = 500;
     // updateScore file
    public static final String SCORE_FILE = ".updateScore";
    // sound directory
    public static final String SOUND_PATH = "sound/";
     // image directory
    public static final String IMG_PATH = "img/";

    public class Game
    {
        public static final float CAMERA_HEIGHT = 800;
        public static final float SCROLL_SPEED = 4.5f;	// game speed
        public static final float FLOOR_BOUND = 601;
        public static final int PIPE_SPAWN_INTERVAL = 100; // distance between pipe obstacles
    }


    /**
     * Bird constants, related to the image of bird, its gravity and angles
     */

    public class Bird
    {
        public static final float BITMAP_WIDTH = 1047f;
        public static final float BITMAP_HEIGHT = 903f;

        public static final float BIRD_WIDTH = 55.8f;
        public static final float BIRD_HEIGHT = 40f;

        public static final float MAX_DROP_SPEED = 12.0f;
        public static final float GRAVITY = 0.04f;
        public static final float FLAP_POWER = 6f;

        public static final float BIRD_MAX_FLAP_ANGLE = -20;
        public static final float BIRD_MAX_DROP_ANGLE = 90;
        public static final float FLAP_ANGLE_DRAG = 4.0f;
        public static final float BIRD_FLAP_ANGLE_POWER = 15.0f;
        // bird sprite filename
        public static final String BIRDFILENAME = "birdmap.png";
    }

    public class Songs
    {
         // Score music
        public static final String SCOREMUSIC = "score.ogg";
         // game over music
        public static final String GAMEOVERMUSHC = "gameover.ogg";
         // game music
        public static final String MUSIC = "song.ogg";
        public static final String JUMPMUSIC = "jump.ogg";
    }

    public class Textures
    {
         // intro image
        public static  final String INSTRUCTION = "shake.png";
        // font filename
        public static  final String FONTNAME = "angrybirds.ttf";
        // background on earth
        public static final String BACKGROUND_EARTH = "earth_back.png";
        // Backgroud in space
        public static final String BACKGROUND_SPACE = "space_back.png";
        // pipe upper head part
        public static final String PIPEUPPER = "pipeupper.png";
        // pipe upper body
        public static final String PIPEUPPERSECTION = "pipesectionupper.png";
        // pipe lower head
        public static final String PIPELOWER = "pipelower.png";
        // pipe lower body
        public static final String PIPELOWERSECTION = "pipesectionlower.png";
    }
}
