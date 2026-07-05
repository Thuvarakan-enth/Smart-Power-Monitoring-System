package com.example.powermeter7;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.storage.*;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PICK_IMAGE_REQUEST = 101;

    private TextView welcomeText;
    private ImageView profileImage, aboutImage;
    private Button viewGraphBtn;

    private FirebaseUser user;
    private DatabaseReference userRef;
    private DatabaseReference powerDataRef;
    private StorageReference storageRef;

    private Uri imageUri;

    private TextView voltageText, currentText, powerText, kWhText, powerFactorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcomeText = findViewById(R.id.welcomeText);
        profileImage = findViewById(R.id.profileImageView);
        viewGraphBtn = findViewById(R.id.goToGraphBtn);
        aboutImage = findViewById(R.id.aboutImage);

        voltageText = findViewById(R.id.voltageText);
        currentText = findViewById(R.id.currentText);
        powerText = findViewById(R.id.powerText);
        kWhText = findViewById(R.id.kWhText);
        powerFactorText = findViewById(R.id.powerFactorText);

        // Firebase user check
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        powerDataRef = FirebaseDatabase.getInstance().getReference("powerData");
        storageRef = FirebaseStorage.getInstance().getReference("profilePics").child(user.getUid() + ".jpg");

        // Load name & profile picture
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue(String.class);
                String imageUrl = snapshot.child("imageUrl").getValue(String.class);

                if (name != null) welcomeText.setText(name + " Power MS");
                if (imageUrl != null) Glide.with(MainActivity.this).load(imageUrl).into(profileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });

        // Load sensor data with real-time updates
        powerDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Snapshot: " + snapshot.toString());

                if (snapshot.exists()) {
                    Double voltage = snapshot.child("voltage").getValue(Double.class);
                    Double current = snapshot.child("current").getValue(Double.class);
                    Double power = snapshot.child("power").getValue(Double.class);
                    Double kwh = snapshot.child("kWh").getValue(Double.class);

                    Double powerFactor = (power != null && voltage != null && current != null && voltage * current != 0) ?
                            power / (voltage * current) : null;

                    if (voltage != null) voltageText.setText(String.format("Voltage: %.2f V", voltage));
                    else voltageText.setText("Voltage: N/A");

                    if (current != null) currentText.setText(String.format("Current: %.2f A", current));
                    else currentText.setText("Current: N/A");

                    if (power != null) powerText.setText(String.format("Power: %.2f W", power));
                    else powerText.setText("Power: N/A");

                    if (kwh != null) kWhText.setText(String.format("KWH: %.2f kWh", kwh));
                    else kWhText.setText("kWh: 0.00 kWh");

                    if (powerFactor != null) powerFactorText.setText(String.format("Power Factor: %.2f", powerFactor));
                    else powerFactorText.setText("Power Factor: N/A");
                } else {
                    Toast.makeText(MainActivity.this, "No power data available", Toast.LENGTH_SHORT).show();
                    voltageText.setText("Voltage: N/A");
                    currentText.setText("Current: N/A");
                    powerText.setText("Power: N/A");
                    kWhText.setText("kWh: 0.00 kWh");
                    powerFactorText.setText("Power Factor: N/A");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load sensor data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Firebase error: " + error.getMessage());
            }
        });

        // Profile image click = update picture
        profileImage.setOnClickListener(v -> selectImage());

        // Navigate to GraphActivity
        viewGraphBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GraphActivity.class);
            startActivity(intent);
        });

        // 🔹 Navigate to Smart Insights page
        aboutImage.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        });
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);

            storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        userRef.child("imageUrl").setValue(uri.toString());
                        Toast.makeText(MainActivity.this, "Profile picture updated", Toast.LENGTH_SHORT).show();
                    })
            ).addOnFailureListener(e ->
                    Toast.makeText(MainActivity.this, "Upload failed", Toast.LENGTH_SHORT).show());
        }
    }
}
