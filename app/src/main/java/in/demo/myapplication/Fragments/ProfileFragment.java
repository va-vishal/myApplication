package in.demo.myapplication.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import in.demo.myapplication.Authentication.LoginActivity1;
import in.demo.myapplication.HomeActivity;
import in.demo.myapplication.Model.User;
import in.demo.myapplication.Profile.AboutActivity;
import in.demo.myapplication.Profile.EditImageActivity;
import in.demo.myapplication.Profile.HelpCenterActivity;
import in.demo.myapplication.Profile.LikeActivity;
import in.demo.myapplication.Profile.PreferenceActivity;
import in.demo.myapplication.Profile.ProfileActivity;
import in.demo.myapplication.Profile.RecentPassesActivity;
import in.demo.myapplication.Profile.SafetyTipsActivity;
import in.demo.myapplication.Profile.VisitsActivity;
import in.demo.myapplication.R;

public class ProfileFragment extends Fragment {

    private CircleImageView profileImage;
    private TextView name, noOfLikes, noOfVisits;
    private Button getPremiumButton, logout;
    private CardView editImageCard, preferencesCard, matchedProfilesCard, recentPassedCard, aboutCard, helpCenterCard, safetyTipsCard, logoutCard;
    private LinearLayout like, visit;
    private FirebaseUser currentUser;
    private List<User> userList;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileImage = view.findViewById(R.id.profileimagep);
        name = view.findViewById(R.id.name); // Corrected the typo
        noOfLikes = view.findViewById(R.id.nooflike);
        noOfVisits = view.findViewById(R.id.noofvisits);
        getPremiumButton = view.findViewById(R.id.getpremium);
        logout = view.findViewById(R.id.logout);

        like = view.findViewById(R.id.like);
        visit = view.findViewById(R.id.visit);
        editImageCard = view.findViewById(R.id.editImageCard);
        preferencesCard = view.findViewById(R.id.preferencesCard);
        matchedProfilesCard = view.findViewById(R.id.matchedProfilesCard);
        recentPassedCard = view.findViewById(R.id.recentPassedCard);
        aboutCard = view.findViewById(R.id.aboutCard);
        helpCenterCard = view.findViewById(R.id.helpCenterCard);
        safetyTipsCard = view.findViewById(R.id.safetyTipsCard);
        logoutCard = view.findViewById(R.id.logoutCard);

        sharedPreferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        userList = new ArrayList<>();

        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(intent);
        });

        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            fetchUserProfile();
        } else {
            Toast.makeText(getActivity(), "User not authenticated", Toast.LENGTH_SHORT).show();
            if (getActivity() != null) {
                getActivity().finish();
            }
        }

        setupButtonListeners();
        setupOnBackPressedCallback();
    }

    private void fetchUserProfile() {
        String userId = currentUser.getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        Log.e("ProfileFragment", "User details fetched ");
                        name.setText(user.getName());
                        Picasso.get().load(user.getImageurl()).into(profileImage);

                        // Fetch and count likesList and visitsList
                        Map<String, Boolean> likesMap = user.getLikesList();
                        if (likesMap != null) {
                            noOfLikes.setText(String.valueOf(likesMap.size()));
                        } else {
                            noOfLikes.setText("0");
                        }

                        Map<String, Boolean> visitsMap = user.getVisitsList();
                        if (visitsMap != null) {
                            noOfVisits.setText(String.valueOf(visitsMap.size()));
                        } else {
                            noOfVisits.setText("0");
                        }
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
    }


    private void setupButtonListeners() {
        getPremiumButton.setOnClickListener(v -> {
            // Handle get premium button click
        });

        logout.setOnClickListener(v -> logoutUser());
        logoutCard.setOnClickListener(v -> logoutUser());

        like.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LikeActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        visit.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), VisitsActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        noOfLikes.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LikeActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        noOfVisits.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), VisitsActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        editImageCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditImageActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        preferencesCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PreferenceActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        matchedProfilesCard.setOnClickListener(v -> {
            MatchedFragment matchedFragment = new MatchedFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            transaction.replace(R.id.fragment_container, matchedFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        recentPassedCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RecentPassesActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        aboutCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AboutActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        helpCenterCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), HelpCenterActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        safetyTipsCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SafetyTipsActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), LoginActivity1.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    private void setupOnBackPressedCallback() {
        if (getActivity() != null) {
            OnBackPressedCallback callback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            };
            requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);
        }
    }
}
