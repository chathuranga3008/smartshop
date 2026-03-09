package lk.exam.smartshop_customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lk.exam.smartshop_customer.broadcast.BatteryLow;
import lk.exam.smartshop_customer.model.User;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NavigationBarView.OnItemSelectedListener, SensorEventListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private FirebaseFirestore firestore;
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth firebaseAuth;
    private User user;
    private SensorManager sensorManager;
    private Sensor accelometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        IntentFilter intentFilter = new IntentFilter("android.intent.action.BATTERY_LOW");
        BatteryLow mbr = new BatteryLow();
        registerReceiver(mbr, intentFilter);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            accelometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        if (accelometer != null) {
            sensorManager.registerListener(MainActivity.this, accelometer, SensorManager.SENSOR_DELAY_UI);
        }

        Settings.System.canWrite(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (firebaseAuth.getCurrentUser() != null) {
                    String userEmail = firebaseAuth.getCurrentUser().getEmail();

                    firestore.collection("user").whereEqualTo("email", userEmail).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                user = snapshot.toObject(User.class);

                                if (user.getEmail().equals(userEmail)) {
                                    TextView firstName = findViewById(R.id.userNameSide);
                                    TextView email = findViewById(R.id.userEmailSide);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            firstName.setText(user.getFirstName().toString());
                                            email.setText(user.getEmail().toString());
                                        }
                                    });

                                    if (user.getImage().equals("null")) {
                                        Picasso.get().load(R.drawable.user).fit();
                                    } else {
                                        firebaseStorage.getReference("user-images/" + user.getImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Picasso.get().load(uri).fit().into((ImageView) findViewById(R.id.profilePictureSide));
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    });
                }
//                else {
//                    firstName.setText("User Name");
//                    email.setText("User Email");
//
//                    Picasso.get().load(R.drawable.user).fit();
//                }
            }
        }).start();

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.open();
            }
        });

        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigationView.setOnItemSelectedListener(this);
        loadFragment(new HomeFragment());

        EditText search = findViewById(R.id.textInputSearch);

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // Hide the keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(search.getWindowToken(), 0);

                    // Handle search action
                    showSearchResult(search.getText().toString());
                    return true;
                }
                return false;
            }
        });

        search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2; // Index for the drawableEnd
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (search.getRight() - search.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // Hide the keyboard
                        InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
                        showSearchResult(search.getText().toString());

                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        if (item.getItemId() == R.id.bottomNavHome ) {
////            loadFragment(new HomeFragment());
//            return true;
//        } else if(item.getItemId() == R.id.bottomNavProfile ){
//            loadFragment(new ProfileFragment());
//            return true;
//        }else if(item.getItemId() == R.id.sideNavLogin ){
//            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
//            return true;
//        }

        if (item.getItemId() == R.id.sideNavProfile) {
//            if (firebaseAuth.getCurrentUser().isEmailVerified()) {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
//            } else {
//                startActivity(new Intent(MainActivity.this, SignInActivity.class));
//            }
        }else if (item.getItemId() == R.id.sideNavLogin) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else if (item.getItemId() == R.id.bottomNavAccount) {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        } else if (item.getItemId() == R.id.bottomNavCart) {
            loadFragment(new CartFragment());
            return true;
        } else if (item.getItemId() == R.id.sideNavLogout) {
            firebaseAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else if (item.getItemId() == R.id.bottomNavHome) {
            loadFragment(new HomeFragment());
            return true;
        }else if (item.getItemId() == R.id.sideNavOrders) {
            loadFragment(new PurchaseHistoryFragment());
            return true;
        }
        return true;
    }

    public void loadFragment(Fragment fragment) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    public void showSearchResult(String text) {
        startActivity(new Intent(MainActivity.this, SearchResultActivity.class).putExtra("text", text));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            double value = Math.floor(x * x + y * y + z * z);

            if (value > 150) {
                final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.shutter_sound);
                try {
                    // Create a bitmap from the root view
                    View rootView = getWindow().getDecorView().getRootView();
                    Bitmap screenshotBitmap = Bitmap.createBitmap(rootView.getWidth(), rootView.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(screenshotBitmap);
                    rootView.draw(canvas);

                    // Save the bitmap to a file
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                    String fileName = "screenshot_" + timeStamp + ".png";

                    File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                    File screenshotFile = new File(directory,  fileName);

                    FileOutputStream outputStream = new FileOutputStream(screenshotFile);
                    screenshotBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();

                    // Notify the user
                    Toast.makeText(MainActivity.this, "Screenshot saved to " + screenshotFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}
}