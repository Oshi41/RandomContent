package dash.dashmode.registry;

import dash.dashmode.recipe.DashShapedRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class DashRecipes {
    public static final RecipeSerializer<DashShapedRecipe> DashRecipes = new DashShapedRecipe.Serializer();

    public static void init(String modid) {
        Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(modid, "crafting_shaped"), DashRecipes);
    }
}
