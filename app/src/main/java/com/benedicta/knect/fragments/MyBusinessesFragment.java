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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.benedicta.knect.R;
import com.benedicta.knect.adapters.BusinessAdapter;
import com.benedicta.knect.listeners.ItemClickListener;
import com.benedicta.knect.models.Business;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyBusinessesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private SwipeRefreshLayout refreshLayout;
    private ProgressBar loading;
    private RecyclerView recyclerView;
    private BusinessAdapter adapter;
    private List<Business> businesses = new ArrayList<>();
    private TextView textView;
    private DatabaseReference reference;
    private FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_businesses, container, false);

        init(view);

        return view;

    }

    private void init(View view) {

        reference = FirebaseDatabase.getInstance().getReference("business");
        user = FirebaseAuth.getInstance().getCurrentUser();

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
                //start activity to edi business
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

                   if (String.valueOf(item.get("userId")).equals(user.getUid())) {


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
