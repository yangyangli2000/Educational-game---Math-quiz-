package au.edu.jcu.cp3406.educationalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class HighScoresActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // turning off the title at the top of the screen.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); // full screen
        setContentView(R.layout.activity_scores);

        Button backBtn = findViewById(R.id.back_btn);
        ScoreDatabase scoreDatabase = new ScoreDatabase(this);
        backBtn.setOnClickListener(this);
        MusicManager.createSfx(this);
        SharedPreferences volumePrefs = getSharedPreferences("volume", 0);
        if (volumePrefs.contains("volume")){
            MusicManager.setSfxVolume(volumePrefs.getInt("volume", 0),
                    volumePrefs.getInt("volume", 0));
        }

        TextView scoreView = findViewById(R.id.high_scores_list);

        ArrayList<String> theList = new ArrayList<>();
        Cursor data = scoreDatabase.getListContents();
        if (data.getCount() == 0){
            System.out.println("empty");
        }
        else {
            while (data.moveToNext()){
                theList.add(data.getString(1) + " - " +
                        data.getString(2));
                System.out.println(data.getString(1) + " - " +
                        data.getString(2));
            }
        }
        StringBuilder scoreBuild = new StringBuilder(" ");
        for(String score : theList){
            scoreBuild.append(score).append("\n");
        }
        scoreView.setText(scoreBuild.toString());
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.back_btn){
            MusicManager.startUI();
            Intent backIntent = new Intent(this, MainActivity.class);
            this.startActivity(backIntent);
            finish();
        }
    }
}