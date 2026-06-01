package lk.exam.smartshop_admin;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import lk.exam.smartshop_admin.adapter.UserAdapter;
import lk.exam.smartshop_admin.model.User;

public class ManageUserActivity extends AppCompatActivity {


    private FirebaseFirestore firestore;
    private FirebaseStorage firebaseStorage;
    private ArrayList<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);

        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        users = new ArrayList<>();


        RecyclerView userView = findViewById(R.id.AllUsersView);

        UserAdapter usersAdapter = new UserAdapter(users, ManageUserActivity.this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ManageUserActivity.this);

        userView.setLayoutManager(linearLayoutManager);
        userView.setAdapter(usersAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                firestore.collection("user").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (DocumentChange change : value.getDocumentChanges()){
                            User usersDt = change.getDocument().toObject(User.class);
                            switch (change.getType()){
                                case ADDED:
                                    users.add(usersDt);
                                case MODIFIED:

                                    User old = users.stream().filter(i -> i.getId().equals(usersDt.getId())).findFirst().orElse(null);
                                    if (old != null) {
                                        old.setFirstName(usersDt.getFirstName());
                                        old.setEmail(usersDt.getEmail());
                                        old.setMobile(usersDt.getMobile());
                                    }
                                    break;
                                case REMOVED:

                            }
                        }
                        usersAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();

    }
}