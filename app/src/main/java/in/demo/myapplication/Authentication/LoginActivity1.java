package in.demo.myapplication.Authentication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hbb20.CountryCodePicker;

import in.demo.myapplication.DOBActivity;
import in.demo.myapplication.HomeActivity;
import in.demo.myapplication.R;

public class LoginActivity1 extends AppCompatActivity {

    private EditText emailOrMobileEditText, passwordEditText;
    private Button loginButton;
    private TextView registerButton;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private CardView signinwithgoogle;
    private CountryCodePicker ccp;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize views
        emailOrMobileEditText = findViewById(R.id.identifier);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        registerButton = findViewById(R.id.register);
        progressBar = findViewById(R.id.progressBar);
        signinwithgoogle = findViewById(R.id.signinwithgoogle);



        signinwithgoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        // Set click listener for login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // Set click listener for register button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity1.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser() {
        String emailOrMobile = emailOrMobileEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(emailOrMobile)) {
            emailOrMobileEditText.setError("Enter email");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Enter password");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        if (emailOrMobile.contains("@")) {
            // Login with email and password
            auth.signInWithEmailAndPassword(emailOrMobile, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                if (user != null && user.isEmailVerified()) {
                                    Toast.makeText(LoginActivity1.this, "Login successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity1.this, HomeActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity1.this, "Please verify your email", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(LoginActivity1.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }else{
            emailOrMobileEditText.setError("Please enter a valid email address");
            progressBar.setVisibility(View.GONE);
        }
    }
}
