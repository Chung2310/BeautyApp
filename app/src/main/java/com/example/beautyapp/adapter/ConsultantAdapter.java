package com.example.beautyapp.adapter;

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
import com.example.beautyapp.model.Consultant;
import com.example.beautyapp.utils.Utils;

import java.util.List;

public class ConsultantAdapter extends RecyclerView.Adapter<ConsultantAdapter.ConsultantViewHolder> {
    private List<Consultant> consultantList;
    private OnConsultantClickListener listener;

    public ConsultantAdapter(List<Consultant> consultantList) {
        this.consultantList = consultantList;
    }

    public void setConsultantList(List<Consultant> consultants) {
        this.consultantList = consultants;
        notifyDataSetChanged();
    }

    public void setOnConsultantClickListener(OnConsultantClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ConsultantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_consultant, parent, false);
        return new ConsultantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsultantViewHolder holder, int position) {
        Consultant consultant = consultantList.get(position);
        holder.name.setText(consultant.getName() != null ? consultant.getName() : "Không có tên");
        holder.phone.setText(consultant.getPhone() != null ? "📞 " + consultant.getPhone() : "📞 Không có số");
        holder.email.setText(consultant.getEmail() != null ? "✉️ " + consultant.getEmail() : "✉️ Không có email");
        holder.address.setText(consultant.getAddress() != null ? "📍 " + consultant.getAddress() : "📍 Không có địa chỉ");

        String imageUrl = consultant.getImageUrl() != null && consultant.getImageUrl().contains("https")
                ? consultant.getImageUrl()
                : Utils.BASE_URL + "image/" + consultant.getImageUrl();

        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.android)
                .error(R.drawable.android)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return consultantList == null ? 0 : consultantList.size();
    }

    public interface OnConsultantClickListener {
        void onConsultantClick(Consultant consultant);
    }

    public class ConsultantViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, phone, email, address;

        public ConsultantViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imgConsultant);
            name = itemView.findViewById(R.id.tvConsultantName);
            phone = itemView.findViewById(R.id.tvConsultantPhone);
            email = itemView.findViewById(R.id.tvConsultantEmail);
            address = itemView.findViewById(R.id.tvConsultantAddress);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onConsultantClick(consultantList.get(position));
                    }
                }
            });
        }
    }
}