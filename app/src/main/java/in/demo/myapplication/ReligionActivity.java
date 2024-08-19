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

import in.demo.myapplication.Adapter.ReligionAdapter;
import in.demo.myapplication.Model.ReligionItem;

public class ReligionActivity extends AppCompatActivity {

    private RecyclerView recyclerViewReligion;
    private ReligionAdapter religionAdapter;
    private List<ReligionItem> religionList;
    private Button buttonNextReligion;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_religion);

        recyclerViewReligion = findViewById(R.id.recyclerViewReligion);
        buttonNextReligion = findViewById(R.id.buttonNext);

        religionList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(auth.getCurrentUser().getUid());


        // Add sample religion options (you can populate this from your data source)
        religionList.add(new ReligionItem("Christianity", R.drawable.christianity));
        religionList.add(new ReligionItem("Islam", R.drawable.islam));
        religionList.add(new ReligionItem("Hinduism", R.drawable.hinduism));
        religionList.add(new ReligionItem("Buddhism", R.drawable.buddhism));
        religionList.add(new ReligionItem("Judaism", R.drawable.judaism));
        religionList.add(new ReligionItem("Sikhism", R.drawable.sikhism));
        religionList.add(new ReligionItem("Other", R.drawable.other_religion));

        religionAdapter = new ReligionAdapter(religionList, new ReligionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Update UI to reflect selection (optional)
                religionAdapter.setSelected(position);
            }
        });

        recyclerViewReligion.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewReligion.setAdapter(religionAdapter);

        buttonNextReligion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedPosition = religionAdapter.getSelectedPosition();
                if (selectedPosition != RecyclerView.NO_POSITION) {
                    ReligionItem selectedReligion = religionList.get(selectedPosition);
                    saveSelectedReligionToDatabase(selectedReligion.getReligionName());
                    Toast.makeText(ReligionActivity.this, "Selected Religion: " + selectedReligion.getReligionName(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ReligionActivity.this, DietActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ReligionActivity.this, "Please select a religion", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void saveSelectedReligionToDatabase(String religion) {
        // Update the user's drinking status in Firebase Realtime Database
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("religion", religion);

        databaseReference.updateChildren(userUpdates)
                .addOnSuccessListener(aVoid -> Toast.makeText(ReligionActivity.this, "User's drinking status updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(ReligionActivity.this, "Failed to update user's drinking status: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
