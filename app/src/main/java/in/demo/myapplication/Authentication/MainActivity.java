package in.demo.myapplication.Authentication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import in.demo.myapplication.DOBActivity;
import in.demo.myapplication.HomeActivity;
import in.demo.myapplication.PrivacyActivity;
import in.demo.myapplication.R;
import in.demo.myapplication.TermsActivity;

public class MainActivity extends AppCompatActivity {

    private CheckBox termsCheckbox;
    FirebaseAuth auth;
    FirebaseDatabase database;
    GoogleSignInClient  mGoogleSignInClient;
    int RC_SIGN_IN=20;
    private CardView LoginCard,EmailCard,googleSignUpButton,TwitterSignUpButton;
    private TextView termsText,privacyText;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        LoginCard=findViewById(R.id.LoginCard);
        EmailCard=findViewById(R.id.EmailCard);
        termsCheckbox = findViewById(R.id.termsCheckbox);
         googleSignUpButton = findViewById(R.id.googleSignUpButton);
        TwitterSignUpButton = findViewById(R.id.facebookSignUpButton);
         termsText = findViewById(R.id.termsText);
         privacyText = findViewById(R.id.privacyText);

        auth=FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
        database=FirebaseDatabase.getInstance();




        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (termsCheckbox.isChecked()) {
                    googleSignIn();
                } else {
                    showTermsDialog();
                }
            }
        });

        TwitterSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (termsCheckbox.isChecked()) {
                    googleSignIn();
                } else {
                    showTermsDialog();
                }
            }
        });

        termsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleTermsPress();
            }
        });

        privacyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePrivacyPress();
            }
        });

        termsCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCheck();
            }
        });

        LoginCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (termsCheckbox.isChecked()) {
                    Intent intent=new Intent(MainActivity.this, LoginActivity1.class);
                    startActivity(intent);
                } else {
                    showTermsDialog();
                }
            }
        });
        EmailCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (termsCheckbox.isChecked()) {
                    Intent intent=new Intent(MainActivity.this, EmailRegistrationActivity.class);
                    startActivity(intent);
                } else {
                    showTermsDialog();
                }
            }
        });
    }

   private void googleSignIn() {
        Intent intent=mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent,RC_SIGN_IN);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account=task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());

            }catch (Exception e)
            {
                Toast.makeText(MainActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
                                if (account != null) {
                                    String email = account.getEmail();

                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("id", user.getUid());
                                    map.put("name", user.getDisplayName());
                                    map.put("email", email); // Add email to the map
                                    map.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/welove-8617d.appspot.com/o/img_avatar2.png?alt=media&token=ea2d9db1-190c-4095-8f9b-725727ac5ef6");

                                    database.getReference().child("users").child(user.getUid()).setValue(map);

                                    // Log and show the Firebase UID
                                    String uid = user.getUid();
                                    Log.d("MainActivity", "Firebase Auth UID: " + uid);
                                    Toast.makeText(MainActivity.this, "Firebase Auth UID: " + uid, Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(MainActivity.this, DOBActivity.class);
                                    startActivity(intent);
                                }
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void showTermsDialog() {

        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.dialog_custom, null);

        // Convert 250dp to pixels
        int width = (int) (300 * getResources().getDisplayMetrics().density + 0.5f);

// Initialize the PopupWindow
        final PopupWindow popupWindow = new PopupWindow(popupView,
                width,  // Set the width to 250dp converted to pixels
                ViewGroup.LayoutParams.WRAP_CONTENT,  // Height is wrap_content
                true);  // PopupWindow is focusable

        // Find the TextView and Button in the custom layout
        TextView dialogMessage = popupView.findViewById(R.id.dialogMessage);
        Button buttonOk = popupView.findViewById(R.id.buttonOk);

        dialogMessage.setText("Please agree to the terms and conditions to proceed.");

        // Set the onClickListener for the OK button
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the popup window
                popupWindow.dismiss();
            }
        });

        // Set the background drawable of the PopupWindow (optional)
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Show the popup window
        // We need to pass an anchor view where the PopupWindow will be shown relative to
        View rootView = findViewById(android.R.id.content);
        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
    }


    private void handleTermsPress() {
        Intent intent = new Intent(MainActivity.this, TermsActivity.class);
        startActivity(intent);
    }


    private void handlePrivacyPress() {
        Intent intent = new Intent(MainActivity.this, PrivacyActivity.class);
        startActivity(intent);
    }

    private void handleCheck() {
        Log.d("MainActivity", "Checkbox toggled: " + termsCheckbox.isChecked());
        // Handle checkbox animation and logic
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }
}







