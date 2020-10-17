package dash.dashmode.registry;

import dash.dashmode.utils.DashToolMaterial;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public class DashToolMaterials {
    public static final ToolMaterial GoldenMultiTool = new DashToolMaterial(0, 32 * 5, 12.0F, 0.0F, 22, () -> Ingredient.ofItems(Items.GOLD_INGOT));
    public static final ToolMaterial IronMultitool = new DashToolMaterial(2, 250 * 5, 6.0F, 2.0F, 14, () -> Ingredient.ofItems(Items.IRON_INGOT));
    public static final ToolMaterial DiamondMultiTool = new DashToolMaterial(3, 1561 * 5, 8.0F, 3.0F, 10, () -> Ingredient.ofItems(Items.DIAMOND));
    public static final ToolMaterial NetheriteMultitool = new DashToolMaterial(4, 2031 * 5, 9.0F, 4.0F, 15, () -> Ingredient.ofItems(Items.NETHERITE_INGOT));

    public static final ToolMaterial GoldenShears = new DashToolMaterial(0, 32 * 0.95f, 12.0F, 0, 22, () -> Ingredient.ofItems(Items.GOLD_INGOT));
    public static final ToolMaterial IronShears = new DashToolMaterial(1, 250 * 0.95f, 6.0F, 0, 14, () -> Ingredient.ofItems(Items.IRON_INGOT));
    public static final ToolMaterial DiamondShears = new DashToolMaterial(2, 1561 * 0.95f, 8.0F, 0, 10, () -> Ingredient.ofItems(Items.DIAMOND));
    public static final ToolMaterial NetheriteShears = new DashToolMaterial(3, 2031 * 0.95f, 9.0F, 0, 15, () -> Ingredient.ofItems(Items.NETHERITE_INGOT));

}
