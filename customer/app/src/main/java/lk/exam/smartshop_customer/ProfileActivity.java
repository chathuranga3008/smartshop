package lk.exam.smartshop_customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.firebase.auth.FirebaseAuth;
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

import lk.exam.smartshop_customer.model.User;

public class ProfileActivity extends AppCompatActivity {

    View view;
    private FirebaseFirestore firestore;
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth firebaseAuth;
    private User user;
    private Uri imagePath;
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == ProfileActivity.this.RESULT_OK) {
                        imagePath = result.getData().getData();

                        Picasso.get()
                                .load(imagePath)
                                .fit()
                                .into((ImageView) findViewById(R.id.profileImageButton));


                    } else {
                        imagePath = null;
                    }
                }
            }
    );
    private ImageButton imageButton1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        findViewById(R.id.profileImageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                activityResultLauncher.launch(Intent.createChooser(intent, "Select Image"));
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (firebaseAuth.getCurrentUser() != null) {

                    String userEmail = firebaseAuth.getCurrentUser().getEmail();


                    firestore.collection("user").whereEqualTo("email", userEmail).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                        user = snapshot.toObject(User.class);

                                        if (user.getEmail().equals(userEmail)) {

                                            TextView name = findViewById(R.id.nameProfile);
                                            TextView firstName = findViewById(R.id.firstNameProfileText);
                                            TextView lastName = findViewById(R.id.lastNameProfileText);
                                            TextView email = findViewById(R.id.emailProfileText);
                                            TextView mobile = findViewById(R.id.mobileProfileText);
                                            TextView address1 = findViewById(R.id.address1);
                                            TextView address2 = findViewById(R.id.address2);
                                            TextView city = findViewById(R.id.city);
                                            TextView postalcode = findViewById(R.id.postalcode);

                                            imageButton1 = findViewById(R.id.profileImageButton);

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    name.setText(user.getFirstName().toString());
                                                    firstName.setText(user.getFirstName().toString());
                                                    lastName.setText(user.getLastName().toString());
                                                    email.setText(user.getEmail().toString());
                                                    mobile.setText(user.getMobile().toString());
                                                    if (user.getAddress1().equals("null")) {
//                                                        address1.setText("data na yako");
                                                    } else {
                                                        address1.setText(user.getAddress1().toString());
                                                    }
                                                    if (user.getAddress2().equals("null")) {

                                                    } else {
                                                        address2.setText(user.getAddress2().toString());
                                                    }
                                                    if (user.getCity().equals("null")) {

                                                    } else {
                                                        city.setText(user.getCity().toString());
                                                    }
                                                    if (user.getPostalCode().equals("null")) {

                                                    } else {
                                                        postalcode.setText(user.getPostalCode().toString());
                                                    }

                                                }
                                            });

                                            imageButton1 = findViewById(R.id.profileImageButton);

                                            if (user.getImage().equals("null")) {
                                                Picasso.get()
                                                        .load(R.drawable.user)
                                                        .fit()
                                                        .into((ImageView) findViewById(R.id.profileImageButton));
                                            } else {

                                                firebaseStorage.getReference("user-images/" + user.getImage())
                                                        .getDownloadUrl()
                                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
//
                                                                Picasso.get()
                                                                        .load(uri)
                                                                        .fit()
                                                                        .into((ImageView) findViewById(R.id.profileImageButton));

                                                            }
                                                        });

                                            }

