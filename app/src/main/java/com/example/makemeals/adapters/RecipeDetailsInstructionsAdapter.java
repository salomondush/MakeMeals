package com.example.makemeals.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.makemeals.databinding.InstructionItemBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

public class RecipeDetailsInstructionsAdapter extends RecyclerView.Adapter<RecipeDetailsInstructionsAdapter.ViewHolder> {

    final private List<JSONObject> instructions;
    final private Context context;
    private InstructionItemBinding binding;

    private InstructionStepsAdapter instructionStepsAdapter;

    public RecipeDetailsInstructionsAdapter(List<JSONObject> instructions, Context context) {
        this.instructions = instructions;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = InstructionItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JSONObject instruction = instructions.get(position);
        holder.bind(instruction);
    }

    @Override
    public int getItemCount() {
        return instructions.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        final private TextView tvInstructionItemText;
        private RecyclerView rvInstructionSteps;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInstructionItemText = binding.tvInstructionItemText;
            rvInstructionSteps = binding.rvInstructionSteps;
        }

        public void bind(JSONObject instruction) {

            tvInstructionItemText.setText(instruction.optString("name"));

            JSONArray steps = instruction.optJSONArray("steps");
            if (steps != null) {
                instructionStepsAdapter = new InstructionStepsAdapter(steps, context);
                rvInstructionSteps.setAdapter(instructionStepsAdapter);
                rvInstructionSteps.setLayoutManager(new LinearLayoutManager(context));
            }
        }
    }
}

