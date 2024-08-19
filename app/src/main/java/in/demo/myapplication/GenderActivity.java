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

import in.demo.myapplication.Adapter.GenderAdapter;
import in.demo.myapplication.Model.GenderItem;

public class GenderActivity extends AppCompatActivity {

    private RecyclerView recyclerViewGender;
    private GenderAdapter genderAdapter;
    private List<GenderItem> genderList;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private Button buttonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender);

        recyclerViewGender = findViewById(R.id.recyclerViewGender);
        buttonNext = findViewById(R.id.buttonNext);

        genderList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(auth.getCurrentUser().getUid());

        // Add sample gender options (you can populate this from your data source)
        genderList.add(new GenderItem("Male", R.drawable.man));
        genderList.add(new GenderItem("Female", R.drawable.woman));
        genderList.add(new GenderItem("Gay", R.drawable.gay));
        genderList.add(new GenderItem("Lesbian", R.drawable.lesbian));

        genderAdapter = new GenderAdapter(genderList, new GenderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Update UI to reflect selection (optional)
                genderAdapter.setSelected(position);
            }
        });

        recyclerViewGender.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewGender.setAdapter(genderAdapter);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedPosition = genderAdapter.getSelectedPosition();
                if (selectedPosition != RecyclerView.NO_POSITION) {
                    GenderItem selectedGender = genderList.get(selectedPosition);
                    saveSelectedGenderToDatabase(selectedGender.getGenderName());
                    Intent intent = new Intent(GenderActivity.this, PersonalityActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(GenderActivity.this, "Please select a gender", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveSelectedGenderToDatabase(String gender) {
        // Update the user's gender in Firebase Realtime Database
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("gender", gender);

        databaseReference.updateChildren(userUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(GenderActivity.this, "User's gender updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(GenderActivity.this, "Failed to update user's gender: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
