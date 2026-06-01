package lk.exam.smartshop_admin;

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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lk.exam.smartshop_admin.lisners.ProductListener;
import lk.exam.smartshop_admin.model.Category;
import lk.exam.smartshop_admin.model.Product;

public class UpdateProductActivity extends AppCompatActivity implements ProductListener, AdapterView.OnItemSelectedListener {
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private ArrayList<String> categories;
    private String selectedCategory;
    private int catPosition;
    private Product item;
    private String oldCategory;
    private ImageButton addImageButton1;
    private ImageButton addImageButton2;
    private ImageButton addImageButton3;
    private Uri imagePath1;
    private Uri imagePath2;
    private Uri imagePath3;
    boolean isFinished1 = true;
    boolean isFinished2 = true;
    boolean isFinished3 = true;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        id = getIntent().getExtras().getString("itemId");

        EditText titleText = findViewById(R.id.titleUpdateText);
        EditText variationText = findViewById(R.id.varationUpdateText);
        EditText descText = findViewById(R.id.descriptionUpdateText);
        EditText priceText = findViewById(R.id.costUpdateText);
        EditText qtyText = findViewById(R.id.qtyUpdateText);
        EditText delivertCostText = findViewById(R.id.deliveyCostUpdate);

        addImageButton1 = findViewById(R.id.update_add_img_btn1);
        addImageButton2 = findViewById(R.id.update_add_img_btn2);
        addImageButton3 = findViewById(R.id.update_add_img_btn3);

//        ImageButton image1=findViewById(R.id.update_add_img_btn1);
//        ImageButton image2=findViewById(R.id.update_add_img_btn2);
//        ImageButton image3=findViewById(R.id.update_add_img_btn3);

        addImageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                activityResultLauncher.launch(Intent.createChooser(intent, "Select Image"));

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
//

//        Load Item Data
        firestore.collection("product").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            item = snapshot.toObject(Product.class);

                            if (item.getId().equals(id)) {
                                titleText.setText(item.getTitle());
                                variationText.setText(item.getVariation());
                                descText.setText(item.getDescription());
                                priceText.setText(item.getCost());
                                qtyText.setText(item.getQuantity());
                                delivertCostText.setText(item.getDelivery_cost());
                                oldCategory = item.getCategory();

                                storage.getReference("productImages/" + item.getImage1Id())
                                        .getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Picasso.get()
                                                        .load(uri)
                                                        .fit()
                                                        .centerCrop()
                                                        .into(addImageButton1);
                                            }
                                        });
                                storage.getReference("productImages/" + item.getImage2Id())
                                        .getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Picasso.get()
                                                        .load(uri)
                                                        .fit()
                                                        .centerCrop()
                                                        .into(addImageButton2);
                                            }
                                        });
                                storage.getReference("productImages/" + item.getImage3Id())
                                        .getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Picasso.get()
                                                        .load(uri)
                                                        .fit()
                                                        .centerCrop()
                                                        .into(addImageButton3);
                                            }
                                        });
                                break;
                            }

                        }
                    }
                });
//        Load Item Data

        categories = new ArrayList<String>();
//        Spinner spinner = findViewById(R.id.update_add_item_catrgories);
//        firestore.collection("Categories").get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
////                            categories.add("Select Category");
////                            int i=0;
//                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
//                                Category category = snapshot.toObject(Category.class);
//                                categories.add(category.getCategoryName());
////                                i=i+1;
////                                if(oldCategory.equals(category.getCategoryName())){
////                                    catPosition=i;
////                                }
//                                for (int i = 0; i < categories.size(); i++) {
//                                    if (categories.get(i).equals(oldCategory)) {
//                                        catPosition = i;
//                                        break;
//                                    }
//                                }
//                            }
//                            // Move the ArrayAdapter and setAdapter inside onComplete
//                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(UpdateItemActivity.this, android.R.layout.simple_spinner_item, categories);
//                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                            spinner.setSelection(catPosition);
//                            spinner.setAdapter(spinnerArrayAdapter);
//                        } else {
//                            // Handle errors here
//                        }
//                    }
//                });
//        spinner.setOnItemSelectedListener(this);


        firestore.collection("category").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                Category item = snapshot.toObject(Category.class);
                                categories.add(item.getCategoryName());
                            }

                            for (int i = 0; i < categories.size(); i++) {
                                if (categories.get(i).equals(item.getCategory().toString())) {
                                    catPosition = i;
                                    break;
                                }
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setupSpinnerAdapter();
                                }
                            });

                        } else {
                        }

                    }
                });


/////////////////////////////////delete item

/////////////////////////////////delete item


