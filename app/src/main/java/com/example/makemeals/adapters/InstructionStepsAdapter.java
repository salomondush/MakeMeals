package com.example.makemeals.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.makemeals.databinding.InstructionStepItemBinding;

import org.json.JSONArray;
import org.json.JSONObject;

public class InstructionStepsAdapter extends RecyclerView.Adapter<InstructionStepsAdapter.ViewHolder> {
    private final JSONArray steps;
    private final Context context;
    private InstructionStepItemBinding binding;
    private final boolean withCheckBox;

    public InstructionStepsAdapter(JSONArray steps, Context context, boolean withCheckBox) {
        this.steps = steps;
        this.context = context;
        this.withCheckBox = withCheckBox;
    }

    public InstructionStepsAdapter(JSONArray steps, Context context) {
        this(steps, context, false);
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
            CheckBox cbInstructionStep = binding.cbInstructionStep;

            if (withCheckBox) {
                cbInstructionStep.setVisibility(View.VISIBLE);
            } else {
                cbInstructionStep.setVisibility(View.GONE);
            }
        }

        public void bind(JSONObject step) {
            String stepNumber = "number";
            String stepText = "step";

            tvInstructionStepNumber.setText(String.format("%s.", step.optInt(stepNumber)));
            tvInstructionStepText.setText(step.optString(stepText));
        }
    }
}
