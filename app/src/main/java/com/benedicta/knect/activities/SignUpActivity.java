package com.benedicta.knect.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.benedicta.knect.R;
import com.benedicta.knect.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignUpActivity extends AppCompatActivity {

    EditText name, email, password;
    Context context = SignUpActivity.this;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        Button sign_up = findViewById(R.id.sign_up);
        TextView login = findViewById(R.id.login);
        mAuth = FirebaseAuth.getInstance();

        // set up progress bar
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Signing up, please wait...");

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void signUp() {

        //Getting user name, email and password
        final String vName = name.getText().toString();
        String vEmail = email.getText().toString();
        String vPassword = password.getText().toString();

        //validation
        if(vName.isEmpty()){

            Toast.makeText(context, "Please input your name.", Toast.LENGTH_SHORT).show();

        }else if(vEmail.isEmpty()){

            Toast.makeText(context, "Please input your email.", Toast.LENGTH_SHORT).show();

        }else if(vPassword.isEmpty()){

            Toast.makeText(context, "Please input your password.", Toast.LENGTH_SHORT).show();

        }else {

            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(vEmail, vPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                updateUserProfile(vName);

                            } else {
                                // If sign in fails, display a message to the user
                                progressDialog.hide();
                                System.out.println("exception "+task.getException());
                                Toast.makeText(context, task.getException().toString(), Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

        }

    }

    // method to update user profile
    private void updateUserProfile(String name) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //start main activity
                            progressDialog.hide();
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users").child(user.getUid()).setValue(new User(name, user.getEmail()));
    }
}
