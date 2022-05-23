package sydatit.ptit.btlandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Random;

import sydatit.ptit.btlandroid.databinding.ActivityQuizBinding;
import sydatit.ptit.btlandroid.model.Question;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener{
    private ActivityQuizBinding binding;
    private ArrayList<Question> list;
    private int index = -1;
    private Question question;
    private CountDownTimer timer;
    private FirebaseFirestore database;
    private int correctAnswer = 0;
    private boolean isAnswered = false;
    private boolean isFinish = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        list = new ArrayList<>();
        database = FirebaseFirestore.getInstance();

        binding.nextBtn.setOnClickListener(this);
        binding.option1.setOnClickListener(this);
        binding.option2.setOnClickListener(this);
        binding.option3.setOnClickListener(this);
        binding.option4.setOnClickListener(this);
        binding.quizBtn.setOnClickListener(this);

        Random random = new Random();
        int rand = random.nextInt(11);
        String categoryID = getIntent().getStringExtra("categoryID");
        database.collection("categories")
                .document(categoryID)
                .collection("questions")
                .whereGreaterThanOrEqualTo("index", rand)
                .orderBy("index")
                .limit(5)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if( queryDocumentSnapshots.getDocuments().size() < 5){
                            database.collection("categories")
                                    .document(categoryID)
                                    .collection("questions")
                                    .whereLessThanOrEqualTo("index", rand)
                                    .orderBy("index")
                                    .limit(5)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for(DocumentSnapshot snapshot : queryDocumentSnapshots){
                                                Question question = snapshot.toObject(Question.class);
                                                list.add(question);
                                            }
                                            setNextQuestion();
                                        }
                                    });
                        }else {
                            for(DocumentSnapshot snapshot : queryDocumentSnapshots){
                                Question question = snapshot.toObject(Question.class);
                                list.add(question);
                            }
                            setNextQuestion();
                        }
                    }
                });
        resetTimer();

    }
    private void showAnswer(){
        String rightAnswer = question.getAnswer().trim();
        String option1 = binding.option1.getText().toString().trim();
        String option2 = binding.option2.getText().toString().trim();
        String option3 = binding.option3.getText().toString().trim();
        String option4 = binding.option4.getText().toString().trim();

        if( rightAnswer.equals(option1)){
            binding.option1.setBackground(getResources().getDrawable(R.drawable.option_right));
        }else if( rightAnswer.equals(option2)){
            binding.option2.setBackground(getResources().getDrawable(R.drawable.option_right));
        }else if( rightAnswer.equals(option3)){
            binding.option3.setBackground(getResources().getDrawable(R.drawable.option_right));
        }else{
            binding.option4.setBackground(getResources().getDrawable(R.drawable.option_right));
        }
    }

    private void resetTimer(){
        timer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long l) {
                binding.timer.setText( (l/1000) + "");
            }

            @Override
            public void onFinish() {
                reset();
                setNextQuestion();
            }
        };
    }

    private void setNextQuestion(){
        if( timer != null)
            timer.cancel();
        timer.start();
        if( index + 1 < list.size()){
            index++;
            binding.questionCounter.setText(String.format("%d/%d", index+1, list.size()));
            question = list.get(index);
            binding.question.setText(question.getQuestion());
            binding.option1.setText(question.getOption1());
            binding.option2.setText(question.getOption2());
            binding.option3.setText(question.getOption3());
            binding.option4.setText(question.getOption4());

        }else {
            if( !isFinish){
                isFinish = true;
                Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
                intent.putExtra("correct", correctAnswer);
                intent.putExtra("total", list.size());
                startActivity(intent);
                finish();

            }
//            Toast.makeText(this, "Quiz Finished!", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkAnswer(TextView textView){
        String selected = textView.getText().toString().trim();
        if( isAnswered )
            return;
        else isAnswered = true;

        if( selected.equals(question.getAnswer().trim())){
            correctAnswer++;
            textView.setBackground(getResources().getDrawable(R.drawable.option_right));
        }else {
            showAnswer();
            textView.setBackground(getResources().getDrawable(R.drawable.option_wrong));
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.option_1:
            case R.id.option_2:
            case R.id.option_3:
            case R.id.option_4:
                if( timer != null)
                    timer.cancel();
                checkAnswer((TextView) view);
                break;
            case R.id.nextBtn:
                reset();
                setNextQuestion();
                break;
        }
    }

    private void reset() {
        isAnswered = false;
        binding.option1.setBackground(getResources().getDrawable(R.drawable.option_unselected));
        binding.option2.setBackground(getResources().getDrawable(R.drawable.option_unselected));
        binding.option3.setBackground(getResources().getDrawable(R.drawable.option_unselected));
        binding.option4.setBackground(getResources().getDrawable(R.drawable.option_unselected));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onPause();
        finish();
    }
}