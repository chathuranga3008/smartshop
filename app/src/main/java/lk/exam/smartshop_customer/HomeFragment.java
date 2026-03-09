package lk.exam.smartshop_customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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

import lk.exam.smartshop_customer.adapter.CategoryAdapter;
import lk.exam.smartshop_customer.adapter.ProductAdapter;
import lk.exam.smartshop_customer.listener.CategorySelectListener;
import lk.exam.smartshop_customer.listener.ProductSelectListener;
import lk.exam.smartshop_customer.model.Category;
import lk.exam.smartshop_customer.model.Product;

public class HomeFragment extends Fragment implements ProductSelectListener, CategorySelectListener {

    private FirebaseFirestore firestore;
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth firebaseAuth;
    private ArrayList<Product> products;
    private ArrayList<Category> categories;
    private
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        ImageSlider imageSlider = view.findViewById(R.id.homeImageSlider);
        ArrayList<SlideModel> slideModels = new ArrayList<SlideModel>(); // Create image list
        slideModels.add(new SlideModel(R.drawable.crsl1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.crsl2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.crsl3, ScaleTypes.FIT));
        imageSlider.setImageList(slideModels, ScaleTypes.FIT);


//        view.findViewById(R.id.profileView).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getActivity().getApplicationContext(),ProfileActivity.class));
//            }
//        });

        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        products = new ArrayList<>();


        categories = new ArrayList<>();
        RecyclerView categoryView = view.findViewById(R.id.categoryView);
        CategoryAdapter categoryAdapter = new CategoryAdapter(categories, getActivity().getApplicationContext(), HomeFragment.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        categoryView.setLayoutManager(linearLayoutManager);
        categoryView.setAdapter(categoryAdapter);
        firestore.collection("category").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange change : value.getDocumentChanges()) {
                    Category category = change.getDocument().toObject(Category.class);
                    switch (change.getType()) {
                        case ADDED:
                            categories.add(category);
                        case MODIFIED:
                            Category old = categories.stream().filter(i -> i.getCategoryName().equals(category.getCategoryName())).findFirst().orElse(null);
                            if (old != null) {
                                old.setCategoryName(category.getCategoryName());
                            }
                            break;
                        case REMOVED:
                            categories.remove(category);
                    }
                }
                categoryAdapter.notifyDataSetChanged();
            }
        });

        RecyclerView productView = view.findViewById(R.id.productView);

        ProductAdapter allProductAdapter = new ProductAdapter(products, getActivity().getApplicationContext(), this);

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);

        productView.setLayoutManager(layoutManager);
        productView.setAdapter(allProductAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                firestore.collection("product").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (DocumentChange change : value.getDocumentChanges()) {
                            Product productItems = change.getDocument().toObject(Product.class);
                            switch (change.getType()) {
                                case ADDED:
                                    products.add(productItems);
                                case MODIFIED:
////                                    for (AddProduct exsistProduct:products){
//                                        if (exsistProduct.getId().equals(productItems.getId())){
//                                            exsistProduct.setTitle(productItems.getTitle());
//                                            exsistProduct.setCost(productItems.getCost());
//                                            exsistProduct.setImage1Id(productItems.getImage1Id());
//                                            break;
//                                        }
////                                    }
                                    Product old = products.stream().filter(i -> i.getId().equals(productItems.getId())).findFirst().orElse(null);
                                    if (old != null) {
                                        old.setTitle(productItems.getTitle());
                                        old.setPrice(productItems.getPrice());
                                    }
                                    break;
                                case REMOVED:
//                                    Iterator<AddProduct> addProductIterator = products.iterator();
//                                    while (addProductIterator.hasNext()){
//                                        AddProduct exsistProduct = addProductIterator.next();
//                                        if (exsistProduct.getId().equals(productItems.getId())){
//                                            addProductIterator.remove();
//                                            break;
//                                        }
//                                    }
                                    products.remove(productItems);
                            }
                        }
                        allProductAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();

        return view;
    }

    @Override
    public void viewProduct(Product allProducts) {
        Bundle bundle = new Bundle();
        bundle.putString("key", allProducts.getId());

        ProductFragment singleProductFragment = new ProductFragment();
        singleProductFragment.setArguments(bundle);

        FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, singleProductFragment);
        fragmentTransaction.commit();

