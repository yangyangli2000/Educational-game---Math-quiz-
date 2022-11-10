package au.edu.jcu.cp3406.educationalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    private final int ADD_OPERATOR = 0, SUBTRACT_OPERATOR = 1, MULTIPLY_OPERATOR = 2,
            DIVIDE_OPERATOR = 3;
    public static final String HIGH_SCORE_PREFS = "ArithmeticFile1";
    public static final String YOUR_SCORE_PREFS = "ArithmeticFile2";
    private int level = 0, wrongAnswer = 0, operator = 0, randQuestion;
    public int operand1 = 0, operand2 = 0,  answer = 0;
    private int chances = 3;
    private boolean hasWrongAnswer = false;
    private final String[] operators = {"+", "-", "x", "/"};
    private Random random;
    private SharedPreferences highScorePrefs;
    private SharedPreferences yourScorePrefs;
    private ScoreDatabase scoreDatabase;
    private final int[][] levelMin = {
            {1, 11, 21},
            {1, 5, 10},
            {2, 5, 10},
            {2, 3, 5}};
    private final int[][] levelMax = {
            {10, 25, 50},
            {10, 20, 30},
            {5, 10, 15},
            {10, 50, 100}};
    private TextView question, scoreText, bestText, firstChance, secondChance, thirdChance;
    private ObjectAnimator animation;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_play_game);

        highScorePrefs = getSharedPreferences(HIGH_SCORE_PREFS, 0);
        yourScorePrefs = getSharedPreferences(YOUR_SCORE_PREFS, 0);
        question =  findViewById(R.id.question);
        scoreText = findViewById(R.id.score_text);
        bestText = findViewById(R.id.best_text);
        firstChance = findViewById(R.id.first_chance);
        secondChance = findViewById(R.id.second_chance);
        thirdChance = findViewById(R.id.third_chance);
        ImageButton tick = findViewById(R.id.tick);
        ImageButton cross = findViewById(R.id.cross);
        scoreDatabase = new ScoreDatabase(this);
        MusicManager.createSfx(this);
        tick.setOnClickListener(this);
        cross.setOnClickListener(this);
        clearYourScores();
        ShakeListener mShaker = new ShakeListener(this);

        mShaker.setOnShakeListener(() -> {
            if(chances > 0) {
                MusicManager.startChange();
                chances--;
                if(chances == 2){
                    firstChance.setCompoundDrawables(null,null,null,
                            null);
                }
                else if (chances == 1){
                    secondChance.setCompoundDrawables(null,null,null,
                            null);
                }
                else if(chances == 0){
                    thirdChance.setCompoundDrawables(null,null,null,
                            null);
                }
                animation.start();
                updateBestScore();
                generateQuestion();
            }
        });

        if(highScorePrefs.getString("highScores", "").length() > 0) {
            bestText.setText(((highScorePrefs.getString("highScores",
                    "").split("\\|"))[0].split(" - "))[1]);
        }
        SharedPreferences volumePrefs = getSharedPreferences("volume", 0);
        if (volumePrefs.contains("volume")){
            MusicManager.setSfxVolume(volumePrefs.getInt("volume", 0),
                    volumePrefs.getInt("volume", 0));
        }
        ProgressBar timerBar = findViewById(R.id.timer);
        timerBar.setScaleY(3f);
        animation = ObjectAnimator.ofInt(timerBar, "progress", 100, 0);
        animation.setDuration(3000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) { }
            @Override
            public void onAnimationEnd(Animator animator) {
                if(!hasWrongAnswer) {
                    MusicManager.startWrong();
                    hasWrongAnswer = true;
                    setYourScore();
                    setHighScore();
                    Intent endIntent = new Intent(getBaseContext(), FinishActivity.class);
                    startActivity(endIntent);
                    finish();
                }
            }
            @Override
            public void onAnimationCancel(Animator animator) {  }
            @Override
            public void onAnimationRepeat(Animator animator) { }
        });
        animation.start();

        if(savedInstanceState!=null){
            level=savedInstanceState.getInt("level");
            int exScore = savedInstanceState.getInt("score");
            scoreText.setText("" + exScore);
        }
        else{
            Bundle extras = getIntent().getExtras();
            if(extras !=null)
            {
                int passedLevel = extras.getInt("level", -1);
                if(passedLevel>=0) level = passedLevel;
            }
        }
        random = new Random();
        generateQuestion();
    }

    protected void onDestroy(){
        if(!hasWrongAnswer) {
            setHighScore();
        }
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        int exScore = getScore();
        savedInstanceState.putInt("score", exScore);
        savedInstanceState.putInt("level", level);
        super.onSaveInstanceState(savedInstanceState);
    }
    // generate question
    @SuppressLint("SetTextI18n")
    public void generateQuestion(){
        randQuestion = random.nextInt(2);
        operator = random.nextInt(operators.length);
        operand1 = getOperand();
        operand2 = getOperand();
        if(operator == SUBTRACT_OPERATOR){
            while(operand2 > operand1){
                operand1 = getOperand();
                operand2 = getOperand();
            }
        }
        else if(operator==DIVIDE_OPERATOR){
            while((((double)operand1/(double)operand2)%1 > 0) || (operand1==operand2))
            {
                operand1 = getOperand();
                operand2 = getOperand();
            }
        }
        switch(operator)
        {
            case ADD_OPERATOR:
                answer = operand1 + operand2;
                break;
            case SUBTRACT_OPERATOR:
                answer = operand1 - operand2;
                break;
            case MULTIPLY_OPERATOR:
                answer = operand1 * operand2;
                break;
            case DIVIDE_OPERATOR:
                answer = operand1 / operand2;
                break;
            default:
                break;
        }
        if (randQuestion == 0) {
            wrongAnswer = getWrongAnswer();
            question.setText(operand1 + " " + operators[operator] + " " + operand2 + "\n= " +
                    wrongAnswer);
        }
        else if (randQuestion == 1){
            question.setText(operand1 + " " + operators[operator] + " " + operand2 + "\n= " +
                    answer);
        }
    }

    private int getOperand(){
        return random.nextInt(levelMax[operator][level] - levelMin[operator][level] + 1)
                + levelMin[operator][level];
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        int exScore = getScore();
        if(view.getId() == R.id.tick){
            if(randQuestion == 0){
                MusicManager.startWrong();
                hasWrongAnswer = true;
                setYourScore();
                setHighScore();
                Intent endIntent = new Intent(this, FinishActivity.class);
                startActivity(endIntent);
                finish();
            }
            else if(randQuestion == 1){
                MusicManager.startCorrect();
                scoreText.setText("" + (exScore+1));
                animation.start();
                updateBestScore();
                generateQuestion();
            }
        }
        else if (view.getId() == R.id.cross){
            if(randQuestion == 0){
                MusicManager.startCorrect();
                scoreText.setText("" + (exScore+1));
                animation.start();
                updateBestScore();
                generateQuestion();
            }
            else if(randQuestion == 1){
                MusicManager.startWrong();
                hasWrongAnswer = true;
                setYourScore();
                setHighScore();
                Intent endIntent = new Intent(this, FinishActivity.class);
                startActivity(endIntent);
                finish();
            }
        }

    }
    private int getScore(){
        String scoreStr = scoreText.getText().toString();
        return Integer.parseInt(scoreStr);
    }

    private void setHighScore(){
        List<ScoreActivity> scoreActivityStrings = new ArrayList<ScoreActivity>();;
        scoreDatabase.deleteData();
        SharedPreferences.Editor scoreEdit = highScorePrefs.edit();
        @SuppressLint("SimpleDateFormat") DateFormat dateForm = new SimpleDateFormat(
                "dd MMMM yyyy");
        String dateOutput = dateForm.format(new Date());
        String scores = highScorePrefs.getString("highScores", "");
        System.out.println("Length: " + scores.length());

        int exScore = getScore();
        if(exScore > 0){
            if(scores.length()>0){
                //we have existing scores
                String[] exScores = scores.split("\\|");
                for(String eSc : exScores){
                    String[] parts = eSc.split(" - ");
                    scoreActivityStrings.add(new ScoreActivity(parts[0], Integer.parseInt(parts[1])));
                }
                ScoreActivity newScoreActivity = new ScoreActivity(dateOutput, exScore);
                scoreActivityStrings.add(newScoreActivity);
                Collections.sort(scoreActivityStrings);
                StringBuilder scoreBuild = new StringBuilder("");
                for(int s = 0; s < scoreActivityStrings.size(); s++){
                    if(s >= 10) break;                      //score need grater ten
                    if(s > 0) scoreBuild.append("|");       //pipe separate the score strings
                    scoreBuild.append(scoreActivityStrings.get(s).getScoreText());
                    addData(scoreActivityStrings.get(s).scoreDate, scoreActivityStrings.get(s).scoreNum);
                }
                scoreEdit.putString("highScores", scoreBuild.toString());
            }
            else{
                addData(dateOutput, exScore);
                scoreEdit.putString("highScores", ""+dateOutput+" - "+exScore);
            }
            scoreEdit.apply();
        }
        else {
            if(scores.length() > 0) {
                String[] exScores = scores.split("\\|");
                for (String eSc : exScores) {
                    String[] parts = eSc.split(" - ");
                    scoreActivityStrings.add(new ScoreActivity(parts[0], Integer.parseInt(parts[1])));
                }
                for (int s = 0; s < scoreActivityStrings.size(); s++) {
                    addData(scoreActivityStrings.get(s).scoreDate, scoreActivityStrings.get(s).scoreNum);
                }
            }
        }
    }

    public void addData(String date, int score){
        boolean insertData = scoreDatabase.addData(date, score);
        if(insertData){
            System.out.println("Success");
        }
        else {
            System.out.println("Something wrong");
        }
    }

    public void setYourScore(){
        SharedPreferences.Editor scoreEdit = yourScorePrefs.edit();
        scoreEdit.putString("yourScore", scoreText.getText().toString());
        scoreEdit.apply();
    }

    public int getWrongAnswer(){
        int randOperator = random.nextInt(2);
        if (randOperator == 0) {
            wrongAnswer = answer + (random.nextInt(10) + 1);
        }
        else {
            wrongAnswer = answer - (random.nextInt(10) + 1);
            while (wrongAnswer < 0){
                wrongAnswer = answer - (random.nextInt(10) + 1);
            }
        }
        return wrongAnswer;
    }

    public void updateBestScore(){
        if(Integer.parseInt(scoreText.getText().toString()) >
                Integer.parseInt(bestText.getText().toString())) {
            bestText.setText(scoreText.getText());
        }
    }

    public void clearYourScores(){
        SharedPreferences.Editor scoreEdit = yourScorePrefs.edit();
        scoreEdit.clear();
        scoreEdit.apply();
    }
}