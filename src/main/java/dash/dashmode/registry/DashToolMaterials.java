package dash.dashmode.registry;

import dash.dashmode.utils.DashToolMaterial;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public class DashToolMaterials {
    public static final ToolMaterial GoldenMultiTool = new DashToolMaterial(0, 32 * 4, 12.0F, 0.0F, 22, () -> Ingredient.ofItems(Items.GOLD_INGOT));
    public static final ToolMaterial IronMultitool = new DashToolMaterial(2, 250 * 4, 6.0F, 2.0F, 14, () -> Ingredient.ofItems(Items.IRON_INGOT));
    public static final ToolMaterial DiamondMultiTool = new DashToolMaterial(3, 1561 * 4, 8.0F, 3.0F, 10, () -> Ingredient.ofItems(Items.DIAMOND));
    public static final ToolMaterial NetheriteMultitool = new DashToolMaterial(4, 2031 * 4, 9.0F, 4.0F, 15, () -> Ingredient.ofItems(Items.NETHERITE_INGOT));
}
