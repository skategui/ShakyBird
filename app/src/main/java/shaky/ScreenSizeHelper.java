package shaky;

import android.app.Activity;
import android.util.DisplayMetrics;


public class ScreenSizeHelper {

    /**
     * Calculate the width of the screen, with a ratio.
     * @param context current context, MainActivity
     * @param windowHeight height phone screen
     * @return width phone screen
     */
	public static float calculateScreenWidth(Activity context, float windowHeight){		
				DisplayMetrics dm = new DisplayMetrics();
				context.getWindowManager().getDefaultDisplay().getMetrics(dm);
				final int realHeight = dm.heightPixels;
				final int realWidth = dm.widthPixels;
				float ratio = (float)realWidth / (float)realHeight;
				return windowHeight * ratio;
	}
}
