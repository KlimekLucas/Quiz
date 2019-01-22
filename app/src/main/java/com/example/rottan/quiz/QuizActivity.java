package com.example.rottan.quiz;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {

    public static final String EXTRA_SCORE = "extraScore";
    public static final long COUNTDOWN_IN_MILIS = 30000;

    private static final String KEY_SCORE = "keyScore";
    private static final String KEY_QUESTION_COUNT = "keyQuestionCount";
    private static final String KEY_MILLIS_LEFT = "keyMillisLeft";
    private static final String KEY_ANSWERED = "keyAnswered";
    private static final String KEY_QUESTION_LIST = "keyQuestionList";

    private TextView tvQuestion;
    private TextView tvViewScore;
    private TextView tvViewQuestionCount;
    private TextView tvViewCountDown;
    private RadioGroup rbGroup;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private RadioButton rb4;
    private Button buttonConfirmNext;

    private Long backPressedTime = 1L;

    private ColorStateList textColorDefaultRb;
    private ColorStateList textColorDefaultCD;

    private CountDownTimer countDownTimer;
    private long timeLeftInMilis;

    private ArrayList<Question> questionList;

    private int questionCounter;
    private int questionCountTotal;
    private Question currentQestion;

    private int score;
    private boolean answered;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qiuz);

        tvQuestion = findViewById(R.id.tv_question);
        tvViewScore = findViewById(R.id.tv_view_score);
        tvViewQuestionCount = findViewById(R.id.tv_question_count);
        tvViewCountDown = findViewById(R.id.tv_countdown);
        rbGroup = findViewById(R.id.radioGroup);
        rb1 = findViewById(R.id.rb1);
        rb2 = findViewById(R.id.rb2);
        rb3 = findViewById(R.id.rb3);
        rb4 = findViewById(R.id.rb4);
        buttonConfirmNext = findViewById(R.id.bt_next_confirm);

        textColorDefaultRb = rb1.getTextColors();
        textColorDefaultCD = tvViewCountDown.getTextColors();

        if (savedInstanceState == null) {
            QiuzDbHelper dbHelper = new QiuzDbHelper(this);
            questionList = dbHelper.getAllQuestions();

            questionCountTotal = questionList.size();
            Collections.shuffle(questionList);

            ShowNextQuestion();
        } else {
            questionList = savedInstanceState.getParcelableArrayList(KEY_QUESTION_LIST);
            questionCountTotal = questionList.size();
            questionCounter = savedInstanceState.getInt(KEY_QUESTION_COUNT);
            currentQestion = questionList.get(questionCounter - 1);
            score = savedInstanceState.getInt(KEY_SCORE);
            timeLeftInMilis = savedInstanceState.getLong(KEY_MILLIS_LEFT);
            answered = savedInstanceState.getBoolean(KEY_ANSWERED);

            if (!answered) {
                startCountDown();
            } else {
                UpdateCountDownText();
                ShowSolution();
            }

        }
        buttonConfirmNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!answered) {
                    if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked() || rb4.isChecked()) {
                        checkAnswer();
                    } else {
                        Toast.makeText(QuizActivity.this, "Please select answer", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ShowNextQuestion();
                }
            }

        });

    }

    private void checkAnswer() {
        answered = true;

        countDownTimer.cancel();

        RadioButton rbSelected = findViewById(rbGroup.getCheckedRadioButtonId());
        int answerNr = rbGroup.indexOfChild(rbSelected) + 1;

        if (answerNr == currentQestion.getAnswer()) {
            score++;
            tvViewScore.setText("Score " + score);
        }
        ShowSolution();
    }


    private void ShowSolution() {
        rb1.setTextColor(Color.RED);
        rb2.setTextColor(Color.RED);
        rb3.setTextColor(Color.RED);
        rb4.setTextColor(Color.RED);

        switch (currentQestion.getAnswer()) {
            case 1:
                rb1.setTextColor(Color.GREEN);
                tvQuestion.setText("answer A is Correct");
                break;
            case 2:
                rb2.setTextColor(Color.GREEN);
                tvQuestion.setText("answer B is Correct");
                break;
            case 3:
                rb3.setTextColor(Color.GREEN);
                tvQuestion.setText("answer C is Correct");
                break;
            case 4:
                rb4.setTextColor(Color.GREEN);
                tvQuestion.setText("answer D is Correct");
                break;
        }
        if (questionCounter < questionCountTotal) {
            buttonConfirmNext.setText("Next");
        } else {
            buttonConfirmNext.setText("Finish");
        }
    }


    private void ShowNextQuestion() {
        rb1.setTextColor(textColorDefaultRb);
        rb2.setTextColor(textColorDefaultRb);
        rb3.setTextColor(textColorDefaultRb);
        rb4.setTextColor(textColorDefaultRb);
        rbGroup.clearCheck();

        if (questionCounter < questionCountTotal) {
            currentQestion = questionList.get(questionCounter);
            tvQuestion.setText(currentQestion.getQuestion());
            rb1.setText(currentQestion.getOption1());
            rb2.setText(currentQestion.getOption2());
            rb3.setText(currentQestion.getOption3());
            rb4.setText(currentQestion.getOption4());

            questionCounter++;
            tvViewQuestionCount.setText("Question " + questionCounter + "/" + questionCountTotal);
            answered = false;
            buttonConfirmNext.setText("Confirm");

            timeLeftInMilis = COUNTDOWN_IN_MILIS;
            startCountDown();

        } else {
            FinishQuiz();
        }
    }

    private void startCountDown() {
        countDownTimer = new CountDownTimer(timeLeftInMilis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMilis = millisUntilFinished;
                UpdateCountDownText();
            }

            @Override
            public void onFinish() {
                timeLeftInMilis = 0;
                UpdateCountDownText();
                checkAnswer();
            }
        }.start();
    }

    private void UpdateCountDownText() {
        int minutes = (int) ((timeLeftInMilis / 1000) / 60);
        int seconds = (int) ((timeLeftInMilis / 1000) % 60);

        String timeFormated = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        tvViewCountDown.setText(timeFormated);
        if (timeLeftInMilis < 10000) {
            tvViewCountDown.setTextColor(Color.RED);
        } else {
            tvViewCountDown.setTextColor(textColorDefaultCD);
        }
    }


    private void FinishQuiz() {

        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_SCORE, score);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            FinishQuiz();
        } else {
            Toast.makeText(this, "press again to exit", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SCORE, score);
        outState.putInt(KEY_QUESTION_COUNT, questionCounter);
        outState.putLong(KEY_MILLIS_LEFT, timeLeftInMilis);
        outState.putBoolean(KEY_ANSWERED, answered);
        outState.putParcelableArrayList(KEY_QUESTION_LIST, questionList);
    }
}
