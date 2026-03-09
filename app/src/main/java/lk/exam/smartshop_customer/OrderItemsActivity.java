package lk.exam.smartshop_customer;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import lk.exam.smartshop_customer.adapter.OrderSummaryAdapter;
import lk.exam.smartshop_customer.model.Order;
import lk.exam.smartshop_customer.model.Product;

public class OrderItemsActivity extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private ArrayList<Product> items;
    private String userId;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_items);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        String orderid = getIntent().getExtras().getString("orderId");

//        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        if (firebaseAuth.getCurrentUser() != null) {
            items = new ArrayList<>();
            RecyclerView categoryView = findViewById(R.id.orderItemsView);
            OrderSummaryAdapter ordersAdapter = new OrderSummaryAdapter(items, this);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            categoryView.setLayoutManager(linearLayoutManager);
            categoryView.setAdapter(ordersAdapter);

            firestore.collection("order").whereEqualTo("id", orderid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot snapshot1 : task.getResult()) {
                            Order order = snapshot1.toObject(Order.class);

                            if (order.getId().equals(orderid)) {
                                orderId = snapshot1.getId();

                                firestore.collection("order/" + orderId + "/product").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot snapshot2 : task.getResult()) {

                                                Product item = snapshot2.toObject(Product.class);
                                                items.add(item);
                                            }
                                            ordersAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                                break;
                            }
                        }
                    } else {
                    }
                }
            });
        } else {
            startActivity(new Intent(OrderItemsActivity.this, LoginActivity.class));
            finish();
        }

    }
}