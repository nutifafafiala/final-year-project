package com.benedicta.knect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.benedicta.knect.R;
import com.benedicta.knect.listeners.ItemClickListener;
import com.benedicta.knect.models.BusinessCategory;

import java.util.List;

public class BusinessCategoryAdapter extends RecyclerView.Adapter<BusinessCategoryAdapter.BusinessCategoryVH> {

    private Context context;
    private List<BusinessCategory> categories;
    private ItemClickListener listener;

    public BusinessCategoryAdapter(Context context, List<BusinessCategory> categories) {
        this.context = context;
        this.categories = categories;
    }

    public void setItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public BusinessCategoryVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BusinessCategoryVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_business_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BusinessCategoryVH holder, int position) {
        BusinessCategory category = categories.get(position);
        holder.name.setText(category.name);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class BusinessCategoryVH extends RecyclerView.ViewHolder {

        private TextView name;

        public BusinessCategoryVH(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(view, getAdapterPosition());
                }
            });
        }
    }
}
