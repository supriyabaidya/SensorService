package com.final_sem_project.tomhardy.sensorservice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "LoginActivity";

    private String token = null;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        token = getIntent().getStringExtra("token");
    }

    public void logIn(View view) {
        String email = ((EditText) findViewById(R.id.email)).getText().toString().trim();
        String password = ((EditText) findViewById(R.id.password)).getText().toString().trim();

        if (email.equalsIgnoreCase("")) {
            Toast.makeText(getApplicationContext(), "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.equalsIgnoreCase("")) {
            Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!PatternChecker.isEmail(email)) {
            Toast.makeText(getApplicationContext(), "Please enter proper email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!PatternChecker.isPassword(password)) {
            Toast.makeText(getApplicationContext(), "Please enter proper password of length 6 or more", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("Login user");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    ((EditText) findViewById(R.id.email)).setText("");
                    ((EditText) findViewById(R.id.password)).setText("");
                    Toast.makeText(getApplicationContext(), "Logged in successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                    intent.putExtra("type", "login");
                    intent.putExtra("token", token);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Could not register, " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    Log.d(TAG, "exception : Could not register, " + task.getException().getMessage());
                }
                progressDialog.cancel();
            }
        });

    }

    public void gotoSignUp(View view) {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}
