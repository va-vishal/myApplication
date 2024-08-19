package in.demo.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import in.demo.myapplication.Authentication.LoginActivity1;
import in.demo.myapplication.FCM.FcmNotificationSender;
import in.demo.myapplication.Fragments.HomeFragment;
import in.demo.myapplication.Fragments.MatchedFragment;
import in.demo.myapplication.Fragments.NotificationFragment;
import in.demo.myapplication.Fragments.ProfileFragment;
import in.demo.myapplication.Model.User;
import in.demo.myapplication.Profile.PreferenceActivity;
import in.demo.myapplication.Profile.ProfileActivity;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private BottomNavigationView bottomNavigationView;
    private Fragment selectedFragment = null;
    private FirebaseAuth mAuth;
    private DatabaseReference Rootref;
    private CircleImageView profileimage;
    private ImageView preferences,message;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirebaseApp.initializeApp(this);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();

                        // Log and save the token
                        Log.d("FCM", "FCM Token: " + token);
                        saveTokenToDatabase(token);
                    }
                });



        profileimage=findViewById(R.id.profileimage);
        preferences=findViewById(R.id.preferences);
        message=findViewById(R.id.messages);

        preferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(HomeActivity.this, PreferenceActivity.class);
                startActivity(intent);

            }
        });
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeActivity.this, MessageActivity.class);
                startActivity(intent);
              /*  FcmNotificationSender fcmNotificationSender = new FcmNotificationSender(
                        "cxXAZcUaTiO75zDRoAPDqD:APA91bH9c8OONEyjHVjTQ8kODlCUsJw-sHiJl0HIryPnfKswdsf8yfDZYnJpVc2ir0OzUt3YoymI33PPMsJvOxobENzhXtFb6vB1l_xDGMFkMlA0WRk6AYpDPV1SRR_Fno-8ha9D-eVj", "You have a new like", "notificationBody", HomeActivity.this);

                fcmNotificationSender.sendNotification();**/

            }
        });
        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            }
        });
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid(); // Get the current user's ID
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            Log.e("ProfileFragment", "User details fetched ");
                            Picasso.get().load(user.getImageurl()).into(profileimage);
                        } else {
                            Log.e("ProfileFragment", "User object is null");
                        }
                    } else {
                        Log.e("ProfileFragment", "No data available for the user");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("ProfileFragment", "DatabaseError: " + databaseError.getMessage());
                }
            });
        } else {
            Toast.makeText(this,"User not authenticated", Toast.LENGTH_SHORT).show();
        }


        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        Rootref = FirebaseDatabase.getInstance().getReference();

        if (firebaseUser == null) {
            Log.d(TAG, "User is null, redirecting to MainActivity");
            Intent intent = new Intent(HomeActivity.this, LoginActivity1.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        } else {
            Log.d(TAG, "User is authenticated: " + firebaseUser.getUid());
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        Bundle intent = getIntent().getExtras();
        if (intent != null) {
            String publisher = intent.getString("publisherid");
            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("profileid", publisher);
            editor.apply();
            Log.d(TAG, "Navigating to ProfileFragment with publisher ID: " + publisher);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
        } else {
            Log.d(TAG, "Navigating to HomeFragment");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (item.getItemId() == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (item.getItemId() == R.id.nav_matched) {
                selectedFragment = new MatchedFragment();
            } else if (item.getItemId() == R.id.nav_noti) {
                selectedFragment = new NotificationFragment();
            } else if (item.getItemId() == R.id.nav_profile) {
                SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                editor.apply();
                selectedFragment = new ProfileFragment();
            } else {
                return false;
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                return true;
            }
            return false;
        }
    };
private void saveTokenToDatabase(String token) {
    // Save the token to your database
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    if (currentUser != null) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users")
                .child(currentUser.getUid());
        databaseReference.child("fcmToken").setValue(token);
    }
}
}
