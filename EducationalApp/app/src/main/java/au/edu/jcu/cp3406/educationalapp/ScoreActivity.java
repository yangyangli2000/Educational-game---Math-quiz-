package au.edu.jcu.cp3406.educationalapp;

import androidx.annotation.NonNull;

public class ScoreActivity implements Comparable<ScoreActivity>{

    public String scoreDate;
    public int scoreNum;

    public ScoreActivity(String date, int num){
        scoreDate = date;
        scoreNum = num;
    }

    @Override
    public int compareTo(@NonNull ScoreActivity scoreActivity) {
        //return 0 if equal, 1 if passed greater than this, -1 if this greater than passed
        return Integer.compare(scoreActivity.scoreNum, scoreNum);
    }

    public String getScoreText()
    {
        return scoreDate+" - "+scoreNum;
    }
}
