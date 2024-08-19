package in.demo.myapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.demo.myapplication.Model.JobTypeItem;
import in.demo.myapplication.R;

public class JobTypeAdapter extends RecyclerView.Adapter<JobTypeAdapter.JobTypeViewHolder> {

    private List<JobTypeItem> jobTypeList;
    private OnItemClickListener listener;
    private int selectedItem = RecyclerView.NO_POSITION;

    public JobTypeAdapter(List<JobTypeItem> jobTypeList, OnItemClickListener listener) {
        this.jobTypeList = jobTypeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public JobTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_type_card_item, parent, false);
        return new JobTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobTypeViewHolder holder, int position) {
        JobTypeItem jobTypeItem = jobTypeList.get(position);
        holder.bind(jobTypeItem);

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
        return jobTypeList.size();
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

    public static class JobTypeViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewJobType;
        private ImageView imageViewJobType;

        public JobTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewJobType = itemView.findViewById(R.id.textViewJobType);
            imageViewJobType = itemView.findViewById(R.id.imageJobType);
        }

        public void bind(JobTypeItem jobTypeItem) {
            textViewJobType.setText(jobTypeItem.getJobTypeName());
            imageViewJobType.setImageResource(jobTypeItem.getImageResource());
        }
    }
}
