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

import in.demo.myapplication.Adapter.JobTypeAdapter;
import in.demo.myapplication.Model.JobTypeItem;

public class JobTypeActivity extends AppCompatActivity {

    private RecyclerView recyclerViewJobType;
    private JobTypeAdapter jobTypeAdapter;
    private List<JobTypeItem> jobTypeList;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private Button buttonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_type);

        recyclerViewJobType = findViewById(R.id.recyclerViewJobType);
        buttonNext = findViewById(R.id.buttonNext);

        jobTypeList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(auth.getCurrentUser().getUid());

        // Add sample job type options (you can populate this from your data source)
        jobTypeList.add(new JobTypeItem("Student", R.drawable.student));
        jobTypeList.add(new JobTypeItem("Engineer", R.drawable.engineer));
        jobTypeList.add(new JobTypeItem("Doctor", R.drawable.doctor));
        jobTypeList.add(new JobTypeItem("Teacher", R.drawable.teacher));
        jobTypeList.add(new JobTypeItem("Artist", R.drawable.guitar));
        jobTypeList.add(new JobTypeItem("Manager", R.drawable.manager));
        jobTypeList.add(new JobTypeItem("Chef", R.drawable.cooking));
        jobTypeList.add(new JobTypeItem("Driver", R.drawable.driver));
        jobTypeList.add(new JobTypeItem("Pilot", R.drawable.pilot));
        jobTypeList.add(new JobTypeItem("Writer", R.drawable.writer));

        jobTypeAdapter = new JobTypeAdapter(jobTypeList, new JobTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Update UI to reflect selection (optional)
                jobTypeAdapter.setSelected(position);
            }
        });

        recyclerViewJobType.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewJobType.setAdapter(jobTypeAdapter);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedPosition = jobTypeAdapter.getSelectedPosition();
                if (selectedPosition != RecyclerView.NO_POSITION) {
                    JobTypeItem selectedJobType = jobTypeList.get(selectedPosition);
                    saveSelectedJobTypeToDatabase(selectedJobType.getJobTypeName());
                    // Replace with the next activity class
                    Intent intent = new Intent(JobTypeActivity.this, ReligionActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(JobTypeActivity.this, "Please select a job type", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveSelectedJobTypeToDatabase(String jobType) {
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("jobType", jobType);
        databaseReference.updateChildren(userUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Job type updated successfully
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to update job type
                    }
                });
    }
}
