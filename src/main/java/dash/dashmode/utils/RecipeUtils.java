package dash.dashmode.utils;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dash.dashmode.mixin.IngredientAccessor;
import dash.dashmode.mixin.ShapedRecipeAccessor;
import dash.dashmode.recipe.DashIngredient;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RecipeUtils {
    /**
     * Parse pattern for shaped/shapeless recipes
     *
     * @param json
     * @return
     */
    public static Map<String, DashIngredient> parsePattern(JsonObject json) {
        Map<String, DashIngredient> map = Maps.newHashMap();

        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            String key = entry.getKey();
            if (key.length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            }

            if (" ".equals(key)) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }

            DashIngredient ingredient = Parser.parseIngredient(entry.getValue().getAsJsonObject());

            map.put(entry.getKey(), ingredient);
        }

        map.put(" ", DashIngredient.EMPTY);
        return map;
    }

    /**
     * Parse whole recipe
     *
     * @param json
     * @return
     */
    public static DefaultedList<DashIngredient> parseIngredients(JsonObject json) {
        Map<String, DashIngredient> components = RecipeUtils.parsePattern(json.getAsJsonObject("key"));
        String[] strings = ShapedRecipeAccessor.rc_combinePattern(ShapedRecipeAccessor.rc_getPattern(JsonHelper.getArray(json, "pattern")));
        DefaultedList<DashIngredient> ingredients = getIngredients(strings, components, strings[0].length(), strings.length, DashIngredient.EMPTY);

        return ingredients;
    }

    public static <T> DefaultedList<T> getIngredients(String[] pattern, Map<String, T> key, int width, int height, T defaultVal) {
        DefaultedList<T> defaultedList = DefaultedList.ofSize(width * height, defaultVal);
        Set<String> set = Sets.newHashSet(key.keySet());
        set.remove(" ");

        for (int i = 0; i < pattern.length; ++i) {
            for (int j = 0; j < pattern[i].length(); ++j) {
                String string = pattern[i].substring(j, j + 1);
                T ingredient = key.get(string);
                if (ingredient == null) {
                    throw new JsonSyntaxException("Pattern references symbol '" + string + "' but it's not defined in the key");
                }

                set.remove(string);
                defaultedList.set(j + width * i, ingredient);
            }
        }

        if (!set.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
        } else {
            return defaultedList;
        }
    }


    /**
     * Match for shaped recipe
     *
     * @param ingredients   - list of ingredients
     * @param inventory     - crafting inventory
     * @param inventorySize - inv size
     * @return
     */
    public static boolean matchShapedSquare(DefaultedList<DashIngredient> ingredients, Inventory inventory, int inventorySize) {
        int recipeSize = (int) Math.ceil(Math.sqrt(ingredients.size()));

        for (int i = 0; i <= inventorySize - recipeSize; ++i) {
            for (int j = 0; j <= inventorySize - recipeSize; ++j) {
                // simple iterate loop (true - false)
                for (int k = 0; k <= 1; k++) {
                    if (matchesSmall(inventory, ingredients, i, j, k == 0, inventorySize, recipeSize)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static boolean matchesSmall(Inventory inventory, DefaultedList<DashIngredient> ingredients, int offsetX, int offsetY, boolean bl, int inventorySize, int recipeSize) {
        for (int i = 0; i < inventorySize; ++i) {
            for (int j = 0; j < inventorySize; ++j) {
                int k = i - offsetX;
                int l = j - offsetY;
                DashIngredient ingredient = DashIngredient.EMPTY;
                if (k >= 0 && l >= 0 && k < recipeSize && l < recipeSize) {
                    if (bl) {
                        ingredient = ingredients.get(recipeSize - k - 1 + l * recipeSize);
                    } else {
                        ingredient = ingredients.get(k + l * recipeSize);
                    }
                }

                if (!ingredient.test(inventory.getStack(i + j * inventorySize))) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Populates tag to cached ItemStacks
     *
     * @param ingredientList - list of ingredients
     */
    public static void populateTag(List<DashIngredient> ingredientList) {
        for (DashIngredient condition : ingredientList) {
            CompoundTag tag = condition.getTag();

            if (tag == null) {
                continue;
            }

            IngredientAccessor mixin = (IngredientAccessor) ((Object) condition.source);
            mixin.rc_cacheMatchingStacks();
            for (ItemStack stack : mixin.rc_getMatchingStacks()) {
                stack.setTag(tag);
            }
        }
    }

    public static void populateTag(DashIngredient... ingredients) {
        populateTag(Arrays.stream(ingredients).collect(Collectors.toList()));
    }
}
