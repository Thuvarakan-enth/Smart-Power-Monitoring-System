package com.example.powermeter7;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailInput, passwordInput, nameInput;
    private Button registerBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        nameInput = findViewById(R.id.nameInput);
        registerBtn = findViewById(R.id.registerBtn);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        registerBtn.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String name = nameInput.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Invalid email");
            return;
        }
        if (password.length() < 6) {
            passwordInput.setError("Password too short");
            return;
        }
        if (name.isEmpty()) {
            nameInput.setError("Name is required");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            // Save user name in Realtime Database under their UID
                            usersRef.child(currentUser.getUid()).child("name").setValue(name)
                                    .addOnCompleteListener(nameTask -> {
                                        if (nameTask.isSuccessful()) {
                                            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(this, LoginActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(this, "Failed to save name", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Registration Failed";
                        Toast.makeText(this, "Registration Failed: " + error, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
