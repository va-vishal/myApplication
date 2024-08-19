package in.demo.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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

import in.demo.myapplication.Adapter.HeightAdapter;
import in.demo.myapplication.Model.HeightItem;

public class HeightActivity extends AppCompatActivity implements HeightAdapter.OnHeightClickListener {

    private RecyclerView recyclerViewHeight;
    private HeightAdapter heightAdapter;
    private List<HeightItem> heightList;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private Button buttonNext;
    private String selectedHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_height);

        recyclerViewHeight = findViewById(R.id.recycler_view_height);
        buttonNext = findViewById(R.id.buttonNext);

        recyclerViewHeight.setLayoutManager(new LinearLayoutManager(this));

        heightList = new ArrayList<>();
        String[] heights = getResources().getStringArray(R.array.height_options);

        for (String height : heights) {
            heightList.add(new HeightItem(height));
        }

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(auth.getCurrentUser().getUid());

        heightAdapter = new HeightAdapter(heightList, this);
        recyclerViewHeight.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerViewHeight.setAdapter(heightAdapter);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedHeight != null) {
                    saveSelectedHeightToDatabase(selectedHeight);
                    Intent intent = new Intent(HeightActivity.this, MotherTongueActivity.class); // Replace NextActivity with your next activity
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(HeightActivity.this, "Please select a height", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onHeightClick(String height) {
        selectedHeight = height;
        Toast.makeText(this, "Selected Height: " + height, Toast.LENGTH_SHORT).show();
    }

    private void saveSelectedHeightToDatabase(String height) {
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("height", height);

        databaseReference.updateChildren(userUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(HeightActivity.this, "User's height updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HeightActivity.this, "Failed to update user's height: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
