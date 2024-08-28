package in.demo.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
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


import in.demo.myapplication.Adapter.PrefGenderAdapter;
import in.demo.myapplication.Model.GenderItem;

public class PreferedGenderActivity extends AppCompatActivity {

    private RecyclerView recyclerViewGender;
    private PrefGenderAdapter genderAdapter;
    private List<GenderItem> genderList;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private Button buttonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_prefered_gender);


        recyclerViewGender = findViewById(R.id.recyclerViewGender);
        buttonNext = findViewById(R.id.buttonNext);

        genderList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(auth.getCurrentUser().getUid());


        genderList.add(new GenderItem("Male", R.drawable.man));
        genderList.add(new GenderItem("Female", R.drawable.woman));
        genderList.add(new GenderItem("Gay", R.drawable.gay));
        genderList.add(new GenderItem("Lesbian", R.drawable.lesbian));

        genderAdapter = new PrefGenderAdapter(genderList, position -> {
            // Optional: Update UI to reflect selection
        });

        recyclerViewGender.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewGender.setAdapter(genderAdapter);

        buttonNext.setOnClickListener(v -> {
            int selectedPosition = genderAdapter.getSelectedPosition();
            if (selectedPosition != -1) {
                String selectedGender = genderList.get(selectedPosition).getGenderName();
                saveSelectedGenderToDatabase(selectedGender);
                Intent intent = new Intent(PreferedGenderActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(PreferedGenderActivity.this, "Please select a gender", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSelectedGenderToDatabase(String gender) {
        // Update the user's preferred gender in Firebase Realtime Database
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("prefGender", gender);

        databaseReference.updateChildren(userUpdates)
                .addOnSuccessListener(aVoid -> Toast.makeText(PreferedGenderActivity.this, "Preferred gender updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(PreferedGenderActivity.this, "Failed to update preferred gender: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}

