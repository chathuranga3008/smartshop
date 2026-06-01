package lk.exam.smartshop_customer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import lk.exam.smartshop_customer.R;
import lk.exam.smartshop_customer.listener.ProductSelectListener;
import lk.exam.smartshop_customer.model.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private ArrayList<Product> products;
    private FirebaseStorage firebaseStorage;
    private Context context;

    private ProductSelectListener productLisner;

    public ProductAdapter(ArrayList<Product> products, Context context, ProductSelectListener productLisner) {
        firebaseStorage = FirebaseStorage.getInstance();
        this.products = products;
        this.context = context;
        this.productLisner = productLisner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.product_view, parent, false);
        return new ViewHolder(view);
    }

//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//
//    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Product allProduct = products.get(position);

        firebaseStorage.getReference("productImages/"+allProduct.getImage1Id()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get()
                        .load(uri)
                        .resize(200, 200)
                        .into(holder.imageView);
            }
        });

        holder.productTitleText.setText(allProduct.getTitle());
        holder.productCostText.setText(allProduct.getPrice());

        holder.homeProductView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productLisner.viewProduct(products.get(position));
            }
        });

        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productLisner.addToCart(products.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView productTitleText;
        TextView productCostText;
        ImageButton addToCart;

        CardView homeProductView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.productViewImage);
            productTitleText = itemView.findViewById(R.id.productTitle);
            productCostText = itemView.findViewById(R.id.productPrice);
            addToCart = itemView.findViewById(R.id.productViewATCButton);
            homeProductView = itemView.findViewById(R.id.homeProductView);
        }
    }
}
