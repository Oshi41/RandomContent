package dash.dashmode.registry;

import dash.dashmode.DashMod;
import dash.dashmode.recipe.DashCraftRecipe;
import dash.dashmode.recipe.DashForgeRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class DashRecipes {
    public static final RecipeSerializer<DashCraftRecipe> DashRecipesSerializer = new DashCraftRecipe.Serializer();
    public static final RecipeSerializer<DashForgeRecipe> DashForgeSerializer = new DashForgeRecipe.Serializer();

    public static RecipeType<DashForgeRecipe> DashForgeRecipeType = RecipeType.register(new Identifier(DashMod.ModId, "forge").toString());

    public static void init(String modid) {
        Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(modid, "crafting_shaped"), DashRecipesSerializer);
        Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(modid, "forge"), DashForgeSerializer);
    }
}
