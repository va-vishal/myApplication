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

import in.demo.myapplication.Adapter.MotherTongueAdapter;
import in.demo.myapplication.Model.MotherTongueItem;

public class MotherTongueActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMotherTongue;
    private MotherTongueAdapter motherTongueAdapter;
    private List<MotherTongueItem> motherTongueList;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private Button buttonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mother_tongue);

        recyclerViewMotherTongue = findViewById(R.id.recyclerViewMotherTongue);
        buttonNext = findViewById(R.id.buttonNext);

        motherTongueList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(auth.getCurrentUser().getUid());

        // Add sample mother tongue options (you can populate this from your data source)
        motherTongueList.add(new MotherTongueItem("Telugu", R.drawable.telugu));
        motherTongueList.add(new MotherTongueItem("Hindi", R.drawable.hindi));
        motherTongueList.add(new MotherTongueItem("English", R.drawable.english));
        motherTongueList.add(new MotherTongueItem("Tamil", R.drawable.tamil));
        motherTongueList.add(new MotherTongueItem("KonKani", R.drawable.konkani));
        motherTongueList.add(new MotherTongueItem("Bengali", R.drawable.bengali));
        motherTongueList.add(new MotherTongueItem("punjab", R.drawable.punjabi));
        motherTongueList.add(new MotherTongueItem("Assamese", R.drawable.assamese));
        motherTongueList.add(new MotherTongueItem("Marathi", R.drawable.marathi));
        motherTongueList.add(new MotherTongueItem("Kannada", R.drawable.kannada));
        motherTongueList.add(new MotherTongueItem("Malyalam", R.drawable.malyalam));
        motherTongueList.add(new MotherTongueItem("Urdu", R.drawable.urdu));
        motherTongueList.add(new MotherTongueItem("Gujarathi", R.drawable.gujarathi));
        motherTongueList.add(new MotherTongueItem("Odia", R.drawable.odia));

        motherTongueAdapter = new MotherTongueAdapter(motherTongueList, new MotherTongueAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Update UI to reflect selection (optional)
                motherTongueAdapter.setSelected(position);
            }
        });

        recyclerViewMotherTongue.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewMotherTongue.setAdapter(motherTongueAdapter);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedPosition = motherTongueAdapter.getSelectedPosition();
                if (selectedPosition != RecyclerView.NO_POSITION) {
                    MotherTongueItem selectedMotherTongue = motherTongueList.get(selectedPosition);
                    saveSelectedMotherTongueToDatabase(selectedMotherTongue.getMotherTongueName());
                    Intent intent = new Intent(MotherTongueActivity.this, JobTypeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MotherTongueActivity.this, "Please select a mother tongue", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveSelectedMotherTongueToDatabase(String motherTongue) {
        // Update the user's mother tongue in Firebase Realtime Database
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("motherTongue", motherTongue);

        databaseReference.updateChildren(userUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MotherTongueActivity.this, "User's mother tongue updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MotherTongueActivity.this, "Failed to update user's mother tongue: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
