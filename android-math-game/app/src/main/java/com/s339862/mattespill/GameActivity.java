package com.s339862.mattespill;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceManager;


import android.content.SharedPreferences;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameActivity extends AppCompatActivity {
    // Text elements in the layout
    private TextView questionText, answerText, feedbackText, correctText;

    // List of question-answer pairs
    private List<String[]> qaPairs;

    // Index of the current question and the number of questions
    private int questionIndex, numberOfQuestions;

    // Shared preferences to store the number of questions and the language
    SharedPreferences sharedPreferences;


    // Handle input from the number buttons
    private void handleDisplayInput(String input) {
        String currentAnswer = answerText.getText().toString() + input;
        answerText.setText(currentAnswer);
    }

    // Handle submit of the answer
    private void handleSubmit() {
        String currentAnswer = answerText.getText().toString();
        if (currentAnswer.equals(qaPairs.get(questionIndex)[1])) {
            feedbackText.setText(R.string.correct_answer);
            answerText.setText("");
        } else {
            feedbackText.setText(R.string.incorrect_answer);
            answerText.setText("");
        }
        String correctString = getString(R.string.correct) + " " + qaPairs.get(questionIndex)[1];
        correctText.setText(correctString);
    }

    // Handle the next question event when the next-button is clicked
    private void handleNextQuestion() {
        questionIndex++;
        feedbackText.setText("");
        correctText.setText("");
        if (questionIndex < numberOfQuestions) {
            questionText.setText(qaPairs.get(questionIndex)[0]);
            answerText.setText("");
        } else {
            showFinishDialog(R.string.end_game_message);
        }
    }

    // Takes in questions and answers arrays and returns a list of question-answer pairs
    private List<String[]> getQAPairs(List<String> questions, List<String> answers) {
        List<String[]> list = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            String[] pair = new String[2];
            pair[0] = questions.get(i);
            pair[1] = answers.get(i);
            list.add(pair);
        }
        return list;
    }

    // Show exit dialog and ask user to confirm
    // Finish the game activity if the user confirms
    public void showFinishDialog(int messageId) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.end_game_title)
                .setMessage(messageId)
                .setPositiveButton(R.string.end_game_no, null)
                .setNegativeButton(R.string.end_game_yes, (arg0, arg1) -> finish())
                .create().show();
    }

    // This is run when the activity is created
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game); // Show the game activity layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.game), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the number of questions from the preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        numberOfQuestions = Integer.parseInt(sharedPreferences.getString("number_of_questions", "5"));

        // Initialize questionIndex
        questionIndex = 0;

        // Get questions and answers from arrays in strings.xml
        List<String> questions = Arrays.asList(getResources().getStringArray(R.array.questions));
        List<String> answers = Arrays.asList(getResources().getStringArray(R.array.answers));
        qaPairs = getQAPairs(questions, answers);

        // Randomize the questions
        Collections.shuffle(qaPairs);

        // Limit the number of questions to the number set in the preferences
        qaPairs = qaPairs.subList(0, numberOfQuestions);

        // Get elements from the layout
        questionText = findViewById(R.id.question_text);
        answerText = findViewById(R.id.answer_text);
        feedbackText = findViewById(R.id.feedback_text);
        correctText = findViewById(R.id.correct_text);

        // Set the first question
        questionText.setText(qaPairs.get(questionIndex)[0]);

        // Add event listeners to buttons
        findViewById(R.id.button_0).setOnClickListener(view -> handleDisplayInput("0"));
        findViewById(R.id.button_1).setOnClickListener(view -> handleDisplayInput("1"));
        findViewById(R.id.button_2).setOnClickListener(view -> handleDisplayInput("2"));
        findViewById(R.id.button_3).setOnClickListener(view -> handleDisplayInput("3"));
        findViewById(R.id.button_4).setOnClickListener(view -> handleDisplayInput("4"));
        findViewById(R.id.button_5).setOnClickListener(view -> handleDisplayInput("5"));
        findViewById(R.id.button_6).setOnClickListener(view -> handleDisplayInput("6"));
        findViewById(R.id.button_7).setOnClickListener(view -> handleDisplayInput("7"));
        findViewById(R.id.button_8).setOnClickListener(view -> handleDisplayInput("8"));
        findViewById(R.id.button_9).setOnClickListener(view -> handleDisplayInput("9"));
        findViewById(R.id.submit_button).setOnClickListener(view -> handleSubmit());
        findViewById(R.id.next_button).setOnClickListener(view -> handleNextQuestion());

        // OnBackPressed is deprecated
        // Using OnBackPressedCallback instead
        // Calls showFinishDialog() when the back button is pressed
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showFinishDialog(R.string.early_end_game_message);
            }});
    }

    // Save the state of the game before the screen is rotated
    // This is necessary because the activity is destroyed and restarted when screen rotates
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("questionIndex", questionIndex);
        outState.putString("questionText", questionText.getText().toString());
        outState.putString("answerText", answerText.getText().toString());
        outState.putString("feedbackText", feedbackText.getText().toString());
        outState.putString("correctText", correctText.getText().toString());
        outState.putSerializable("qaPairs", new ArrayList<>(qaPairs));
    }

    // Restore the state of the game after after the screen is rotated
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        questionIndex = savedInstanceState.getInt("questionIndex");
        questionText.setText(savedInstanceState.getString("questionText"));
        answerText.setText(savedInstanceState.getString("answerText"));
        feedbackText.setText(savedInstanceState.getString("feedbackText"));
        correctText.setText(savedInstanceState.getString("correctText"));
        qaPairs = (List<String[]>) savedInstanceState.getSerializable("qaPairs");
    }


}