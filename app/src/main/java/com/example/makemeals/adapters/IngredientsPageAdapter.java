package com.example.makemeals.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.makemeals.R;
import com.example.makemeals.databinding.IngredientItemBinding;
import com.example.makemeals.models.Ingredient;
import com.google.android.material.snackbar.Snackbar;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.List;

public class IngredientsPageAdapter extends RecyclerView.Adapter<IngredientsPageAdapter.ViewHolder> {
    final private List<Ingredient> ingredients;
    final private Context context;
    private IngredientItemBinding binding;
    private OnItemClickListener removeIngredientClickListener;
    private OnItemClickListener onSelectIngredientListener;
    final private boolean isSearchIngredient;

    public IngredientsPageAdapter(List<Ingredient> ingredients, Context context, boolean isSearchIngredient) {
        this.ingredients = ingredients;
        this.context = context;
        this.isSearchIngredient = isSearchIngredient;
    }

    @NonNull
    @Override
    public IngredientsPageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = IngredientItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new IngredientsPageAdapter.ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientsPageAdapter.ViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        holder.bind(ingredient);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnRemoveIngredientClickListener(OnItemClickListener listener) {
        this.removeIngredientClickListener = listener;
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnSelectIngredientClickListener(OnItemClickListener listener) {
        this.onSelectIngredientListener = listener;
    }

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final private TextView name;
        final private TextView date;
        final private ImageButton removeIngredient;
        final private CheckBox cbSelectIngredient;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = binding.ingredientName;
            date = binding.ingredientDate;
            removeIngredient = binding.removeButton;
            cbSelectIngredient = binding.cbSelectIngredient;

            removeIngredient.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeIngredientClickListener.onItemClick(itemView, getAdapterPosition());
                }
            });

            cbSelectIngredient.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    onSelectIngredientListener.onItemClick(itemView, getAdapterPosition());
                }
            });
        }

        public void bind(Ingredient ingredient) {
            name.setText(ingredient.getName());
            date.setText(ingredient.getCreatedAt().toString());

            if (isSearchIngredient){
                removeIngredient.setVisibility(View.GONE);
                cbSelectIngredient.setVisibility(View.VISIBLE);
            } else {
                removeIngredient.setVisibility(View.VISIBLE);
                cbSelectIngredient.setVisibility(View.GONE);
            }
        }
    }

    public static class SwipeHelper extends ItemTouchHelper.SimpleCallback{
        private final IngredientsPageAdapter adapter;
        private final RecyclerView parentView;

        public SwipeHelper(IngredientsPageAdapter adapter, RecyclerView parentView) {
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

    private void restoreItem(RecyclerView parentView, Ingredient ingredient, int position) {

        Ingredient newIngredient = new Ingredient();
        newIngredient.setName(ingredient.getName());
        newIngredient.setCreatedAt(ingredient.getCreatedAt());
        newIngredient.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(context, context.getString(R.string.error) + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                ingredients.add(position, newIngredient);
                notifyItemInserted(position);
                parentView.scrollToPosition(position);
            }
        });
    }

    public void deleteItem(RecyclerView parentView, int position) {
        Ingredient ingredient = ingredients.get(position);
        ingredient.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
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
                } else {
                    Toast.makeText(context, context.getString(R.string.error_deleting_ingredient), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
