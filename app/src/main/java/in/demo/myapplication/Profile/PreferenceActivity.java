package in.demo.myapplication.Profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.demo.myapplication.R;

public class PreferenceActivity extends AppCompatActivity {

    private TextView area, state, distancetext, agetext;
    private Button update, viewprofile, backButton;
    private SeekBar distanceseek, ageSeekbar;
    private Switch hidelocation, hidename, hideAge, hideProfile;
    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private ProgressBar progressBar;
    private int minAge = 18;
    private int maxAge = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // Initialize Views
        backButton = findViewById(R.id.backButton);
        progressBar = findViewById(R.id.progressBar);
        agetext = findViewById(R.id.agetext);
        distancetext = findViewById(R.id.distancetext);
        update = findViewById(R.id.update);
        viewprofile = findViewById(R.id.viewprofile);
        distanceseek = findViewById(R.id.distanceseek);
        ageSeekbar = findViewById(R.id.ageSeekbar);
        hidelocation = findViewById(R.id.hidelocation);
        hidename = findViewById(R.id.hidename);
        hideAge = findViewById(R.id.hideAge);
        hideProfile = findViewById(R.id.hideprofile);

        ageSeekbar.setMax(42);
        distanceseek.setMax(500);

        // Initialize Location Client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Set up listeners
        setupSwitchListeners();
        setupSeekBarListeners();
        updatePreferencesStateToUi();

