package in.demo.myapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import in.demo.myapplication.Model.GenderItem;
import in.demo.myapplication.R;

public class PrefGenderAdapter extends RecyclerView.Adapter<PrefGenderAdapter.GenderViewHolder> {

    private List<GenderItem> genderList;
    private int selectedItem = -1; // To keep track of the selected item
    private OnItemClickListener listener;

    public PrefGenderAdapter(List<GenderItem> genderList, OnItemClickListener listener) {
        this.genderList = genderList;
        this.listener = listener;
    }

    public void toggleSelection(int position) {
        if (selectedItem == position) {
            selectedItem = -1; // Deselect if the same item is selected again
        } else {
            selectedItem = position;
        }
        notifyDataSetChanged(); // Update all items
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @NonNull
    @Override
    public GenderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.preferedgender_card_item, parent, false);
        return new GenderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenderViewHolder holder, int position) {
        GenderItem genderItem = genderList.get(position);
        holder.bind(genderItem);

        holder.itemView.setOnClickListener(v -> {
            toggleSelection(holder.getAdapterPosition());
            listener.onItemClick(holder.getAdapterPosition());
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
        return genderList.size();
    }

    public int getSelectedPosition() {
        return selectedItem;
    }

    public class GenderViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewGender;
        private ImageView imageViewGender;

        public GenderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewGender = itemView.findViewById(R.id.textViewpGender);
            imageViewGender = itemView.findViewById(R.id.pimage);
        }

        public void bind(GenderItem genderItem) {
            textViewGender.setText(genderItem.getGenderName());
            imageViewGender.setImageResource(genderItem.getImageResId());
        }
    }
}
