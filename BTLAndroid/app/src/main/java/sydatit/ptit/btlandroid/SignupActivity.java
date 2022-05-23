package sydatit.ptit.btlandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import sydatit.ptit.btlandroid.databinding.ActivitySignupBinding;
import sydatit.ptit.btlandroid.dialog.LoadingDiaglog;
import sydatit.ptit.btlandroid.model.User;

public class SignupActivity extends AppCompatActivity {

    ActivitySignupBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore database;
    LoadingDiaglog loadingDiaglog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        loadingDiaglog = new LoadingDiaglog(this);

        binding.createNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass, email, name, referCode;
                pass = binding.passwordBox.getText().toString();
                email = binding.emailBox.getText().toString();
                name = binding.nameBox.getText().toString();
                referCode = binding.referBox.getText().toString();

                User user = new User(name, pass, email, referCode);
                loadingDiaglog.showDialog("Sign up...");
                auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if( task.isSuccessful()){
                            String uid = task.getResult().getUser().getUid();
                            database
                                    .collection("users")
                                    .document(uid)
                                    .set(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if( task.isSuccessful()){
                                                loadingDiaglog.hideDialog();
                                                startActivity(new Intent(SignupActivity.this,
                                                        MainActivity.class));
                                                finish();
                                            }else {
                                                Toast.makeText(SignupActivity.this, task.getException()
                                                        .getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        }else {
                            loadingDiaglog.hideDialog();
                            Toast.makeText(SignupActivity.this, task.getException()
                                    .getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}