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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.demo.myapplication.Adapter.MaritalStatusAdapter;
import in.demo.myapplication.Model.MaritalStatusItem;

public class MaritalStatusActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMaritalStatus;
    private MaritalStatusAdapter maritalStatusAdapter;
    private List<MaritalStatusItem> maritalStatusList;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private Button buttonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marital_status);

        recyclerViewMaritalStatus = findViewById(R.id.recyclerViewMaritalStatus);
        buttonNext = findViewById(R.id.buttonNext);

        maritalStatusList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(auth.getCurrentUser().getUid());

        // Add sample marital status options (you can populate this from your data source)
        maritalStatusList.add(new MaritalStatusItem("Single", R.drawable.single));
        maritalStatusList.add(new MaritalStatusItem("Single with child", R.drawable.singlewithchild));
        maritalStatusList.add(new MaritalStatusItem("Married", R.drawable.married));
        maritalStatusList.add(new MaritalStatusItem("Divorced", R.drawable.divorced));
        maritalStatusList.add(new MaritalStatusItem("Divorced with child", R.drawable.seperatedwithchild));
        maritalStatusList.add(new MaritalStatusItem("Widowed", R.drawable.widowed));
        maritalStatusList.add(new MaritalStatusItem("Widowed with child", R.drawable.widowedwithchild));

        maritalStatusAdapter = new MaritalStatusAdapter(maritalStatusList, new MaritalStatusAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                maritalStatusAdapter.setSelected(position);
            }
        });

        recyclerViewMaritalStatus.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewMaritalStatus.setAdapter(maritalStatusAdapter);

        buttonNext.setOnClickListener(v -> {
            int selectedPosition = maritalStatusAdapter.getSelectedPosition();
            if (selectedPosition != RecyclerView.NO_POSITION) {
                MaritalStatusItem selectedMaritalStatus = maritalStatusList.get(selectedPosition);
                saveSelectedMaritalStatusToDatabase(selectedMaritalStatus.getStatus());
                Intent intent = new Intent(MaritalStatusActivity.this, DrinkingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(MaritalStatusActivity.this, "Please select a marital status", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSelectedMaritalStatusToDatabase(String maritalStatus) {
        // Update the user's marital status in Firebase Realtime Database
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("maritalStatus", maritalStatus);

        databaseReference.updateChildren(userUpdates)
                .addOnSuccessListener(aVoid -> Toast.makeText(MaritalStatusActivity.this, "User's marital status updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(MaritalStatusActivity.this, "Failed to update user's marital status: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
