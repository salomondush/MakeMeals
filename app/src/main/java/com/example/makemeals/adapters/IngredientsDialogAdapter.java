package com.example.makemeals.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.makemeals.databinding.DialogIngredientItemBinding;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class IngredientsDialogAdapter extends RecyclerView.Adapter<IngredientsDialogAdapter.ViewHolder> {

    private List<String> ingredients;
    private Context context;
    private DialogIngredientItemBinding binding;

    public IngredientsDialogAdapter(List<String> ingredients, Context context) {
        this.ingredients = ingredients;
        this.context = context;
    }

    @NonNull
    @Override
    public IngredientsDialogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = DialogIngredientItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new IngredientsDialogAdapter.ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientsDialogAdapter.ViewHolder holder, int position) {
        String ingredient = ingredients.get(position);
        holder.bind(ingredient);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDialogIngredientDate;
        private EditText etIngredientName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            etIngredientName = binding.etDialogIngredientName;
            tvDialogIngredientDate = binding.tvDialogIngredientDate;
        }

        public void bind(String ingredient) {
            etIngredientName.setText(ingredient);
            // get current date
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = new Date();
            tvDialogIngredientDate.setText(dateFormat.format(date));
        }
    }
}
