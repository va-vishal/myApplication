package in.demo.myapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.demo.myapplication.Message.messagingActivity;
import in.demo.myapplication.Model.Chat;
import in.demo.myapplication.Model.User;
import in.demo.myapplication.R;


public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private boolean isChat;
    private List<User> selectedUsers;

    public UsersAdapter(Context mContext, List<User> mUsers, boolean isChat) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isChat = isChat;
        this.selectedUsers = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.users_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = mUsers.get(position);

        Glide.with(mContext).load(user.getImageurl()).into(holder.profile_image);
        if (isChat) {
            holder.username.setText(user.getName());
            // Apply any additional styling for the chat view if needed
        } else {
            holder.username.setText(user.getName());
            // Set margin top
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.username.getLayoutParams();
            params.topMargin = (int) (5 * holder.username.getContext().getResources().getDisplayMetrics().density);
            holder.username.setLayoutParams(params);

            // Set text size
            holder.username.setTextSize(25);
        }
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, messagingActivity.class);
            intent.putExtra("userid", user.getId());
            mContext.startActivity(intent);
        });
        if(isChat)
        {
            holder.last_msg.setVisibility(View.VISIBLE);
            lastMessage(user.getId(), holder.last_msg, holder.lastmsgdate,holder.lastmsgTime);
        }else {
            holder.last_msg.setVisibility(View.GONE);
            holder.lastmsgTime.setVisibility(View.GONE);
            holder.lastmsgdate.setVisibility(View.GONE);
        }


        if (isChat) {
            Boolean status = user.getStatus();
            if (status != null && status.equals(true)) {
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            } else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        } else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.VISIBLE);
        }
    }
    

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public List<User> getSelectedUsers() {
        return selectedUsers;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username, last_msg,lastmsgdate,lastmsgTime,unseenMessages;
        public ImageView profile_image;
        public View img_on, img_off;


        public ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            lastmsgdate=itemView.findViewById(R.id.Date);
            last_msg = itemView.findViewById(R.id.last_msg);
            lastmsgTime=itemView.findViewById(R.id.time);
            unseenMessages=itemView.findViewById(R.id.unseenMessages);

        }
    }

    private void lastMessage(String userid, TextView last_msg, TextView lastmsgdate, TextView lastmsgTime) {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // To hold the latest message
                String latestMessage = "default";
                String latestDate = "default";
                String latestMsgTime = "default";

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat != null && firebaseUser != null) {
                        String chatReceiver = chat.getReceiver();
                        String chatSender = chat.getSender();
                        String currentUserUid = firebaseUser.getUid();

                        if (chatReceiver != null && chatSender != null && currentUserUid != null) {
                            if ((chatReceiver.equals(currentUserUid) && chatSender.equals(userid)) ||
                                    (chatReceiver.equals(userid) && chatSender.equals(currentUserUid))) {
                                Long timestamp = chat.getTimestamp();
                                String formattedTime = null;
                                if (timestamp != null) {
                                    Date date = new Date(timestamp);
                                    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                                    formattedTime = timeFormat.format(date);

                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                    latestDate = dateFormat.format(date);
                                }

                                if ("text".equals(chat.getType())) {
                                    latestMessage = chat.getMessage();
                                } else if ("image".equals(chat.getType())) {
                                    latestMessage = "Image";
                                }

                                latestMsgTime = formattedTime;
                            }
                        }
                    }
                }

                // Update UI with the latest message details
                switch (latestMessage) {
                    case "text":
                        last_msg.setText(latestMessage);
                        lastmsgdate.setText(latestDate);
                        lastmsgTime.setText(latestMsgTime);
                        break;
                    case "Image":
                        last_msg.setText("Image");
                        lastmsgdate.setText(latestDate);
                        lastmsgTime.setText(latestMsgTime);
                        break;
                    default:
                        lastmsgdate.setText("");
                        last_msg.setText(latestMessage);
                        lastmsgTime.setText("");
                        break;
                }

                if (!"default".equals(latestMsgTime)) {
                    lastmsgTime.setText(latestMsgTime);
                }
                if (!"default".equals(latestDate)) {
                    lastmsgdate.setText(latestDate);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Log.e("lastMessage", "Error fetching last message: " + error.getMessage());
            }
        });
    }
}

