package in.demo.myapplication.Message;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import in.demo.myapplication.R;

public class SendImage extends AppCompatActivity {

    private ImageView imageView;
    private Button sendButton;
    private FirebaseUser firebaseUser;
    private Uri fileUri;
    private TextView text;
    private ProgressBar progressBar;
    String userid;

    private static final String TAG = "SendImage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_image);

        userid = getIntent().getStringExtra("userid");

        imageView = findViewById(R.id.iv_sendimage);
        sendButton = findViewById(R.id.btn_send);
        text   = findViewById(R.id.tv_dont);
        progressBar=findViewById(R.id.pb_sendImage);
        text.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        if (intent != null) {
            String uriString = intent.getStringExtra("u");
            if (uriString != null) {
                fileUri = Uri.parse(uriString);
                Glide.with(this).load(fileUri).into(imageView);
            } else {
                Log.e(TAG, "No URI received");
                finish();
            }
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile("image");
                text.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
        text.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void uploadFile(String type) {
        if (fileUri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("uploads");

            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(fileUri));

            fileReference.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl = uri.toString();
                            sendMessage(firebaseUser.getUid(), getIntent().getStringExtra("userid"), downloadUrl, "image");
                            Toast.makeText(SendImage.this, "Image Sent", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error getting download URL", e);
                            Toast.makeText(SendImage.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Upload failed", e);
                    Toast.makeText(SendImage.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void sendMessage(String sender, final String receiver, String message, String type) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        // Get the current date
        Date date1 = new Date();
        // Format the date into a string (e.g., "dd-MM-yyyy HH:mm:ss")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String date = dateFormat.format(date1);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("type", type);
        hashMap.put("isseen", false);
        hashMap.put("timestamp",System.currentTimeMillis());
        hashMap.put("date", date);

        // Save the message to the "Chats" node in Firebase Database
        reference.child("Chats").push().setValue(hashMap);

        // Update the chat list for the sender and receiver
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(sender).child(receiver);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(receiver);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
        // Optionally, you can update the chat list for the receiver as well
        DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(receiver).child(sender);

        chatRefReceiver.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRefReceiver.child("id").setValue(sender);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });

    }
}
