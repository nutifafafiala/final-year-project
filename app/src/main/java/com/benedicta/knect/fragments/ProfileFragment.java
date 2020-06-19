package com.benedicta.knect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.benedicta.knect.R;
import com.benedicta.knect.models.Business;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ProfileFragment extends Fragment {

    private TextView email, name, contact, counter;
    private FirebaseUser user;
    private DatabaseReference reference;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        init(root);

        return root;
    }

    private void init(View view) {

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();

        email = view.findViewById(R.id.email);
        name = view.findViewById(R.id.name);
//        contact = view.findViewById(R.id.contact);
        counter = view.findViewById(R.id.count);

        email.setText(user.getEmail());
        name.setText(user.getDisplayName());


        reference.child("business").addListenerForSingleValueEvent(new ValueEventListener() {
            int count = 0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HashMap<String, Object> item = (HashMap<String, Object>) snapshot.getValue();

                    if (String.valueOf(item.get("userId")).equals(user.getUid()))
                        count = count + 1;


                }
                if (count < 2) {
                    counter.setText(count + " business uploaded");
                }else counter.setText(count + " businesses uploaded");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}