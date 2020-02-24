package com.example.knect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.knect.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private FirebaseAuth mAuth;
    private Context context = LoginActivity.this;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        TextView forgot_password = findViewById(R.id.forgot_password);
        TextView sign_up = findViewById(R.id.sign_up);
        Button login = findViewById(R.id.login);

        //instantiate the fire_base auth
        mAuth = FirebaseAuth.getInstance();

        // set up progress bar
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Logging, please wait...");



        //login onclick listener
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // authenticate user
                login();
            }
        });


        //sign_up onclick listener
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SignUpActivity.class);
                startActivity(intent);
            }
        });

        //forgot password onclick listener
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // check if user is already logged
        if (currentUser != null) {
            startActivity(new Intent(context, MainActivity.class)); //redirect to main activity if user logged in
            finish();
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void login() {
        //Getting email and password
        String vEmail = email.getText().toString();
        String vPassword  = password.getText().toString();

        //validation
        if(vEmail.isEmpty()){

            Toast.makeText(context, "Please input your email.", Toast.LENGTH_SHORT).show();

        }else if(vPassword.isEmpty()){

            Toast.makeText(context, "Please input your password.", Toast.LENGTH_SHORT).show();

        }else {
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(vEmail, vPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                progressDialog.hide();
                                startActivity(new Intent(context, MainActivity.class));
                                finish();

                            } else {
                                // If sign in fails, display a message to the user.
                                progressDialog.hide();
                                Toast.makeText(context, "Invalid email or password!", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

        }

    }
}
