package in.demo.myapplication.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import in.demo.myapplication.DOBActivity;
import in.demo.myapplication.R;

public class EmailRegistrationActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private EditText otpEditText;
    private Button sendOtpButton;
    private Button verifyOtpButton;
    private TextView loginTextView;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_registration);

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize views
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirmpassword);
        otpEditText = findViewById(R.id.otp);
        sendOtpButton = findViewById(R.id.send_otp);
        verifyOtpButton = findViewById(R.id.verify_otp);
        loginTextView = findViewById(R.id.txt_Login);

        // Set click listener for send OTP button
        sendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOtp();

            }
        });

        // Set click listener for verify OTP button
        verifyOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyOtp();
            }
        });

        // Set click listener for login text view
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmailRegistrationActivity.this, LoginActivity1.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void sendOtp() {
        String email = emailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            return;
        }

        // Check if the email is already registered
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.isSuccessful()) {
                    boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                    if (isNewUser) {
                        registerUser(email);
                    } else {
                        showEmailAlreadyRegisteredDialog();
                    }
                } else {
                    Toast.makeText(EmailRegistrationActivity.this, "Failed to check email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void registerUser(String email) {
        auth.createUserWithEmailAndPassword(email, "defaultPassword")
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            auth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(EmailRegistrationActivity.this, "Verification email sent", Toast.LENGTH_SHORT).show();
                                                //otpEditText.setVisibility(View.VISIBLE);
                                                verifyOtpButton.setVisibility(View.VISIBLE);
                                            } else {
                                                Toast.makeText(EmailRegistrationActivity.this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(EmailRegistrationActivity.this, "Failed to create user", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void verifyOtp() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordEditText.setError("Confirm password is required");
            return;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            return;
        }
        auth.signInWithEmailAndPassword(email, "defaultPassword").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null && user.isEmailVerified()) {
                        user.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("id", user.getUid());
                                    map.put("email", email);
                                    map.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/welove-8617d.appspot.com/o/img_avatar2.png?alt=media&token=ea2d9db1-190c-4095-8f9b-725727ac5ef6");

                                    database.getReference().child("users").child(user.getUid()).setValue(map);

                                    Toast.makeText(EmailRegistrationActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(EmailRegistrationActivity.this, DOBActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(EmailRegistrationActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(EmailRegistrationActivity.this, "Email not verified", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EmailRegistrationActivity.this, "Failed to sign in", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void showEmailAlreadyRegisteredDialog() {
        new AlertDialog.Builder(EmailRegistrationActivity.this)
                .setTitle("Email Already Registered")
                .setMessage("This email is already registered. You cannot create an account with this email.")
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
}
