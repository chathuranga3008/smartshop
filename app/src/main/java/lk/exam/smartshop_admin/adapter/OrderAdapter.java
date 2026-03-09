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
import lk.exam.smartshop_admin.lisners.OrderSelectListner;
import lk.exam.smartshop_admin.model.Order;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private ArrayList<Order> orders;
    private Context context;
    private OrderSelectListner orderSelectListner;

    public OrderAdapter(ArrayList<Order> orders, Context context, OrderSelectListner orderSelectListner) {
        this.orders = orders;
        this.context = context;
        this.orderSelectListner = orderSelectListner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.order_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Order order = orders.get(position);

        holder.orderIdTxt.setText(order.getId());
        holder.orderDateTxt.setText(order.getDate_time());
        holder.orderPriceTxt.setText("Rs."+order.getTotal()+".00");
        holder.orderEmailTxt.setText(order.getEmail());

        if (order.getDeliver_status() == 0) {
            holder.orderStatusTxt.setText("Pending");
            holder.statusBtn.setText("Confirm");
        }else if (order.getDeliver_status() == 1) {
            holder.orderStatusTxt.setText("Confirmed");
            holder.statusBtn.setText("Dilivered");
        } else if (order.getDeliver_status() == 2){
            holder.orderStatusTxt.setText("Delivered");
            holder.statusBtn.setVisibility(View.GONE);
        }

        holder.orderCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderSelectListner.selectOrder(orders.get(position));
            }
        });
        holder.statusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderSelectListner.confirmDelivered(orders.get(position));
            }
        });


    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTxt, orderDateTxt, orderPriceTxt, orderStatusTxt, orderEmailTxt;
        View orderCard;
        Button statusBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTxt = itemView.findViewById(R.id.orderIdText);
            orderDateTxt = itemView.findViewById(R.id.orderDateText);
            orderPriceTxt = itemView.findViewById(R.id.orderPriceText);
            orderStatusTxt = itemView.findViewById(R.id.orderStatusText);
            orderEmailTxt = itemView.findViewById(R.id.orderEmailText);
            orderCard = itemView.findViewById(R.id.orderCard);
            statusBtn = itemView.findViewById(R.id.statusBtn);
        }
    }
}