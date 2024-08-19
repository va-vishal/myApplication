package in.demo.myapplication.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import in.demo.myapplication.HelpCenter.DeleteAccountActivity;
import in.demo.myapplication.PrivacyActivity;
import in.demo.myapplication.R;
import in.demo.myapplication.TermsActivity;

public class HelpCenterActivity extends AppCompatActivity {

    private CardView appSupportLayout;
    private CardView termsAndConditionsLayout;
    private CardView privacyPoliciesLayout;
    private CardView safetyTipsLayout;
    private CardView deleteAccountLayout;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_help_center);

        backButton=findViewById(R.id.backButton);

        appSupportLayout = findViewById(R.id.appsupportlayout);
        termsAndConditionsLayout = findViewById(R.id.termsandconditionslayout);
        privacyPoliciesLayout = findViewById(R.id.privacypolicieslayout);
        safetyTipsLayout = findViewById(R.id.safteytipslayout);
        deleteAccountLayout = findViewById(R.id.deleteaccountlayout);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        appSupportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to send an email
                Intent emailIntent = new Intent(Intent.ACTION_SEND);

                // Set the email recipient, subject, and body
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"vishalods225@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support Request");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Please Mention below your Registered Email id,username,and What is your issue Facing we will connect back Soon...\n Email id : ?\n username : ?");

                // Check if there's an email client installed
                if (emailIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(Intent.createChooser(emailIntent, "Send email using..."));
                } else {
                    // Handle the case where no email client is installed
                    Toast.makeText(HelpCenterActivity.this, "No email client installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        termsAndConditionsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpCenterActivity.this, TermsActivity.class);
                startActivity(intent);
            }
        });

        privacyPoliciesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpCenterActivity.this, PrivacyActivity.class);
                startActivity(intent);
            }
        });

        safetyTipsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpCenterActivity.this, SafetyTipsActivity.class);
                startActivity(intent);
            }
        });

        deleteAccountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpCenterActivity.this, DeleteAccountActivity.class);
                startActivity(intent);
            }
        });
    }
}
