package com.benedicta.knect.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.benedicta.knect.R;
import com.benedicta.knect.adapters.BusinessAdapter;
import com.benedicta.knect.listeners.ItemClickListener;
import com.benedicta.knect.models.Business;
import com.benedicta.knect.models.BusinessCategory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BusinessCategoryActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private SwipeRefreshLayout refreshLayout;
    private ProgressBar loading;
    private RecyclerView recyclerView;
    private BusinessAdapter adapter;
    private List<Business> businesses = new ArrayList<>();
    private TextView textView;
    private DatabaseReference reference;
    private Context context = BusinessCategoryActivity.this;
    private BusinessCategory cat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_category);

        if (getIntent().hasExtra("business"))
            cat = new Gson().fromJson(getIntent().getStringExtra("business"), BusinessCategory.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(cat.name);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        init();


    }

    private void init() {


        reference = FirebaseDatabase.getInstance().getReference("business");

        refreshLayout = findViewById(R.id.refresh);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark), getResources().getColor(R.color.colorPrimary));
        refreshLayout.setOnRefreshListener(this);


        loading = findViewById(R.id.loading);
        textView = findViewById(R.id.info);
        recyclerView = findViewById(R.id.recycler_view_business);

        refreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark)
        );

        adapter = new BusinessAdapter(context, businesses);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String business = new Gson().toJson(businesses.get(position));

                Intent intent = new Intent(context, BusinessDetailsActivity.class);
                intent.putExtra("business", business);

                startActivity(intent);
            }
        });

        loadBusiness();

    }

    private void loadBusiness() {
        businesses.clear();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HashMap<String, Object> item = (HashMap<String, Object>) snapshot.getValue();



                    if (String.valueOf(item.get("category")).equals(cat.id)) {

                        businesses.add(0, new Business(
                                snapshot.getKey(), String.valueOf(item.get("name")),
                                String.valueOf(item.get("contact")), String.valueOf(item.get("location")),
                                String.valueOf(item.get("services")), String.valueOf(item.get("delivery")), String.valueOf(item.get("category")), String.valueOf(item.get("userId"))));


                    }

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
