package shaky;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ScoreManager {

	public static int GetBestScore(Context context){
		SharedPreferences prefs = context.getSharedPreferences( context.getPackageName() + Constants.SCORE_FILE, Context.MODE_PRIVATE);

		return prefs.getInt("bestscore", 0);
	}

	public static void SetBestScore(Context context, int newScore){

		if(newScore > GetBestScore(context)){

			SharedPreferences prefs = context.getSharedPreferences(
					context.getPackageName() + Constants.SCORE_FILE, Context.MODE_PRIVATE);

			Editor editor = prefs.edit();
			editor.putInt("bestscore", newScore);
			editor.commit();
		}
	}
}
