package in.demo.myapplication;

import android.annotation.SuppressLint;
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

import in.demo.myapplication.Adapter.PersonalityAdapter;
import in.demo.myapplication.Model.PersonalityItem;

public class PersonalityActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPersonality;
    private PersonalityAdapter personalityAdapter;
    private List<PersonalityItem> personalityList;
    private FirebaseAuth auth;
    private Button buttonNext;
    private DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personality);

        recyclerViewPersonality = findViewById(R.id.recyclerViewPersonality);
        personalityList = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(auth.getCurrentUser().getUid());

        // Add sample personality options (you can populate this from your data source)
        personalityList.add(new PersonalityItem("Introvert", R.drawable.introvert));
        personalityList.add(new PersonalityItem("Extrovert", R.drawable.extrovert));
        personalityList.add(new PersonalityItem("Ambivert", R.drawable.ambivert));
        personalityList.add(new PersonalityItem("Omnivert", R.drawable.omnivert));

        personalityAdapter = new PersonalityAdapter(personalityList, new PersonalityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Do nothing here
            }
        });

        buttonNext = findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedPosition = personalityAdapter.getSelectedPosition();
                if (selectedPosition != RecyclerView.NO_POSITION) {
                    PersonalityItem selectedPersonality = personalityList.get(selectedPosition);
                    saveSelectedPersonalityToDatabase(selectedPersonality.getPersonalityName());
                } else {
                    Toast.makeText(PersonalityActivity.this,"Please select a personality type", Toast.LENGTH_SHORT).show();
                }
            }
        });
        recyclerViewPersonality.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewPersonality.setAdapter(personalityAdapter);
    }

    private void saveSelectedPersonalityToDatabase(String personality) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("personality", personality);

        usersRef.child(userId).updateChildren(userUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(PersonalityActivity.this, "User's personality updated successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PersonalityActivity.this, HeightActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PersonalityActivity.this, "Failed to update user's personality: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
