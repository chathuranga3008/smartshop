package lk.exam.smartshop_customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import lk.exam.smartshop_customer.adapter.OrdersAdapter;
import lk.exam.smartshop_customer.listener.OrderSelectListener;
import lk.exam.smartshop_customer.model.Order;

public class PurchaseHistoryFragment extends Fragment implements OrderSelectListener {

    View view;

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private ArrayList<Order> orders;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_purchas_history, container, false);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            if (firebaseAuth.getCurrentUser().isEmailVerified()) {

                orders = new ArrayList<>();
                RecyclerView categoryView = view.findViewById(R.id.ordersList);
                OrdersAdapter ordersAdapter = new OrdersAdapter(orders, getActivity().getApplicationContext(), this);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                categoryView.setLayoutManager(linearLayoutManager);
                categoryView.setAdapter(ordersAdapter);

                firestore.collection("order")
                        .whereEqualTo("email", firebaseAuth.getCurrentUser().getEmail())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot snapshot1 : task.getResult()) {
                                        Order order = snapshot1.toObject(Order.class);
                                        if (order.getEmail().equals(firebaseAuth.getCurrentUser().getEmail())) {
                                            orders.add(order);
                                        }
                                    }

                                    ordersAdapter.notifyDataSetChanged();
                                } else {

                                }
                            }
                        });

            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Please Verify Your Email.", Toast.LENGTH_SHORT).show();
                firebaseAuth.getCurrentUser().sendEmailVerification();
            }
        } else {
            startActivity(new Intent(getActivity().getApplicationContext(), LoginActivity.class));
            getActivity().finish();
        }

        return view;
    }

    @Override
    public void selectOrder(Order order) {
        startActivity(new Intent(getActivity().getApplicationContext(), OrderItemsActivity.class)
                .putExtra("orderId", order.getId()));
    }
}