/////////////////////////////////update item

        findViewById(R.id.addProductUpdateBTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText titleText = findViewById(R.id.titleUpdateText);
                EditText colorText = findViewById(R.id.varationUpdateText);
                EditText descText = findViewById(R.id.descriptionUpdateText);
                EditText priceText = findViewById(R.id.costUpdateText);
                EditText qtyText = findViewById(R.id.qtyUpdateText);
                EditText deliveryCostText = findViewById(R.id.deliveyCostUpdate);

                String title = titleText.getText().toString();
                String color = colorText.getText().toString();
                String desc = descText.getText().toString();
                String price = priceText.getText().toString();
                String qty = qtyText.getText().toString();
                String deliveryCost = deliveryCostText.getText().toString();

                String category = oldCategory;

                String image1Id = UUID.randomUUID().toString();
                String image2Id = UUID.randomUUID().toString();
                String image3Id = UUID.randomUUID().toString();


                if (title.isEmpty()) {
                    Toast.makeText(UpdateProductActivity.this, "Please enter item title", Toast.LENGTH_SHORT).show();
                } else if (desc.isEmpty()) {
                    Toast.makeText(UpdateProductActivity.this, "Please enter item description", Toast.LENGTH_SHORT).show();
                } else if (color.isEmpty()) {
                    Toast.makeText(UpdateProductActivity.this, "Please enter item color", Toast.LENGTH_SHORT).show();
                } else if (price.isEmpty()) {
                    Toast.makeText(UpdateProductActivity.this, "Please enter item price", Toast.LENGTH_SHORT).show();
                } else if (deliveryCost.isEmpty()) {
                    Toast.makeText(UpdateProductActivity.this, "Please enter item delivery Cost", Toast.LENGTH_SHORT).show();
                } else if (qty.isEmpty()) {
                    Toast.makeText(UpdateProductActivity.this, "Please enter item qty", Toast.LENGTH_SHORT).show();
                } else {

                    firestore.collection("product")
                            .whereNotEqualTo("id", id).get()
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
                                        Toast.makeText(UpdateProductActivity.this, "Item title Already exists", Toast.LENGTH_SHORT).show();
                                    } else {

                                        String ref = String.valueOf(System.currentTimeMillis());

                                        Map<String, Object> updates = new HashMap<>();
                                        updates.put("title", title);
                                        updates.put("cost", price);
                                        updates.put("color", color);
                                        updates.put("quantity", qty);
                                        updates.put("description", desc);
                                        updates.put("delivery_cost", deliveryCost);

                                        if (imagePath1 != null) {
                                            updates.put("image1Id", image1Id);
                                            isFinished1 = false;
                                        }

                                        if (imagePath2 != null) {
                                            updates.put("image2Id", image2Id);
                                            isFinished2 = false;
                                        }

                                        if (imagePath3 != null) {
                                            updates.put("image3Id", image3Id);
                                            isFinished3 = false;
                                        }

                                        firestore.collection("product")
                                                .whereEqualTo("id", id).get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            List<String> documentIds = new ArrayList<>();

                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                String documentId = document.getId();

                                                                ProgressDialog dialog = new ProgressDialog(UpdateProductActivity.this);
                                                                dialog.setMessage("Adding new item...");
                                                                dialog.setCancelable(false);
                                                                dialog.show();

                                                                firestore.collection("product").document(documentId).update(updates)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {
                                                                                dialog.dismiss();

                                                                                StorageReference storageRef = storage.getReference();


                                                                                if (imagePath1 != null) {

                                                                                    ProgressDialog dialog = new ProgressDialog(UpdateProductActivity.this);
                                                                                    dialog.setMessage("Uploading");
                                                                                    dialog.setCancelable(false);
                                                                                    dialog.show();

                                                                                    StorageReference desertRef = storageRef.child("productImages/" + item.getImage1Id());

                                                                                    desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {
                                                                                        }
                                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception exception) {
                                                                                        }
                                                                                    });

                                                                                    StorageReference reference = storage.getReference("productImages")
                                                                                            .child(image1Id);
                                                                                    reference.putFile(imagePath1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                                        @Override
                                                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                            isFinished1 = true;

                                                                                            if (isFinished1 & isFinished2 & isFinished3) {
                                                                                                finish();
                                                                                            }

                                                                                            dialog.dismiss();
                                                                                        }
                                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                            dialog.dismiss();
                                                                                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                                                        }
                                                                                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                                                        @Override
                                                                                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                                                                            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                                                                                            dialog.setMessage("Image 1 Uploading " + (int) progress + "%");
                                                                                        }
                                                                                    });


                                                                                } else {
                                                                                    isFinished1 = true;
                                                                                }


                                                                                if (imagePath2 != null) {
                                                                                    ProgressDialog dialog = new ProgressDialog(UpdateProductActivity.this);
                                                                                    dialog.setMessage("Uploading");
                                                                                    dialog.setCancelable(false);
                                                                                    dialog.show();

                                                                                    StorageReference desertRef = storageRef.child("productImages/" + item.getImage2Id());

                                                                                    desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {
                                                                                        }
                                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception exception) {
                                                                                        }
                                                                                    });

                                                                                    StorageReference reference = storage.getReference("productImages")
                                                                                            .child(image2Id);
                                                                                    reference.putFile(imagePath2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                                        @Override
                                                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                            isFinished2 = true;
                                                                                            if (isFinished1 & isFinished2 & isFinished3) {
                                                                                                finish();
                                                                                            }
                                                                                            dialog.dismiss();
                                                                                        }
                                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                            dialog.dismiss();
                                                                                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                                                        }
                                                                                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                                                        @Override
                                                                                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                                                                            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                                                                                            dialog.setMessage("Image 2 Uploading " + (int) progress + "%");
                                                                                        }
                                                                                    });
                                                                                } else {
                                                                                    isFinished2 = true;
                                                                                }


                                                                                if (imagePath3 != null) {
                                                                                    ProgressDialog dialog = new ProgressDialog(UpdateProductActivity.this);
                                                                                    dialog.setMessage("Uploading");
                                                                                    dialog.setCancelable(false);
                                                                                    dialog.show();

                                                                                    StorageReference desertRef = storageRef.child("productImages/" + item.getImage3Id());

                                                                                    desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {
                                                                                        }
                                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception exception) {
                                                                                        }
                                                                                    });

                                                                                    StorageReference reference = storage.getReference("productImages")
                                                                                            .child(image3Id);
                                                                                    reference.putFile(imagePath3).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                                        @Override
                                                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                            isFinished3 = true;
                                                                                            if (isFinished1 & isFinished2 & isFinished3) {
                                                                                                finish();
                                                                                            }
                                                                                            dialog.dismiss();
                                                                                        }
                                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                            dialog.dismiss();
                                                                                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                                                        }
                                                                                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                                                        @Override
                                                                                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                                                                            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                                                                                            dialog.setMessage("Image 3 Uploading " + (int) progress + "%");
                                                                                        }
                                                                                    });
                                                                                } else {
                                                                                    isFinished3 = true;
                                                                                }


                                                                            }
                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                dialog.dismiss();
                                                                                Toast.makeText(UpdateProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });


                                                            }

                                                        } else {
                                                        }
                                                    }
                                                });

                                    }
                                }
                            });
                }
            }
        });

