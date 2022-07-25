package com.example.makemeals.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.makemeals.R;
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
        this.context = context;    }

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
        final private TextView dialogIngredientDateTextView;
        final private EditText dialogIngredientNameEditText;
        final private TextView ingredientQuantityTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            dialogIngredientNameEditText = binding.dialogIngredientNameEditText;
            dialogIngredientDateTextView = binding.dialogIngredientDateTextView;
            ingredientQuantityTextView = binding.ingredientQuantityTextView;

            dialogIngredientNameEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String ingredientName = s.toString();
                    if (ingredientName.isEmpty()) {
                        dialogIngredientNameEditText.setError(context.getString(R.string.ingredient_name_is_required));
                    } else {
                        ingredients.set(getAdapterPosition(), ingredientName);
                    }
                }
            });
        }

        public void bind(String ingredient) {
            dialogIngredientNameEditText.setText(ingredient);
            // get current date
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = new Date();
            dialogIngredientDateTextView.setText(dateFormat.format(date));
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

        Snackbar snackbar = Snackbar.make(parentView, context.getString(R.string.ingredient_deleted), Snackbar.LENGTH_LONG)
                .setAction(context.getString(R.string.undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        restoreItem(parentView, ingredient, position);
                    }
                });
        snackbar.show();
    }
}
