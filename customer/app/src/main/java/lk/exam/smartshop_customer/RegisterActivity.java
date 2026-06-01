package lk.exam.smartshop_customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.UUID;

import lk.exam.smartshop_customer.model.User;
import lk.exam.smartshop_customer.util.Encryption;

public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getName();
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        findViewById(R.id.registerSignupButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText firstNameText = findViewById(R.id.registerFirstName);
                EditText lastNameText = findViewById(R.id.registerLastName);
                EditText emailText = findViewById(R.id.registerEmail);
                EditText passwordText = findViewById(R.id.registerPassword);
                EditText passwordConfirmText = findViewById(R.id.registerPasswordConfirm);
                EditText mobileText = findViewById(R.id.registerMobile);

                String id = UUID.randomUUID().toString();
                String firstName = firstNameText.getText().toString();
                String lastName = lastNameText.getText().toString();
                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();
                String passwordConfirm = passwordConfirmText.getText().toString();
                String mobile = mobileText.getText().toString();

                if (firstName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter First Name", Toast.LENGTH_SHORT).show();
                } else if (lastName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter Last Name", Toast.LENGTH_SHORT).show();
                } else if (email.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter Email", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter Password", Toast.LENGTH_SHORT).show();
                } else if (passwordConfirm.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter Confirm Password", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(passwordConfirm)) {
                    Toast.makeText(getApplicationContext(), "Password doesn't match", Toast.LENGTH_SHORT).show();
                } else if (mobile.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter Mobile", Toast.LENGTH_SHORT).show();
                } else {
                    String encryptPassword = Encryption.encrypt(password);

                    firestore.collection("user").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        boolean existsUserStatus = false;

                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                User existingUser = snapshot.toObject(User.class);

                                if (email.equals(existingUser.getEmail())) {
                                    existsUserStatus = true;
                                }
                            }

                            if (existsUserStatus) {
                                Toast.makeText(getApplicationContext(), "User Email already exists", Toast.LENGTH_SHORT).show();
                            } else {
                                ProgressDialog dialog = new ProgressDialog(RegisterActivity.this);
                                dialog.setMessage("Registering...");
                                dialog.setCancelable(false);
                                dialog.show();
                                firebaseAuth.createUserWithEmailAndPassword(email, encryptPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                            currentUser.sendEmailVerification();

                                            User user = new User(id, firstName, lastName, email, encryptPassword, mobile, "null", "null", "null", "null", "null");

                                            firestore.collection("user").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    dialog.dismiss();
                                                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    dialog.dismiss();
                                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            dialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Not Working, check code!!!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
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
}