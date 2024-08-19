package in.demo.myapplication.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import in.demo.myapplication.FCM.FcmNotificationSender;
import in.demo.myapplication.HomeActivity;
import in.demo.myapplication.Message.messagingActivity;
import in.demo.myapplication.Model.Notification;
import in.demo.myapplication.Model.User;
import in.demo.myapplication.Profile.ProfileActivity;
import in.demo.myapplication.R;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final Context context;
    private final List<User> userList;
    private final Set<String> likedUsers;
    private Set<String> Notifications = null;
    private final Set<String> dislikedUsers;
    private double currentUserLat;
    private double currentUserLon;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        this.Notifications = Notifications;
        this.likedUsers = new HashSet<>();
        this.dislikedUsers = new HashSet<>();
        loadUserLists();
        loadCurrentUserLocation();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        if (user != null) {
            updateUI(holder, user);
            setupClickListeners(holder, user);
        }
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra("publisherid", user.getId());
            context.startActivity(intent);
            addNotification(user.getId(), "visited", "Your profile was visited", " has visited your profile");
            handleProfileVisit(user.getId());
            sendUserActionNotification(user, "visit");
        });

        holder.profileview.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra("publisherid", user.getId());
            context.startActivity(intent);
            handleProfileVisit(user.getId());
            addNotification(user.getId(), "visited", "Your profile was visited", "Someone has visited your profile");
            sendUserActionNotification(user, "visit");
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    private void handleProfileVisit(String userId) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference visitedUserVisitsRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userId)
                .child("visitsList");

        // Update the visited user's visits list
        visitedUserVisitsRef.child(currentUserId).setValue(true).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("ProfileVisit", "Visit recorded successfully");
            } else {
                Log.e("ProfileVisit", "Failed to record visit: " + task.getException().getMessage());
            }
        });
    }

    private void loadUserLists() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);

        userRef.child("likedList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likedUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    likedUsers.add(snapshot.getKey());
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                handleDatabaseError(databaseError);
            }
        });
        userRef.child("dislikedList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dislikedUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    dislikedUsers.add(snapshot.getKey());
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                handleDatabaseError(databaseError);
            }
        });
    }

    private void loadCurrentUserLocation() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);

        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                if (currentUser != null) {
                    currentUserLat = currentUser.getLatitude();
                    currentUserLon = currentUser.getLongitude();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                handleDatabaseError(databaseError);
            }
        });
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private void updateUI(UserViewHolder holder, User user) {
        handleUserStatus(holder, user);
        handleUserNameAndAge(holder, user);
        handleUserJobAndState(holder, user);
        handleUserImages(holder, user);
        handleUserDistance(holder, user);
        updateButtonStates(holder, user);
    }

    private void handleUserStatus(UserViewHolder holder, User user) {
        if (user.getStatus() != null && user.getStatus()) {
            holder.online.setVisibility(View.VISIBLE);
            holder.offline.setVisibility(View.GONE);
        } else {
            holder.offline.setVisibility(View.VISIBLE);
            holder.online.setVisibility(View.GONE);
        }
    }

    private void handleUserNameAndAge(UserViewHolder holder, User user) {
        holder.username.setText(user.getName() != null && (user.getHideName() == null || !user.getHideName()) ?
                user.getName() + "," : "");
        holder.age.setText(user.getAge() != null && (user.getHideAge() == null || !user.getHideAge()) ?
                String.valueOf(user.getAge()) : "Hidden");
    }

    private void handleUserJobAndState(UserViewHolder holder, User user) {
        holder.job.setText(user.getJobType() + ",");
        holder.state.setText(user.getState());
    }

    private void handleUserImages(UserViewHolder holder, User user) {
        // Clear any previous views
        holder.viewFlipper.removeAllViews();

        // Add images to the ViewFlipper based on availability
        addImageToFlipper(holder.viewFlipper, user.getImageurl());
        addImageToFlipper(holder.viewFlipper, user.getImageurl1());
        addImageToFlipper(holder.viewFlipper, user.getImageurl2());
        addImageToFlipper(holder.viewFlipper, user.getImageurl3());

        // Determine the number of views added
        int childCount = holder.viewFlipper.getChildCount();
        if (childCount == 0 || childCount == 1) {
            holder.viewFlipper.setAutoStart(false);
            holder.viewFlipper.stopFlipping();       // Start flipping
        } else {
            holder.viewFlipper.setFlipInterval(3000); // Set flipping interval
            holder.viewFlipper.setAutoStart(true);    // Enable auto start
            holder.viewFlipper.startFlipping();// Stop flipping if only one or no image
        }
    }

    private void addImageToFlipper(ViewFlipper viewFlipper, String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            ImageView imageView = new ImageView(viewFlipper.getContext());
            imageView.setLayoutParams(new ViewFlipper.LayoutParams(
                    ViewFlipper.LayoutParams.MATCH_PARENT,
                    ViewFlipper.LayoutParams.MATCH_PARENT
            ));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(viewFlipper.getContext()).load(imageUrl).into(imageView);
            viewFlipper.addView(imageView);
        }
    }

    private void handleUserDistance(UserViewHolder holder, User user) {
        if (user.getLatitude() != 0 && user.getLongitude() != 0 &&
                (user.getHideLocation() == null || !user.getHideLocation())) {
            double distance = calculateDistance(currentUserLat, currentUserLon, user.getLatitude(), user.getLongitude());
            holder.kmtext.setText(String.format("%.2f km from you", distance));
        } else {
            holder.kmtext.setText("Distance not available");
        }
    }

    private void updateButtonStates(UserViewHolder holder, User user) {
        holder.likeButton.setImageResource(likedUsers.contains(user.getId()) ? R.drawable.liked : R.drawable.like);
        holder.dislikeButton.setImageResource(dislikedUsers.contains(user.getId()) ? R.drawable.disliked : R.drawable.dislike);
    }

    private void setupClickListeners(UserViewHolder holder, User user) {
        holder.likeButton.setOnClickListener(v -> {
            handleLikeClick(user);

        });

        holder.dislikeButton.setOnClickListener(v -> handleDislikeClick(user));
    }

    private void handleLikeClick(User user) {
        String userId = user.getId();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (likedUsers.contains(userId)) {
            likedUsers.remove(userId);
            removeFromLikedList(userId);
            removeMatchFromFirebase(user);
        } else if (dislikedUsers.contains(userId)) {
            Toast.makeText(context, "Undo dislike before liking", Toast.LENGTH_SHORT).show();
            return;
        } else {
            likedUsers.add(userId);
            addToLikedList(userId);
            sendUserActionNotification(user, "like");
            addNotification(userId, "liked", "You have a new like", "Someone has liked your profile");
            checkForMatch(user, currentUserId);
        }
        notifyDataSetChanged();
    }
    private void addNotification(String userId, String notificationType, String title, String body) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e("Notification", "Current user is null");
            return;
        }
        String currentUserId = currentUser.getUid();
        // Reference to the current user
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);
        // Fetch the current user's details to get the username
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                if (currentUser == null) {
                    Log.e("Notification", "Current user data is null");
                    return;
                }
                String currentUsername = currentUser.getName(); // Assuming you have a getUsername() method

                // Create the notification message with the user's name based on the type
                String personalizedBody;
                switch (notificationType) {
                    case "liked":
                        personalizedBody = String.format("%s has liked your profile", currentUsername);
                        break;
                    case "visited":
                        personalizedBody = String.format("%s has visited your profile", currentUsername);
                        break;
                    case "match":
                        personalizedBody = String.format("You and %s have a new match!", currentUsername);
                        break;
                    default:
                        personalizedBody = body; // Use the default body if type doesn't match
                        break;
                }
                // Create a new Notification object
                Notification notification = new Notification();
                notification.setOtheruserid(currentUserId);
                notification.setCurrentuserid(userId);
                notification.setTitle(title);
                notification.setBody(personalizedBody);
                notification.setNotificationType(notificationType);
                notification.setTimestamp(System.currentTimeMillis());
                // Reference to the target user's notifications
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users")
                        .child(userId)
                        .child("Notifications");
                // Push the notification object to Firebase
                reference.push().setValue(notification).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Notification", "Notification recorded successfully");
                    } else {
                        Log.e("Notification", "Failed to record notification", task.getException());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Notification", "Failed to fetch current user details", databaseError.toException());
            }
        });
    }
    private void checkForMatch(User user, String currentUserId) {
        // Reference to the "likedList" of the user being checked
        DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(user.getId())
                .child("likedList");

        likesRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    sendUserActionNotification(user, "match");
                    addNotification(user.getId(), "match", "It's a match!", "You have found a match");
                    // Both users have liked each other, so it's a match
                    LayoutInflater inflater = LayoutInflater.from(context);
                    View dialogView = inflater.inflate(R.layout.dialog_custom_match, null);

                    // Find the RoundedImageView within the inflated view
                    RoundedImageView currentUserImage = dialogView.findViewById(R.id.currentuserimage);
                    RoundedImageView otherUserImage = dialogView.findViewById(R.id.otheruserimage);
                    Button Button = dialogView.findViewById(R.id.startchat);
                    TextView keepsearching = dialogView.findViewById(R.id.keepsearching);
                    TextView textView = dialogView.findViewById(R.id.textView);

                    DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference()
                            .child("users")
                            .child(currentUserId)
                            .child("imageurl");

                    currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String currentUserImageUrl = snapshot.getValue(String.class);
                                if (currentUserImageUrl != null) {
                                    // Load the current user image using Glide
                                    Glide.with(context)
                                            .load(currentUserImageUrl)
                                            .into(currentUserImage);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle potential errors
                            Log.e("ImageLoad", "Database error: " + error.getMessage());
                        }
                    });
                    DatabaseReference otherUserRef = FirebaseDatabase.getInstance().getReference()
                            .child("users")
                            .child(user.getId())
                            .child("imageurl");

                    otherUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String otherUserImageUrl = snapshot.getValue(String.class);
                                if (otherUserImageUrl != null) {
                                    // Load the other user image using Glide
                                    Glide.with(context)
                                            .load(otherUserImageUrl)
                                            .into(otherUserImage);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle potential errors
                            Log.e("ImageLoad", "Database error: " + error.getMessage());
                        }
                    });
                    if (context instanceof Activity && !((Activity) context).isFinishing()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setView(dialogView);
                        AlertDialog dialog = builder.create();
                        if (dialog.getWindow() != null) {
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        }
                        dialog.show();

                        keepsearching.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, HomeActivity.class);
                                context.startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                        Button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, messagingActivity.class);
                                intent.putExtra("userid", user.getId());
                                context.startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                    } else {
                        Log.e("UserAdapter", "Activity is finishing or context is not valid, cannot show dialog.");
                    }

                    saveMatchesToFirebase(user.getId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle potential errors
                Log.e("MatchCheck", "Database error: " + error.getMessage());
            }
        });
    }
    private void addToLikedList(String userId) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(currentUserId)
                .child("likedList")
                .child(userId).setValue(true);
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(userId)
                .child("likesList")
                .child(currentUserId).setValue(true);
    }
    private void removeFromLikedList(String userId) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(currentUserId)
                .child("likedList")
                .child(userId).removeValue();
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(userId)
                .child("likesList")
                .child(currentUserId).removeValue();
    }
    private void saveMatchesToFirebase(String user) {
        String userId = user;
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(currentUserId)
                .child("matches");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userId)
                .child("matches");

        currentUserRef.child(userId).setValue(true);
        userRef.child(currentUserId).setValue(true);
    }

    private void removeMatchFromFirebase(User user) {
        String userId = user.getId();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(currentUserId)
                .child("matches");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userId)
                .child("matches");

        currentUserRef.child(userId).removeValue();
        userRef.child(currentUserId).removeValue();
    }

    private void handleDislikeClick(User user) {
        String userId = user.getId();

        if (dislikedUsers.contains(userId)) {
            dislikedUsers.remove(userId);
            removeFromDislikedList(userId);
        } else if (likedUsers.contains(userId)) {
            Toast.makeText(context, "Undo like before disliking", Toast.LENGTH_SHORT).show();
            return;
        } else {
            dislikedUsers.add(userId);
            addToDislikedList(userId);
        }
        notifyDataSetChanged();
    }

    private void addToDislikedList(String userId) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(currentUserId)
                .child("dislikedList")
                .child(userId).setValue(true);
    }

    private void removeFromDislikedList(String userId) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(currentUserId)
                .child("dislikedList")
                .child(userId).removeValue();
    }

    private void handleDatabaseError(DatabaseError databaseError) {
        Toast.makeText(context, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
    }
    private void sendUserActionNotification(User user, String actionType) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e("Notification", "Current user is null");
            return;
        }

        String currentUserId = currentUser.getUid();

        // Reference to the current user
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);

        // Fetch the current user's details to get the username
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                if (currentUser == null) {
                    Log.e("Notification", "Current user data is null");
                    return;
                }
                String currentUserName = currentUser.getName(); // Assuming you have a getName() method

                // Create the notification message with the user's name based on the type
                String notificationBody = generateNotificationMessage(currentUserName, actionType);

                String userToken = user.getFcmToken(); // Assuming user has an FCM token
                if (userToken != null) {
                    Log.e("FCM Token", userToken);
                    FcmNotificationSender fcmNotificationSender = new FcmNotificationSender(
                            userToken,
                            "Notification",
                            notificationBody,
                            context
                    );
                    Log.e("Notification", "Working");
                    fcmNotificationSender.sendNotification();
                } else {
                    Log.e("Notification", "User FCM token is null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Notification", "Failed to retrieve user name", databaseError.toException());
            }
        });
    }
    private String generateNotificationMessage(String userName, String actionType) {
        switch (actionType) {
            case "like":
                return userName + " has liked your profile";
            case "visit":
                return userName + " has visited your profile";
            case "match":
                return "You and " + userName + " have a new match!";
            default:
                return userName + " performed an action";
        }
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        private final ImageView image1;
        private final ImageView image2;
        private final ImageView image3,profileview;
        private final ImageView image4;
        private final ViewFlipper viewFlipper;
        private final TextView username;
        private final TextView age;
        private final TextView job;
        private final TextView state;
        private final TextView kmtext;
        private final ImageView likeButton;
        private final ImageView dislikeButton;
        private final ImageView online;
        private final ImageView offline;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            image1 = itemView.findViewById(R.id.image11);
            image2 = itemView.findViewById(R.id.image22);
            image3 = itemView.findViewById(R.id.image33);
            image4 = itemView.findViewById(R.id.image44);
            viewFlipper = itemView.findViewById(R.id.viewFlipper);
            username = itemView.findViewById(R.id.username);
            age = itemView.findViewById(R.id.age);
            profileview = itemView.findViewById(R.id.profileview);
            job = itemView.findViewById(R.id.job);
            state = itemView.findViewById(R.id.state);
            kmtext = itemView.findViewById(R.id.kmtext);
            likeButton = itemView.findViewById(R.id.like_button);
            dislikeButton = itemView.findViewById(R.id.dislike_button);
            online = itemView.findViewById(R.id.online_status);
            offline = itemView.findViewById(R.id.offline_status);
        }
    }
}