//                                            imageButton1.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
//                                                    Intent intent = new Intent();
//                                                    intent.setType("image/*");
//                                                    intent.setAction(Intent.ACTION_GET_CONTENT);
//                                                    imageButton1.launch(Intent.createChooser(intent, "Select Image"));
//
//                                                }
//                                            });

                                            findViewById(R.id.updateProfileBtn).setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    EditText firstNameView = findViewById(R.id.firstNameProfileText);
                                                    EditText lastNameView = findViewById(R.id.lastNameProfileText);
                                                    EditText mobileView = findViewById(R.id.mobileProfileText);
                                                    EditText address1View = findViewById(R.id.address1);
                                                    EditText address2View = findViewById(R.id.address2);
                                                    EditText cityView = findViewById(R.id.city);
                                                    EditText postalcodeView = findViewById(R.id.postalcode);

                                                    String fname = firstNameView.getText().toString();
                                                    String lname = lastNameView.getText().toString();
                                                    String address1 = address1View.getText().toString();
                                                    String address2 = address2View.getText().toString();
                                                    String city = cityView.getText().toString();
                                                    String postalcode = postalcodeView.getText().toString();
                                                    String mobile = mobileView.getText().toString();

                                                    if (fname.isEmpty()) {
                                                        Toast.makeText(ProfileActivity.this, "Please enter Name", Toast.LENGTH_SHORT).show();
//                                                    } else if (address1.isEmpty()) {
//                                                        Toast.makeText(ProfileActivity.this, "Please enter Address line1", Toast.LENGTH_SHORT).show();
//                                                    } else if (address2.isEmpty()) {
//                                                        Toast.makeText(ProfileActivity.this, "Please enter Address line2", Toast.LENGTH_SHORT).show();
//                                                    } else if (city.isEmpty()) {
//                                                        Toast.makeText(ProfileActivity.this, "Please enter City", Toast.LENGTH_SHORT).show();
//                                                    } else if (postalcode.isEmpty()) {
//                                                        Toast.makeText(ProfileActivity.this, "Please enter Postal code", Toast.LENGTH_SHORT).show();
                                                    } else if (mobile.isEmpty()) {
                                                        Toast.makeText(ProfileActivity.this, "Please enter Mobile", Toast.LENGTH_SHORT).show();
                                                    } else {

                                                        firestore.collection("user")
                                                                .whereNotEqualTo("id", user.getId()).get()
                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    boolean existsMobile = false;

                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                                            User exitsItem = snapshot.toObject(User.class);
                                                                            if (mobile.equals(exitsItem.getMobile())) {
                                                                                existsMobile = true;
                                                                            }
                                                                        }
                                                                        if (existsMobile) {
                                                                            Toast.makeText(ProfileActivity.this, "Mobile number already exists", Toast.LENGTH_SHORT).show();
                                                                        } else {
                                                                            String ref = String.valueOf(System.currentTimeMillis());

                                                                            Map<String, Object> updates = new HashMap<>();
                                                                            updates.put("firstName", fname);
                                                                            updates.put("lastName", lname);
                                                                            updates.put("address1", address1);
                                                                            updates.put("address2", address2);
                                                                            updates.put("city", city);
                                                                            updates.put("postalcode", postalcode);
                                                                            updates.put("mobile", mobile);

                                                                            String image1Id = UUID.randomUUID().toString();

                                                                            if (imagePath == null) {
                                                                                updates.put("image", user.getImage());
                                                                            } else {

                                                                                updates.put("image", image1Id);
                                                                            }

                                                                            firestore.collection("user")
                                                                                    .whereEqualTo("id", user.getId()).get()
                                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                List<String> documentIds = new ArrayList<>();

                                                                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                                    String documentId = document.getId();

                                                                                                    ProgressDialog dialog = new ProgressDialog(ProfileActivity.this);
                                                                                                    dialog.setMessage("Adding new item...");
                                                                                                    dialog.setCancelable(false);
                                                                                                    dialog.show();

                                                                                                    firestore.collection("user").document(documentId).update(updates)
                                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                @Override
                                                                                                                public void onSuccess(Void unused) {
                                                                                                                    dialog.dismiss();

                                                                                                                    StorageReference storageRef = firebaseStorage.getReference();


                                                                                                                    if (imagePath != null) {

                                                                                                                        ProgressDialog dialog = new ProgressDialog(ProfileActivity.this);
                                                                                                                        dialog.setMessage("Uploading");
                                                                                                                        dialog.setCancelable(false);
                                                                                                                        dialog.show();

                                                                                                                        if (user.getImage() != null) {
                                                                                                                            StorageReference desertRef = storageRef.child("user-images/" + user.getImage());

                                                                                                                            desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                @Override
                                                                                                                                public void onSuccess(Void aVoid) {
                                                                                                                                }
                                                                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                                                                                @Override
                                                                                                                                public void onFailure(@NonNull Exception exception) {
                                                                                                                                }
                                                                                                                            });
                                                                                                                        }


                                                                                                                        StorageReference reference = firebaseStorage.getReference("user-images")
                                                                                                                                .child(image1Id);
                                                                                                                        reference.putFile(imagePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                                                                            @Override
                                                                                                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                                                                dialog.dismiss();
                                                                                                                                Toast.makeText(getApplicationContext(), "Your profile has been updated successfully", Toast.LENGTH_LONG).show();
                                                                                                                                finish();
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
                                                                                                                                dialog.setMessage("Image Uploading " + (int) progress + "%");
                                                                                                                            }
                                                                                                                        });


                                                                                                                    } else {
                                                                                                                        Toast.makeText(getApplicationContext(), "Your profile has been updated successfully", Toast.LENGTH_LONG).show();
                                                                                                                        finish();
                                                                                                                    }


                                                                                                                }
                                                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                                                                @Override
                                                                                                                public void onFailure(@NonNull Exception e) {
                                                                                                                    dialog.dismiss();
                                                                                                                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

                                            break;
                                        }

                                    }

                                }
                            });


                } else {
                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                }
            }
        }).start();
    }

}