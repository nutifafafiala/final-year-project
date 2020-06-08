package com.benedicta.knect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.benedicta.knect.R;
import com.benedicta.knect.listeners.ItemClickListener;
import com.benedicta.knect.models.Business;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BusinessAdapter extends RecyclerView.Adapter<BusinessAdapter.BusinessVH> {

    private Context context;
    private List<Business> businesses;
    private ItemClickListener listener;

    public BusinessAdapter(Context context, List<Business> businesses) {
        this.context = context;
        this.businesses = businesses;
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public BusinessVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BusinessVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_business, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BusinessVH holder, int position) {
        Business business = businesses.get(position);
        holder.description.setText(business.getDescription());
        Picasso.get().load(business.getImage()).placeholder(R.drawable.placeholder).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return businesses.size();
    }

    class BusinessVH extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView description;

        public BusinessVH(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            description = itemView.findViewById(R.id.description);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(view, getAdapterPosition());
                }
            });

        }
    }
}