        backButton.setOnClickListener(v -> finish());
        update.setOnClickListener(v -> updateLocation());
        viewprofile.setOnClickListener(v -> startActivity(new Intent(PreferenceActivity.this, ProfileActivity.class)));
    }

    private void setupSwitchListeners() {
        setupHidelocationListener();
        setupHidenameListener();
        setupHideAgeListener();
        setupHideProfileListener();
    }

    private void setupHidelocationListener() {
        hidelocation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            handleSwitchChange(hidelocation, "Your location", isChecked);
        });
    }

    private void setupHidenameListener() {
        hidename.setOnCheckedChangeListener((buttonView, isChecked) -> {
            handleSwitchChange(hidename, "Your Name", isChecked);
        });
    }

    private void setupHideAgeListener() {
        hideAge.setOnCheckedChangeListener((buttonView, isChecked) -> {
            handleSwitchChange(hideAge, "Your Age", isChecked);
        });
    }

    private void setupHideProfileListener() {
        hideProfile.setOnCheckedChangeListener((buttonView, isChecked) -> {
            handleSwitchChange(hideProfile, "Your Profile", isChecked);
        });
    }

    private void handleSwitchChange(Switch switchView, String label, boolean isChecked) {
        boolean isHidden = isChecked;
        switchView.setText(label + (isHidden ? " is Hidden now" : " is Visible now"));
        switchView.setTextColor(isHidden ? Color.RED : Color.GREEN);
        switchView.setThumbResource(isHidden ? R.drawable.ic_circle : R.drawable.ic_circle1);

        // Call method to update preferences in the database
        updatePreferenceInDatabase(label, isChecked);
    }

    private void updatePreferenceInDatabase(String preferenceKey, boolean isHidden) {
        String userId = auth.getCurrentUser().getUid();
        DatabaseReference databaseReference = database.getReference("users").child(userId);

        Map<String, Object> preferences = new HashMap<>();
        // Convert the label to the correct database key if needed
        String databaseKey = "";
        switch (preferenceKey) {
            case "Your location":
                databaseKey = "hideLocation";
                break;
            case "Your Name":
                databaseKey = "hideName";
                break;
            case "Your Age":
                databaseKey = "hideAge";
                break;
            case "Your Profile":
                databaseKey = "hideProfile";
                break;
        }

        preferences.put(databaseKey, isHidden);

        databaseReference.updateChildren(preferences)
                .addOnSuccessListener(aVoid -> Toast.makeText(PreferenceActivity.this, "Preferences updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(PreferenceActivity.this, "Failed to update preferences: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void setupSeekBarListeners() {
        distanceseek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distancetext.setText("Maximum distance: " + progress + "km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                updateDistancePreferenceInDatabase(progress);
            }
        });

        ageSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int adjustedProgress = progress + 18; // Adjust progress to start from 18
                agetext.setText("Age Range 18 : " + adjustedProgress);
                maxAge = adjustedProgress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress() + 18;
                updateAgePreferenceInDatabase(progress);
            }
        });
    }

    private void updateDistancePreferenceInDatabase(int distance) {
        String userId = auth.getCurrentUser().getUid();
        DatabaseReference databaseReference = database.getReference("users").child(userId);

        Map<String, Object> preferences = new HashMap<>();
        preferences.put("maxDistance", distance);

        databaseReference.updateChildren(preferences)
                .addOnSuccessListener(aVoid -> Toast.makeText(PreferenceActivity.this, "Distance preference updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(PreferenceActivity.this, "Failed to update distance preference: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void updateAgePreferenceInDatabase(int age) {
        String userId = auth.getCurrentUser().getUid();
        DatabaseReference databaseReference = database.getReference("users").child(userId);

        Map<String, Object> preferences = new HashMap<>();
        preferences.put("ageRange", age);

        databaseReference.updateChildren(preferences)
                .addOnSuccessListener(aVoid -> Toast.makeText(PreferenceActivity.this, "Age preference updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(PreferenceActivity.this, "Failed to update age preference: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void updatePreferencesStateToUi() {
        String userId = auth.getCurrentUser().getUid();
        DatabaseReference databaseReference = database.getReference("users").child(userId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Boolean isLocationHidden = snapshot.child("hideLocation").getValue(Boolean.class);
                    Boolean isNameHidden = snapshot.child("hideName").getValue(Boolean.class);
                    Boolean isAgeHidden = snapshot.child("hideAge").getValue(Boolean.class);
                    Boolean isProfileHidden = snapshot.child("hideProfile").getValue(Boolean.class);
                    Integer maxDistance = snapshot.child("maxDistance").getValue(Integer.class);
                    Integer ageRange = snapshot.child("ageRange").getValue(Integer.class);

                    if (isLocationHidden != null) {
                        hidelocation.setChecked(isLocationHidden);
                        hidelocation.setText("Your location" + (isLocationHidden ? " is Hidden now" : " is Visible now"));
                    }
                    if (isNameHidden != null) {
                        hidename.setChecked(isNameHidden);
                        hidename.setText("Your Name" + (isNameHidden ? " is Hidden now" : " is Visible now"));
                    }
                    if (isAgeHidden != null) {
                        hideAge.setChecked(isAgeHidden);
                        hideAge.setText("Your Age" + (isAgeHidden ? " is Hidden now" : " is Visible now"));
                    }
                    if (isProfileHidden != null) {
                        hideProfile.setChecked(isProfileHidden);
                        hideProfile.setText("Your Profile" + (isProfileHidden ? " is Hidden now" : " is Visible now"));
                    }
                    if (maxDistance != null) {
                        distanceseek.setProgress(maxDistance);
                        distancetext.setText("Maximum distance: " + maxDistance + "km");
                    }
                    if (ageRange != null) {
                        ageSeekbar.setProgress(ageRange - 18);
                        agetext.setText("Age Range 18 : " + ageRange);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PreferenceActivity.this, "Failed to load preferences: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                saveUserLocation(location);
            } else {
                Toast.makeText(PreferenceActivity.this, "Unable to get location", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void saveUserLocation(Location location) {
        Geocoder geocoder = new Geocoder(PreferenceActivity.this);
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String area = address.getLocality();
                String state = address.getAdminArea();

                String userId = auth.getCurrentUser().getUid();
                DatabaseReference databaseReference = database.getReference("users").child(userId);

                Map<String, Object> locationUpdates = new HashMap<>();
                locationUpdates.put("area", area);
                locationUpdates.put("state", state);
                locationUpdates.put("latitude", location.getLatitude());
                locationUpdates.put("longitude", location.getLongitude());
                progressBar.setVisibility(View.GONE);
                databaseReference.updateChildren(locationUpdates)
                        .addOnSuccessListener(aVoid -> Toast.makeText(PreferenceActivity.this, "User's location updated successfully", Toast.LENGTH_SHORT).show())

                        .addOnFailureListener(e -> Toast.makeText(PreferenceActivity.this, "Failed to update user's location: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(PreferenceActivity.this, "Geocoder failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateLocation();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
