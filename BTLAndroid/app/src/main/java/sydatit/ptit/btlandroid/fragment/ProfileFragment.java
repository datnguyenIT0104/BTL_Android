package sydatit.ptit.btlandroid.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;

import sydatit.ptit.btlandroid.R;
import sydatit.ptit.btlandroid.databinding.FragmentProfileBinding;
import sydatit.ptit.btlandroid.dialog.LoadingDiaglog;
import sydatit.ptit.btlandroid.model.User;


public class ProfileFragment extends Fragment implements View.OnClickListener {
    private FirebaseFirestore database;
    private FragmentProfileBinding binding;
    private FirebaseAuth auth;
    private Uri imageUri;
    private StorageReference storageReference;
    private LoadingDiaglog diaglog;
    private String userID;
    private User user;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        database = FirebaseFirestore.getInstance();
        diaglog = new LoadingDiaglog(getContext());
        auth = FirebaseAuth.getInstance();
        userID = auth.getUid();


        binding.profileImage.setOnClickListener(this);
        binding.updateBtn.setOnClickListener(this);

        database.collection("users")
                .document(userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        user = documentSnapshot.toObject(User.class);
                        Glide.with(getContext())
                                .load(user.getProfile())
                                .into(binding.profileImage);
                        binding.nameBox.setText(user.getName());
                        binding.emailBox.setText(user.getEmail());
                        binding.passwordBox.setText(user.getPass());
                    }
                });
        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.profileImage:
                selectProfile();
                break;
            case R.id.updateBtn:
                update();
                break;
        }
    }

    private void update() {
        diaglog.showDialog("Update...");

        SimpleDateFormat format = new SimpleDateFormat("HH_mm_ss_dd_MM_yyyy");
        Date date = new Date();
        String fileName = format.format(date);

        storageReference = FirebaseStorage.getInstance().getReference("images/" + fileName);
        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        diaglog.hideDialog();

                        storageReference.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        updatePorile( uri.toString());
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        diaglog.hideDialog();
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void updatePorile(String url) {
        String name = binding.nameBox.getText().toString();
        String email = binding.emailBox.getText().toString();
        String pass = binding.passwordBox.getText().toString();
        if( name != null && !name.equals(""))
            user.setName(name);
        if( email != null && !email.equals(""))
            user.setEmail(email);
        if( pass != null && !pass.equals(""))
            user.setPass(pass);
        user.setProfile(url);


        database.collection("users")
                .document(userID)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Update success.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void selectProfile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode == 100 && data != null && data.getData() != null){
            imageUri = data.getData();
            binding.profileImage.setImageURI(imageUri);
        }
    }
}