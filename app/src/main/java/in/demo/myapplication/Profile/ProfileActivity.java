package in.demo.myapplication.Profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import in.demo.myapplication.Message.messagingActivity;
import in.demo.myapplication.Model.User;
import in.demo.myapplication.R;

public class ProfileActivity extends AppCompatActivity {

    // Views
    private TextView prefText, dobText, maritalText, descText, name, age, locationArea, locationState, jobType, educationText, genderText, motherTongueText;
    private ImageView prefImage, locationImage,back_image, dobImage, maritalImage, descImage, jobImage, educationImage, genderImage, motherTongueImage;
    private FirebaseUser firebaseUser;
    private CardView  cardImage4,cardImage1,cardImage2,cardImage3,backCard;
    private String profileid;
    private RoundedImageView image1,image2,image3,image4;
    private Button message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("publisherid")) {
            profileid = intent.getStringExtra("publisherid");
        } else {
            // Check if firebaseUser is not null
            if (firebaseUser != null) {
                profileid = firebaseUser.getUid();
            }
        }
        initializeViews();
        fetchUserDetails();
    }
    // Initialize views
    private void initializeViews() {
        message=findViewById(R.id.messageButton);
        prefText=findViewById(R.id.preftext);
        prefImage=findViewById(R.id.prefimage);
        cardImage1=findViewById(R.id.card);
        cardImage2=findViewById(R.id.cardImage2);
        cardImage3=findViewById(R.id.cardImage3);
        cardImage4=findViewById(R.id.cardImage4);
        backCard=findViewById(R.id.backCard);
        back_image=findViewById(R.id.back_image);
        image1=findViewById(R.id.image1);
        image2=findViewById(R.id.image2);
        image3=findViewById(R.id.image3);
        image4=findViewById(R.id.image4);
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        locationArea = findViewById(R.id.locationarea);
        locationState = findViewById(R.id.locationstate);
        jobType = findViewById(R.id.jobType);
        educationText = findViewById(R.id.educationtext);
        genderText = findViewById(R.id.gendertext);
        motherTongueText = findViewById(R.id.mother_tonguetext);
        dobText = findViewById(R.id.dobtext);
        maritalText = findViewById(R.id.maritaltext);
        descText = findViewById(R.id.desctext);
        locationImage = findViewById(R.id.locationimage);
        jobImage = findViewById(R.id.jobImage);
        educationImage = findViewById(R.id.educationimage);
        genderImage = findViewById(R.id.genderImage);
        motherTongueImage = findViewById(R.id.mother_tongueImage);
        dobImage = findViewById(R.id.dobimage);
        maritalImage = findViewById(R.id.maritalimage);
        descImage = findViewById(R.id.descImage);
        cardImage1.setVisibility(View.GONE);
        cardImage2.setVisibility(View.GONE);
        cardImage3.setVisibility(View.GONE);
        cardImage4.setVisibility(View.GONE);
        image2.setVisibility(View.GONE);
        image3.setVisibility(View.GONE);
        image4.setVisibility(View.GONE);

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageUrl(image1, new ImageUrlCallback() {
                    @Override
                    public void onImageUrlRetrieved(String imageUrl) {
                        showFullScreenImage(image1, imageUrl);
                    }
                });
            }
        });
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageUrl(image2, new ImageUrlCallback() {
                    @Override
                    public void onImageUrlRetrieved(String imageUrl) {
                        showFullScreenImage(image2, imageUrl);
                    }
                });
            }
        });
        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageUrl(image3, new ImageUrlCallback() {
                    @Override
                    public void onImageUrlRetrieved(String imageUrl) {
                        showFullScreenImage(image3, imageUrl);
                    }
                });
            }
        });
        image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageUrl(image4, new ImageUrlCallback() {
                    @Override
                    public void onImageUrlRetrieved(String imageUrl) {
                        showFullScreenImage(image4, imageUrl);
                    }
                });
            }
        });
        if (locationImage!=null) {
            Glide.with(ProfileActivity.this).load(R.drawable.location).into(locationImage);
        }
        backCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ProfileActivity.this, messagingActivity.class);
                intent.putExtra("userid", profileid);
                startActivity(intent);
            }
        });
    }
    private void getImageUrl(ImageView imageView, ImageUrlCallback callback) {
        if (profileid != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(profileid);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User userProfile = dataSnapshot.getValue(User.class);
                        if (userProfile != null) {
                            String imageUrl = null;
                            if (imageView == image1) {
                                imageUrl = userProfile.getImageurl();}
                            else if (imageView == image2) {
                                imageUrl = userProfile.getImageurl1();
                            } else if (imageView == image3) {
                                imageUrl = userProfile.getImageurl2();
                            } else if (imageView == image4) {
                                imageUrl = userProfile.getImageurl3();
                            } else {
                                Toast.makeText(ProfileActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                            }
                            callback.onImageUrlRetrieved(imageUrl);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle possible errors
                }
            });
        }
    }
    public interface ImageUrlCallback {
        void onImageUrlRetrieved(String imageUrl);
    }
    private void fetchUserDetails() {
        if (profileid != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(profileid);

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User userProfile = dataSnapshot.getValue(User.class);
                        if (userProfile != null) {

                            if (name != null) name.setText(userProfile.getName());
                            if (genderText != null) genderText.setText(userProfile.getGender());
                            if (age != null) age.setText(userProfile.getAge());
                            if (dobText != null) dobText.setText(userProfile.getDob());
                            if (educationText != null)
                                educationText.setText(userProfile.getEducationLevel());
                            if (jobType != null) jobType.setText(userProfile.getJobType());
                            if (motherTongueText != null)
                                motherTongueText.setText(userProfile.getMotherTongue());
                            if (maritalText != null)
                                maritalText.setText(userProfile.getMaritalStatus());
                            if (descText != null) descText.setText(userProfile.getDescription());
                            if (locationArea != null) locationArea.setText(userProfile.getArea());
                            if (locationState != null)
                                locationState.setText(userProfile.getState());
                            if (prefText != null) prefText.setText(userProfile.getPrefGender());

                            loadImage(userProfile);
                            loadImageIntoView(cardImage1,image1, userProfile.imageurl);
                            loadImageIntoView(cardImage2,image2, userProfile.imageurl1);
                            loadImageIntoView(cardImage3,image3, userProfile.imageurl2);
                            loadImageIntoView(cardImage4,image4, userProfile.imageurl3);

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle possible errors
                }
            });
        }
    }
    private void loadImageIntoView(CardView cardImage,RoundedImageView imageView, String imageUrl) {
        if (imageUrl != null) {
            if (!isFinishing() && !isDestroyed()) {
                cardImage.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                Glide.with(ProfileActivity.this)
                        .load(imageUrl)
                        .into(imageView);
            }
        } else {
            imageView.setVisibility(View.GONE);
            cardImage.setVisibility(View.GONE);
        }
    }
    private void loadImage(User userProfile) {

        loadImageForMaritalStatus(userProfile.getMaritalStatus());
        loadImageForJob(userProfile.getJobType());
        loadImageForEducation(userProfile.getEducationLevel());
        loadImageForGender(userProfile.getGender());
        loadImageForMotherTongue(userProfile.getMotherTongue());
        loadImageForPrefGender(userProfile.getPrefGender());
        loadImageForCondition(dobImage, userProfile.getDob(), R.drawable.calendar_month);
        loadImageForCondition(descImage, userProfile.getDescription(), R.drawable.search);
    }
    private void loadImageForCondition(ImageView imageView, String condition, int drawableResId) {
        if (condition != null && imageView != null) {
            if (!isFinishing() && !isDestroyed()) {
            Glide.with(ProfileActivity.this).load(drawableResId).into(imageView);}
        }
    }
    private void loadImageForPrefGender(String prefGender) {
        if(prefGender!=null&&prefImage!=null) {
            if (!isFinishing() && !isDestroyed()) {
                switch (prefGender) {
                    case "Female":
                        Glide.with(this).load(R.drawable.woman).into(prefImage);
                        break;
                    case "Male":
                        Glide.with(this).load(R.drawable.man).into(prefImage);
                        break;
                    case "Gay":
                        Glide.with(this).load(R.drawable.gay).into(prefImage);
                        break;
                    case "Lesbian":
                        Glide.with(this).load(R.drawable.lesbian).into(prefImage);
                        break;
                    default:
                        Glide.with(this).load(R.drawable.defaultimage).into(prefImage);
                        break;
                }
            }
        }
    }
    private void loadImageForMaritalStatus(String maritalStatus) {
        if (maritalImage != null && maritalStatus != null) {
            int imageResId;
            switch (maritalStatus) {
                case "Single":
                    imageResId = R.drawable.single;
                    break;
                case "Married":
                    imageResId = R.drawable.married;
                    break;
                case "Single with child":
                    imageResId = R.drawable.singlewithchild;
                    break;
                case "Divorced":
                    imageResId = R.drawable.divorced;
                    break;
                case "Divorced with child":
                    imageResId = R.drawable.seperatedwithchild;
                    break;
                case "Widowed":
                    imageResId = R.drawable.widowed;
                    break;
                case "Widowed with child":
                    imageResId = R.drawable.widowedwithchild;
                    break;
                default:
                    imageResId = R.drawable.defaultimage;
                    break;
            }
            // Ensure the activity is not finishing or destroyed before loading the image
            if (!isFinishing() && !isDestroyed()) {
                Glide.with(this).load(imageResId).into(maritalImage);
            }
        }
    }
    // Methods to load images based on user details
    private void loadImageForJob(String job) {
        if (jobImage != null && job != null) {
            // Ensure the activity is not finishing or destroyed before loading the image
            if (!isFinishing() && !isDestroyed()) {
                switch (job) {
                    case "Student":
                        Glide.with(this).load(R.drawable.student).into(jobImage);
                        break;
                    case "Engineer":
                        Glide.with(this).load(R.drawable.engineer).into(jobImage);
                        break;
                    case "Doctor":
                        Glide.with(this).load(R.drawable.doctor).into(jobImage);
                        break;
                    case "Teacher":
                        Glide.with(this).load(R.drawable.teacher).into(jobImage);
                        break;
                    case "Artist":
                        Glide.with(this).load(R.drawable.guitar).into(jobImage);
                        break;
                    case "Manager":
                        Glide.with(this).load(R.drawable.manager).into(jobImage);
                        break;
                    case "Chef":
                        Glide.with(this).load(R.drawable.cooking).into(jobImage);
                        break;
                    case "Driver":
                        Glide.with(this).load(R.drawable.driver).into(jobImage);
                        break;
                    case "Pilot":
                        Glide.with(this).load(R.drawable.pilot).into(jobImage);
                        break;
                    case "Writer":
                        Glide.with(this).load(R.drawable.writer).into(jobImage);
                        break;
                    default:
                        Glide.with(this).load(R.drawable.defaultimage).into(jobImage);
                        break;
                }
            }
        }
    }
    private void loadImageForEducation(String education) {
        if (education != null && educationImage != null) {
            // Ensure the activity is not finishing or destroyed before loading the image
            if (!isFinishing() && !isDestroyed()) {
                switch (education) {
                    case "High School":
                        Glide.with(this).load(R.drawable.school).into(educationImage);
                        break;
                    case "Bachelor's":
                        Glide.with(this).load(R.drawable.bachelors).into(educationImage);
                        break;
                    case "Master's":
                        Glide.with(this).load(R.drawable.masters).into(educationImage);
                        break;
                    case "PhD":
                        Glide.with(this).load(R.drawable.phd).into(educationImage);
                        break;
                    default:
                        Glide.with(this).load(R.drawable.defaultimage).into(educationImage);
                        break;
                }
            }
        }
    }

    private void loadImageForGender(String gender) {
        if(gender!=null&&genderImage!=null) {
            if (!isFinishing() && !isDestroyed()) {
        switch (gender) {
            case "Female":
                Glide.with(this).load(R.drawable.woman).into(genderImage);
                break;
            case "Male":
                Glide.with(this).load(R.drawable.man).into(genderImage);
                break;
            case "Gay":
                Glide.with(this).load(R.drawable.gay).into(genderImage);
                break;
            case "Lesbian":
                Glide.with(this).load(R.drawable.lesbian).into(genderImage);
                break;
            default:
                Glide.with(this).load(R.drawable.defaultimage).into(genderImage);
                break;
        }
            }
        }
    }

    private void loadImageForMotherTongue(String motherTongue) {
        if(motherTongue!=null&&motherTongueImage!=null) {
            if (!isFinishing() && !isDestroyed()) {
                switch (motherTongue) {
                    case "Telugu":
                        Glide.with(this).load(R.drawable.telugu).into(motherTongueImage);
                        break;
                    case "Hindi":
                        Glide.with(this).load(R.drawable.hindi).into(motherTongueImage);
                        break;
                    case "English":
                        Glide.with(this).load(R.drawable.english).into(motherTongueImage);
                        break;
                    case "Tamil":
                        Glide.with(this).load(R.drawable.tamil).into(motherTongueImage);
                        break;
                    case "KonKani":
                        Glide.with(this).load(R.drawable.konkani).into(motherTongueImage);
                        break;
                    case "Bengali":
                        Glide.with(this).load(R.drawable.bengali).into(motherTongueImage);
                        break;
                    case "punjab":
                        Glide.with(this).load(R.drawable.punjabi).into(motherTongueImage);
                        break;
                    case "Assamese":
                        Glide.with(this).load(R.drawable.assamese).into(motherTongueImage);
                        break;
                    case "Marathi":
                        Glide.with(this).load(R.drawable.marathi).into(motherTongueImage);
                        break;
                    case "Kannada":
                        Glide.with(this).load(R.drawable.kannada).into(motherTongueImage);
                        break;
                    case "Malyalam":
                        Glide.with(this).load(R.drawable.malyalam).into(motherTongueImage);
                        break;
                    case "Urdu":
                        Glide.with(this).load(R.drawable.urdu).into(motherTongueImage);
                        break;
                    case "Gujarathi":
                        Glide.with(this).load(R.drawable.gujarathi).into(motherTongueImage);
                        break;
                    case "Odia":
                        Glide.with(this).load(R.drawable.odia).into(motherTongueImage);
                        break;
                    default:
                        Glide.with(this).load(R.drawable.defaultimage).into(motherTongueImage);
                        break;
                }
            }
        }
    }

    private void showFullScreenImage(View rootView, String imageUrl) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_image, null);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int popupWidth = (int) (displayMetrics.widthPixels * 0.8); // 80% of screen width
        int popupHeight = (int) (displayMetrics.heightPixels * 0.8); // 80% of screen height

        PopupWindow popupWindow = new PopupWindow(popupView, popupWidth, popupHeight, true);

        RoundedImageView popupImageView = popupView.findViewById(R.id.popupImageView);
        Glide.with(this).load(imageUrl).into(popupImageView);

        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.alpha = 0.9f;
        getWindow().setAttributes(layoutParams);

        popupView.setOnClickListener(v -> {
            popupWindow.dismiss();
            WindowManager.LayoutParams layoutParams1 = getWindow().getAttributes();
            layoutParams1.alpha = 1.0f;
            getWindow().setAttributes(layoutParams1);
        });
    }
}
