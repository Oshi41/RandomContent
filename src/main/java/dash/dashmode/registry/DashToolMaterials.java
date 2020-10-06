package dash.dashmode.registry;

import dash.dashmode.utils.DashToolMaterial;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public class DashToolMaterials {
    public static final ToolMaterial GoldenMultiTool = new DashToolMaterial(0, 32 * 5, 12.0F, 0.0F, 22, () -> Ingredient.ofItems(Items.GOLD_INGOT));
}
