package in.demo.myapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.demo.myapplication.Model.MaritalStatusItem;
import in.demo.myapplication.R;

public class MaritalStatusAdapter extends RecyclerView.Adapter<MaritalStatusAdapter.MaritalStatusViewHolder> {

    private List<MaritalStatusItem> maritalStatusList;
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

    public MaritalStatusAdapter(List<MaritalStatusItem> maritalStatusList, OnItemClickListener listener) {
        this.maritalStatusList = maritalStatusList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MaritalStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.marital_status_card_item, parent, false);
        return new MaritalStatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MaritalStatusViewHolder holder, int position) {
        MaritalStatusItem maritalStatusItem = maritalStatusList.get(position);
        holder.bind(maritalStatusItem);

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
        return maritalStatusList.size();
    }

    public int getSelectedPosition() {
        return selectedItem;
    }

    public class MaritalStatusViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewStatus;
        private ImageView imageViewStatus;

        public MaritalStatusViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            imageViewStatus = itemView.findViewById(R.id.imageViewStatus);
        }

        public void bind(MaritalStatusItem maritalStatusItem) {
            textViewStatus.setText(maritalStatusItem.getStatus());
            imageViewStatus.setImageResource(maritalStatusItem.getImageResId());
        }
    }
}
