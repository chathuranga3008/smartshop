package lk.exam.smartshop_customer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import lk.exam.smartshop_customer.R;
import lk.exam.smartshop_customer.listener.CartSelectListener;
import lk.exam.smartshop_customer.model.Cart;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private final ArrayList<Cart> products;
    private final Context context;
    private final FirebaseStorage storage;
    private final CartSelectListener selectListener;

    public CartAdapter(ArrayList<Cart> products, Context context, CartSelectListener selectListener) {
        this.products = products;
        this.context = context;
        this.storage = FirebaseStorage.getInstance();
        this.selectListener = selectListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.product_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Cart cart = products.get(position);
        holder.productName.setText(cart.getTitle());
        holder.price.setText("Rs. " + cart.getCost() + ".00");
        holder.qty.setText(cart.getQuantity());

        holder.plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectListener.productAddQty(products.get(position));
            }
        });

        holder.minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectListener.productRemoveQty(products.get(position));
            }
        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectListener.removeProduct(products.get(position));
            }
        });

        storage.getReference("productImages/" + cart.getImage1Id()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(holder.image);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName;

        TextView price;
        TextView qty;
        ImageView image;
        ImageView plusBtn;
        ImageView minusBtn;
        ImageView deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.cartProductName);
            price = itemView.findViewById(R.id.cartPrice);
            qty = itemView.findViewById(R.id.cartQty);
            image = itemView.findViewById(R.id.cartImage);
            plusBtn = itemView.findViewById(R.id.cartPlusButton);
            minusBtn = itemView.findViewById(R.id.cartMinusButton);
            deleteBtn = itemView.findViewById(R.id.deleteCartButton);
        }
    }
}