/////////////////////////////////update item

    }


    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        imagePath1 = result.getData().getData();
//                        Log.i(TAG,"image path : "+imagePath.getPath());

                        Picasso.get()
                                .load(imagePath1)
                                .fit()
                                .centerCrop()
                                .into(addImageButton1);
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
//                        Log.i(TAG,"image path : "+imagePath.getPath());

                        Picasso.get()
                                .load(imagePath2)
                                .fit()
                                .centerCrop()
                                .into(addImageButton2);
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
//                        Log.i(TAG,"image path : "+imagePath.getPath());

                        Picasso.get()
                                .load(imagePath3)
                                .fit()
                                .centerCrop()
                                .into(addImageButton3);
                    }
                }
            }
    );

    private void setupSpinnerAdapter() {
        Spinner spinner = findViewById(R.id.spinnerUpdate);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        if (catPosition != -1) {
            spinner.setSelection(catPosition);
        }
        spinner.setOnItemSelectedListener(this);

    }

    @Override
    public void deleteProduct(Product addProduct) {

        findViewById(R.id.deleteBTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firestore.collection("product").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                    Product item = snapshot.toObject(Product.class);

                                    if (item.getId().equals(id)) {

                                        StorageReference storageRef = storage.getReference();
                                        StorageReference desertRef1 = storageRef.child("productImages/" + item.getImage1Id());

                                        desertRef1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(UpdateProductActivity.this, "Image one Removed", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                Toast.makeText(UpdateProductActivity.this, "Please Try again later.", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        StorageReference desertRef2 = storageRef.child("productImages/" + item.getImage2Id());
                                        desertRef2.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(UpdateProductActivity.this, "Image two Removed", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                Toast.makeText(UpdateProductActivity.this, "Please Try again later.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        StorageReference desertRef3 = storageRef.child("productImages/" + item.getImage3Id());
                                        desertRef3.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(UpdateProductActivity.this, "Image three Removed", Toast.LENGTH_SHORT).show();
                                                firestore.document("products/" + snapshot.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(UpdateProductActivity.this, "Item Removed.", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(UpdateProductActivity.this, ManageProductActivity.class));
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(UpdateProductActivity.this, "Please Try again later.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                Toast.makeText(UpdateProductActivity.this, "Please Try again later.", Toast.LENGTH_SHORT).show();
                                            }
                                        });


                                        break;
                                    }

                                }
                            }
                        });
            }
        });

    }

    @Override
    public void updateProduct(Product addProduct) {

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}