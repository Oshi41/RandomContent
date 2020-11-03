package dash.dashmode.recipe;

import com.google.gson.JsonObject;
import dash.dashmode.registry.DashRecipes;
import dash.dashmode.utils.Parser;
import dash.dashmode.utils.RecipeUtils;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class DashForgeRecipe implements Recipe<Inventory> {
    public final int experience;
    private final Identifier id;
    private final DefaultedList<DashIngredient> inner;
    private final DashIngredient catalyst;
    private final String group;
    private ItemStack output;

    public DashForgeRecipe(Identifier id, DefaultedList<DashIngredient> inner, DashIngredient catalyst, int experience, ItemStack output, String group) {
        this.id = id;
        this.inner = inner;
        this.catalyst = catalyst;
        this.experience = experience;
        this.output = output;
        this.group = group;
    }

    @Override
    public boolean matches(Inventory inv, World world) {
        if (!RecipeUtils.matchShapedSquare(inner, inv, 3))
            return false;

        return catalyst.test(inv.getStack(9));
    }

    @Override
    public ItemStack craft(Inventory inv) {
        return getOutput().copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput() {
        return output;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return DashRecipes.DashForgeSerializer;
    }

    @Override
    public RecipeType<?> getType() {
        return DashRecipes.DashForgeRecipeType;
    }

    public static class Serializer implements RecipeSerializer<DashForgeRecipe> {
        @Override
        public DashForgeRecipe read(Identifier id, JsonObject json) {
            DefaultedList<DashIngredient> shaped = RecipeUtils.parseIngredients(json.getAsJsonObject("shaped"));
            int exp = JsonHelper.getInt(json, "experience", 0);

            DashIngredient catalyst = DashIngredient.EMPTY;
            JsonObject catalystJson = json.getAsJsonObject("catalyst");
            if (catalystJson != null) {
                catalyst = Parser.parseIngredient(catalystJson);
            }

            ItemStack output = Parser.parseItemStack(json.getAsJsonObject("shaped").getAsJsonObject("result"));
            String group = JsonHelper.getString(json, "group", "");

            return new DashForgeRecipe(id, shaped, catalyst, exp, output, group);
        }

        @Override
        public DashForgeRecipe read(Identifier id, PacketByteBuf buf) {
            DefaultedList<DashIngredient> defaultedList = DefaultedList.ofSize(buf.readVarInt(), DashIngredient.EMPTY);
            String group = buf.readString(32767);
            int experience = buf.readVarInt();

            for (int k = 0; k < defaultedList.size(); ++k) {
                defaultedList.set(k, new DashIngredient(Ingredient.fromPacket(buf)));
            }

            DashIngredient catalyst = new DashIngredient(Ingredient.fromPacket(buf));

            ItemStack output = buf.readItemStack();

            return new DashForgeRecipe(id, defaultedList, catalyst, experience, output, group);
        }

        @Override
        public void write(PacketByteBuf buf, DashForgeRecipe recipe) {
            DefaultedList<DashIngredient> ingredients = recipe.inner;

            RecipeUtils.populateTag(ingredients);
            RecipeUtils.populateTag(recipe.catalyst);

            buf.writeVarInt(ingredients.size());
            buf.writeString(recipe.group);
            buf.writeVarInt(recipe.experience);

            for (DashIngredient ingredient : ingredients) {
                ingredient.source.write(buf);
            }

            recipe.catalyst.source.write(buf);
            buf.writeItemStack(recipe.output);
        }
    }
}
