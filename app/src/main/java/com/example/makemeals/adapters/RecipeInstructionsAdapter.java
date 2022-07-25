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

public class RecipeInstructionsAdapter extends RecyclerView.Adapter<RecipeInstructionsAdapter.ViewHolder> {

    final private JSONArray instructions;
    final private Context context;
    private InstructionItemBinding binding;
    private final boolean withCheckBox;

    public RecipeInstructionsAdapter(JSONArray ingredients, Context context, boolean withCheckBox) {
        this.instructions = ingredients;
        this.context = context;
        this.withCheckBox = withCheckBox;
    }

    public RecipeInstructionsAdapter(JSONArray ingredients, Context context) {
        this(ingredients, context, false);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = InstructionItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JSONObject instruction = instructions.optJSONObject(position);
        holder.bind(instruction);
    }

    @Override
    public int getItemCount() {
        return instructions.length();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        final private TextView instructionItemTextView;
        private final RecyclerView instructionStepsRecyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            instructionItemTextView = binding.instructionItemTextView;
            instructionStepsRecyclerView = binding.instructionStepsRecyclerView;
        }

        public void bind(JSONObject instruction) {
            String instructionName = "name";
            String instructionSteps = "steps";

            instructionItemTextView.setText(instruction.optString(instructionName));

            JSONArray steps = instruction.optJSONArray(instructionSteps);
            if (steps != null) {
                InstructionStepsAdapter instructionStepsAdapter = new InstructionStepsAdapter(steps, context, withCheckBox);
                instructionStepsRecyclerView.setAdapter(instructionStepsAdapter);
                instructionStepsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            }
        }
    }
}

