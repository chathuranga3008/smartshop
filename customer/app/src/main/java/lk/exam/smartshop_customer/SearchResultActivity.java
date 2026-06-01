package lk.exam.smartshop_customer;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import lk.exam.smartshop_customer.adapter.ProductAdapter;
import lk.exam.smartshop_customer.listener.ProductSelectListener;
import lk.exam.smartshop_customer.model.Product;

public class SearchResultActivity extends AppCompatActivity implements ProductSelectListener {

    private ArrayList<Product> products;

    private FirebaseStorage storage;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        EditText search = findViewById(R.id.textInputSearch);

        if (!getIntent().getExtras().getString("text").isEmpty()) {
            search.setText(getIntent().getExtras().getString("text"));
            showSearchResult(getIntent().getExtras().getString("text"));
        } else {
            search.setText("");
            showSearchResult("");
        }

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

        search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    showSearchResult(search.getText().toString());
                }
                return false;
            }
        });

    }

    public void showSearchResult(String text) {


        products = new ArrayList<>();

        RecyclerView itemView = findViewById(R.id.searchResultRecyclerView);

        ProductAdapter searchResultAdapter = new ProductAdapter(products, SearchResultActivity.this, SearchResultActivity.this);

        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        itemView.setLayoutManager(layoutManager);

        itemView.setAdapter(searchResultAdapter);


        firestore.collection("product")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                Product product = snapshot.toObject(Product.class);
                                if (product.getTitle().toLowerCase().contains(text.toLowerCase()) || product.getDescription().toLowerCase().contains(text.toLowerCase()) || product.getCategory().toLowerCase().contains(text.toLowerCase())) {
                                    products.add(product);
                                }
                            }

                            if (products.size() <= 0) {
//                                findViewById(R.id.noResult).setVisibility(View.VISIBLE);
                            } else {
//                                findViewById(R.id.noResult).setVisibility(View.GONE);
                                searchResultAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });

    }


    @Override
    public void viewProduct(Product product) {

    }

    @Override
    public void addToCart(Product product) {

    }
}