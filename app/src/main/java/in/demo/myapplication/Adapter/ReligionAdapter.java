package in.demo.myapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.demo.myapplication.Model.ReligionItem;
import in.demo.myapplication.R;

public class ReligionAdapter extends RecyclerView.Adapter<ReligionAdapter.ReligionViewHolder> {

    private List<ReligionItem> religionList;
    private OnItemClickListener listener;
    private int selectedItem = RecyclerView.NO_POSITION;

    public ReligionAdapter(List<ReligionItem> religionList, OnItemClickListener listener) {
        this.religionList = religionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReligionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.religion_card_item, parent, false);
        return new ReligionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReligionViewHolder holder, int position) {
        ReligionItem religionItem = religionList.get(position);
        holder.bind(religionItem);

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
        return religionList.size();
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

    public static class ReligionViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewReligion;
        private ImageView imageViewReligion;

        public ReligionViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewReligion = itemView.findViewById(R.id.textViewReligion);
            imageViewReligion = itemView.findViewById(R.id.imageReligion);
        }

        public void bind(ReligionItem religionItem) {
            textViewReligion.setText(religionItem.getReligionName());
            imageViewReligion.setImageResource(religionItem.getImageResource());
        }
    }
}
