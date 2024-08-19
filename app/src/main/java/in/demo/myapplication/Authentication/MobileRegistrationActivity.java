package in.demo.myapplication.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import in.demo.myapplication.DOBActivity;
import in.demo.myapplication.R;

public class MobileRegistrationActivity extends AppCompatActivity {

    private EditText mobileNumber, password, confirmPassword, otp;
    private Button sendOtp, verifyOtp;
    private TextView txtLogin;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private CountryCodePicker ccp;

    private String verificationId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_registration);

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // Initialize views
        mobileNumber = findViewById(R.id.mobilenumber);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmpassword);
        otp = findViewById(R.id.otp);
        sendOtp = findViewById(R.id.send_otp);
        verifyOtp = findViewById(R.id.verify_otp);
        txtLogin = findViewById(R.id.txt_Login);
        ccp = findViewById(R.id.ccp);

        ccp.registerCarrierNumberEditText(mobileNumber);

        // Set click listener for send OTP button
        sendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOtp();
            }
        });

        // Set click listener for verify OTP button
        verifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyOtp();
            }
        });

        // Set click listener for login text view
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MobileRegistrationActivity.this, LoginActivity1.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void sendOtp() {
        String mobile = ccp.getFullNumberWithPlus().replace(" ", "");
        if (TextUtils.isEmpty(mobile)) {
            mobileNumber.setError("Enter mobile number");
            return;
        }

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(mobile) // Country code should be prefixed to the phone number
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallBack)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    verificationId = s;
                    sendOtp.setVisibility(View.GONE);
                    otp.setVisibility(View.VISIBLE);
                    verifyOtp.setVisibility(View.VISIBLE);
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    String code = phoneAuthCredential.getSmsCode();
                    if (code != null) {
                        otp.setText(code);
                        verifyCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(MobileRegistrationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            };

    private void verifyOtp() {
        String code = otp.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            otp.setError("Enter OTP");
            return;
        }
        verifyCode(code);
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                String mobile = ccp.getFullNumberWithPlus().replace(" ", "");
                                String pass = password.getText().toString().trim();
                                String confirmPass = confirmPassword.getText().toString().trim();

                                if (TextUtils.isEmpty(pass) || TextUtils.isEmpty(confirmPass)) {
                                    password.setError("Enter password");
                                    return;
                                }
                                if (!pass.equals(confirmPass)) {
                                    confirmPassword.setError("Passwords do not match");
                                    return;
                                }

                                user.updateEmail(mobile + "@myapp.com")
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    user.updatePassword(pass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                // Save user details to the database
                                                                HashMap<String, String> userDetails = new HashMap<>();
                                                                userDetails.put("mobile", mobile);
                                                                userDetails.put("password", pass);

                                                                database.getReference("users")
                                                                        .child(user.getUid())
                                                                        .setValue(userDetails)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    Toast.makeText(MobileRegistrationActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                                                                    Intent intent = new Intent(MobileRegistrationActivity.this, DOBActivity.class);
                                                                                    startActivity(intent);
                                                                                    finish();
                                                                                } else {
                                                                                    Toast.makeText(MobileRegistrationActivity.this, "Database update failed", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });
                                                            } else {
                                                                Toast.makeText(MobileRegistrationActivity.this, "Password update failed", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    Toast.makeText(MobileRegistrationActivity.this, "Email update failed", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(MobileRegistrationActivity.this, "Verification failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
