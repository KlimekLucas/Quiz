package com.example.rottan.quiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_QUIZ = 1;
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String KEY_HIGHSCORE = "keyHighScore";

    private TextView tvHIghscore;
    private int highScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvHIghscore = findViewById(R.id.tv_highscore);
        LoadHighScore();

        Button buttonStartQiuz = findViewById(R.id.btn_start);

        buttonStartQiuz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQiuz();
            }
        });
    }

    private void startQiuz() {
        Intent intent = new Intent(MainActivity.this, QuizActivity.class);
        startActivityForResult(intent, REQUEST_CODE_QUIZ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_QUIZ) {
            if (resultCode == RESULT_OK) {
                int score = data.getIntExtra(QuizActivity.EXTRA_SCORE, 0);
                if(score > highScore) {
                    UpdateHighSore(score);
                }
            }
        }
    }

    private void LoadHighScore() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        highScore = prefs.getInt(KEY_HIGHSCORE, highScore);
        tvHIghscore.setText("Highscore " + highScore);

    }

    private void UpdateHighSore(int score) {
        highScore = score;
            tvHIghscore.setText("Highscore " + highScore);
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_HIGHSCORE, highScore);
        editor.apply();
    }
}
