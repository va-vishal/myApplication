package in.demo.myapplication.Message;
import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;
import com.theartofdev.edmodo.cropper.CropImage;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.google.GoogleEmojiProvider;
import com.zegocloud.uikit.components.audiovideocontainer.ZegoLayoutMode;
import com.zegocloud.uikit.components.audiovideocontainer.ZegoLayoutPictureInPictureConfig;
import com.zegocloud.uikit.plugin.invitation.ZegoInvitationType;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService;
import com.zegocloud.uikit.prebuilt.call.config.DurationUpdateListener;
import com.zegocloud.uikit.prebuilt.call.config.ZegoCallDurationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoCallInvitationData;
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoUIKitPrebuiltCallConfigProvider;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import de.hdodenhof.circleimageview.CircleImageView;
import in.demo.myapplication.Adapter.MessageAdapter;
import in.demo.myapplication.FCM.FcmNotificationSender;
import in.demo.myapplication.MessageActivity;
import in.demo.myapplication.Model.Chat;
import in.demo.myapplication.Model.User;
import in.demo.myapplication.R;

public class messagingActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username, blockedText, lastSeen, online;
    EditText editText;
    ImageView more;
    RecyclerView recyclerView_c;
    FirebaseUser currentUser;
    DatabaseReference reference, typingRef;
    MessageAdapter messageAdapter;
    LinearLayout msgData;
    private String sname, rname, receiver_token;
    List<Chat> mchat;
    LottieAnimationView animation_view;
    ValueEventListener seenListener;
    private String otherUser;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private Context mContext;
    private static final int PICK_IMAGE = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 102;
    private Boolean isblocked = false;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference userRef = database.getReference("users");
    private static final String TAG = "VideoCall";
    private ZegoSendCallInvitationButton videocall, audiocall;
    private final long appID = 19161546; // Your App ID
    private final String appSign = "675b397595252aae19a8ceef861514f584d6de6ed147273068939191043f053e";
    Application application = new Application() ;// Your App Sign


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        EmojiManager.install(new GoogleEmojiProvider());

        mContext = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> startActivity(new Intent(messagingActivity.this, MessageActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

        recyclerView_c = findViewById(R.id.recycler_view_c);
        recyclerView_c.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView_c.setLayoutManager(linearLayoutManager);
        videocall = findViewById(R.id.video);
        audiocall = findViewById(R.id.voice);
        if (videocall == null) {
            Log.e(TAG, "Video call button is null");
        }
        if (audiocall == null) {
            Log.e(TAG, "Voice call button is null");
        }

        // Proceed with setting up the buttons if they are not null
        if (videocall != null) {
            videocall.setOnClickListener(v -> initiateVideoCall());
        }
        if (audiocall != null) {
            audiocall.setOnClickListener(v -> initiateVoiceCall());
        }
        online = findViewById(R.id.online);
        lastSeen = findViewById(R.id.lastSeen);
        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        animation_view = findViewById(R.id.animation_view);
        animation_view.setVisibility(View.GONE);
        editText = findViewById(R.id.send_msg);
        editText.setFocusableInTouchMode(true);
        editText.setFocusable(true);
        editText.setEnabled(true);
        editText.setVisibility(View.VISIBLE);
        more = findViewById(R.id.more);
        blockedText = findViewById(R.id.blockedText);
        msgData = findViewById(R.id.msgData);

        recyclerView_c.setVisibility(View.VISIBLE);
        msgData.setVisibility(View.VISIBLE);
        videocall.setVisibility(View.VISIBLE);
        audiocall.setVisibility(View.VISIBLE);
        more.setVisibility(View.VISIBLE);
        blockedText.setVisibility(View.GONE);

        otherUser = getIntent().getStringExtra("userid");

        if (otherUser == null) {
            Log.e("messagingActivity", "UserID is null");
            return;
        } else {
            Log.d("messagingActivity", "UserID: " + otherUser);
        }

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users").child(otherUser);
        typingRef = FirebaseDatabase.getInstance().getReference("typing");


        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("users").child(otherUser);

        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    rname = user.getName();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(messagingActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });

        animation_view.setAnimation("animation.json");
        setupSendMessageButton(otherUser);
        loadUserData(otherUser);
        seenMessage(otherUser);
        initCallInviteService();
        setupTypingStatus(editText, otherUser, typingRef);
        checkOnlineStatus(otherUser);


        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isUserBlocked(currentUser.getUid(), otherUser, new BlockCheckCallback() {
                    @Override
                    public void onResult(boolean isBlocked) {
                        PopupMenu popupMenu = new PopupMenu(mContext, view);
                        popupMenu.inflate(R.menu.chat_menu);
                        popupMenu.getMenu().findItem(R.id.block).setVisible(!isBlocked);
                        popupMenu.getMenu().findItem(R.id.unblock).setVisible(isBlocked);
                        popupMenu.setOnMenuItemClickListener(item -> {
                            int itemId = item.getItemId();
                            if (itemId == R.id.block) {
                                addToBlockedList(otherUser);
                                Toast.makeText(messagingActivity.this, "You blocked this user", Toast.LENGTH_SHORT).show();
                                return true;
                            } else if (itemId == R.id.unblock) {
                                removeFromBlockedList(otherUser);
                                Toast.makeText(messagingActivity.this, "You unblocked this user", Toast.LENGTH_SHORT).show();
                                return true;
                            } else if (itemId == R.id.Report) {
                                Toast.makeText(messagingActivity.this, "Report Clicked", Toast.LENGTH_SHORT).show();
                                return true;
                            } else {
                                return false;
                            }
                        });

                        popupMenu.show();
                    }
                });
            }
        });
        PermissionX.init(this).permissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
                .onExplainRequestReason(new ExplainReasonCallback() {
                    @Override
                    public void onExplainReason(@NonNull ExplainScope scope, @NonNull List<String> deniedList) {
                        String message = "We need your consent for the following permissions in order to use the offline call function properly";
                        scope.showRequestReasonDialog(deniedList, message, "Allow", "Deny");
                    }
                }).request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, @NonNull List<String> grantedList,
                                         @NonNull List<String> deniedList) {
                    }
                });PermissionX.init(this).permissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
                .onExplainRequestReason(new ExplainReasonCallback() {
                    @Override
                    public void onExplainReason(@NonNull ExplainScope scope, @NonNull List<String> deniedList) {
                        String message = "We need your consent for the following permissions in order to use the offline call function properly";
                        scope.showRequestReasonDialog(deniedList, message, "Allow", "Deny");
                    }
                }).request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, @NonNull List<String> grantedList,
                                         @NonNull List<String> deniedList) {
                    }
                });
       //initCallInviteService(appID, appSign, currentUser.getUid(), userName);

    }
    private void initCallInviteService() {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference().child("users").child(currentUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        sname = user.getName();
                        String userID = currentUser.getUid();
                        String userName = sname;

                        // Create and configure call invitation settings
                        ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();

                        callInvitationConfig.provider = new ZegoUIKitPrebuiltCallConfigProvider() {
                            @Override
                            public ZegoUIKitPrebuiltCallConfig requireConfig(ZegoCallInvitationData invitationData) {
                                ZegoUIKitPrebuiltCallConfig config;
                                boolean isVideoCall = invitationData.type == ZegoInvitationType.VIDEO_CALL.getValue();
                                if (!isVideoCall) {
                                    config = ZegoUIKitPrebuiltCallConfig.oneOnOneVoiceCall();
                                } else {
                                    config = ZegoUIKitPrebuiltCallConfig.oneOnOneVideoCall();
                                }
                                callInvitationConfig.outgoingCallBackground = new ColorDrawable(Color.BLUE);
                                callInvitationConfig.incomingCallBackground = new ColorDrawable(Color.GREEN);
                                callInvitationConfig.incomingCallRingtone = "incomingCallRingtone";
                                callInvitationConfig.outgoingCallRingtone = "outgoingCallRingtone";

                                // Add duration configuration
                                config.durationConfig = new ZegoCallDurationConfig();
                                config.durationConfig.isVisible = true;
                                config.durationConfig.durationUpdateListener = new DurationUpdateListener() {
                                    @Override
                                    public void onDurationUpdate(long seconds) {
                                        Log.d("CallDuration", "onDurationUpdate() called with: seconds = [" + seconds + "]");
                                        if (seconds == 60 * 5) {  // End call after 5 minutes
                                            ZegoUIKitPrebuiltCallService.endCall();
                                        }
                                    }
                                };

                                // Modify your custom calling configurations here
                                ZegoLayoutPictureInPictureConfig pipConfig = new ZegoLayoutPictureInPictureConfig();
                                pipConfig.switchLargeOrSmallViewByClick = true;
                                config.layout.mode = ZegoLayoutMode.PICTURE_IN_PICTURE;
                                config.layout.config = pipConfig;

                                return config;
                            }
                        };

                        // Initialize the call invitation service
                        ZegoUIKitPrebuiltCallService.init(
                                getApplication(),
                                appID,
                                appSign,
                                userID,
                                userName,
                                callInvitationConfig
                        );
                    } else {
                        Log.e("messagingActivity", "User data does not exist");
                    }
                } else {
                    Log.e("messagingActivity", "User data does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("messagingActivity", "Error loading user data: " + databaseError.getMessage());
            }
        });
    }


    private void initiateVideoCall() {

        videocall.setIsVideoCall(true);
        videocall.setResourceID("Welove_ResId"); // Please fill in the resource ID name that has been configured in the ZEGOCLOUD's console here.
        videocall.setInvitees(Collections.singletonList(new ZegoUIKitUser(otherUser,rname)));
       String sid=currentUser.getUid();
        sendNotification(sid,otherUser,otherUser,"Incoming Video Call");
    }

    private void initiateVoiceCall() {
        audiocall.setIsVideoCall(false);
        audiocall.setResourceID("Welove_ResId"); // Please fill in the resource ID name that has been configured in the ZEGOCLOUD's console here.
        audiocall.setInvitees(Collections.singletonList(new ZegoUIKitUser(otherUser,rname)));
         String sid=currentUser.getUid();
        sendNotification(sid,otherUser,otherUser,"Incoming Audio Call");
    }
    public interface BlockCheckCallback {
        void onResult(boolean isBlocked);
    }

    private void isUserBlocked(String currentUserId, String otherUserId, BlockCheckCallback callback) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference.child(currentUserId).child("blocksList").child(otherUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                callback.onResult(dataSnapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onResult(false); // In case of error, consider user not blocked
            }
        });
    }


    private void addToBlockedList(String userId) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        databaseReference.child(currentUserId).child("blocksList").child(userId).setValue(true);

        // Add the currentUserId to the other user's blockedList
        databaseReference.child(userId).child("blockedList").child(currentUserId).setValue(true);
    }

    private void removeFromBlockedList(String userId) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        // Remove the userId from the current user's blockedList
        databaseReference.child(currentUserId).child("blocksList").child(userId).removeValue();
        // Remove the currentUserId from the other user's blockedList
        databaseReference.child(userId).child("blockedList").child(currentUserId).removeValue();
    }

    private void loadUserData(final String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    username.setText(user.getName());
                    if (!isFinishing() && !isDestroyed()) {
                        if ("default".equals(user.getImageurl())) {
                            profile_image.setImageResource(R.drawable.defaultimage);
                        } else {
                            Glide.with(messagingActivity.this).load(user.getImageurl()).into(profile_image);
                        }
                    }
                    checkBlockingStatus(currentUser.getUid(), userid);
                    readMessages(currentUser.getUid(), userid, user.getImageurl());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(messagingActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkBlockingStatus(String currentUserId, String otherUserId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference.child(otherUserId).child("blockedList").child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isBlockedByOtherUser = dataSnapshot.exists();
                databaseReference.child(currentUserId).child("blockedList").child(otherUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean isBlockedByCurrentUser = dataSnapshot.exists();

                        // Handle all blocking scenarios
                        if (isBlockedByOtherUser && isBlockedByCurrentUser) {
                            // Both users have blocked each other
                            updateUIForBlockedState(true, true);
                        } else if (isBlockedByOtherUser) {
                            // The other user has blocked the current user
                            updateUIForBlockedState(true, false);
                        } else if (isBlockedByCurrentUser) {
                            // The current user has blocked the other user
                            updateUIForBlockedState(false, true);
                        } else {
                            // Neither user has blocked the other
                            updateUIForBlockedState(false, false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void updateUIForBlockedState(boolean isBlockedByOtherUser, boolean isBlockedByCurrentUser) {
        if (isBlockedByOtherUser && isBlockedByCurrentUser) {
            // Both users have blocked each other
            recyclerView_c.setVisibility(View.GONE);
            msgData.setVisibility(View.GONE);
            videocall.setVisibility(View.GONE);
            audiocall.setVisibility(View.GONE);
            blockedText.setVisibility(View.VISIBLE);
            blockedText.setText("You both have blocked each other. Messaging is disabled.");
            editText.setEnabled(false);
        } else if (isBlockedByOtherUser) {
            // The current user is blocked by the other user
            recyclerView_c.setVisibility(View.GONE);
            msgData.setVisibility(View.GONE);
            videocall.setVisibility(View.GONE);
            audiocall.setVisibility(View.GONE);
            blockedText.setVisibility(View.VISIBLE);
            blockedText.setText("You have blocked this user. You can't send or receive messages.");
            editText.setEnabled(false);
        } else if (isBlockedByCurrentUser) {
            // The current user has blocked the other user
            recyclerView_c.setVisibility(View.GONE);
            msgData.setVisibility(View.GONE);
            videocall.setVisibility(View.GONE);
            audiocall.setVisibility(View.GONE);
            blockedText.setVisibility(View.VISIBLE);
            blockedText.setText("You are blocked by the other user. They can't send or receive messages from you.");
            editText.setEnabled(false);
        } else {
            // No one has blocked anyone
            recyclerView_c.setVisibility(View.VISIBLE);
            msgData.setVisibility(View.VISIBLE);
            videocall.setVisibility(View.VISIBLE);
            audiocall.setVisibility(View.VISIBLE);
            blockedText.setVisibility(View.GONE);
            editText.setEnabled(true);
        }
    }

    private void sendMessage(String sender, String receiver, String message, String type) {
        checkBlockingStatus(currentUser.getUid(), otherUser);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        // Generate a unique ID for the message

        String messageId = reference.child("Chats").push().getKey();

        if (messageId != null) {

            long timestamp = System.currentTimeMillis();

            // Optionally, format the timestamp into a date string
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String date = sdf.format(new Date(timestamp));

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("sender", sender);
            hashMap.put("receiver", receiver);
            hashMap.put("message", message);
            hashMap.put("type", type);
            hashMap.put("isseen", false);
            hashMap.put("timestamp", System.currentTimeMillis());
            hashMap.put("date", date);

            // Store the message in the database
            reference.child("Chats").child(messageId).setValue(hashMap);

            // Update chat list for the sender
            DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                    .child(sender).child(receiver);

            chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        chatRef.child("id").setValue(receiver);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle possible errors
                }
            });

            // Optionally, update chat list for the receiver as well
            DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                    .child(receiver).child(sender);

            chatRefReceiver.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        chatRefReceiver.child("id").setValue(sender);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle possible errors
                }
            });
            if(type=="text")
               message=editText.getText().toString();
            else
               message="image";
            sendNotification(currentUser.getUid(), otherUser,message,"you have new message");
        }
    }

    public void sendNotification(String suid, String ruid,String body,String title) {

        FirebaseDatabase.getInstance().getReference("users").child(otherUser).child("fcmToken").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                receiver_token = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                FcmNotificationSender notificationsSender =
                        new FcmNotificationSender(receiver_token, title, body, messagingActivity.this);

                notificationsSender.sendNotification();

            }
        }, 1000);
    }


    private void setupSendMessageButton(final String userid) {
        final EditText editText = findViewById(R.id.send_msg);

        editText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Check for clicks on the drawableRight
                int drawableRightWidth = editText.getCompoundDrawables()[2] != null ? editText.getCompoundDrawables()[2].getBounds().width() : 0;
                if (event.getX() >= (editText.getWidth() - drawableRightWidth - editText.getPaddingRight())) {
                    // Handle the send button click
                    String msg = editText.getText().toString().trim();
                    if (!msg.equals("")) {
                        sendMessage(currentUser.getUid(), userid, msg, "text");
                    } else {
                        Toast.makeText(messagingActivity.this, "You can't send an empty message", Toast.LENGTH_SHORT).show();
                    }
                    editText.setText("");
                    typingRef.child(currentUser.getUid()).child(userid).removeValue();
                    return true;
                }

                // Check for clicks on the drawableLeft
                int drawableLeftWidth = editText.getCompoundDrawables()[0] != null ? editText.getCompoundDrawables()[0].getBounds().width() : 0;
                if (event.getX() <= (drawableLeftWidth + editText.getPaddingLeft())) {

                    // Inflate the custom popup layout
                    LayoutInflater inflater = getLayoutInflater();
                    View popupView = inflater.inflate(R.layout.dialog_select_option, null);

                    // Create the PopupWindow
                    PopupWindow popupWindow = new PopupWindow(popupView,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            true);

                    // Set click listeners for the buttons in the popup
                    ImageView cameraOption = popupView.findViewById(R.id.camera);
                    ImageView galleryOption = popupView.findViewById(R.id.gallerys);
                    ImageView emojiOption = popupView.findViewById(R.id.emoji);

                    cameraOption.setOnClickListener(view -> {
                        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(captureIntent, REQUEST_IMAGE_CAPTURE);
                        popupWindow.dismiss();
                    });

                    galleryOption.setOnClickListener(view -> {
                        // Handle gallery option click
                        openGallery();
                        popupWindow.dismiss();
                    });
                    emojiOption.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditText editText = findViewById(R.id.send_msg);

                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                            View rootView = findViewById(R.id.root_view); // root_view is the parent view of your layout
                            //editText.requestFocus();
                            EmojiPopup emojiPopup = EmojiPopup.Builder.fromRootView(rootView)
                                    .build(editText); // Attach it to your EditText
                            emojiPopup.toggle();

                            if (popupWindow != null && popupWindow.isShowing()) {
                                popupWindow.dismiss();
                            }
                        }
                    });

                    // Show the popup window
                    popupWindow.setElevation(10);
                    popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    int[] location = new int[2];
                    msgData.getLocationOnScreen(location);

                    // Adjust the yOffset to move the popup slightly higher
                    int yOffset = -(msgData.getHeight() + popupWindow.getHeight() + 180); // The 20dp additional offset

                    // Show PopupWindow above msgDataLayout with additional offset
                    popupWindow.showAsDropDown(msgData, 30, yOffset);


                    View container = popupWindow.getContentView().getRootView();
                    if (container != null) {
                        container.setBackgroundResource(android.R.color.transparent);
                    }
                    return true;
                }
            }
            return false;
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }


    private void seenMessage(final String userid) {
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat != null && chat.getReceiver().equals(currentUser.getUid()) && chat.getSender().equals(userid)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_IMAGE:
                case REQUEST_IMAGE_CAPTURE:
                    handleImageResult(requestCode, data);
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    handleCroppedImageResult(data);
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE:
                    Exception error = CropImage.getActivityResult(data).getError();
                    Toast.makeText(this, "Crop error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private void handleCroppedImageResult(@Nullable Intent data) {
        CropImage.ActivityResult result = CropImage.getActivityResult(data);

        if (result != null) {
            Uri croppedImageUri = result.getUri();
            Intent intent = new Intent(this, SendImage.class);
            intent.putExtra("u", croppedImageUri.toString());
            intent.putExtra("userid", otherUser);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Failed to get cropped image", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleImageResult(int requestCode, @Nullable Intent data) {
        Uri fileUri = null;

        if (requestCode == PICK_IMAGE && data != null && data.getData() != null) {
            fileUri = data.getData();
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && data != null && data.getExtras() != null) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            if (imageBitmap != null) {
                fileUri = getImageUri(this, imageBitmap);
            }
        }

        if (fileUri != null) {
            CropImage.activity(fileUri)
                    .setAspectRatio(1, 1)
                    .start(this);
        } else {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri getImageUri(Context context, Bitmap image) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), image, "Title", null);
        return Uri.parse(path);
    }

    // Load Lottie animation from raw resources
    private void setupTypingStatus(EditText editText, String userid, DatabaseReference typingRef) {
        // Get the original bottom margin of the RecyclerView
        final int originalMarginBottom = ((ViewGroup.MarginLayoutParams) recyclerView_c.getLayoutParams()).bottomMargin;

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    typingRef.child(currentUser.getUid()).child(userid).removeValue();
                } else {
                    typingRef.child(currentUser.getUid()).child(userid).setValue(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        typingRef.child(userid).child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && Boolean.TRUE.equals(dataSnapshot.getValue(Boolean.class))) {
                    animation_view.setVisibility(View.VISIBLE);
                    animation_view.playAnimation();


                    animation_view.addAnimatorListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            // Adjust RecyclerView margin
                            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) recyclerView_c.getLayoutParams();
                            params.bottomMargin = (int) (45 * getResources().getDisplayMetrics().density); // 45dp in pixels
                            recyclerView_c.setLayoutParams(params);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            // Restore original margin
                            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) recyclerView_c.getLayoutParams();
                            params.bottomMargin = originalMarginBottom; // Restore original margin
                            recyclerView_c.setLayoutParams(params);

                            // Scroll to the last item after animation ends
                            recyclerView_c.smoothScrollToPosition(mchat.size() - 1);
                        }
                    });

                    online.setVisibility(View.GONE);
                    lastSeen.setVisibility(View.GONE);
                } else {
                    animation_view.setVisibility(View.GONE);
                    animation_view.cancelAnimation();
                    online.setVisibility(View.VISIBLE);
                    lastSeen.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Error retrieving typing status", databaseError.toException());
            }
        });
    }

    private void checkOnlineStatus(String userId) {
        DatabaseReference userStatusRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userStatusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    // Assuming status is a Boolean, if not adjust comparison
                    Boolean status = user.getStatus();
                    if (status != null && status.equals(true)) {
                        online.setVisibility(View.VISIBLE);
                        lastSeen.setVisibility(View.GONE);
                    } else {
                        online.setVisibility(View.GONE);
                        lastSeen.setVisibility(View.VISIBLE);
                        DatabaseReference userlastSeenRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
                        userlastSeenRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                if (user != null) {
                                    // Assuming status is a Boolean, if not adjust comparison
                                    String lastseen = user.getLastSeen();
                                    lastSeen.setText("Last Seen "+lastseen);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e("FirebaseError", databaseError.getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
                Log.e("FirebaseError", databaseError.getMessage());
            }
        });
    }

    private void readMessages(final String myId, final String userId, final String imageUrl) {
        mchat = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat != null && ((chat.getReceiver().equals(myId) && chat.getSender().equals(userId)) ||
                            (chat.getReceiver().equals(userId) && chat.getSender().equals(myId)))) {
                        mchat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(messagingActivity.this, mchat, imageUrl);
                    recyclerView_c.setAdapter(messageAdapter);
                    recyclerView_c.scrollToPosition(mchat.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // reference.removeEventListener(seenListener);
    }


}