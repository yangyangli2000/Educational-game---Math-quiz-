package au.edu.jcu.cp3406.educationalapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        GestureDetector.OnGestureListener{

    private final String[] levelNames = {"Easy", "Medium", "Hard"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        View view = findViewById(R.id.main);
        view.setOnTouchListener(new OnSwipeTouchListener(this){
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                Intent settingIntent = new Intent(MainActivity.this,
                        SettingActivity.class);
                MainActivity.this.startActivity(settingIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });
        Button playBtn = findViewById(R.id.play_btn);
        Button settingBtn = findViewById(R.id.setting_btn);
        Button highBtn = findViewById(R.id.high_btn);
        playBtn.setOnClickListener(this);
        settingBtn.setOnClickListener(this);
        highBtn.setOnClickListener(this);
        MusicManager.createSfx(this);
        SharedPreferences musicStatePrefs = getSharedPreferences("musicState", 0);
        SharedPreferences volumePrefs = getSharedPreferences("volume", 0);
        if (volumePrefs.contains("volume")){
            MusicManager.setSfxVolume(volumePrefs.getInt("volume", 0),
                    volumePrefs.getInt("volume", 0));
        }
        MusicManager.setBackgroundMusicIsOn(musicStatePrefs.getBoolean("musicToggle",
                true));
        if(MusicManager.backgroundMusicIsOn && !MusicManager.backgroundMusicIsCurrentlyPlayed){
            MusicManager.startBackgroundMusic(this);
            MusicManager.backgroundMusicIsCurrentlyPlayed = true;
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if(e1.getX() - e2.getX() > 50){
            Toast.makeText(this, "Swipe Left", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (e1.getX() - e2.getX() > 50){
            Toast.makeText(this, "Swipe Right", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.play_btn){
            MusicManager.startUI();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose a level")
                    .setSingleChoiceItems(levelNames, 0, (dialog, which) -> {
                        dialog.dismiss();
                        //start game play
                        startPlay(which);
                    });
            AlertDialog ad = builder.create();
            ad.show();
        }
        else if(view.getId() == R.id.setting_btn){
            MusicManager.startUI();
            Intent settingIntent = new Intent(this, SettingActivity.class);
            this.startActivity(settingIntent);
            finish();
        }
        else if(view.getId() == R.id.high_btn){
            MusicManager.startUI();
            Intent highIntent = new Intent(this, HighScoresActivity.class);
            this.startActivity(highIntent);
            finish();
        }

    }
    private void startPlay(int chosenLevel)
    {
        MusicManager.startUI();
        Intent playIntent = new Intent(this, GameActivity.class);
        playIntent.putExtra("level", chosenLevel);
        this.startActivity(playIntent);
        finish();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

}