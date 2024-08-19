package in.demo.myapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.demo.myapplication.Model.MotherTongueItem;
import in.demo.myapplication.R;

public class MotherTongueAdapter extends RecyclerView.Adapter<MotherTongueAdapter.MotherTongueViewHolder> {

    private List<MotherTongueItem> motherTongueList;
    private int selectedItem = RecyclerView.NO_POSITION;
    private OnItemClickListener listener;

    public MotherTongueAdapter(List<MotherTongueItem> motherTongueList, OnItemClickListener listener) {
        this.motherTongueList = motherTongueList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MotherTongueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mother_tongue_card_item, parent, false);
        return new MotherTongueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MotherTongueViewHolder holder, int position) {
        MotherTongueItem motherTongueItem = motherTongueList.get(position);
        holder.bind(motherTongueItem);

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
        return motherTongueList.size();
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


    public class MotherTongueViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewMotherTongue;
        private ImageView imageViewMotherTongue;

        public MotherTongueViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMotherTongue = itemView.findViewById(R.id.mother_tongue);
            imageViewMotherTongue = itemView.findViewById(R.id.image);
        }

        public void bind(MotherTongueItem motherTongueItem) {
            textViewMotherTongue.setText(motherTongueItem.getMotherTongueName());
            imageViewMotherTongue.setImageResource(motherTongueItem.getImageResId());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
