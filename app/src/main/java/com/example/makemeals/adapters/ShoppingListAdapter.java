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
import com.example.makemeals.databinding.RecipeIngredientItemBinding;
import com.example.makemeals.models.ShoppingItem;

import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder>{
    private final List<ShoppingItem> shoppingItems;
    private final Context context;
    private RecipeIngredientItemBinding binding;
    private OnItemClickListener isCheckedListener;

    public ShoppingListAdapter(List<ShoppingItem> shoppingItems, Context context) {
        this.shoppingItems = shoppingItems;
        this.context = context;
    }

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    // Define the method that allows the parent activity or fragment to define the listener
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
        private final TextView tvDetailIngredientName;
        private final TextView tvIngredientQuantity;
        private final CheckBox cbSelectIngredient;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDetailIngredientName = binding.tvDetailIngredientName;
            tvIngredientQuantity = binding.tvIngredientQuantity;
            ivIngredientImage = binding.ivIngredientImage;
            cbSelectIngredient = binding.cbSelectIngredient;

            cbSelectIngredient.setVisibility(View.VISIBLE);
            cbSelectIngredient.setOnClickListener(v -> {
                if (cbSelectIngredient.isChecked()) {
                    tvDetailIngredientName.setPaintFlags(tvDetailIngredientName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    tvDetailIngredientName.setPaintFlags(tvDetailIngredientName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }

                isCheckedListener.onItemClick(v, getAdapterPosition());
            });
        }



        public void bind(ShoppingItem shoppingItem) {
            JSONObject ingredientFields = shoppingItem.getFields();
            tvDetailIngredientName.setText(ingredientFields.optString("name"));

            JSONObject measure = Objects.requireNonNull(ingredientFields.optJSONObject("measures")).optJSONObject("us");
            String quantity = Objects.requireNonNull(measure).optInt("amount") + " " + measure.optString("unitShort");
            tvIngredientQuantity.setText(quantity);

            String image = ingredientFields.optString("image");
            ;
            Glide.with(context).load(Constant.IMAGE_BASE_URL + image)
                    .centerCrop()
                    .transform(new RoundedCorners(Constant.IMAGE_RADIUS))
                    .into(ivIngredientImage);

            if (shoppingItem.getIsChecked()){
                cbSelectIngredient.setChecked(true);
                tvDetailIngredientName.setPaintFlags(tvDetailIngredientName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                cbSelectIngredient.setChecked(false);
                tvDetailIngredientName.setPaintFlags(tvDetailIngredientName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        }
    }
}
