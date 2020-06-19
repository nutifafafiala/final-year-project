package com.benedicta.knect.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;

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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class NewBusinessFragment extends Fragment {

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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_new_business, container, false);

        init(root);

        return root;
    }

    private void init(View view) {

        context = getContext();

        reference = FirebaseDatabase.getInstance().getReference("business");
        storage = FirebaseStorage.getInstance().getReference();

        loading = new ProgressDialog(getActivity());
        loading.setMessage("Adding, Please wait...");
        loading.setCanceledOnTouchOutside(false);
        loading.setCancelable(false);

        fb = view.findViewById(R.id.facebook);
        instagram = view.findViewById(R.id.instagram);
        twitter = view.findViewById(R.id.twitter);

        name = view.findViewById(R.id.name);
        image = view.findViewById(R.id.image);
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.pick_photo, menu);
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

                image.setImageURI(result.getUri());

                imageUrl = result.getUri().getPath();

//                storeImage(result.getUri().getPath());


            }
        }
    }


    private void uploadImageAndBusiness(final String inputName, final String inputLocation, final String inputServices, final String inputContact) {
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

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    reference.push().setValue(new Business(
                            inputName, inputContact, inputLocation, inputServices, deliveryId,
                            catId, user.getUid(), imageUrl, fb.getText().toString(), instagram.getText().toString(), twitter.getText().toString()));
                    Toast.makeText(getActivity(), "Business Added successfully", Toast.LENGTH_SHORT).show();

                    name.setText("");
                    location.setText("");
                    services.setText("");
                    contact.setText("");
                    image.setImageResource(R.drawable.placeholder);
                    fb.setText("");
                    instagram.setText("");
                    twitter.setText("");
                    loading.hide();

                }
            }
        });

    }

    private void pickImage() {
        CropImage.activity()
                .start(context, this);
    }

    private void checkPermission() {
        Dexter.withContext(getActivity())
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

    private void addBusiness() {

        String inputName = name.getText().toString();
        String inputLocation = location.getText().toString();
        String inputServices = services.getText().toString();
        String inputContact = contact.getText().toString();

        if (inputName.isEmpty() || inputContact.isEmpty() || inputLocation.isEmpty() || inputServices.isEmpty() || delivery == null || catId == null) {
            Toast.makeText(getActivity(), "Please fill required fields", Toast.LENGTH_SHORT).show();

        }else if(imageUrl == null){
            Toast.makeText(getActivity(), "Please choose an image", Toast.LENGTH_SHORT).show();
        }else if(inputContact.length() != 10 ){
            Toast.makeText(getActivity(), "Invalid contact", Toast.LENGTH_SHORT).show();
        }else {
            loading.show();

            uploadImageAndBusiness(inputName, inputLocation, inputServices, inputContact);

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