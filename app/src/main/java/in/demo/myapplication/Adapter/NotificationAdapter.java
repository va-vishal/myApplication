package in.demo.myapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import in.demo.myapplication.Model.Notification;
import in.demo.myapplication.Model.User;
import in.demo.myapplication.Profile.ProfileActivity;
import in.demo.myapplication.R;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final Context context;
    private final List<Notification> notificationList;
    private final String currentUserImageUrl;

    public NotificationAdapter(Context context, List<Notification> notificationList, String currentUserImageUrl) {
        this.context = context;
        this.notificationList = notificationList;
        this.currentUserImageUrl = currentUserImageUrl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notificationList.get(position);

        String anotherUserId = notification.getOtheruserid(); // Ensure this matches the model field name
        if (anotherUserId != null) {
            getUserInfo(holder.anotherUserImage, anotherUserId);
        } else {
            holder.anotherUserImage.setImageResource(R.drawable.defaultimage);
        }
        String currentuserId = notification.getCurrentuserid(); // Ensure this matches the model field name
        if (currentuserId != null) {
            getUserInfo(holder.currentuserimage, currentuserId);
        } else {
            holder.currentuserimage.setImageResource(R.drawable.defaultimage);
        }

        holder.title.setText(notification.getTitle());
        holder.body.setText(notification.getBody());

        // Format and set the timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM YYYY, hh:mm a", Locale.getDefault());
        String formattedDate = sdf.format(notification.getTimestamp());
        holder.timestamp.setText(formattedDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("publisherid", notification.getOtheruserid()); // Ensure this matches the model field name
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, body, timestamp;
        public RoundedImageView anotherUserImage,currentuserimage;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            body = itemView.findViewById(R.id.body);
            timestamp = itemView.findViewById(R.id.timestamp);
            anotherUserImage = itemView.findViewById(R.id.anotherUser);
            currentuserimage= itemView.findViewById(R.id.currentuser);
        }
    }

    private void getUserInfo(RoundedImageView imageView, String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null && user.getImageurl() != null) {
                    Glide.with(context).load(user.getImageurl()).into(imageView);
                } else {
                    imageView.setImageResource(R.drawable.defaultimage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
            }
        });
    }
}
