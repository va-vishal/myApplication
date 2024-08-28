package in.demo.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.demo.myapplication.Adapter.CustomSpinnerAdapter;

public class DOBActivity extends AppCompatActivity {
    private static final String TAG = "DobActivity";
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int CROP_IMAGE_ACTIVITY_REQUEST_CODE = 3;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private EditText dobEditText,nameEditText,descriptionEditText;
    private Button saveButton,allowLocationButton;
    private FusedLocationProviderClient fusedLocationClient;
    private String selectedDOB;
    private int userAge;
    TextView age;
    ImageView plus,imageview;
    FirebaseAuth auth;
    CardView cardView;
    Spinner gender,education_level,jobtype,motherToungue,maritgal_status,prefGender;
    FirebaseDatabase database;
    private DatabaseReference reference;
    private Uri mediaUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            reference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
        }

        setContentView(R.layout.activity_dobactivity);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        cardView = findViewById(R.id.cardView);
        plus = findViewById(R.id.plus);
        imageview = findViewById(R.id.imageview);
        allowLocationButton = findViewById(R.id.location);
        descriptionEditText = findViewById(R.id.description);
        age=findViewById(R.id.age);
        nameEditText = findViewById(R.id.name);
        gender=findViewById(R.id.gender);
        education_level=findViewById(R.id.educationlevel);
        jobtype=findViewById(R.id.jobtype);
        motherToungue=findViewById(R.id.motherToungue);
        prefGender=findViewById(R.id.prefGender);
        maritgal_status=findViewById(R.id.maritgal_status);

        dobEditText = findViewById(R.id.dobEditText);
        saveButton = findViewById(R.id.buttonNext);

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        cardView.setOnClickListener(v -> showMediaPickerDialog());
        plus.setOnClickListener(v -> showMediaPickerDialog());

        allowLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLocationPermission()) {

                    updateUserLocationInDatabase();
                }
            }
        });

        dobEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDatePickerBottomSheet dialog = new CustomDatePickerBottomSheet(DOBActivity.this, new CustomDatePickerBottomSheet.OnDateSetListener() {
                    @Override
                    public void onDateSet(int day, int month, int year) {
                        selectedDOB = day + "/" + (month + 1) + "/" + year;
                        dobEditText.setText(selectedDOB);
                        userAge = calculateAge(day, month, year);
                        if (userAge >= 18) {
                            System.out.println("User's age is: " + userAge);
                            age.setText("Age : " + userAge);
                            updateUserInDatabase(selectedDOB, String.valueOf(userAge));
                        }else {
                            showCustomMessageDialog("Only 18 years old or above are allowed to create an account!!");
                        }
                    }
                });
                dialog.show();
            }
        });

        // Initialize spinners
        setupSpinner(gender, R.array.gender_options);
        setupSpinner(education_level, R.array.Educationlevel_options);
        setupSpinner(jobtype, R.array.jobtype_options);
        setupSpinner(motherToungue, R.array.mother_tongue_options);
        setupSpinner(maritgal_status, R.array.maritalstatus_options);
        setupSpinner(prefGender, R.array.pref_gender_options);





        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
                saveProfileToDatabase();
                Intent intent = new Intent(DOBActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }
        });
    }
    private void setupSpinner(Spinner spinner, int arrayResId) {
        String[] options = getResources().getStringArray(arrayResId);
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0); // Set default selection if needed
    }

    private void updateUserLocationInDatabase() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Geocoder geocoder = new Geocoder(DOBActivity.this);
                            try {
                                // Get the list of address based on the latitude and longitude
                                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                if (addresses != null && !addresses.isEmpty()) {
                                    Address address = addresses.get(0);
                                    String area = address.getLocality(); // Locality (city or area)
                                    String state = address.getAdminArea(); // Administrative area (state)


                                    String userId = auth.getCurrentUser().getUid();

                                    // Update the database with area and state
                                    DatabaseReference databaseReference = database.getReference("users").child(userId);

                                    Map<String, Object> locationUpdates = new HashMap<>();
                                    locationUpdates.put("area", area);
                                    locationUpdates.put("state", state);
                                    locationUpdates.put("latitude", location.getLatitude());
                                    locationUpdates.put("longitude", location.getLongitude());

                                    databaseReference.updateChildren(locationUpdates)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(DOBActivity.this, "User's location updated successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(DOBActivity.this, "Failed to update user's location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(DOBActivity.this, "Geocoder failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void updateUserInDatabase(String dob, String age) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Create a map to store the new values
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("dob", dob);
        userUpdates.put("age", age);

        usersRef.child(userId).updateChildren(userUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(DOBActivity.this, "DOB and age saved successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DOBActivity.this, "Failed to update user's DOB and age: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }
        // Permission is already granted
        return true;
    }

    private void showCustomMessageDialog(String message) {
        DialogFragment dialog = new CustomMessageDialog(message);
        dialog.show(getSupportFragmentManager(), "CustomMessageDialog");
    }
    // Calculate age based on provided date of birth
    private int calculateAge(int day, int month, int year) {
        Calendar today = Calendar.getInstance();
        Calendar dob = Calendar.getInstance();
        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start LocationActivity

            } else {
                // Permission denied, show a message or handle accordingly
                Toast.makeText(this, "Location permission is required to proceed.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void showMediaPickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DOBActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_media_choice, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        AppCompatImageView buttonCamera = dialogView.findViewById(R.id.button_camera);
        AppCompatImageView buttonGallery = dialogView.findViewById(R.id.button_gallery);

        buttonCamera.setOnClickListener(v -> {
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(captureIntent, REQUEST_IMAGE_CAPTURE);
            dialog.dismiss();
        });

        buttonGallery.setOnClickListener(v -> {
            Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickImageIntent, PICK_IMAGE_REQUEST);
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST || requestCode == REQUEST_IMAGE_CAPTURE) {
                handleImageSelectionOrCapture(requestCode, data);
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                handleCroppedImageResult(data);
            } else {
                Toast.makeText(this, "Unknown request code: " + requestCode, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Action cancelled or failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleImageSelectionOrCapture(int requestCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            mediaUri = data.getData();
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && data != null && data.getExtras() != null) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            if (imageBitmap != null) {
                mediaUri = getImageUri(DOBActivity.this, imageBitmap);
            }
        }

        if (mediaUri != null) {
            CropImage.activity(mediaUri)
                    .setAspectRatio(1, 1)
                    .start(this);
        } else {
            Toast.makeText(this, "Failed to get image URI", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCroppedImageResult(Intent data) {
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        if (result != null) {
            mediaUri = result.getUri();
            imageview.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(mediaUri)
                    .into(imageview);
        } else {
            Toast.makeText(this, "Failed to get cropped image", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    private String getFileExtension(Uri uri) {
        if (uri == null) return null;
        String extension;
        if (uri.getScheme().equals("content")) {
            extension = getContentResolver().getType(uri).split("/")[1];
        } else {
            extension = uri.getPath().substring(uri.getPath().lastIndexOf(".") + 1);
        }
        return extension;
    }
    private void uploadImage() {
        if (mediaUri != null) {
            String fileExtension = getFileExtension(mediaUri);
            if (fileExtension == null) {
                Toast.makeText(this, "Could not determine file extension", Toast.LENGTH_SHORT).show();
                return;
            }
            StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + fileExtension);
            UploadTask uploadTask = reference.putFile(mediaUri);

            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return reference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String imageUrl = task.getResult().toString();
                    saveImageToDatabase(imageUrl);
                } else {
                    Toast.makeText(DOBActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    Log.e("ProfilePhotoActivity", "Upload failed: ", task.getException());
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(DOBActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ProfilePhotoActivity", "Upload failed: ", e);
            });
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageToDatabase(String imageurl) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("imageurl", imageurl);

            reference.updateChildren(hashMap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(DOBActivity.this, "profile image uploaded", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DOBActivity.this, "Failed to update profile image", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(DOBActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }
   /* private void saveDescription() {
        String description = descriptionEditText.getText().toString().trim();

        if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Description cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("description", description);

        reference.updateChildren(hashMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.e(TAG, "msg: ","" );
            } else {
                Toast.makeText(DOBActivity.this, "Failed to save description", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error: ", task.getException());
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(DOBActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    //private void saveNameToDatabase() {
        String name = nameEditText.getText().toString().trim();
        String userId = auth.getCurrentUser().getUid();
        DatabaseReference userRef = database.getReference("users").child(userId);

        userRef.child("name").setValue(name)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(DOBActivity.this, "Name saved successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DOBActivity.this, "Failed to save name: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }*/
    private void saveProfileToDatabase() {
        String name = nameEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String Gender = gender.getSelectedItem().toString();
        String educationLevel = education_level.getSelectedItem().toString();
        String jobType = jobtype.getSelectedItem().toString();
        String motherTongue = motherToungue.getSelectedItem().toString();
        String maritalStatus = maritgal_status.getSelectedItem().toString();
        String PrefGender = prefGender.getSelectedItem().toString();


   String userId = auth.getCurrentUser().getUid();

        // Create a reference to the user's profile
        DatabaseReference userRef = database.getReference("users").child(userId);


        Map<String, Object> profileData = new HashMap<>();
        profileData.put("name", name);
        profileData.put("description", description);
        profileData.put("gender", Gender);
        profileData.put("educationLevel", educationLevel);
        profileData.put("jobType", jobType);
        profileData.put("motherTongue", motherTongue);
        profileData.put("maritalStatus", maritalStatus);
        profileData.put("prefGender", PrefGender);

        // Save the data to Firebase
        userRef.updateChildren(profileData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(DOBActivity.this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DOBActivity.this, "Failed to save profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
