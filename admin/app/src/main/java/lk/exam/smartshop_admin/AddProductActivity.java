package lk.exam.smartshop_admin;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.UUID;

import lk.exam.smartshop_admin.model.Category;
import lk.exam.smartshop_admin.model.Product;

public class AddProductActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private ArrayList<String> arrayList;
    private Spinner spinner;
    private ArrayAdapter<String> spinnerArrayAdapter;
    private String category;

    private ImageButton addImageButton1;
    private ImageButton addImageButton2;
    private ImageButton addImageButton3;

    private Uri imagePath1;
    private Uri imagePath2;
    private Uri imagePath3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        addImageButton1 = findViewById(R.id.imageButton1);
        addImageButton2 = findViewById(R.id.imageButton2);
        addImageButton3 = findViewById(R.id.imageButton3);

        String image1Id = UUID.randomUUID().toString();
        String image2Id = UUID.randomUUID().toString();
        String image3Id = UUID.randomUUID().toString();

        addImageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                activityResultLauncher1.launch(Intent.createChooser(intent, "Select Image"));
            }
        });

        addImageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                activityResultLauncher2.launch(Intent.createChooser(intent, "Select Image"));
            }
        });

        addImageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                activityResultLauncher3.launch(Intent.createChooser(intent, "Select Image"));
            }
        });

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        firestore = FirebaseFirestore.getInstance();
        arrayList = new ArrayList<>();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                firestore.collection("category").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                        Category item = snapshot.toObject(Category.class);
                                        arrayList.add(item.getCategoryName());
                                    }
                                    // Update UI on the main (UI) thread
                                    setupSpinnerAdapter();
                                } else {
                                }
                            }
                        });
            }
        });
        thread.start();


        findViewById(R.id.addProductButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Spinner categoryText = findViewById(R.id.categorySpinner);
                EditText descriptionText = findViewById(R.id.descriptionText);
                EditText titleText = findViewById(R.id.titleText);
                EditText variationText = findViewById(R.id.variationText);
                EditText qtyText = findViewById(R.id.qtyText);
                EditText priceText = findViewById(R.id.priceText);
                EditText deliveryCostText = findViewById(R.id.deliveryCostText);


                String id = UUID.randomUUID().toString();
                String category = categoryText.getSelectedItem().toString();
                String description = descriptionText.getText().toString();
                String title = titleText.getText().toString();
                String color = variationText.getText().toString();
                String qty = qtyText.getText().toString();
                String cost = priceText.getText().toString();
                String deliveryCost = deliveryCostText.getText().toString();

                if (category.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Select category", Toast.LENGTH_SHORT).show();
                } else if (description.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter Description", Toast.LENGTH_SHORT).show();
                } else if (title.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter title", Toast.LENGTH_SHORT).show();
                } else if (color.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter color", Toast.LENGTH_SHORT).show();
                } else if (qty.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter Quantity", Toast.LENGTH_SHORT).show();
                } else if (cost.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter cost", Toast.LENGTH_SHORT).show();
                } else if (deliveryCost.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter delivery Cost", Toast.LENGTH_SHORT).show();
                } else {

                    firestore.collection("product").get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                boolean existsProductStatus = false;

                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                        Product existsUser = snapshot.toObject(Product.class);

                                        if (title.equals(existsUser.getTitle())) {
                                            existsProductStatus = true;
                                        }
                                    }
                                    if (existsProductStatus) {
                                        Toast.makeText(getApplicationContext(), "Product already exists", Toast.LENGTH_SHORT).show();
                                    } else {

                                        firestore.collection("product").get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    boolean existsTitle = false;

                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                            Product exitsItem = snapshot.toObject(Product.class);
                                                            if (title.equals(exitsItem.getTitle())) {
                                                                existsTitle = true;
                                                            }
                                                        }
                                                        if (existsTitle) {
                                                            Toast.makeText(AddProductActivity.this, "Item title Already exists", Toast.LENGTH_SHORT).show();
                                                        } else {

                                                            String ref = String.valueOf(System.currentTimeMillis());

                                                            Product item = new Product(ref, title, category, color, qty, cost, deliveryCost, description, image1Id, image2Id, image3Id);

                                                            ProgressDialog dialog = new ProgressDialog(AddProductActivity.this);
                                                            dialog.setMessage("Adding new item...");
                                                            dialog.setCancelable(false);
                                                            dialog.show();

                                                            firestore.collection("product").add(item)
                                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                        @Override
                                                                        public void onSuccess(DocumentReference documentReference) {
                                                                            dialog.setMessage("Uploading image 1...");

                                                                            StorageReference reference = storage.getReference("productImages")
                                                                                    .child(image1Id);
                                                                            reference.putFile(imagePath1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                                @Override
                                                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                    dialog.setMessage("Uploading image 2...");

                                                                                    StorageReference reference2 = storage.getReference("productImages")
                                                                                            .child(image2Id);
                                                                                    reference2.putFile(imagePath2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                                        @Override
                                                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                            dialog.setMessage("Uploading image 3...");

                                                                                            StorageReference reference3 = storage.getReference("productImages")
                                                                                                    .child(image3Id);
                                                                                            reference3.putFile(imagePath3).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                                                @Override
                                                                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                                    dialog.dismiss();
                                                                                                    Toast.makeText(AddProductActivity.this, "Item add success", Toast.LENGTH_SHORT).show();
                                                                                                    startActivity(new Intent(AddProductActivity.this, MainActivity.class));
                                                                                                    finish();
                                                                                                }
                                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                                                @Override
                                                                                                public void onFailure(@NonNull Exception e) {
                                                                                                    dialog.dismiss();
                                                                                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                                                                @Override
                                                                                                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                                                                                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                                                                                                    dialog.setMessage("Image 3 uploading " + (int) progress + "%");
                                                                                                }
                                                                                            });
                                                                                        }
                                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                            dialog.dismiss();
                                                                                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                                                        @Override
                                                                                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                                                                            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                                                                                            dialog.setMessage("Image 2 uploading " + (int) progress + "%");
                                                                                        }
                                                                                    });
                                                                                }
                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    dialog.dismiss();
                                                                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                                                @Override
                                                                                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                                                                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                                                                                    dialog.setMessage("Image 1 uploading " + (int) progress + "%");
                                                                                }
                                                                            });

                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            dialog.dismiss();
                                                                            Toast.makeText(AddProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });

                                    }
                                }
                            });
                }
            }
        });
    }

    ActivityResultLauncher<Intent> activityResultLauncher1 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        imagePath1 = result.getData().getData();

                        Picasso.get().load(imagePath1).resize(150, 150).into(addImageButton1);
                    }
                }
            }
    );
    ActivityResultLauncher<Intent> activityResultLauncher2 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        imagePath2 = result.getData().getData();

                        Picasso.get().load(imagePath2).resize(150, 150).into(addImageButton2);
                    }
                }
            }
    );
    ActivityResultLauncher<Intent> activityResultLauncher3 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        imagePath3 = result.getData().getData();

                        Picasso.get().load(imagePath3).resize(150, 150).into(addImageButton3);
                    }
                }
            }
    );

    private void setupSpinnerAdapter() {
        spinner = findViewById(R.id.categorySpinner);
        spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        category = adapterView.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}