package dash.dashmode.recipe;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dash.dashmode.mixin.IngredientAccessor;
import dash.dashmode.mixin.ShapedRecipeAccessor;
import dash.dashmode.registry.DashRecipes;
import dash.dashmode.utils.NbtUtil;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DashCraftRecipe extends ShapedRecipe {
    private final DefaultedList<DashIngredient> conditions;

    public DashCraftRecipe(ShapedRecipe recipe) {
        this(recipe, getFrom(recipe));
    }

    public DashCraftRecipe(ShapedRecipe recipe, DefaultedList<DashIngredient> conditions) {
        super(recipe.getId(), ((ShapedRecipeAccessor) recipe).getGroup(), recipe.getWidth(), recipe.getHeight(), getFrom(conditions), recipe.getOutput());
        this.conditions = conditions;
    }

    @Override
    public boolean matches(CraftingInventory craftingInventory, World world) {
        for (int i = 0; i <= craftingInventory.getWidth() - getWidth(); ++i) {
            for (int j = 0; j <= craftingInventory.getHeight() - getHeight(); ++j) {
                // simple iterate loop (true - false)
                for (int k = 0; k <= 1; k++) {
                    if (this.matchesSmall(craftingInventory, i, j, k == 0)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean matchesSmall(CraftingInventory inv, int offsetX, int offsetY, boolean bl) {
        for (int i = 0; i < inv.getWidth(); ++i) {
            for (int j = 0; j < inv.getHeight(); ++j) {
                int k = i - offsetX;
                int l = j - offsetY;
                DashIngredient ingredient = DashIngredient.EMPTY;
                if (k >= 0 && l >= 0 && k < getWidth() && l < getHeight()) {
                    if (bl) {
                        ingredient = this.conditions.get(getWidth() - k - 1 + l * getWidth());
                    } else {
                        ingredient = this.conditions.get(k + l * getWidth());
                    }
                }

                if (!ingredient.test(inv.getStack(i + j * inv.getWidth()))) {
                    return false;
                }
            }
        }

        return true;
    }

    private static DefaultedList<DashIngredient> getFrom(ShapedRecipe recipe) {
        DefaultedList<Ingredient> inputs = recipe.getPreviewInputs();

        DefaultedList<DashIngredient> result = DefaultedList.ofSize(inputs.size(), DashIngredient.EMPTY);

        for (int i = 0; i < inputs.size(); i++) {
            IngredientAccessor mixin = (IngredientAccessor) ((Object) inputs.get(i));
            DashIngredient ingredient = new DashIngredient(inputs.get(i));

            mixin.rc_cacheMatchingStacks();
            ItemStack[] stacks = mixin.rc_getMatchingStacks();

            if (stacks != null && stacks.length > 0) {
                ItemStack stack = stacks[0];
                if (stack.hasTag()) {
                    CompoundTag tag = stack.getTag();
                    ingredient = ingredient.and(tag);
                }
            }

            result.set(i, ingredient);
        }

        return result;
    }

    private static DefaultedList<Ingredient> getFrom(DefaultedList<DashIngredient> source) {
        DefaultedList<Ingredient> list = DefaultedList.of();
        list.addAll(source.stream().map(x -> x.source).collect(Collectors.toList()));
        return list;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return DashRecipes.DashRecipesSerializer;
    }

    public static class Serializer implements RecipeSerializer<DashCraftRecipe> {
        private static final String tagName = "compound";
        private RecipeSerializer<ShapedRecipe> inner = RecipeSerializer.SHAPED;

        private static <T> DefaultedList<T> getIngredients(String[] pattern, Map<String, T> key, int width, int height, T defaultVal) {
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

        @Override
        public DashCraftRecipe read(Identifier id, JsonObject json) {
            ShapedRecipe read = inner.read(id, json);
            ItemStack output = read.getOutput();

            JsonObject result = json.getAsJsonObject("result");
            if (result.has(tagName)) {
                CompoundTag compound = NbtUtil.parseCompound(result.getAsJsonObject(tagName));
                output.setTag(compound);
            }

            Map<String, DashIngredient> components = getComponents(json.getAsJsonObject("key"));
            String[] strings = ShapedRecipeAccessor.combinePattern(ShapedRecipeAccessor.getPattern(JsonHelper.getArray(json, "pattern")));
            DefaultedList<DashIngredient> ingredients = getIngredients(strings, components, strings[0].length(), strings.length, DashIngredient.EMPTY);

            return new DashCraftRecipe(read, ingredients);
        }

        @Override
        public DashCraftRecipe read(Identifier id, PacketByteBuf buf) {
            return new DashCraftRecipe(inner.read(id, buf));
        }

        @Override
        public void write(PacketByteBuf buf, DashCraftRecipe recipe) {
            for (DashIngredient condition : recipe.conditions) {
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

            inner.write(buf, recipe);
        }

        private Map<String, DashIngredient> getComponents(JsonObject json) {
            Map<String, DashIngredient> map = Maps.newHashMap();

            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                String key = entry.getKey();
                if (key.length() != 1) {
                    throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
                }

                if (" ".equals(key)) {
                    throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
                }

                DashIngredient ingredient = new DashIngredient(Ingredient.fromJson(entry.getValue()));

                if (entry.getValue().getAsJsonObject().has(tagName)) {
                    String rawJson = NbtUtil.GSON.toJson(entry.getValue().getAsJsonObject().getAsJsonObject(tagName));
                    try {
                        CompoundTag tag = StringNbtReader.parse(rawJson);
                        ingredient = ingredient.and(tag);
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                }

                map.put(entry.getKey(), ingredient);
            }

            map.put(" ", DashIngredient.EMPTY);
            return map;
        }
    }
}
