package com.example.beautyapp.adapter;

import android.content.Intent;
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
import com.example.beautyapp.activity.BookingActivity;
import com.example.beautyapp.activity.DetailProductActivity;
import com.example.beautyapp.interface_click.ItemClickListener;
import com.example.beautyapp.model.Consultant;
import com.example.beautyapp.utils.Utils;

import java.util.List;

public class ConsultantAdapter extends RecyclerView.Adapter<ConsultantAdapter.ConsultantViewHolder> {
    private List<Consultant> consultantList;

    public ConsultantAdapter(List<Consultant> consultantList) {
        this.consultantList = consultantList;
    }

    public void setConsultantList(List<Consultant> consultants) {
        this.consultantList = consultants;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ConsultantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_consultant, parent, false);
        return new ConsultantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsultantViewHolder holder, int position) {

        ConsultantAdapter.ConsultantViewHolder myViewHolder = (ConsultantAdapter.ConsultantViewHolder) holder;

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

        myViewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int pos, boolean isLongClick) {
                if(!isLongClick){
                    Intent intent = new Intent(holder.itemView.getContext(), BookingActivity.class);
                    intent.putExtra("consultant", consultant);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    holder.itemView.getContext().startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return consultantList == null ? 0 : consultantList.size();
    }

    public interface OnConsultantClickListener {
        void onConsultantClick(Consultant consultant);
    }

    public class ConsultantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView image;
        private TextView name, phone, email, address;
        private ItemClickListener itemClickListener;

        public ConsultantViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imgConsultant);
            name = itemView.findViewById(R.id.tvConsultantName);
            phone = itemView.findViewById(R.id.tvConsultantPhone);
            email = itemView.findViewById(R.id.tvConsultantEmail);
            address = itemView.findViewById(R.id.tvConsultantAddress);
            itemView.setOnClickListener(this);
        }

            public void setItemClickListener(ItemClickListener itemClickListener) {
                this.itemClickListener = itemClickListener;
            }
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v ,getAdapterPosition(),false);
            }

    }
}