//        Fragment fragmentB = new SingleProductFragment();
//        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//        transaction.replace(R.id.fragmentConst, fragmentB);
//        transaction.addToBackStack(null); // Optional: Add to back stack
//        transaction.commit();
//        Toast.makeText(getActivity().getApplicationContext(), "ok", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void addToCart(Product product) {

        if (firebaseAuth.getCurrentUser() != null && firebaseAuth.getCurrentUser().isEmailVerified()) {

            firestore.collection("user")
                    .whereEqualTo("email", firebaseAuth.getCurrentUser().getEmail())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                String userId = documentSnapshot.getId();

                                firestore.collection("user/" + userId + "/cart")
                                        .whereEqualTo("id", product.getId())
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                if (queryDocumentSnapshots.size() > 0) {
                                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                        String productDoc = documentSnapshot.getId();

                                                        firestore.collection("user/" + userId + "/cart")
                                                                .whereEqualTo("id", product.getId())
                                                                .get()
                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                        for (QueryDocumentSnapshot snapshot : task.getResult()) {

                                                                            String qty = snapshot.getData().get("quantity").toString();

                                                                            if ((Integer.parseInt(qty) + 1) <= Integer.parseInt(product.getQuantity())) {


                                                                                Map<String, Object> data = new HashMap<>();
                                                                                data.put("id", product.getId());
                                                                                data.put("quantity", Integer.parseInt(qty) + 1);

                                                                                firestore.document("user/" + userId + "/cart/" + productDoc)
                                                                                        .update(data)
                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void aVoid) {

                                                                                                // Update successful
                                                                                                Toast.makeText(getActivity().getApplicationContext(), "Already product have in your cart and increased qty.", Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        })
                                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                                            @Override
                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                // Update failed
                                                                                                Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        });

                                                                            } else {
                                                                                Toast.makeText(getActivity().getApplicationContext(), "Not enough stock", Toast.LENGTH_SHORT).show();
                                                                            }

                                                                        }
                                                                    }
                                                                });

                                                    }
                                                } else {

                                                    Map<String, Object> data = new HashMap<>();
                                                    data.put("id", product.getId());
                                                    data.put("quantity", 1);

                                                    firestore.collection("user")
                                                            .document(userId)
                                                            .collection("cart")
                                                            .add(data)
                                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                @Override
                                                                public void onSuccess(DocumentReference documentReference) {
                                                                    Toast.makeText(getActivity().getApplicationContext(), "Added to Cart", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });


        } else {
            startActivity(new Intent(getActivity().getApplicationContext(), LoginActivity.class));
        }

    }

    @Override
    public void categoryItemView(Category category) {

        RecyclerView productView = view.findViewById(R.id.productView);

        ProductAdapter allProductAdapter = new ProductAdapter(products, getActivity().getApplicationContext(), this);

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);

        productView.setLayoutManager(layoutManager);
        productView.setAdapter(allProductAdapter);

        products.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                firestore.collection("products").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (DocumentChange change : value.getDocumentChanges()) {
                            Product productItems = change.getDocument().toObject(Product.class);
                            if (productItems.getCategory().equals(category.getCategoryName())) {

                                switch (change.getType()) {
                                    case ADDED:
                                        products.add(productItems);
                                    case MODIFIED:
////                                    for (AddProduct exsistProduct:products){
//                                        if (exsistProduct.getId().equals(productItems.getId())){
//                                            exsistProduct.setTitle(productItems.getTitle());
//                                            exsistProduct.setCost(productItems.getCost());
//                                            exsistProduct.setImage1Id(productItems.getImage1Id());
//                                            break;
//                                        }
////                                    }
                                        Product old = products.stream().filter(i -> i.getId().equals(productItems.getId())).findFirst().orElse(null);
                                        if (old != null) {
                                            old.setTitle(productItems.getTitle());
                                            old.setPrice(productItems.getPrice());
                                        }
                                        break;
                                    case REMOVED:
//                                    Iterator<AddProduct> addProductIterator = products.iterator();
//                                    while (addProductIterator.hasNext()){
//                                        AddProduct exsistProduct = addProductIterator.next();
//                                        if (exsistProduct.getId().equals(productItems.getId())){
//                                            addProductIterator.remove();
//                                            break;
//                                        }
//                                    }
                                        products.remove(productItems);
                                }
                            }
                        }
                        allProductAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();

    }
}