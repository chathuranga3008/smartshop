package lk.exam.smartshop_admin;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nullable;

import lk.exam.smartshop_admin.adapter.CategoryAdapter;
import lk.exam.smartshop_admin.lisners.CategorySelectListener;
import lk.exam.smartshop_admin.model.Category;

public class AddCategoryActivity extends AppCompatActivity implements CategorySelectListener {

    private FirebaseFirestore firestore;

    private ArrayList<Category> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        firestore = FirebaseFirestore.getInstance();

        categories = new ArrayList<>();

        RecyclerView categoryView = findViewById(R.id.category_list_recycle_view);

        CategoryAdapter categoryAdapter = new CategoryAdapter(categories, AddCategoryActivity.this,this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());

        categoryView.setLayoutManager(linearLayoutManager);

        categoryView.setAdapter(categoryAdapter);

        firestore.collection("category").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange change : value.getDocumentChanges()) {
                    Category category = change.getDocument().toObject(Category.class);
                    switch (change.getType()) {
                        case ADDED:
                            categories.add(category);
                        case MODIFIED:
//                            Category old = categories.stream().filter(i -> i.getCategoryName().equals(category.getCategoryName())).findFirst().orElse(null);
//                            if (old != null) {
//                                old.setCategoryName(category.getCategoryName());
//                            }
                            break;
                        case REMOVED:
                            categories.remove(category);
                    }
                }
                categoryAdapter.notifyDataSetChanged();
            }
        });

        findViewById(R.id.SaveCategoryBTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText categoryNameText = findViewById(R.id.categoryNameInput);

                String categoryName = categoryNameText.getText().toString();
                String id = UUID.randomUUID().toString();

                if (categoryName.isEmpty()) {

                    Toast.makeText(getApplicationContext(), "Please Enter Category Name", Toast.LENGTH_SHORT).show();

                } else {


                    firestore.collection("category").get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                boolean existsCat = false;

                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                    items.clear();
                                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                        Category existscategory = snapshot.toObject(Category.class);

                                        if (categoryName.equals(existscategory.getCategoryName())) {
                                            existsCat = true;
                                        }
                                    }


                                    if (existsCat) {
                                        Toast.makeText(getApplicationContext(), "Category already exists", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Category category = new Category(id, categoryName);

                                        ProgressDialog dialog = new ProgressDialog(AddCategoryActivity.this);
                                        dialog.setMessage("Adding new category...");
                                        dialog.setCancelable(false);
                                        dialog.show();

                                        firestore.collection("category").add(category)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        dialog.dismiss();
                                                        Toast.makeText(getApplicationContext(),"Success", Toast.LENGTH_SHORT).show();

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        dialog.dismiss();
                                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }

                            });


                }
            }
        });

    }

    @Override
    public void deleteCategory(Category category) {
        firestore.collection("category").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            Category category1 = snapshot.toObject(Category.class);

                            if (Objects.equals(category1.getId(), category.getId())) {

                                firestore.document("category/"+snapshot.getId()).delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                break;
                            }

                        }
                    }
                });
    }
}