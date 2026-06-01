package lk.exam.smartshop_admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import lk.exam.smartshop_admin.adapter.ProductAdapter;
import lk.exam.smartshop_admin.lisners.ProductListener;
import lk.exam.smartshop_admin.model.Product;

public class ManageProductActivity extends AppCompatActivity implements ProductListener {

    private FirebaseFirestore firestore;
    private ArrayList<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_product);

        firestore = FirebaseFirestore.getInstance();
        products = new ArrayList<>();

        RecyclerView productView = findViewById(R.id.AllProductView);

        ProductAdapter allProductAdapter = new ProductAdapter(products, this,this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ManageProductActivity.this);

        productView.setLayoutManager(linearLayoutManager);
        productView.setAdapter(allProductAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                firestore.collection("product").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (DocumentChange change : value.getDocumentChanges()){
                            Product productItems = change.getDocument().toObject(Product.class);
                            switch (change.getType()){
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
                                        old.setCost(productItems.getCost());
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

    }

    @Override
    public void deleteProduct(Product addProduct) {

    }

    @Override
    public void updateProduct(Product addProduct) {

        startActivity(new Intent(ManageProductActivity.this,UpdateProductActivity.class).putExtra("itemId",addProduct.getId()));

    }
}