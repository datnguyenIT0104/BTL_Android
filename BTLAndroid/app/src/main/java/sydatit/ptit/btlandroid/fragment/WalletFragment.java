package sydatit.ptit.btlandroid.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import sydatit.ptit.btlandroid.R;
import sydatit.ptit.btlandroid.databinding.FragmentWalletBinding;
import sydatit.ptit.btlandroid.model.User;
import sydatit.ptit.btlandroid.model.WithdrawRequest;


public class WalletFragment extends Fragment {

    private FragmentWalletBinding binding;
    private FirebaseFirestore database;
    private User user;
    public WalletFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWalletBinding.inflate(inflater, container, false);
        database = FirebaseFirestore.getInstance();
        String userID = FirebaseAuth.getInstance().getUid();

        database.collection("users")
                .document(userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        user = documentSnapshot.toObject(User.class);
                        binding.currentCoins.setText( user.getCoins() + "");
                    }
                });
        binding.sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( user.getCoins() > 50000){
                    String payPal = binding.emailBox.getText().toString();
                    WithdrawRequest request = new WithdrawRequest(userID, payPal, user.getName());

                    database.collection("withdraws")
                            .document(userID)
                            .set(request)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getContext(), "Send request successfully.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }else {
                    Toast.makeText(getContext(), "You need more coins to withdraw!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return binding.getRoot();
    }
}