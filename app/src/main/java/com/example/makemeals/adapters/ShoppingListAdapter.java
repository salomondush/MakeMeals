package com.example.makemeals.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.makemeals.Constant;
import com.example.makemeals.R;
import com.example.makemeals.databinding.RecipeIngredientItemBinding;
import com.example.makemeals.models.ShoppingItem;

import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {
    private final List<ShoppingItem> shoppingItems;
    private final Context context;
    private RecipeIngredientItemBinding binding;
    private OnItemClickListener isCheckedListener;

    public ShoppingListAdapter(List<ShoppingItem> shoppingItems, Context context) {
        this.shoppingItems = shoppingItems;
        this.context = context;
    }

    /**
     * Define the listener interface for click events on items.
     */
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    /**
     * Allows the parent activity or fragment to define an onChecked click  listener for the
     * checkbox.
     * @param isCheckedListener
     */
    public void setOnIsCheckedListener(OnItemClickListener isCheckedListener) {
        this.isCheckedListener = isCheckedListener;
    }

    @NonNull
    @Override
    public ShoppingListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RecipeIngredientItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ShoppingListAdapter.ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingListAdapter.ViewHolder holder, int position) {
        ShoppingItem shoppingItem = shoppingItems.get(position);
        holder.bind(shoppingItem);
    }

    @Override
    public int getItemCount() {
        return shoppingItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final private ImageView ivIngredientImage;
        private final TextView detailIngredientNameTextView;
        private final TextView ingredientQuantityTextView;
        private final CheckBox selectIngredientCheckBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            detailIngredientNameTextView = binding.detailIngredientNameTextView;
            ingredientQuantityTextView = binding.ingredientQuantityTextView;
            ivIngredientImage = binding.ivIngredientImage;
            selectIngredientCheckBox = binding.selectIngredientCheckBox;

            selectIngredientCheckBox.setVisibility(View.VISIBLE);
            selectIngredientCheckBox.setOnClickListener(v -> {
                if (selectIngredientCheckBox.isChecked()) {
                    detailIngredientNameTextView.setPaintFlags(detailIngredientNameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    detailIngredientNameTextView.setPaintFlags(detailIngredientNameTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }

                isCheckedListener.onItemClick(v, getAdapterPosition());
            });
        }


        public void bind(ShoppingItem shoppingItem) {
            JSONObject ingredientFields = shoppingItem.getFields();
            String name = "name";
            String measures = "measures";
            String unitType = "us";
            String shortUnit = "unitShort";
            String amount = "amount";
            String imageField = "image";

            detailIngredientNameTextView.setText(ingredientFields.optString(name));
            JSONObject measure = Objects.requireNonNull(ingredientFields.optJSONObject(measures)).optJSONObject(unitType);
            String quantity = Objects.requireNonNull(measure).optInt(amount) + " " + measure.optString(shortUnit);
            ingredientQuantityTextView.setText(quantity);

            String image = ingredientFields.optString(imageField);
            ;
            Glide.with(context).load(Constant.IMAGE_BASE_URL + image)
                    .placeholder(R.drawable.recipe_image_placeholder)
                    .centerCrop()
                    .transform(new RoundedCorners(Constant.IMAGE_RADIUS))
                    .into(ivIngredientImage);

            if (shoppingItem.getIsChecked()) {
                selectIngredientCheckBox.setChecked(true);
                detailIngredientNameTextView.setPaintFlags(detailIngredientNameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                selectIngredientCheckBox.setChecked(false);
                detailIngredientNameTextView.setPaintFlags(detailIngredientNameTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        }
    }
}
