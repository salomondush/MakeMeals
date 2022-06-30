package com.example.makemeals.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.makemeals.databinding.DialogIngredientItemBinding;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class IngredientsDialogAdapter extends RecyclerView.Adapter<IngredientsDialogAdapter.ViewHolder> {

    final private List<String> ingredients;
    final private Context context;
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
        final private TextView tvDialogIngredientDate;
        final private EditText etIngredientName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            etIngredientName = binding.etDialogIngredientName;
            tvDialogIngredientDate = binding.tvDialogIngredientDate;

            etIngredientName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        String ingredientName = etIngredientName.getText().toString();
                        if (ingredientName.isEmpty()) {
                            etIngredientName.setError("Ingredient name is required");
                        } else {
                            ingredients.set(getAdapterPosition(), ingredientName);
                        }
                    }
                }
            });
        }

        public void bind(String ingredient) {
            etIngredientName.setText(ingredient);
            // get current date
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = new Date();
            tvDialogIngredientDate.setText(dateFormat.format(date));
        }
    }

    public static class SwipeHelper extends ItemTouchHelper.SimpleCallback{
        private final IngredientsDialogAdapter adapter;
        private final RecyclerView parentView;

        public SwipeHelper(IngredientsDialogAdapter adapter, RecyclerView parentView) {
            super(0, ItemTouchHelper.LEFT);
            this.adapter = adapter;
            this.parentView = parentView;
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            adapter.deleteItem(parentView, position);
        }
    }

    private void restoreItem(RecyclerView parentView, String ingredient, int position) {
        ingredients.add(position, ingredient);
        notifyItemInserted(position);
        parentView.scrollToPosition(position);
    }

    private void deleteItem(RecyclerView parentView, int position) {
        String ingredient = ingredients.get(position);
        ingredients.remove(position);
        notifyItemRemoved(position);

        Snackbar snackbar = Snackbar.make(parentView, "Ingredient deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        restoreItem(parentView, ingredient, position);
                    }
                });
        snackbar.show();
    }
}
