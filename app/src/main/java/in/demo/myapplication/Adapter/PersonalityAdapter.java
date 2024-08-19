package in.demo.myapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.demo.myapplication.Model.PersonalityItem;
import in.demo.myapplication.R;

public class PersonalityAdapter extends RecyclerView.Adapter<PersonalityAdapter.PersonalityViewHolder> {

    private List<PersonalityItem> personalityList;
    private int selectedItem = RecyclerView.NO_POSITION;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public PersonalityAdapter(List<PersonalityItem> personalityList, OnItemClickListener listener) {
        this.personalityList = personalityList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PersonalityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.personality_card_item, parent, false);
        return new PersonalityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonalityViewHolder holder, int position) {
        PersonalityItem personalityItem = personalityList.get(position);
        holder.bind(personalityItem);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int previousSelectedItem = selectedItem;
                selectedItem = holder.getAdapterPosition();
                notifyItemChanged(previousSelectedItem); // Update previous selected item
                notifyItemChanged(selectedItem); // Update newly selected item
                listener.onItemClick(selectedItem);
            }
        });

        // Update UI based on selection
        if (selectedItem == position) {
            holder.itemView.setBackgroundResource(R.drawable.selected_background); // Apply selected UI
        } else {
            holder.itemView.setBackgroundResource(R.drawable.default_background); // Apply default UI
        }
    }

    @Override
    public int getItemCount() {
        return personalityList.size();
    }

    public int getSelectedPosition() {
        return selectedItem;
    }

    public class PersonalityViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewPersonality;
        private ImageView imageViewPersonality;

        public PersonalityViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewPersonality = itemView.findViewById(R.id.textViewPersonality);
            imageViewPersonality = itemView.findViewById(R.id.image);
        }

        public void bind(PersonalityItem personalityItem) {
            textViewPersonality.setText(personalityItem.getPersonalityName());
            imageViewPersonality.setImageResource(personalityItem.getImageResId());
        }
    }
}
