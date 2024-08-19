package in.demo.myapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import in.demo.myapplication.Model.User;
import in.demo.myapplication.Profile.ProfileActivity;
import in.demo.myapplication.R;

public class UserAdapter2 extends RecyclerView.Adapter<UserAdapter2.UserViewHolder> {

    private Context context;
    private List<User> userList;
    public UserAdapter2(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.nameTextView.setText(user.getName()+",");
        holder.ageTextView.setText(user.getAge());
        holder.genderTextView.setText(user.getState());
        holder.motherTongueTextView.setText(user.getJobType());

        // Load image using Glide or Picasso
        Glide.with(context).load(user.getImageurl()).into(holder.profileImage);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra("publisherid", user.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView profileImage;
        ImageView profileView;
        TextView nameTextView;
        TextView ageTextView;
        TextView genderTextView;
        TextView motherTongueTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.image);
            nameTextView = itemView.findViewById(R.id.name);
            ageTextView = itemView.findViewById(R.id.age);
            genderTextView = itemView.findViewById(R.id.state);
            motherTongueTextView = itemView.findViewById(R.id.jobType);
            profileView=itemView.findViewById(R.id.profileview);
        }
    }
}
