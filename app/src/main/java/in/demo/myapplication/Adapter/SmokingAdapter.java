package in.demo.myapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.demo.myapplication.Model.SmokingItem;
import in.demo.myapplication.R;

public class SmokingAdapter extends RecyclerView.Adapter<SmokingAdapter.SmokingViewHolder> {

    private List<SmokingItem> smokingList;
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

    public SmokingAdapter(List<SmokingItem> smokingList, OnItemClickListener listener) {
        this.smokingList = smokingList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SmokingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.smoking_card_item, parent, false);
        return new SmokingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SmokingViewHolder holder, int position) {
        SmokingItem smokingItem = smokingList.get(position);
        holder.bind(smokingItem);

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
        return smokingList.size();
    }

    public int getSelectedPosition() {
        return selectedItem;
    }

    public class SmokingViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewSmokingStatus;
        private ImageView imageViewSmokingStatus;

        public SmokingViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSmokingStatus = itemView.findViewById(R.id.textViewSmokingStatus);
            imageViewSmokingStatus = itemView.findViewById(R.id.imageViewSmokingStatus);
        }

        public void bind(SmokingItem smokingItem) {
            textViewSmokingStatus.setText(smokingItem.getSmokingStatus());
            imageViewSmokingStatus.setImageResource(smokingItem.getImageResId());
        }
    }
}
