package lk.exam.smartshop_admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lk.exam.smartshop_admin.adapter.OrderAdapter;
import lk.exam.smartshop_admin.lisners.OrderSelectListner;
import lk.exam.smartshop_admin.model.Order;

public class OrdersActivity extends AppCompatActivity implements OrderSelectListner {

    private FirebaseFirestore firestore;
    private ArrayList<Order> orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        firestore = FirebaseFirestore.getInstance();

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        orders = new ArrayList<>();

        RecyclerView orderView = findViewById(R.id.orders_recycle_view);
        OrderAdapter orderAdapter = new OrderAdapter(orders, OrdersActivity.this, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        orderView.setLayoutManager(linearLayoutManager);
        orderView.setAdapter(orderAdapter);

        firestore.collection("order").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                Order order = snapshot.toObject(Order.class);
                                orders.add(order);
                            }
                            orderAdapter.notifyDataSetChanged();

                        }
                    }
                });

    }

    @Override
    public void selectOrder(Order order) {
        startActivity(new Intent(OrdersActivity.this, OrderItemsActivity.class)
                .putExtra("orderId", order.getId()));
    }

    @Override
    public void confirmDelivered(Order order) {
        firestore.collection("order")
                .whereEqualTo("id", order.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Map<String, Object> updates = new HashMap<>();
                            if(order.getDeliver_status()==0){
                                updates.put("deliver_status", 1);
                            } else if (order.getDeliver_status()==1) {
                                updates.put("deliver_status", 2);
                            }

                            for(QueryDocumentSnapshot snapshot:task.getResult()){
                                String orderId=snapshot.getId();

                                firestore.collection("order").document(orderId).update(updates)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(OrdersActivity.this, "Order Status Updated", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }

                        }
                    }
                });
    }
}