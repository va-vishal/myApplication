package in.demo.myapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.demo.myapplication.Model.DietItem;
import in.demo.myapplication.R;

public class DietAdapter extends RecyclerView.Adapter<DietAdapter.DietViewHolder> {

    private List<DietItem> dietList;
    private OnItemClickListener listener;
    private int selectedItem = RecyclerView.NO_POSITION;

    public DietAdapter(List<DietItem> dietList, OnItemClickListener listener) {
        this.dietList = dietList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DietViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.diet_card_item, parent, false);
        return new DietViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DietViewHolder holder, int position) {
        DietItem dietItem = dietList.get(position);
        holder.bind(dietItem);

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
        return dietList.size();
    }

    public int getSelectedPosition() {
        return selectedItem;
    }

    public void setSelected(int position) {
        int previousSelectedItem = selectedItem;
        selectedItem = position;
        notifyItemChanged(previousSelectedItem); // Update previously selected item
        notifyItemChanged(selectedItem); // Update newly selected item
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static class DietViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewDiet;
        private ImageView imageViewDiet;

        public DietViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDiet = itemView.findViewById(R.id.textViewDiet);
            imageViewDiet = itemView.findViewById(R.id.imageDiet);
        }

        public void bind(DietItem dietItem) {
            textViewDiet.setText(dietItem.getDietName());
            imageViewDiet.setImageResource(dietItem.getImageResource());
        }
    }
}
