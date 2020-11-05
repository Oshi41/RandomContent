package dash.dashmode.recipe;

import com.google.gson.JsonObject;
import dash.dashmode.mixin.IngredientAccessor;
import dash.dashmode.mixin.ShapedRecipeAccessor;
import dash.dashmode.registry.DashRecipes;
import dash.dashmode.utils.JsonUtils;
import dash.dashmode.utils.RecipeUtils;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.stream.Collectors;

public class DashCraftRecipe extends ShapedRecipe {
    private final DefaultedList<DashIngredient> conditions;

    public DashCraftRecipe(ShapedRecipe recipe) {
        this(recipe, getFrom(recipe));
    }

    public DashCraftRecipe(ShapedRecipe recipe, DefaultedList<DashIngredient> conditions) {
        super(recipe.getId(), ((ShapedRecipeAccessor) recipe).rc_getGroup(), recipe.getWidth(), recipe.getHeight(), getFrom(conditions), recipe.getOutput());
        this.conditions = conditions;
    }

    @Override
    public boolean matches(CraftingInventory craftingInventory, World world) {
        return RecipeUtils.matchShapedSquare(conditions, craftingInventory, Math.min(craftingInventory.getHeight(), craftingInventory.getWidth()));
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

        @Override
        public DashCraftRecipe read(Identifier id, JsonObject json) {
            ShapedRecipe read = inner.read(id, json);
            ItemStack output = read.getOutput();

            JsonObject result = json.getAsJsonObject("result");
            if (result.has(tagName)) {
                CompoundTag compound = JsonUtils.parseCompound(result.getAsJsonObject(tagName));
                output.setTag(compound);
            }

            DefaultedList<DashIngredient> ingredients = RecipeUtils.parseIngredients(json);

            return new DashCraftRecipe(read, ingredients);
        }

        @Override
        public DashCraftRecipe read(Identifier id, PacketByteBuf buf) {
            return new DashCraftRecipe(inner.read(id, buf));
        }

        @Override
        public void write(PacketByteBuf buf, DashCraftRecipe recipe) {
            RecipeUtils.populateTag(recipe.conditions);
            inner.write(buf, recipe);
        }
    }
}
