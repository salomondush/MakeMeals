package com.example.makemeals.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.makemeals.databinding.InstructionStepItemBinding;

import org.json.JSONArray;
import org.json.JSONObject;

public class InstructionStepsAdapter extends RecyclerView.Adapter<InstructionStepsAdapter.ViewHolder> {
    private JSONArray steps;
    private Context context;
    private InstructionStepItemBinding binding;

    public InstructionStepsAdapter(JSONArray steps, Context context) {
        this.steps = steps;
        this.context = context;
    }

    @NonNull
    @Override
    public InstructionStepsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = InstructionStepItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new InstructionStepsAdapter.ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull InstructionStepsAdapter.ViewHolder holder, int position) {
        holder.bind(steps.optJSONObject(position));
    }

    @Override
    public int getItemCount() {
        return steps.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final private TextView tvInstructionStepNumber;
        final private TextView tvInstructionStepText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInstructionStepNumber = binding.tvInstructionStepNumber;
            tvInstructionStepText = binding.tvInstructionStepText;
        }

        public void bind(JSONObject step) {
            tvInstructionStepNumber.setText(String.format("%s.", step.optInt("number")));
            tvInstructionStepText.setText(step.optString("step"));
        }
    }
}
