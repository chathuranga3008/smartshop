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
import lk.exam.smartshop_customer.model.Product;

public class OrderSummaryAdapter extends RecyclerView.Adapter<OrderSummaryAdapter.ViewHolder> {
    private final ArrayList<Product> products;
    private final Context context;
    private final FirebaseStorage storage;

    public OrderSummaryAdapter(ArrayList<Product> items, Context context) {
        this.products = items;
        this.context = context;
        this.storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.order_summary_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Product item = products.get(position);

        holder.itemTitleText.setText(item.getTitle());
        holder.itemPriceText.setText("Rs." + item.getPrice() + ".00");
        holder.itemQtyText.setText(item.getQuantity());

        storage.getReference("productImages/" + item.getImage1Id()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get()
                        .load(uri)
                        .fit()
                        .centerCrop()
                        .into(holder.image);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemTitleText, itemPriceText, itemQtyText;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTitleText = itemView.findViewById(R.id.itemTitleTxt);
            itemPriceText = itemView.findViewById(R.id.itemPriceTxt);
            itemQtyText = itemView.findViewById(R.id.itemQtyTxt);
            image = itemView.findViewById(R.id.itemImg);
        }
    }
}