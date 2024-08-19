package in.demo.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;


import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class ProfilePhotoActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int CROP_IMAGE_ACTIVITY_REQUEST_CODE = 3;

    private CardView cardView;
    private ImageView plus;
    private ImageView imageView;
    private Button buttonNext;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private Uri mediaUri;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_photo);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        auth = FirebaseAuth.getInstance();

        cardView = findViewById(R.id.cardView);
        plus = findViewById(R.id.plus);
        imageView = findViewById(R.id.imageview);
        buttonNext = findViewById(R.id.buttonNext);

        cardView.setOnClickListener(v -> showMediaPickerDialog());

        plus.setOnClickListener(v -> showMediaPickerDialog());

        buttonNext.setOnClickListener(v -> uploadImage());
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
                    Toast.makeText(ProfilePhotoActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    Log.e("ProfilePhotoActivity", "Upload failed: ", task.getException());
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(ProfilePhotoActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Intent intent = new Intent(ProfilePhotoActivity.this, DescriptionActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ProfilePhotoActivity.this, "Failed to update profile image", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(ProfilePhotoActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void showMediaPickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfilePhotoActivity.this);
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
                mediaUri = getImageUri(ProfilePhotoActivity.this, imageBitmap);
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
            imageView.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(mediaUri)
                    .into(imageView);
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
}
