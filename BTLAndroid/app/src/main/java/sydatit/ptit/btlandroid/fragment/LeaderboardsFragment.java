package sydatit.ptit.btlandroid.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import sydatit.ptit.btlandroid.R;
import sydatit.ptit.btlandroid.adapter.LeaderboardAdapter;
import sydatit.ptit.btlandroid.databinding.FragmentLeaderboardsBinding;
import sydatit.ptit.btlandroid.model.User;


public class LeaderboardsFragment extends Fragment {
    private FragmentLeaderboardsBinding binding;
    private FirebaseFirestore database;
    private ArrayList<User> users;
    private LeaderboardAdapter adapter;

    public LeaderboardsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLeaderboardsBinding.inflate(inflater, container, false);

        users = new ArrayList<>();
        adapter = new LeaderboardAdapter(getContext(), users);
        database = FirebaseFirestore.getInstance();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.recyclerView.setAdapter(adapter);

        database.collection("users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        users.clear();
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            User user = snapshot.toObject(User.class);
                            users.add(user);
                        }
                        Collections.sort(users, new Comparator<User>() {
                            @Override
                            public int compare(User user, User user2) {
                                return user.getCoins() > user2.getCoins() ? -1 : 1;
                            }
                        });
                        adapter.notifyDataSetChanged();
                    }
                });
//        database.collection("users")
//                .orderBy("coins", Query.Direction.DESCENDING)
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
//                            User user = snapshot.toObject(User.class);
//                            users.add(user);
//                        }
//                        adapter.notifyDataSetChanged();
//                    }
//                });


        return binding.getRoot();
    }
}