package sydatit.ptit.btlandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import sydatit.ptit.btlandroid.databinding.ActivityResultBinding;

public class ResultActivity extends AppCompatActivity {
    private ActivityResultBinding binding;
    private final int POINTS = 10;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseFirestore.getInstance();

        int correctAnswer = getIntent().getIntExtra("correct", 0);
        int totalQuestion = getIntent().getIntExtra("total", 0);
        long earnCoins = correctAnswer * POINTS;

        binding.score.setText( correctAnswer + "/" + totalQuestion);
        binding.earnedCoins.setText(earnCoins + "");

        String userId = FirebaseAuth.getInstance().getUid();

        database.collection("users")
                .document(userId)
                .update("coins", FieldValue.increment(earnCoins));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}