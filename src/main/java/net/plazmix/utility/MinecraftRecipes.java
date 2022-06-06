package net.plazmix.utility;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import net.plazmix.PlazmixApiPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;

@UtilityClass
public class MinecraftRecipes {

    public void createRecipe(@NonNull ItemStack resultItem,
                             @NonNull Consumer<MinecraftRecipeData> recipeDataConsumer) {

        MinecraftRecipeData minecraftRecipeData = new MinecraftRecipeData(resultItem);
        recipeDataConsumer.accept(minecraftRecipeData);

        String[] recipeShape = new String[3];

        if (Bukkit.getServer().getRecipesFor(resultItem).stream().anyMatch(recipe -> (recipe instanceof ShapedRecipe))) {
            return;
        }

        ShapedRecipe shapedRecipe = new ShapedRecipe(resultItem);

        int shapeLineCounter = 0;
        for (int i = 0 ; i < 9 ; i++) {
            ItemStack itemStack = minecraftRecipeData.recipeCraft[i];
            char ingredientChar = Character.toChars('a' + i)[0];

            if (itemStack == null) {
                ingredientChar = ' ';
            }

            String shapeLine = recipeShape[shapeLineCounter];
            recipeShape[shapeLineCounter] = (shapeLine == null ? "" : shapeLine) + ingredientChar;

            if ((i + 1) % 3 == 0) {
                shapeLineCounter++;
            }
        }

        shapedRecipe.shape(recipeShape);

        shapeLineCounter = 0;
        for (int i = 0; i < 9 ; i++) {

            if (i > 0 && i % 3 == 0) {
                shapeLineCounter++;
            }

            String shapeLine = recipeShape[shapeLineCounter];
            char ingredientChar = shapeLine.charAt(i - (shapeLineCounter * 3));

            if (ingredientChar == ' ') {
                continue;
            }

            shapedRecipe.setIngredient(ingredientChar, minecraftRecipeData.recipeCraft[i].getData());
        }

        Bukkit.getServer().addRecipe(shapedRecipe);
    }


    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static class MinecraftRecipeData {

        private final ItemStack recipeResult;
        private final ItemStack[] recipeCraft = new ItemStack[9];

        public void setCraftSlot(int craftSlot, @NonNull ItemStack itemStack) {
            if (craftSlot > 9 || craftSlot < 1) {
                return;
            }

            recipeCraft[craftSlot - 1] = itemStack;
        }

        public void setCraftSlot(int craftSlot, @NonNull Material material) {
            if (craftSlot > 9 || craftSlot < 1) {
                return;
            }

            recipeCraft[craftSlot - 1] = new ItemStack(material);
        }

        public void setCraftSlot(int craftSlot, @NonNull MaterialData materialData) {
            if (craftSlot > 9 || craftSlot < 1) {
                return;
            }

            recipeCraft[craftSlot - 1] = materialData.toItemStack(1);
        }
    }
}
