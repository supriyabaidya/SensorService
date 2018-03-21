package com.final_sem_project.tomhardy.sensorservice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private String token = null;
    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("onCreate", "main");

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    token = FirebaseInstanceId.getInstance().getToken();
                    if (token != null) {
                        Log.d("device token", "is received " + token);
                        break;
                    } else
                        Log.d("device token", "is not received ");

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        firebaseAuth = FirebaseAuth.getInstance();
                        if (firebaseAuth.getCurrentUser() != null) {
                            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                            intent.putExtra("type", "signin");
                            intent.putExtra("token", token);
                            startActivity(intent);
                            finish();
                        }

                        ((Button) findViewById(R.id.sign_up)).setEnabled(true);
                        ((TextView) findViewById(R.id.textView3)).setEnabled(true);
                    }
                });
            }
        }).start();

        progressDialog = new ProgressDialog(this);

        FirebaseMessaging.getInstance().subscribeToTopic("test");

    }


    public void signUp(View view) {
        final String email = ((EditText) findViewById(R.id.email)).getText().toString().trim();
        final String password = ((EditText) findViewById(R.id.password)).getText().toString().trim();

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

        progressDialog.setMessage("Registering user");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    ((EditText) findViewById(R.id.email)).setText("");
                    ((EditText) findViewById(R.id.password)).setText("");
                    Toast.makeText(getApplicationContext(), "Registered successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    intent.putExtra("type", "signup");
                    intent.putExtra("email", email);
                    intent.putExtra("password", password);
                    intent.putExtra("token", token);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(getApplicationContext(), "Could not register, " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    Log.d("exception", "Could not register, " + task.getException().getMessage());
                }

                progressDialog.cancel();
            }
        });

    }

    public void gotoLogIn(View view) {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.putExtra("token", token);
        startActivity(intent);
        finish();
    }
}
