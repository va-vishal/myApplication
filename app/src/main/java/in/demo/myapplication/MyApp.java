package in.demo.myapplication;

import android.app.Application;
import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MyApp extends Application implements ActivityLifecycleCallbacks {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        // Register ActivityLifecycleCallbacks to manage app lifecycle events
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        updateStatus(false);
    }

    // Manage status based on activity lifecycle
    @Override
    public void onActivityResumed(Activity activity) {
        updateStatus(true);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        updateStatus(false);
    }


    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
    private void updateStatus(boolean status) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            Calendar time1=Calendar.getInstance();
            SimpleDateFormat currenttime=new SimpleDateFormat("HH:mm a");
            String savetime = currenttime.format(time1.getTime());
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
            HashMap<String, Object> statusMap = new HashMap<>();
            statusMap.put("status", status);
            statusMap.put("lastSeen",savetime);
            reference.updateChildren(statusMap);
        }
    }
}
