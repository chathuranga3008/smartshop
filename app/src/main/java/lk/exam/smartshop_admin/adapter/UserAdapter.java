package lk.exam.smartshop_admin.adapter;

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

import lk.exam.smartshop_admin.R;
import lk.exam.smartshop_admin.model.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private ArrayList<User> users;
    private FirebaseStorage firebaseStorage;
    private Context context;

    public UserAdapter(ArrayList<User> users, Context context) {
        this.users = users;
        this.firebaseStorage = FirebaseStorage.getInstance();
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.user_view, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        User user = users.get(position);

        firebaseStorage.getReference("user-images/"+user.getImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get()
                        .load(uri)
                        .fit()
                        .into(holder.image);
            }
        });

        holder.UserName.setText(user.getFirstName());
        holder.UserEmail.setText(user.getEmail());
        holder.UserMobile.setText(user.getMobile());

    }


    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView UserName;
        TextView UserEmail;
        TextView UserMobile;

        public ViewHolder(@NonNull View userView) {
            super(userView);
            image = userView.findViewById(R.id.imageViewUser);
            UserName = userView.findViewById(R.id.UserName);
            UserEmail = userView.findViewById(R.id.UserEmail);
            UserMobile = userView.findViewById(R.id.UserMobile);
        }
    }

}
