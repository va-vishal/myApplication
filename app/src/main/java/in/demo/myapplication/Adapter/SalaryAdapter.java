package in.demo.myapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.demo.myapplication.Model.SalaryItem;
import in.demo.myapplication.R;

public class SalaryAdapter extends RecyclerView.Adapter<SalaryAdapter.SalaryViewHolder> {

    private List<SalaryItem> salaryList;
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

    public SalaryAdapter(List<SalaryItem> salaryList, OnItemClickListener listener) {
        this.salaryList = salaryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SalaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.salary_card_item, parent, false);
        return new SalaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SalaryViewHolder holder, int position) {
        SalaryItem salaryItem = salaryList.get(position);
        holder.bind(salaryItem);

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
        return salaryList.size();
    }

    public int getSelectedPosition() {
        return selectedItem;
    }

    public class SalaryViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewSalaryRange;

        public SalaryViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSalaryRange = itemView.findViewById(R.id.textViewSalaryRange);
        }

        public void bind(SalaryItem salaryItem) {
            textViewSalaryRange.setText(salaryItem.getSalaryRange());
        }
    }
}
