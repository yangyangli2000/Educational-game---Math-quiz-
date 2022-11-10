package au.edu.jcu.cp3406.educationalapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class FinishActivity extends AppCompatActivity implements View.OnClickListener{


    private static final String TAG = "tag";
    private SharedPreferences yourScorePrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        TextView yourScore = findViewById(R.id.your_text);
        TextView bestScore = findViewById(R.id.best_text);
        Button restartBnt = findViewById(R.id.restart_btn);
        Button shareBnt = findViewById(R.id.share_btn);
        Button homeBnt = findViewById(R.id.home_btn);
        restartBnt.setOnClickListener(this);
        shareBnt.setOnClickListener(this);
        homeBnt.setOnClickListener(this);
        yourScorePrefs = getSharedPreferences(GameActivity.YOUR_SCORE_PREFS, 0);
        SharedPreferences highScorePrefs = getSharedPreferences(GameActivity.HIGH_SCORE_PREFS,
                0);
        yourScore.setText(yourScorePrefs.getString("yourScore", ""));
        if(highScorePrefs.getString("highScores", "").length() > 0) {
            bestScore.setText(((highScorePrefs.getString("highScores", "").split(
                    "\\|"))[0].split(" - "))[1]);
        }
    }

    @Override
    public void onClick(View view) {
        // restart the quiz
        if(view.getId() == R.id.restart_btn){
            MusicManager.startUI();
            Intent restartIntent = new Intent(this, GameActivity.class);
            this.startActivity(restartIntent);
            finish();
        }
        // share on tweeter
        else if(view.getId() == R.id.share_btn){
            MusicManager.startUI();
            String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s&url=%s",
                    urlEncode("I got " + yourScorePrefs.getString("yourScore",
                            "") + " scores while playing math quiz in " +
                            "@MathQuizz. Check out on GG Play: "),
                    urlEncode("https://www.google.fi/"));
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));
            // Narrow down to official Twitter app, if available:
            @SuppressLint("QueryPermissionsNeeded") List<ResolveInfo> matches =
                    getPackageManager().queryIntentActivities(intent, 0);
            for (ResolveInfo info : matches) {
                if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter")) {
                    intent.setPackage(info.activityInfo.packageName);
                }
            }
            startActivity(intent);
        }
        // back to main screen
        else if(view.getId() == R.id.home_btn){
            MusicManager.startUI();
            Intent homeIntent = new Intent(this, MainActivity.class);
            this.startActivity(homeIntent);
            finish();
        }
    }

    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            Log.wtf(TAG, "UTF-8 should always be supported", e);
            throw new RuntimeException("URLEncoder.encode() failed for " + s);
        }
    }
}