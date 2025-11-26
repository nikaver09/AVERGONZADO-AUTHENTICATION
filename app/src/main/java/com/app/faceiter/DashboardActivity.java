package com.app.faceiter;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class DashboardActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        auth = FirebaseAuth.getInstance();

        // Find the logout card or image
        LinearLayout logoutCard = findViewById(R.id.logoutCard);
        logoutCard.findViewById(R.id.logoutIcon);// optional if you want icon separately

        // Set click listener on the logout card
        logoutCard.setOnClickListener(v -> logoutUser());
    }

    private void logoutUser() {
        auth.signOut(); // Firebase logout
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Go back to Login Activity
        Intent intent = new Intent(DashboardActivity.this, LoginformActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // clear back stack
        startActivity(intent);
        finish();
    }
}
