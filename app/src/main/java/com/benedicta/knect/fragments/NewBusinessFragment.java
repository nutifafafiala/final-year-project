package com.benedicta.knect.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;

import com.benedicta.knect.R;
import com.benedicta.knect.models.Business;
import com.benedicta.knect.models.BusinessCategory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewBusinessFragment extends Fragment {

    private ProgressDialog loading;
    private EditText name, location, services, contact;
    private AppCompatSpinner delivery, category;
    private DatabaseReference reference;
    private String catId = null, deliveryId = null;
    private List<BusinessCategory> categories = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private List<String> cats = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_new_business, container, false);

        init(root);

        return root;
    }

    private void init(View view) {

        reference = FirebaseDatabase.getInstance().getReference("business");

        loading = new ProgressDialog(getActivity());
        loading.setMessage("Adding, Please wait...");
        loading.setCanceledOnTouchOutside(false);
        loading.setCancelable(false);

        name = view.findViewById(R.id.name);
        location = view.findViewById(R.id.location);
        contact = view.findViewById(R.id.contact);
        services = view.findViewById(R.id.services);
        delivery = view.findViewById(R.id.delivery);
        category = view.findViewById(R.id.category);

        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, cats);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(arrayAdapter);

        loadCategories();





        view.findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBusiness();
            }
        });

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                catId = categories.get(i).id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        delivery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                deliveryId = adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void addBusiness() {

        String inputName = name.getText().toString();
        String inputLocation = location.getText().toString();
        String inputServices = services.getText().toString();
        String inputContact = contact.getText().toString();

        if (inputName.isEmpty() || inputContact.isEmpty() || inputLocation.isEmpty() || inputServices.isEmpty() || delivery == null || catId == null) {
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();

        }else if(inputContact.length() != 10 ){
            Toast.makeText(getActivity(), "Invalid contact", Toast.LENGTH_SHORT).show();
        }else {
            loading.show();
            reference.push().setValue(new Business(inputName, inputContact, inputLocation, inputServices, deliveryId, catId));
            Toast.makeText(getActivity(), "Business Added successfully", Toast.LENGTH_SHORT).show();

            name.setText("");
            location.setText("");
            services.setText("");
            contact.setText("");
            loading.hide();
        }


    }

    private void loadCategories() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("categories");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HashMap<String, Object> item = (HashMap<String, Object>) snapshot.getValue();

                    if(item != null) {
                        categories.add(0, new BusinessCategory(snapshot.getKey(), String.valueOf(item.get("name"))));
                        cats.add(0, String.valueOf(item.get("name")));
                    }


                    arrayAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}