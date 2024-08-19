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

import in.demo.myapplication.Adapter.DietAdapter;
import in.demo.myapplication.Model.DietItem;

public class DietActivity extends AppCompatActivity {

    private RecyclerView recyclerViewDiet;
    private DietAdapter dietAdapter;
    private List<DietItem> dietList;
    private Button buttonNextDiet;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet);

        recyclerViewDiet = findViewById(R.id.recyclerViewDiet);
        buttonNextDiet = findViewById(R.id.buttonNextDiet);

        dietList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(auth.getCurrentUser().getUid());


        // Add sample diet options (you can populate this from your data source)
        dietList.add(new DietItem("Vegetarian", R.drawable.vegetarian));
        dietList.add(new DietItem("Vegan", R.drawable.vegan));
        dietList.add(new DietItem("Non-Vegetarian", R.drawable.nonvegetarian));
        dietList.add(new DietItem("Pescatarian", R.drawable.pescatarian));
        dietList.add(new DietItem("Paleo", R.drawable.paleo));
        dietList.add(new DietItem("Keto", R.drawable.keto));

        dietAdapter = new DietAdapter(dietList, new DietAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                dietAdapter.setSelected(position);
            }
        });

        recyclerViewDiet.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewDiet.setAdapter(dietAdapter);

        buttonNextDiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedPosition = dietAdapter.getSelectedPosition();
                if (selectedPosition != RecyclerView.NO_POSITION) {
                    DietItem selectedDiet = dietList.get(selectedPosition);
                    saveSelectedDietToDatabase(selectedDiet.getDietName());
                    Toast.makeText(DietActivity.this, "Selected Diet: " + selectedDiet.getDietName(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DietActivity.this, MaritalStatusActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(DietActivity.this, "Please select a diet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void saveSelectedDietToDatabase(String diet) {
        // Update the user's drinking status in Firebase Realtime Database
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("diet",diet);

        databaseReference.updateChildren(userUpdates)
                .addOnSuccessListener(aVoid -> Toast.makeText(DietActivity.this, "User's drinking status updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(DietActivity.this, "Failed to update user's drinking status: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
