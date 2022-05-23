package sydatit.ptit.btlandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import sydatit.ptit.btlandroid.SpinWheel.LuckyWheelView;
import sydatit.ptit.btlandroid.SpinWheel.model.LuckyItem;
import sydatit.ptit.btlandroid.databinding.ActivitySpinnerBinding;

public class SpinnerActivity extends AppCompatActivity {
    private ActivitySpinnerBinding binding;
    private FirebaseFirestore database;
    private FirebaseAuth auth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySpinnerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseFirestore.getInstance();

        auth = FirebaseAuth.getInstance();
        userId = auth.getUid();

        List<LuckyItem> data = new ArrayList<>();

        LuckyItem item1 = new LuckyItem();
        item1.topText = "35";
        item1.secondaryText = "COINS";
        item1.color = Color.parseColor("#ffffff");
        item1.textColor = Color.parseColor("#000000");
        data.add(item1);

        LuckyItem item2 = new LuckyItem();
        item2.topText = "0";
        item2.secondaryText = "COINS";
        item2.color = Color.parseColor("#00ffe1");
        item2.textColor = Color.parseColor("#ff0000");
        data.add(item2);

        LuckyItem item3 = new LuckyItem();
        item3.topText = "5";
        item3.secondaryText = "COINS";
        item3.color = Color.parseColor("#ffffff");
        item3.textColor = Color.parseColor("#0008ff");
        data.add(item3);

        LuckyItem item4 = new LuckyItem();
        item4.topText = "10";
        item4.secondaryText = "COINS";
        item4.color = Color.parseColor("#ff0000");
        item4.textColor = Color.parseColor("#ffffff");
        data.add(item4);

        LuckyItem item5 = new LuckyItem();
        item5.topText = "15";
        item5.secondaryText = "COINS";
        item5.color = Color.parseColor("#ffffff");
        item5.textColor = Color.parseColor("#000000");
        data.add(item5);

        LuckyItem item6 = new LuckyItem();
        item6.topText = "20";
        item6.secondaryText = "COINS";
        item6.color = Color.parseColor("#0800ff");
        item6.textColor = Color.parseColor("#00ffed");
        data.add(item6);

        LuckyItem item7 = new LuckyItem();
        item7.topText = "25";
        item7.secondaryText = "COINS";
        item7.color = Color.parseColor("#ffffff");
        item7.textColor = Color.parseColor("#000000");
        data.add(item7);

        LuckyItem item8 = new LuckyItem();
        item8.topText = "80";
        item8.secondaryText = "COINS";
        item8.color = Color.parseColor("#37ff00");
        item8.textColor = Color.parseColor("#ff00e5");
        data.add(item8);


        binding.wheelview.setData(data);
        binding.wheelview.setRound(5);

        binding.spinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random random = new Random();
                int rand = random.nextInt(8);

                binding.wheelview.startLuckyWheelWithTargetIndex(rand);

            }
        });

        binding.wheelview.setLuckyRoundItemSelectedListener(new LuckyWheelView.LuckyRoundItemSelectedListener() {
            @Override
            public void LuckyRoundItemSelected(int index) {
                updateCoins(index);
            }
        });

    }

    private void updateCoins(int index){
        long coins = 0;
        switch (index){
            case 0:
                coins = 35;
                break;
            case 1:
                coins = 0;
                break;
            case 2:
                coins = 5;
                break;
            case 3:
                coins = 10;
                break;
            case 4:
                coins = 15;
                break;
            case 5:
                coins = 20;
                break;
            case 6:
                coins = 25;
                break;
            case 7:
                coins = 80;
                break;

        }
        long finalCoins = coins;
        database.collection("users")
                .document(userId)
                .update("coins", FieldValue.increment(coins))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if( finalCoins < 80){
                            Toast.makeText(SpinnerActivity.this, "Coins added in account.", Toast.LENGTH_SHORT).show();
                        }else
                            Toast.makeText(SpinnerActivity.this, "Congratulations!!!", Toast.LENGTH_SHORT).show();

                        finish();
                    }
                });

    }
}