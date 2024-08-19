package in.demo.myapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.demo.myapplication.Model.DrinkingItem;
import in.demo.myapplication.R;

public class DrinkingAdapter extends RecyclerView.Adapter<DrinkingAdapter.DrinkingViewHolder> {

    private List<DrinkingItem> drinkingList;
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

    public DrinkingAdapter(List<DrinkingItem> drinkingList, OnItemClickListener listener) {
        this.drinkingList = drinkingList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DrinkingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drinking_card_item, parent, false);
        return new DrinkingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrinkingViewHolder holder, int position) {
        DrinkingItem drinkingItem = drinkingList.get(position);
        holder.bind(drinkingItem);

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
        return drinkingList.size();
    }

    public int getSelectedPosition() {
        return selectedItem;
    }

    public class DrinkingViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewDrinkStatus;
        private ImageView imageViewDrinkStatus;

        public DrinkingViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDrinkStatus = itemView.findViewById(R.id.textViewDrinkStatus);
            imageViewDrinkStatus = itemView.findViewById(R.id.imageViewDrinkStatus);
        }

        public void bind(DrinkingItem drinkingItem) {
            textViewDrinkStatus.setText(drinkingItem.getDrinkStatus());
            imageViewDrinkStatus.setImageResource(drinkingItem.getImageResId());
        }
    }
}
