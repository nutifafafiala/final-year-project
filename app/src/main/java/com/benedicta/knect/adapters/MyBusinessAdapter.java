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
import com.benedicta.knect.models.BusinessCategory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class MyBusinessAdapter extends RecyclerView.Adapter<MyBusinessAdapter.MyBusinessVH> {

    private Context context;
    private List<Business> businesses;
    private ItemClickListener listener;

    public MyBusinessAdapter(Context context, List<Business> businesses) {
        this.context = context;
        this.businesses = businesses;
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyBusinessVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyBusinessVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_business, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyBusinessVH holder, int position) {
        final Business business = businesses.get(position);

        holder.name.setText(business.name);

        System.out.println("business name "+business.name);
        holder.services.setText(business.services);
        holder.contact.setText(business.contact);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("categories").child(business.category);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, String> item = (HashMap<String, String>) dataSnapshot.getValue();

                holder.services.setText(String.valueOf(item.get("name")));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Picasso.get().load(business.imageUrl).placeholder(R.drawable.placeholder).into(holder.image);

    }

    @Override
    public int getItemCount() {
        return businesses.size();
    }

    class MyBusinessVH extends RecyclerView.ViewHolder {

        private TextView name, contact, services;
        private ImageView image, more;

        public MyBusinessVH(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            contact = itemView.findViewById(R.id.contact);
            services = itemView.findViewById(R.id.services);
            image = itemView.findViewById(R.id.image);
            more = itemView.findViewById(R.id.more);

            more.setVisibility(View.VISIBLE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(view, getAdapterPosition());
                }
            });

            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(view, getAdapterPosition());
                }
            });

        }
    }
}
