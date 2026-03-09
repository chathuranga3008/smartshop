package lk.exam.smartshop_admin.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import lk.exam.smartshop_admin.R;
import lk.exam.smartshop_admin.lisners.CategorySelectListener;
import lk.exam.smartshop_admin.model.Category;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>{
    private ArrayList<Category> categories;
    private Context context;

    private CategorySelectListener categorySelectListener;

    public CategoryAdapter(ArrayList<Category> categories, Context context, CategorySelectListener categorySelectListener) {
        this.categories = categories;
        this.context = context;
        this.categorySelectListener = categorySelectListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.categorylist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Category category = categories.get(position);
        holder.categoryNameTextView.setText(category.getCategoryName());

        holder.catRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categorySelectListener.deleteCategory(categories.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryNameTextView;

        Button catRemoveBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryNameTextView = itemView.findViewById(R.id.categoryNameText);
            catRemoveBtn = itemView.findViewById(R.id.categoryDeletebtn);
        }
    }
}
