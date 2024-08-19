package in.demo.myapplication.Profile;

import android.graphics.Rect;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import in.demo.myapplication.Adapter.UserAdapter2;
import in.demo.myapplication.Model.User;
import in.demo.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class RecentPassesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter2 userAdapter;
    private List<User> userList;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private Button backButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recent_passes);

        backButton=findViewById(R.id.backButtonr);

        if (backButton!=null){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });}

        recyclerView = findViewById(R.id.recyclerViewDisliked);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int spacing = 8;
                int pacing=-25;// Adjust this value to set the desired spacing
                int position = parent.getChildAdapterPosition(view); // Get the item's position
                int spanCount = 2; // The number of columns in your grid

                // Apply spacing to all items
                outRect.right = pacing;
                outRect.bottom = spacing;

                // Apply left spacing only if it's not the second item in a row
                if (position % spanCount == 0) {
                    // First item in the row
                    outRect.left = spacing;
                } else {
                    // Second item in the row
                    outRect.left = pacing;
                    outRect.right = spacing;
                }

                // Add top margin only for the first row
                if (position < spanCount) {
                    outRect.top =spacing;
                }
            }
        });
        userList = new ArrayList<>();
        userAdapter = new UserAdapter2(this, userList);
        recyclerView.setAdapter(userAdapter);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        getDislikedUsers();
    }

    private void getDislikedUsers() {
        if (currentUser != null) {
            // Reference to the current user's disliked list
            DatabaseReference dislikesRef = FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(currentUser.getUid())
                    .child("dislikedList");

            dislikesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userList.clear(); // Clear the existing list

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String dislikedUserId = snapshot.getKey(); // Get the user ID from the key of the snapshot

                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                                .child("users")
                                .child(dislikedUserId);

                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                User user = userSnapshot.getValue(User.class);
                                if (user != null) {
                                    userList.add(user); // Add user to the list
                                }
                                userAdapter.notifyDataSetChanged(); // Notify adapter of changes
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(RecentPassesActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(RecentPassesActivity.this, "Failed to get disliked users", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
