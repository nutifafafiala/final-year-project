package com.benedicta.knect.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;

import com.benedicta.knect.R;
import com.benedicta.knect.models.Business;
import com.benedicta.knect.models.BusinessCategory;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EditBusinessActivity extends AppCompatActivity {

    private ProgressDialog loading;
    private EditText name, location, services, contact, fb,twitter, instagram;
    private AppCompatSpinner delivery, category;
    private DatabaseReference reference;
    private String catId = null, deliveryId = null;
    private List<BusinessCategory> categories = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private List<String> cats = new ArrayList<>();
    private ImageView image;
    private StorageReference storage;
    private Context context;
    private String imageUrl = null;
    private Business bus;
    private boolean isNewPhoto = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_business);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Edit Business");

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (getIntent().hasExtra("business"))
            bus = new Gson().fromJson(getIntent().getStringExtra("business"), Business.class);

        init();
    }


    private void init() {

        reference = FirebaseDatabase.getInstance().getReference("business");
        storage = FirebaseStorage.getInstance().getReference();

        loading = new ProgressDialog(this);
        loading.setMessage("Updating, Please wait...");
        loading.setCanceledOnTouchOutside(false);
        loading.setCancelable(false);

        fb = findViewById(R.id.facebook);
        instagram = findViewById(R.id.instagram);
        twitter = findViewById(R.id.twitter);

        name = findViewById(R.id.name);
        image = findViewById(R.id.image);
        location = findViewById(R.id.location);
        contact = findViewById(R.id.contact);
        services = findViewById(R.id.services);
        delivery = findViewById(R.id.delivery);
        category = findViewById(R.id.category);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cats);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(arrayAdapter);

        loadCategories();

        bindData();



        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editBusiness();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pick_photo, menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.pick_image) {
            checkPermission();
            return  true;
        }

        return  super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                if (result != null) isNewPhoto = true;

                image.setImageURI(result.getUri());

                imageUrl = result.getUri().getPath();

            }
        }
    }

    private void bindData() {
        name.setText(bus.name);
        Picasso.get().load(bus.imageUrl).placeholder(R.drawable.placeholder).into(image);

        imageUrl = bus.imageUrl;

        location.setText(bus.location);
        contact.setText(bus.contact);
        services.setText(bus.services);

        for(int i=0; i<categories.size(); i++) {
            if (categories.get(i).id.equals(bus.category)) {
                category.setSelection(i);
                break;
            }
        }

        delivery.setSelection(bus.delivery.equalsIgnoreCase("yes") ? 0 : 1);
    }



    private void editImageAndBusiness() {
        File pic = new File(imageUrl);
        Uri file = Uri.fromFile(pic);

        final StorageReference imageReference = storage.child("flyers/"+pic.getName());

        UploadTask uploadTask = imageReference.putFile(file);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {

                    imageUrl = task.getResult().toString();

                   updateBusiness();

                }
            }
        });

    }

    private void updateBusiness() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        reference.child(bus.id).setValue(new Business(
                name.getText().toString(), contact.getText().toString(), location.getText().toString(), services.getText().toString(), deliveryId,
                catId, user.getUid(), imageUrl, fb.getText().toString(), instagram.getText().toString(), twitter.getText().toString()));
        Toast.makeText(EditBusinessActivity.this, "Business Updated successfully", Toast.LENGTH_SHORT).show();

        loading.hide();
    }

    private void pickImage() {
        CropImage.activity()
                .start(this);
    }

    private void checkPermission() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        pickImage();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                    }
                }).check();
    }

    private void editBusiness() {

        String inputName = name.getText().toString();
        String inputLocation = location.getText().toString();
        String inputServices = services.getText().toString();
        String inputContact = contact.getText().toString();

        if (inputName.isEmpty() || inputContact.isEmpty() || inputLocation.isEmpty() || inputServices.isEmpty() || delivery == null || catId == null) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();

        }else if(imageUrl == null){
            Toast.makeText(this, "Please choose an image", Toast.LENGTH_SHORT).show();
        }else if(inputContact.length() != 10 ){
            Toast.makeText(this, "Invalid contact", Toast.LENGTH_SHORT).show();
        }else {
            loading.show();

            if (isNewPhoto) {
                editImageAndBusiness();
            }else {
                updateBusiness();
            }

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
