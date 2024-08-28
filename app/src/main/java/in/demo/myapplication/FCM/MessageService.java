package in.demo.myapplication.FCM;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import in.demo.myapplication.Calls.VideoCallinComing;
import in.demo.myapplication.HomeActivity;
import in.demo.myapplication.Message.messagingActivity;
import in.demo.myapplication.R;


public class MessageService extends FirebaseMessagingService {

    private NotificationManager notificationManager;

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FCM", "Refreshed token: " + token);
        saveTokenToDatabase(token);
    }
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 100, 200, 300}; // Pattern in milliseconds
        if (vibrator != null) {
            vibrator.vibrate(pattern, -1);
        }
        Bitmap largeicon= BitmapFactory.decodeResource(getResources(),R.drawable.lo);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Notification");
        String type = message.getNotification().getTitle();
        Intent resultIntent;

        if (type.equals("Incoming Video Call") || type.equals("Incoming Audio Call")) {
            resultIntent = new Intent(this, messagingActivity.class);
        } else {
            resultIntent = new Intent(this, HomeActivity.class);
        }
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            builder.setContentTitle(message.getNotification() != null ? message.getNotification().getTitle() : "No Title");
            builder.setContentText(message.getNotification() != null ? message.getNotification().getBody() : "No Body");
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message.getNotification() != null ? message.getNotification().getBody() : "No Body"));
            builder.setAutoCancel(true);
            builder.setSmallIcon(R.drawable.notification);
            builder.setLargeIcon(largeicon);
            builder.setVibrate(pattern);
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
            builder.setContentIntent(pendingIntent);

            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    String channelId = "Notification";
                    String channelName = "Coding";
                    int importance = NotificationManager.IMPORTANCE_HIGH;

                    NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
                    channel.enableLights(true);
                    channel.setLightColor(Color.RED);
                    channel.enableVibration(true);
                    channel.setVibrationPattern(pattern);

                    MediaPlayer notificationSound=MediaPlayer.create(this,R.raw.jhim);
                    notificationSound.start();
                    // Set the custom sound URI

                    notificationManager.createNotificationChannel(channel);
                    builder.setChannelId(channelId);
                }

                notificationManager.notify(100, builder.build());
            }
    }

    private void saveTokenToDatabase(String token) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
            databaseReference.child("fcmToken").setValue(token);
        }
    }
}
