package com.app.faceiter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private EditText fullNameEditText, emailEditText, passwordEditText, confirmPasswordEditText;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupform);

        // Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Input Fields
        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);

        // Buttons
        Button createAccountButton = findViewById(R.id.createAccountButton);
        Button backToLoginButton = findViewById(R.id.backToLoginButton);

        // Create Account Button Click
        createAccountButton.setOnClickListener(v -> createAccount());

        // Back to Login
        backToLoginButton.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginformActivity.class));
            finish();
        });
    }

    private void createAccount() {
        Log.d("SignupActivity", "Create Account button clicked");

        String fullName = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Validations
        if (fullName.isEmpty()) {
            Log.d("SignupActivity", "Full name empty");
            fullNameEditText.setError("Enter full name");
            return;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Log.d("SignupActivity", "Invalid email");
            emailEditText.setError("Enter valid email");
            return;
        }

        if (password.length() < 6) {
            Log.d("SignupActivity", "Password too short");
            passwordEditText.setError("Password must be 6+ characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            Log.d("SignupActivity", "Passwords do not match");
            confirmPasswordEditText.setError("Passwords do not match");
            return;
        }

        Log.d("SignupActivity", "Attempting Firebase signup...");

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        Log.d("SignupActivity", "Firebase Auth success");

                        String userId = auth.getCurrentUser().getUid();

                        FirebaseDatabase.getInstance()
                                .getReference("Users")
                                .child(userId)
                                .setValue(new User(fullName, email))
                                .addOnCompleteListener(saveTask -> {

                                    if (saveTask.isSuccessful()) {
                                        Log.d("SignupActivity", "User saved to database");

                                        Toast.makeText(SignupActivity.this,
                                                "Account created successfully!",
                                                Toast.LENGTH_SHORT).show();

                                        startActivity(new Intent(SignupActivity.this, LoginformActivity.class));
                                        finish();

                                    } else {
                                        Log.e("SignupActivity", "Database save failed",
                                                saveTask.getException());
                                        Toast.makeText(SignupActivity.this,
                                                "Failed to save user data",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });

                    } else {
                        Log.e("SignupActivity", "Signup Failed",
                                task.getException());

                        Toast.makeText(SignupActivity.this,
                                "Signup Failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    // User model class
    public static class User {
        public String fullName;
        public String email;

        public User() {}

        public User(String fullName, String email) {
            this.fullName = fullName;
            this.email = email;
        }
    }
}
