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

import in.demo.myapplication.Adapter.SalaryAdapter;
import in.demo.myapplication.Model.SalaryItem;

public class SalaryActivity extends AppCompatActivity {

    private RecyclerView recyclerViewSalary;
    private SalaryAdapter salaryAdapter;
    private List<SalaryItem> salaryList;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private Button buttonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salary);

        recyclerViewSalary = findViewById(R.id.recyclerViewSalary);
        buttonNext = findViewById(R.id.buttonNext);

        salaryList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(auth.getCurrentUser().getUid());

        salaryList.add(new SalaryItem("student"));
        salaryList.add(new SalaryItem("0-30k per month"));
        salaryList.add(new SalaryItem("30k-50k per month"));
        salaryList.add(new SalaryItem("50-75k per month"));
        salaryList.add(new SalaryItem("75k-1lak per month"));
        salaryList.add(new SalaryItem("1lak-1.5lak per month"));
        salaryList.add(new SalaryItem("1.5lak-2lak per month"));
        salaryList.add(new SalaryItem("2lak-2.5lak month"));
        salaryList.add(new SalaryItem("2.5lak-3lak month"));
        salaryList.add(new SalaryItem("3lak-3.5lak month"));
        salaryList.add(new SalaryItem("3.5lak-4lak month"));
        salaryList.add(new SalaryItem("4lak-4.5lak month"));
        salaryList.add(new SalaryItem("4.5lak-5lak month"));
        salaryList.add(new SalaryItem("5lak-above month"));
        salaryList.add(new SalaryItem("I Dont want to disclose my salary details!"));
        salaryAdapter = new SalaryAdapter(salaryList, position -> salaryAdapter.setSelected(position));

        recyclerViewSalary.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewSalary.setAdapter(salaryAdapter);

        buttonNext.setOnClickListener(v -> {
            int selectedPosition = salaryAdapter.getSelectedPosition();
            if (selectedPosition != RecyclerView.NO_POSITION) {
                SalaryItem selectedSalaryRange = salaryList.get(selectedPosition);
                saveSelectedSalaryRangeToDatabase(selectedSalaryRange.getSalaryRange());
                Intent intent = new Intent(SalaryActivity.this, ProfilePhotoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(SalaryActivity.this, "Please select a salary range", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSelectedSalaryRangeToDatabase(String salaryRange) {
        // Update the user's salary range in Firebase Realtime Database
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("salary", salaryRange);

        databaseReference.updateChildren(userUpdates)
                .addOnSuccessListener(aVoid -> Toast.makeText(SalaryActivity.this, "User's salary range updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(SalaryActivity.this, "Failed to update user's salary range: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
