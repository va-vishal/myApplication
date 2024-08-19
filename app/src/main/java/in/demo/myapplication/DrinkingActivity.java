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

import in.demo.myapplication.Adapter.DrinkingAdapter;
import in.demo.myapplication.Model.DrinkingItem;

public class DrinkingActivity extends AppCompatActivity {

    private RecyclerView recyclerViewDrinking;
    private DrinkingAdapter drinkingAdapter;
    private List<DrinkingItem> drinkingList;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private Button buttonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drinking);

        recyclerViewDrinking = findViewById(R.id.recyclerViewDrinking);
        buttonNext = findViewById(R.id.buttonNext);

        drinkingList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(auth.getCurrentUser().getUid());

        // Add sample drinking status options (you can populate this from your data source)
        drinkingList.add(new DrinkingItem("Teetotaller", R.drawable.teetotaller));
        drinkingList.add(new DrinkingItem("Drink Socially", R.drawable.social));
        drinkingList.add(new DrinkingItem("Drink Regularly", R.drawable.regular));
        drinkingList.add(new DrinkingItem("Drink Occuasinally", R.drawable.occuasinally));
        drinkingList.add(new DrinkingItem("Plan to Quit Drinking", R.drawable.plantoquit));


        drinkingAdapter = new DrinkingAdapter(drinkingList, position -> drinkingAdapter.setSelected(position));

        recyclerViewDrinking.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewDrinking.setAdapter(drinkingAdapter);

        buttonNext.setOnClickListener(v -> {
            int selectedPosition = drinkingAdapter.getSelectedPosition();
            if (selectedPosition != RecyclerView.NO_POSITION) {
                DrinkingItem selectedDrinkingStatus = drinkingList.get(selectedPosition);
                saveSelectedDrinkingStatusToDatabase(selectedDrinkingStatus.getDrinkStatus());
                Intent intent = new Intent(DrinkingActivity.this, SmokingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(DrinkingActivity.this, "Please select a drinking status", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSelectedDrinkingStatusToDatabase(String drinkingStatus) {
        // Update the user's drinking status in Firebase Realtime Database
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("drinkingStatus", drinkingStatus);

        databaseReference.updateChildren(userUpdates)
                .addOnSuccessListener(aVoid -> Toast.makeText(DrinkingActivity.this, "User's drinking status updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(DrinkingActivity.this, "Failed to update user's drinking status: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
