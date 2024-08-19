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

import in.demo.myapplication.Adapter.SmokingAdapter;
import in.demo.myapplication.Model.DrinkingItem;
import in.demo.myapplication.Model.SmokingItem;

public class SmokingActivity extends AppCompatActivity {

    private RecyclerView recyclerViewSmoking;
    private SmokingAdapter smokingAdapter;
    private List<SmokingItem> smokingList;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private Button buttonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smoking);

        recyclerViewSmoking = findViewById(R.id.recyclerViewSmoking);
        buttonNext = findViewById(R.id.buttonNext);

        smokingList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(auth.getCurrentUser().getUid());


        smokingList.add(new SmokingItem("Never", R.drawable.never));
        smokingList.add(new SmokingItem("I Smoke", R.drawable.smoking));
        smokingList.add(new SmokingItem("Smoke Occuasinally", R.drawable.occuasinally));
        smokingList.add(new SmokingItem("Plan to Quit Smoking", R.drawable.quitsmoking));

        smokingAdapter = new SmokingAdapter(smokingList, position -> smokingAdapter.setSelected(position));

        recyclerViewSmoking.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewSmoking.setAdapter(smokingAdapter);

        buttonNext.setOnClickListener(v -> {
            int selectedPosition = smokingAdapter.getSelectedPosition();
            if (selectedPosition != RecyclerView.NO_POSITION) {
                SmokingItem selectedSmokingStatus = smokingList.get(selectedPosition);
                saveSelectedSmokingStatusToDatabase(selectedSmokingStatus.getSmokingStatus());
                Intent intent = new Intent(SmokingActivity.this, EducationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(SmokingActivity.this, "Please select a smoking status", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSelectedSmokingStatusToDatabase(String smokingStatus) {
        // Update the user's smoking status in Firebase Realtime Database
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("smokingStatus", smokingStatus);

        databaseReference.updateChildren(userUpdates)
                .addOnSuccessListener(aVoid -> Toast.makeText(SmokingActivity.this, "User's smoking status updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(SmokingActivity.this, "Failed to update user's smoking status: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
