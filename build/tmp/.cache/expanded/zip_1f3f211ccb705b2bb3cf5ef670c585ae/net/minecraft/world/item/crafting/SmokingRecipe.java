package net.minecraft.world.item.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class SmokingRecipe extends AbstractCookingRecipe {
    public SmokingRecipe(String p_249312_, CookingBookCategory p_251017_, Ingredient p_252345_, ItemStack p_250002_, float p_250535_, int p_251222_) {
        super(RecipeType.SMOKING, p_249312_, p_251017_, p_252345_, p_250002_, p_250535_, p_251222_);
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(Blocks.SMOKER);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SMOKING_RECIPE;
    }
}