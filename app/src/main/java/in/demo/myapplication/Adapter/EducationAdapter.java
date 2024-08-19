package in.demo.myapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.demo.myapplication.Model.EducationItem;
import in.demo.myapplication.R;

public class EducationAdapter extends RecyclerView.Adapter<EducationAdapter.EducationViewHolder> {

    private List<EducationItem> educationList;
    private int selectedItem = RecyclerView.NO_POSITION;
    private OnItemClickListener listener;

    public void setSelected(int position) {
        int previousSelectedItem = selectedItem;
        selectedItem = position;
        notifyItemChanged(previousSelectedItem); // Update previously selected item
        notifyItemChanged(selectedItem); // Update newly selected item
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public EducationAdapter(List<EducationItem> educationList, OnItemClickListener listener) {
        this.educationList = educationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EducationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.education_card_item, parent, false);
        return new EducationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EducationViewHolder holder, int position) {
        EducationItem educationItem = educationList.get(position);
        holder.bind(educationItem);

        holder.itemView.setOnClickListener(v -> {
            int previousSelectedItem = selectedItem;
            selectedItem = holder.getAdapterPosition();
            notifyItemChanged(previousSelectedItem); // Update previous selected item
            notifyItemChanged(selectedItem); // Update newly selected item
            listener.onItemClick(selectedItem);
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
        return educationList.size();
    }

    public int getSelectedPosition() {
        return selectedItem;
    }

    public class EducationViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewEducationLevel;
        private ImageView imageViewEducationLevel;

        public EducationViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewEducationLevel = itemView.findViewById(R.id.textViewEducationLevel);
            imageViewEducationLevel = itemView.findViewById(R.id.imageViewEducationLevel);
        }

        public void bind(EducationItem educationItem) {
            textViewEducationLevel.setText(educationItem.getEducationLevel());
            imageViewEducationLevel.setImageResource(educationItem.getImageResId());
        }
    }
}
