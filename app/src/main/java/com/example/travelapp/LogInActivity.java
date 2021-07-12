package com.example.travelapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LogInActivity extends AppCompatActivity {
    public static final String TAG = "LoginActivity";
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnSignUp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // if currently logged in
        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }

        this.etUsername = findViewById(R.id.etUsername);
        this.etPassword = findViewById(R.id.etPassword);
        this.btnLogin = findViewById(R.id.btnLogin);
        this.btnSignUp = findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                // Create the ParseUser
                ParseUser user = new ParseUser();
                // Set core properties
                user.setUsername(username);
                user.setPassword(password);
                // Invoke signUpInBackground
                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            // Hooray! Let them use the app now.
                            Toast.makeText(LogInActivity.this, "successfully signed user up!", Toast.LENGTH_SHORT).show();
                            goMainActivity();
                        } else {
                            // Sign up didn't succeed. Look at the ParseException
                            // to figure out what went wrong
                            Log.d(TAG, e.toString());
                            Toast.makeText(LogInActivity.this, "sign up failed :(", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                loginUser(username, password);
            }
        });
    }

    private void loginUser(String username, String password) {
        // we use logInInBackground to have a better UE than just using logIn
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                // if succeeded, then e should be null
                if (e != null) {
                    Log.e(TAG, "issue with login", e);
                    Toast.makeText(LogInActivity.this, "issue with login", Toast.LENGTH_SHORT).show();
                    return;
                }
                // navigate to MainActivity if user is properly signed in
                Toast.makeText(LogInActivity.this, "successfully logged in!", Toast.LENGTH_SHORT).show();
                goMainActivity();
            }
        });
    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
