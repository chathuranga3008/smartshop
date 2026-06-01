package lk.exam.smartshop_admin.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import lk.exam.smartshop_admin.R;
import lk.exam.smartshop_admin.lisners.ProductListener;
import lk.exam.smartshop_admin.model.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder>{
    private ArrayList<Product> products;
    private FirebaseStorage firebaseStorage;
    private Context context;

    private ProductListener productLisner;

    public ProductAdapter(ArrayList<Product> products, Context context, ProductListener productLisner) {
        firebaseStorage = FirebaseStorage.getInstance();
        this.products = products;
        this.context = context;
        this.productLisner = productLisner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.product_cart, parent, false);
        return new ViewHolder(view);
    }

//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//
//    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Product product = products.get(position);

        firebaseStorage.getReference("productImages/"+product.getImage1Id()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get()
                        .load(uri)
                        .resize(200, 200)
                        .into(holder.imageView);
            }
        });

        holder.productTitleText.setText(product.getTitle());
        holder.productCostText.setText(product.getCost());

        holder.updateBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productLisner.updateProduct(products.get(position));
            }
        });

        holder.deleteBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productLisner.deleteProduct(products.get(position));
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
        Button updateBTN;
        Button deleteBTN;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            productTitleText = itemView.findViewById(R.id.productTitle);
            productCostText = itemView.findViewById(R.id.productCost);
            updateBTN = itemView.findViewById(R.id.updateBTN);
            deleteBTN = itemView.findViewById(R.id.deleteBTN);
        }
    }
}
