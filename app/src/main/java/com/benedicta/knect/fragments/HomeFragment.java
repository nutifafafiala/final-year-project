package com.benedicta.knect.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.benedicta.knect.R;
import com.benedicta.knect.activities.BusinessDetailsActivity;
import com.benedicta.knect.adapters.BusinessAdapter;
import com.benedicta.knect.listeners.ItemClickListener;
import com.benedicta.knect.models.Business;
import com.benedicta.knect.models.BusinessCategory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment  implements SwipeRefreshLayout.OnRefreshListener{

    private SwipeRefreshLayout refreshLayout;
    private ProgressBar loading;
    private RecyclerView recyclerView;
    private BusinessAdapter adapter;
    private List<Business> businesses = new ArrayList<>();
    private TextView textView;
    private DatabaseReference reference;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        init(root);

        return root;
    }

    private void init(View view) {

        reference = FirebaseDatabase.getInstance().getReference("business");

        refreshLayout = view.findViewById(R.id.refresh);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark), getResources().getColor(R.color.colorPrimary));
        refreshLayout.setOnRefreshListener(this);


        loading = view.findViewById(R.id.loading);
        textView = view.findViewById(R.id.info);
        recyclerView = view.findViewById(R.id.recycler_view_business);

        refreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark)
        );

        adapter = new BusinessAdapter(getActivity(), businesses);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String business = new Gson().toJson(businesses.get(position));

                Intent intent = new Intent(getActivity(), BusinessDetailsActivity.class);
                intent.putExtra("business", business);

                startActivity(intent);
            }
        });

        loadBusiness();

    }

    private void loadBusiness() {
//        businesses.clear();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                businesses.clear();
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final HashMap<String, Object> item = (HashMap<String, Object>) snapshot.getValue();

                    businesses.add(0, new Business(
                            snapshot.getKey(), String.valueOf(item.get("name")), String.valueOf(item.get("contact")),
                            String.valueOf(item.get("location")), String.valueOf(item.get("services")), String.valueOf(item.get("delivery")),
                            String.valueOf(item.get("category")), String.valueOf(item.get("userId")), String.valueOf(item.get("imageUrl")),
                            String.valueOf(item.get("facebook")), String.valueOf(item.get("instagram")), String.valueOf(item.get("twitter"))));


                }

                refreshLayout.setRefreshing(false);
                loading.setVisibility(View.GONE);

                if (businesses.size() > 0){
                    recyclerView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.GONE);
                }else {
                    recyclerView.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                refreshLayout.setRefreshing(false);
                loading.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onRefresh() {
        loadBusiness();
    }
}