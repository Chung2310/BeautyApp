package com.example.beautyapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.beautyapp.R;
import com.example.beautyapp.activity.UserActivity;
import com.example.beautyapp.model.Booking;
import com.example.beautyapp.utils.Utils;

import java.util.List;

public class AppointmentHistoryAdapter extends RecyclerView.Adapter<AppointmentHistoryAdapter.ViewHolder> {

    private Context context;
    private List<Booking> bookingList;

    public AppointmentHistoryAdapter(Context context, List<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public AppointmentHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentHistoryAdapter.ViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        holder.txtConsultantName.setText(booking.getConsultantName());
        holder.txtConsultantPhone.setText("SĐT chuyên gia: " + booking.getConsultantPhone());
        holder.txtBookingPhone.setText("SĐT đặt lịch: " + booking.getBookingPhone());
        holder.txtDate.setText("Ngày: " + booking.getDate());
        holder.txtTime.setText("Giờ: " + booking.getTimeStart() + " - " + booking.getTimeEnd());
        holder.txtAddress.setText("Địa chỉ: " + booking.getAddress());

        String fullImageUrl;
        if (booking.getConsultantImage().startsWith("http")) {
            fullImageUrl = booking.getConsultantImage();
        } else {
            fullImageUrl = Utils.BASE_URL + "avt/" + booking.getConsultantImage();
        }

        Glide.with(context)
                .load(fullImageUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.android)
                .skipMemoryCache(true)
                .into(holder.imgConsultant);
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtConsultantName, txtDate, txtTime, txtAddress, txtConsultantPhone, txtBookingPhone;;
        ImageView imgConsultant;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtConsultantName = itemView.findViewById(R.id.txtConsultantName);
            txtConsultantPhone = itemView.findViewById(R.id.txtConsultantPhone);
            txtBookingPhone = itemView.findViewById(R.id.txtBookingPhone);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            imgConsultant = itemView.findViewById(R.id.imgConsultant);
        }
    }
}
