package in.demo.myapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.demo.myapplication.Model.GenderItem;
import in.demo.myapplication.R;

public class GenderAdapter extends RecyclerView.Adapter<GenderAdapter.GenderViewHolder> {

    private List<GenderItem> genderList;
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

    public GenderAdapter(List<GenderItem> genderList, OnItemClickListener listener) {
        this.genderList = genderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GenderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gender_card_item, parent, false);
        return new GenderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenderViewHolder holder, int position) {
        GenderItem genderItem = genderList.get(position);
        holder.bind(genderItem);

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
            textViewGender = itemView.findViewById(R.id.textViewGender);
            imageViewGender = itemView.findViewById(R.id.image);
        }

        public void bind(GenderItem genderItem) {
            textViewGender.setText(genderItem.getGenderName());
            imageViewGender.setImageResource(genderItem.getImageResId());
        }
    }
}
