package lk.exam.smartshop_customer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import lk.exam.smartshop_customer.adapter.OrderSummaryAdapter;
import lk.exam.smartshop_customer.model.Order;
import lk.exam.smartshop_customer.model.Product;

public class OrderSummeryActivity extends AppCompatActivity {
    private final String channelId = "info";
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private FirebaseAuth firebaseAuth;
    private ArrayList<Product> products;
    private String userId;
    private Integer total = 0;
    private TextView totalLabel;
    private OrderSummaryAdapter cartItemAdapter;
    private int x = 0;
    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summery);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        ///////////////////////Notification
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "INFO", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setShowBadge(true);
            channel.setDescription("This is Information");
            channel.enableLights(true);
            channel.setLightColor(R.color.info);
            channel.setVibrationPattern(new long[]{0, 1000, 1000, 1000});
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }
        ///////////////////////Notification

        //////////////////////////////Cart Load Item
        String mobile = getIntent().getExtras().getString("mobile");
        String address1 = getIntent().getExtras().getString("address1");
        String address2 = getIntent().getExtras().getString("address2");
        String city = getIntent().getExtras().getString("city");
        String postal = getIntent().getExtras().getString("postalcode");
        String latitude = getIntent().getExtras().getString("latitude");
        String longitude = getIntent().getExtras().getString("longitude");

        TextView uname = findViewById(R.id.usernameOS);
        uname.setText(firebaseAuth.getCurrentUser().getDisplayName());

        TextView uemail = findViewById(R.id.emailOS);
        uemail.setText(firebaseAuth.getCurrentUser().getEmail());

        TextView umobile = findViewById(R.id.mobileOS);
        umobile.setText(mobile);

        TextView uaddress1 = findViewById(R.id.address1OS);
        uaddress1.setText(address1);

        TextView uaddress2 = findViewById(R.id.address2OS);
        uaddress2.setText(address2);

        TextView ucity = findViewById(R.id.cityOS);
        ucity.setText(city);

        TextView upostal = findViewById(R.id.postalCodeOS);
        upostal.setText(postal);

        if (firebaseAuth.getCurrentUser() != null && firebaseAuth.getCurrentUser().isEmailVerified()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    products = new ArrayList<>();

                    RecyclerView itemView = findViewById(R.id.recycleViewOS);

                    cartItemAdapter = new OrderSummaryAdapter(products, OrderSummeryActivity.this);

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OrderSummeryActivity.this);
                    itemView.setLayoutManager(linearLayoutManager);

                    itemView.setAdapter(cartItemAdapter);

                    firestore.collection("user").whereEqualTo("email", firebaseAuth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                userId = documentSnapshot.getId();

                                firestore.collection("user/" + userId + "/cart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                        if (task.isSuccessful() && task.getResult().size() > 0) {

                                            CollectionReference collectionReference = firestore.collection("product");
                                            HashMap<String, String> pInfo = new HashMap<>();
                                            int numberOfIds = task.getResult().size();
                                            int index = 0;

                                            String[] idsArray = new String[numberOfIds];


                                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                String id = snapshot.getData().get("id").toString();
                                                pInfo.put(id, snapshot.getData().get("quantity").toString());
                                                idsArray[index++] = id;
                                            }

                                            List<String> idsToMatch = Arrays.asList(idsArray);

                                            Query query = collectionReference.whereIn("id", idsToMatch);

                                            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    products.clear();

                                                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                        Product item = snapshot.toObject(Product.class);

                                                        products.add(new Product(item.getId(), item.getTitle(), pInfo.get(item.getId()), item.getPrice(), item.getImage1Id()));

                                                    }

                                                    cartItemAdapter.notifyDataSetChanged();
                                                    updateTotal();
                                                }
                                            });
                                        } else {
                                            TextView textView = new TextView(OrderSummeryActivity.this);
                                            textView.setTextSize(24);
                                            textView.setTextColor(Color.RED);
                                            textView.setText("Empty Cart");
                                            textView.setGravity(Gravity.CENTER);

                                            FrameLayout frameLayout = findViewById(R.id.cartContainer);
                                            frameLayout.addView(textView);
                                        }
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(OrderSummeryActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).start();
        } else {
            startActivity(new Intent(OrderSummeryActivity.this, LoginActivity.class));
            finish();
        }
        //////////////////////////////Cart Load Item

        //////////////////////////////Confirm Btn
        findViewById(R.id.confirmButton).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String ref = String.valueOf(System.currentTimeMillis());

                LocalDateTime currentDateTime = LocalDateTime.now();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDateTime = currentDateTime.format(formatter);

                Order order = new Order(ref, firebaseAuth.getCurrentUser().getEmail(), total.toString(), mobile, address1, address2, city, postal, latitude, longitude, formattedDateTime, 0);

                firestore.collection("user").whereEqualTo("email", firebaseAuth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String userId = documentSnapshot.getId();

                            firestore.collection("order").add(order).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    String orderDocId = documentReference.getId();

                                    for (Product cartItem : products) {
                                        firestore.collection("product").whereEqualTo("id", cartItem.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                    Product addproduct = snapshot.toObject(Product.class);

                                                    firestore.collection("product/").whereEqualTo("id", addproduct.getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                                Integer nowQty = Integer.parseInt(addproduct.getQuantity()) - Integer.parseInt(cartItem.getQuantity());

                                                                HashMap<String, Object> data = new HashMap<>();
                                                                data.put("quantity", String.valueOf(nowQty));

                                                                firestore.document("products/" + documentSnapshot.getId()).update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        addproduct.setQuantity(cartItem.getQuantity());

                                                                        firestore.collection("order/" + orderDocId + "/product").add(addproduct).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                            @Override
                                                                            public void onSuccess(DocumentReference documentReference) {
                                                                                firestore.collection("user/" + userId + "/cart/").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                                firestore.collection("user/" + userId + "/cart/").document(document.getId()).delete();
                                                                                                int count = products.size();
                                                                                                x++;
                                                                                                if (count == x) {
                                                                                                    /////////////////////Notification
                                                                                                    Intent intent = new Intent(OrderSummeryActivity.this, PurchaseHistoryFragment.class);
                                                                                                    intent.putExtra("name", "ABCD");

                                                                                                    PendingIntent pendingIntent = PendingIntent
                                                                                                            .getActivity(OrderSummeryActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                                                                                                    Notification notification = new NotificationCompat.Builder(getApplicationContext(), channelId)
                                                                                                            .setSmallIcon(R.drawable.ic_stat_name)
                                                                                                            .setContentTitle("Order Confirmation")
                                                                                                            .setContentText("Your Order Confirmed Soon")
                                                                                                            .setColor(Color.RED)
                                                                                                            .setContentIntent(pendingIntent)
                                                                                                            .build();

                                                                                                    notificationManager.notify(1, notification);
                                                                                                    /////////////////////Notification

                                                                                                    Toast.makeText(getApplicationContext(), "Confirmed Your Order.", Toast.LENGTH_SHORT).show();
                                                                                                    startActivity(new Intent(OrderSummeryActivity.this, MainActivity.class));
                                                                                                    finish();
                                                                                                    finish();
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                });
                                                                            }
                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
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
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void updateTotal() {
        total = 0;
        for (Product cartitem : products) {
            total = total + Integer.valueOf((Integer.valueOf(cartitem.getPrice()) * Integer.valueOf(cartitem.getQuantity())));
        }
        totalLabel = findViewById(R.id.totalOS);
        totalLabel.setText(total.toString() + ".00");
    }
}