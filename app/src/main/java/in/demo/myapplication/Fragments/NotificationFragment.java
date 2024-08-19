package in.demo.myapplication.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import in.demo.myapplication.Adapter.NotificationAdapter;
import in.demo.myapplication.HomeActivity;
import in.demo.myapplication.Model.Notification;
import in.demo.myapplication.Model.User;
import in.demo.myapplication.R;

public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;
    private String currentUserImageUrl;
    private ImageView deletenotification;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerView = view.findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(getContext(), notificationList, currentUserImageUrl);
        recyclerView.setAdapter(notificationAdapter);
        
        deletenotification=view.findViewById(R.id.deletenotification);
        
        deletenotification.setOnClickListener(v -> {deletenotificationaofCurrentUser();});

        readNotifications();

        // Handle back press
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                    getParentFragmentManager().popBackStack();
                } else {
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            currentUserImageUrl = user.getImageurl();
                        }

                        // Set up the adapter with the current user's image URL
                        notificationAdapter = new NotificationAdapter(getContext(), notificationList, currentUserImageUrl);
                        recyclerView.setAdapter(notificationAdapter);
                        readNotifications();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle error
                    }
                });
        return view;
    }

    private void deletenotificationaofCurrentUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e("Notification", "Current user is null");
            return;
        }
        String currentUserId = currentUser.getUid();

        // Reference to the current user's notifications
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference("users")
                .child(currentUserId)
                .child("Notifications");

        notificationsRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Notification", "All notifications deleted successfully");
                Toast.makeText(getContext(), "Notifications Deleted Sucessfully", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("Notification", "Failed to delete notifications", task.getException());
            }
        });
    }


    private void readNotifications() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users")
                    .child(firebaseUser.getUid())
                    .child("Notifications");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    notificationList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Notification notification = snapshot.getValue(Notification.class);
                        if (notification != null) {
                            notificationList.add(notification);
                        }
                    }
                    Collections.reverse(notificationList);
                    notificationAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }
}
