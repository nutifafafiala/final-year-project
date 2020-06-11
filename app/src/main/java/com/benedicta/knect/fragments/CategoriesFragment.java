package com.benedicta.knect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.benedicta.knect.R;
import com.benedicta.knect.adapters.BusinessCategoryAdapter;
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

public class CategoriesFragment extends Fragment implements  SwipeRefreshLayout.OnRefreshListener, ItemClickListener {

    private List<BusinessCategory> categories = new ArrayList<>();
    private BusinessCategoryAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refresh;
    private TextView textView;
    private ProgressBar loading;
    private DatabaseReference ref;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_category, container, false);

        init(view);

        return view;

    }


    private void init(View view) {

        ref = FirebaseDatabase.getInstance().getReference("categories");

        textView = view.findViewById(R.id.info);
        loading = view.findViewById(R.id.loading);
        refresh = view.findViewById(R.id.refresh);
        refresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark));
        refresh.setOnRefreshListener(this);

        recyclerView = view.findViewById(R.id.recycler_view_business_categories);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        adapter = new BusinessCategoryAdapter(getActivity(), categories);
        recyclerView.setAdapter(adapter);

        adapter.setItemClickListener(this);

        loadData();

    }

    private void loadData() {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categories.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HashMap<String, Object> item = (HashMap<String, Object>) snapshot.getValue();

                    if(item != null)
                        categories.add(0, new BusinessCategory(
                                snapshot.getKey(), String.valueOf(item.get("name"))
                        ));

                }

                adapter.notifyDataSetChanged();
                refresh.setRefreshing(false);
                loading.setVisibility(View.GONE);

                if (categories.size() > 0){
                    recyclerView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.GONE);
                }else {
                    recyclerView.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                refresh.setRefreshing(false);
                loading.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
            }
        });



    }



    @Override
    public void onRefresh() {
        loadData();
    }

    @Override
    public void onItemClick(View view, int position) {
        String item = new Gson().toJson(categories.get(position));
    }
}
