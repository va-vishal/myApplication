package in.demo.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.widget.Toast;

public class NameActivity extends AppCompatActivity {

    private EditText nameEditText;
    private Button buttonNext;
    private FirebaseAuth auth;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        nameEditText = findViewById(R.id.Name);
        buttonNext = findViewById(R.id.buttonNext);

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    nameEditText.setError("Please enter your name");
                    return;
                }
                saveNameToDatabase(name);
            }
        });
    }

    private void saveNameToDatabase(String name) {
        String userId = auth.getCurrentUser().getUid();
        DatabaseReference userRef = database.getReference("users").child(userId);

        userRef.child("name").setValue(name)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(NameActivity.this, "Name saved successfully", Toast.LENGTH_SHORT).show();
                        navigateToGenderActivity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NameActivity.this, "Failed to save name: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToGenderActivity() {
        Intent intent = new Intent(NameActivity.this, GenderActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
