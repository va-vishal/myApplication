package in.demo.myapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.demo.myapplication.Model.HeightItem;
import in.demo.myapplication.R;

public class HeightAdapter extends RecyclerView.Adapter<HeightAdapter.HeightViewHolder> {

    private List<HeightItem> heightList;
    private OnHeightClickListener onHeightClickListener;
    private int selectedItem = RecyclerView.NO_POSITION;

    public void setSelected(int position) {
        int previousSelectedItem = selectedItem;
        selectedItem = position;
        notifyItemChanged(previousSelectedItem); // Update previously selected item
        notifyItemChanged(selectedItem); // Update newly selected item
    }
    public interface OnHeightClickListener {
        void onHeightClick(String height);
    }

    public HeightAdapter(List<HeightItem> heightList, OnHeightClickListener onHeightClickListener) {
        this.heightList = heightList;
        this.onHeightClickListener = onHeightClickListener;
    }

    @NonNull
    @Override
    public HeightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.height_card_item, parent, false);
        return new HeightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HeightViewHolder holder, int position) {
        HeightItem currentItem = heightList.get(position);
        holder.heightText.setText(currentItem.getHeight());

        holder.itemView.setSelected(selectedItem == position);
        holder.itemView.setOnClickListener(v -> {
            selectedItem = holder.getAdapterPosition();
            notifyDataSetChanged();
            onHeightClickListener.onHeightClick(currentItem.getHeight());
        });

        if (selectedItem == position) {
            holder.itemView.setBackgroundResource(R.drawable.selected_background); // Apply selected UI
        } else {
            holder.itemView.setBackgroundResource(R.drawable.default_background); // Apply default UI
        }
    }

    @Override
    public int getItemCount() {
        return heightList.size();
    }

    public static class HeightViewHolder extends RecyclerView.ViewHolder {
        public TextView heightText;

        public HeightViewHolder(@NonNull View itemView) {
            super(itemView);
            heightText = itemView.findViewById(R.id.height);
        }
    }
}
