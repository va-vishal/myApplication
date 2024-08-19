package in.demo.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DOBActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private EditText dobEditText;
    private Button buttonNext;
    private String selectedDOB;
    private int userAge;
    FirebaseAuth auth;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dobactivity);

        dobEditText = findViewById(R.id.dobEditText);
        buttonNext = findViewById(R.id.buttonNext);

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();


        dobEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDatePickerBottomSheet dialog = new CustomDatePickerBottomSheet(DOBActivity.this, new CustomDatePickerBottomSheet.OnDateSetListener() {
                    @Override
                    public void onDateSet(int day, int month, int year) {
                        selectedDOB = day + "/" + (month + 1) + "/" + year;
                        dobEditText.setText(selectedDOB);
                        userAge = calculateAge(day, month, year);
                        System.out.println("User's age is: " + userAge);
                        updateUserInDatabase(selectedDOB, String.valueOf(userAge));
                    }
                });
                dialog.show();
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userAge >= 18) {
                    if (checkLocationPermission()) {
                        startLocationActivity();
                    }
                } else {
                    showCustomMessageDialog("Only 18 years old or above are allowed to create an account!!");
                }
            }
        });
    }

    private void updateUserInDatabase(String dob, String age) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Create a map to store the new values
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("dob", dob);
        userUpdates.put("age", age);

        // Update the user's data in Firebase Realtime Database
        usersRef.child(userId).updateChildren(userUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(DOBActivity.this, "DOB and age saved successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DOBActivity.this, "Failed to update user's DOB and age: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }
        // Permission is already granted
        return true;
    }

    private void startLocationActivity() {
        Intent intent = new Intent(DOBActivity.this, LocationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void showCustomMessageDialog(String message) {
        DialogFragment dialog = new CustomMessageDialog(message);
        dialog.show(getSupportFragmentManager(), "CustomMessageDialog");
    }

    // Calculate age based on provided date of birth
    private int calculateAge(int day, int month, int year) {
        Calendar today = Calendar.getInstance();
        Calendar dob = Calendar.getInstance();
        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start LocationActivity
                startLocationActivity();
            } else {
                // Permission denied, show a message or handle accordingly
                Toast.makeText(this, "Location permission is required to proceed.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
