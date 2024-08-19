package in.demo.myapplication.Fragments;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import in.demo.myapplication.Adapter.PreferredGenderCallback;
import in.demo.myapplication.Adapter.UserAdapter;
import in.demo.myapplication.Model.User;
import in.demo.myapplication.R;

public class HomeFragment extends Fragment {

    private RecyclerView swipeView;
    private UserAdapter userAdapter;
    private List<User> mUsers;
    private ProgressBar progressBar;
    private int minAge = 18;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        swipeView = view.findViewById(R.id.swipe_view);
        progressBar = view.findViewById(R.id.progress_circular);

        swipeView.setHasFixedSize(true);


        swipeView.setLayoutManager(new LinearLayoutManager(getContext()));


        mUsers = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), mUsers);
        swipeView.setAdapter(userAdapter);

        setUpItemTouchHelper();

        readUsers();

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                    getParentFragmentManager().popBackStack();
                } else {
                    requireActivity().finishAffinity(); // Close the app
                }
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        return view;

    }
    private void setUpItemTouchHelper() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                mUsers.remove(position);
                userAdapter.notifyItemRemoved(position);
                if (mUsers.isEmpty()) {
                    swipeView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;
                float alpha = 1.0f - (Math.abs(dX) / recyclerView.getWidth());
                itemView.setAlpha(alpha);
                itemView.setTranslationX(dX);
            }
        };

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(swipeView);
    }

    private void readUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        getCurrentUserPreferredGender(new PreferredGenderCallback() {
            @Override
            public void onPreferredGenderRetrieved(String preferredGender) {
                Query query;
                if (preferredGender != null) {
                    query = reference.orderByChild("gender").equalTo(preferredGender);
                } else {
                    query = reference;
                }

                query.addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mUsers.clear();
                        // Retrieve age range asynchronously
                        getCurrentUserAgeRange(ageRange -> {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                User user = snapshot.getValue(User.class);
                                boolean isProfileHidden = user.getHideProfile() != null ? user.getHideProfile() : false;
                                if (user != null) {
                                    try {
                                        if (!isProfileHidden ) {
                                            mUsers.add(user);
                                            Log.d("HomeFragment", "User added: " + user.getName());
                                        }
                                    } catch (NumberFormatException e) {
                                        Log.e("HomeFragment", "Invalid age format for user: " + user.getName());
                                    }
                                }
                            }
                            userAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("HomeFragment", "Database error: " + error.getMessage());
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private void getCurrentUserAgeRange(OnAgeRangeRetrievedCallback callback) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("users").child(currentUserId).child("ageRange").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                Object ageRangeObject = task.getResult().getValue();
                int ageRange = 18; // Default value in case of an error

                if (ageRangeObject instanceof Long) {
                    ageRange = ((Long) ageRangeObject).intValue();
                } else if (ageRangeObject instanceof String) {
                    try {
                        ageRange = Integer.parseInt((String) ageRangeObject);
                    } catch (NumberFormatException e) {
                        Log.e("HomeFragment", "Error parsing ageRange: " + e.getMessage());
                    }
                } else {
                    Log.e("HomeFragment", "Unexpected type for ageRange: " + (ageRangeObject != null ? ageRangeObject.getClass().getName() : "null"));
                }

                callback.onAgeRangeRetrieved(ageRange);
            } else {
                Log.e("HomeFragment", "AgeRange not found or error retrieving data");
                callback.onAgeRangeRetrieved(0); // Provide a default value or handle as needed
            }
        }).addOnFailureListener(e -> {
            Log.e("HomeFragment", "Failed to retrieve ageRange: " + e.getMessage());
            callback.onAgeRangeRetrieved(0); // Provide a default value or handle as needed
        });
    }

    // Define the callback interface
    public interface OnAgeRangeRetrievedCallback {
        void onAgeRangeRetrieved(int ageRange);
    }




    // Example method to get the current user's preferred gender
    private void getCurrentUserPreferredGender(final PreferredGenderCallback callback) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.child("prefGender").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String preferredGender = dataSnapshot.getValue(String.class);
                    callback.onPreferredGenderRetrieved(preferredGender);
                } else {
                    callback.onPreferredGenderRetrieved(null); // Handle the case where preferred gender is not set
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HomeFragment", "Failed to get preferred gender: " + error.getMessage());
                callback.onPreferredGenderRetrieved(null); // Handle the error case
            }
        });
    }

}
