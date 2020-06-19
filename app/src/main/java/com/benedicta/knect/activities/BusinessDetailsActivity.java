package com.benedicta.knect.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.benedicta.knect.R;
import com.benedicta.knect.models.Business;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.HashMap;


public class BusinessDetailsActivity extends AppCompatActivity {

    private Business business;
    private TextView bName, category, contact, location, services, delivery, owner, email;

    private DatabaseReference reference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_details);

        if (getIntent().hasExtra("business"))
            business = new Gson().fromJson(getIntent().getStringExtra("business"), Business.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(business.name);

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

        reference = FirebaseDatabase.getInstance().getReference();

        bName = findViewById(R.id.business);
        category = findViewById(R.id.category);
        contact = findViewById(R.id.contact);
        location = findViewById(R.id.location);
        services = findViewById(R.id.services);
        delivery = findViewById(R.id.delivery);
        owner = findViewById(R.id.name);
        email = findViewById(R.id.email);

        bName.setText(business.name);
        contact.setText(business.contact);
        location.setText(business.location);
        services.setText(business.services);
        delivery.setText(business.delivery);

        System.out.println("user id "+business.userId);


        reference.child("categories").child(business.category).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Object> item = (HashMap<String, Object>) dataSnapshot.getValue();

                category.setText(String.valueOf(item.get("name")));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.child("users").child(business.userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Object> item = (HashMap<String, Object>) dataSnapshot.getValue();

                owner.setText(String.valueOf(item.get("name")));
                email.setText(String.valueOf(item.get("email")));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
