package in.demo.myapplication.HelpCenter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import in.demo.myapplication.R;

public class DeleteAccountActivity extends AppCompatActivity {

    private Button deleteButton,backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delete_account);

        deleteButton=findViewById(R.id.deletebutton);
        backButton=findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = LayoutInflater.from(v.getContext());
                View dialogView = inflater.inflate(R.layout.custom_dialog, null);

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(v.getContext());
                dialogBuilder.setView(dialogView);
                AlertDialog alertDialog = dialogBuilder.create();
                Button cancelButton = dialogView.findViewById(R.id.dialogCancelButton);
                Button confirmButton = dialogView.findViewById(R.id.dialogConfirmButton);

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent emailIntent = new Intent(Intent.ACTION_SEND);

                        // Set the email recipient, subject, and body
                        emailIntent.setType("text/plain");
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"vishalods225@gmail.com"});
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Delete Account Request");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "Please Mention below your Registered Email id,username,Your account is hidden now WeLove App account will be deleted within 2weeks....\n Email id : ?\n username : ?");

                        // Check if there's an email client installed
                        if (emailIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(Intent.createChooser(emailIntent, "Send email using..."));
                        } else {
                            // Handle the case where no email client is installed
                            Toast.makeText(DeleteAccountActivity.this, "No email client installed.", Toast.LENGTH_SHORT).show();
                        }
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });
    }
}