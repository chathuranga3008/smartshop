package lk.exam.smartshop_customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lk.exam.smartshop_customer.adapter.ProductAdapter;
import lk.exam.smartshop_customer.listener.ProductSelectListener;
import lk.exam.smartshop_customer.model.Product;

public class ProductFragment extends Fragment implements ProductSelectListener {
    View view;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private FirebaseAuth firebaseAuth;
    private ArrayList<Product> products;
    private Product product;
    private String productId;
    private String category;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_product, container, false);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!getArguments().getString("key").isEmpty()) {
                    String id = getArguments().getString("key");

                    firestore.collection("product").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                product = snapshot.toObject(Product.class);

                                if (product.getId().equals(id)) {
                                    TextView title = view.findViewById(R.id.productTitle);
                                    TextView price = view.findViewById(R.id.productPrice);
                                    TextView variation = view.findViewById(R.id.productVariation);
                                    TextView qty = view.findViewById(R.id.productQuantity);
                                    TextView desc = view.findViewById(R.id.productDescription);
                                    TextView deliveryCost = view.findViewById(R.id.productDeliveryCost);

                                    ImageSlider slider = view.findViewById(R.id.productImageSlider);
                                    ArrayList<SlideModel> slideModels = new ArrayList<>();

                                    slideModels.add(new SlideModel(R.drawable.sample, ScaleTypes.FIT));
                                    slideModels.add(new SlideModel(R.drawable.sample, ScaleTypes.FIT));
                                    slideModels.add(new SlideModel(R.drawable.sample, ScaleTypes.FIT));

                                    slider.setImageList(slideModels, ScaleTypes.FIT);

                                    title.setText(product.getTitle());
                                    price.setText(product.getPrice().toString() + ".00");
                                    variation.setText(product.getVariation());
                                    qty.setText(product.getQuantity());
                                    desc.setText(product.getDescription());
                                    deliveryCost.setText(product.getDelivery_cost());

                                    productId = product.getId().toString();

                                    break;
                                }
                            }
                        }
                    });

                    products = new ArrayList<>();
                    RecyclerView productView = view.findViewById(R.id.productRecyclerView);

                    ProductAdapter adapter = new ProductAdapter(products, getActivity().getApplicationContext(), ProductFragment.this);
                    GridLayoutManager layoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
                    productView.setLayoutManager(layoutManager);
                    productView.setAdapter(adapter);

                    firestore.collection("product").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            for (DocumentChange change : value.getDocumentChanges()) {
                                Product product = change.getDocument().toObject(Product.class);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
                }

            }
        }).start();

        view.findViewById(R.id.productViewATCButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser() != null && firebaseAuth.getCurrentUser().isEmailVerified()) {
                    firestore.collection("user").whereEqualTo("email", firebaseAuth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                String userId = snapshot.getId();

                                firestore.collection("user/" + userId + "/cart").whereEqualTo("id", product.getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if (queryDocumentSnapshots.size() > 0) {
                                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                                String productId = snapshot.getId();

                                                firestore.collection("user/" + userId + "/cart").whereEqualTo("id", product.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                                            String qty = snapshot.getData().get("quantity").toString();

                                                            if ((Integer.parseInt(qty) + 1) <= Integer.parseInt(product.getQuantity())) {
                                                                Map<String, Object> data = new HashMap<>();
                                                                data.put("id", product.getId());
                                                                data.put("quantity", Integer.parseInt(qty) + 1);

                                                                firestore.document("user/" + userId + "/cart/" + productId).update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        // Update successful
                                                                        Toast.makeText(getActivity().getApplicationContext(), "Already product have in your cart and increased qty.", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        // Update failed
                                                                        Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            } else {
                                                                Toast.makeText(getActivity().getApplicationContext(), "Not enough quantity in stock", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }
                                                });
                                            }
                                        } else {
                                            Map<String, Object> data = new HashMap<>();
                                            data.put("id", product.getId());
                                            data.put("quantity", 1);

                                            firestore.collection("user/" + userId + "/cart").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Toast.makeText(getActivity().getApplicationContext(), "Product added to Cart", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });


                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    startActivity(new Intent(getActivity().getApplicationContext(), LoginActivity.class));
                }
            }
        });
        return view;
    }

    @Override
    public void viewProduct(Product product) {
    }

    @Override
    public void addToCart(Product product) {
    }
}