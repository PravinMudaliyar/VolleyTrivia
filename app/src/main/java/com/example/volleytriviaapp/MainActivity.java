package com.example.volleytriviaapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.volleytriviaapp.data.AnswerListAsyncResponse;
import com.example.volleytriviaapp.data.QuestionBank;
import com.example.volleytriviaapp.model.Question;
import com.example.volleytriviaapp.model.Score;
import com.example.volleytriviaapp.util.SharedPrefs;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button trueButton;
    private Button falseButton;
    private ImageButton restartButton;
    private ImageButton nextButton;
    private TextView questionsTextView;
    private TextView highestScoreTextView;
    private TextView currentScoreTextView;
    private CardView cardView;
    private int currentQuestionIndex;
    List<Question> questionList;
    private TextView questionsCounter;
    private int scoreCounter = 0;


    private Score score;
    SharedPrefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        score = new Score();
        prefs = new SharedPrefs(MainActivity.this);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the status bar is hidden, so hide that too if necessary.
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        questionList = QuestionBank.getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
                questionsTextView.setText(questionArrayList.get(currentQuestionIndex).getQuestion());
                questionsCounter.setText(MessageFormat.format("{0}/{1}", currentQuestionIndex, questionList.size()));

            }
        });


        trueButton = findViewById(R.id.true_button);
        falseButton = findViewById(R.id.false_button);
        restartButton = findViewById(R.id.restart_button);
        nextButton = findViewById(R.id.next_button);
        questionsTextView = findViewById(R.id.questions_textView);
        highestScoreTextView = findViewById(R.id.highest_score_text_view);
        currentScoreTextView = findViewById(R.id.current_score_text_view);
        cardView = findViewById(R.id.card_view);
        questionsCounter= findViewById(R.id.questions_counter_text_View);

        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);
        restartButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        scoreCounter = prefs.getCurrentScore();
        score.setScore(scoreCounter);
        currentScoreTextView.setText(MessageFormat.format("Current_score : {0}", String.valueOf(score.getScore())));

        currentQuestionIndex = prefs.getState();


        highestScoreTextView.setText(MessageFormat.format("Highest score : {0}", String.valueOf(prefs.getHighScore())));

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.next_button:
                goNext();
                break;
            case R.id.false_button:
                checkAnswer(false);
                updateQuestions();
                break;
            case R.id.true_button:
                checkAnswer(true);
                updateQuestions();
                break;
            case R.id.restart_button:
                currentQuestionIndex = 0;
                scoreCounter = 0;
                currentScoreSetter();
                updateQuestions();
                break;

        }

    }

    public void goNext() {
        currentQuestionIndex = (currentQuestionIndex + 1) % questionList.size();
        updateQuestions();
    }

    public void updateQuestions(){
        questionsTextView.setText(questionList.get(currentQuestionIndex).getQuestion());
        questionsCounter.setText(MessageFormat.format("{0}/{1}", currentQuestionIndex, questionList.size()));
    }

    public void checkAnswer(boolean userChose){
        int toastMessageId;
        boolean checkAnswerTrue = questionList.get(currentQuestionIndex).isAnswerTrue();
        if(userChose == checkAnswerTrue){
            addPoints();
            fadeView();
            toastMessageId = R.string.correct_answer;
        }else{
            deductPoints();
            shakeAnimation();
            toastMessageId = R.string.wrong_answer;
        }

       // Toast.makeText(MainActivity.this , toastMessageId , Toast.LENGTH_SHORT).show();
        final Toast toast = Toast.makeText(getApplicationContext(), toastMessageId , Toast.LENGTH_SHORT);
        toast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 200);

        }

    private void shakeAnimation(){
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this , R.anim.shake_animations);
        cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                goNext();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
    private void fadeView(){
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f , 0.0f);

        alphaAnimation.setDuration(350);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.RESTART);   // Back and forth animation mode.
        cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                 cardView.setCardBackgroundColor(Color.WHITE);
                 goNext();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void addPoints(){
        scoreCounter+=10;
        score.setScore(scoreCounter);
        currentScoreTextView.setText(MessageFormat.format("Current score : {0}", String.valueOf(score.getScore())));

    }
    private void deductPoints(){

        if(scoreCounter>0) {
            scoreCounter -= 10;
        }else{
            scoreCounter = 0;
        }
        currentScoreSetter();
    }

    private void currentScoreSetter(){
        score.setScore(scoreCounter);
        currentScoreTextView.setText(MessageFormat.format("Current score : {0}", String.valueOf(score.getScore())));

    }

    @Override
    protected void onPause() {
        prefs.setHighScore(score.getScore());
        prefs.setState(currentQuestionIndex);
        prefs.setCurrentScore(scoreCounter);
        super.onPause();
    }


}