package in.demo.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.demo.myapplication.Adapter.EducationAdapter;
import in.demo.myapplication.Model.EducationItem;

public class EducationActivity extends AppCompatActivity {

    private RecyclerView recyclerViewEducation;
    private EducationAdapter educationAdapter;
    private List<EducationItem> educationList;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private Button buttonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education);

        recyclerViewEducation = findViewById(R.id.recyclerViewEducation);
        buttonNext = findViewById(R.id.buttonNext);

        educationList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(auth.getCurrentUser().getUid());

        // Add sample education level options (you can populate this from your data source)
        educationList.add(new EducationItem("High School", R.drawable.school));
        educationList.add(new EducationItem("Bachelor's", R.drawable.bachelors));
        educationList.add(new EducationItem("Master's", R.drawable.masters));
        educationList.add(new EducationItem("Phd", R.drawable.phd));

        educationAdapter = new EducationAdapter(educationList, position -> educationAdapter.setSelected(position));

        recyclerViewEducation.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewEducation.setAdapter(educationAdapter);

        buttonNext.setOnClickListener(v -> {
            int selectedPosition = educationAdapter.getSelectedPosition();
            if (selectedPosition != RecyclerView.NO_POSITION) {
                EducationItem selectedEducationLevel = educationList.get(selectedPosition);
                saveSelectedEducationLevelToDatabase(selectedEducationLevel.getEducationLevel());
                Intent intent = new Intent(EducationActivity.this, SalaryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(EducationActivity.this, "Please select an education level", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSelectedEducationLevelToDatabase(String educationLevel) {
        // Update the user's education level in Firebase Realtime Database
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("educationLevel", educationLevel);

        databaseReference.updateChildren(userUpdates)
                .addOnSuccessListener(aVoid -> Toast.makeText(EducationActivity.this, "User's education level updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(EducationActivity.this, "Failed to update user's education level: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
