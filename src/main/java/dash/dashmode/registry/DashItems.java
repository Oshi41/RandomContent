package dash.dashmode.registry;

import dash.dashmode.DashMod;
import dash.dashmode.item.MultiToolItem;
import net.minecraft.item.FoodComponents;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class DashItems {
    public static final Item PaperApple;
    public static final Item PaperCrystal;

    public static final Item GoldenMultitool;
    public static final Item IronMultitool;
    public static final Item DiamondMultitool;
    public static final Item NetheriteMultitool;

    static {
        PaperApple = new Item((new Item.Settings()).group(DashMod.DashItemsTab).food(FoodComponents.APPLE));
        PaperCrystal = new Item(new Item.Settings().group(DashMod.DashItemsTab));

        GoldenMultitool = new MultiToolItem(DashToolMaterials.GoldenMultiTool, 3, -3F, new Item.Settings().group(DashMod.DashItemsTab));
        IronMultitool = new MultiToolItem(DashToolMaterials.IronMultitool, 3, -3F, new Item.Settings().group(DashMod.DashItemsTab));
        DiamondMultitool = new MultiToolItem(DashToolMaterials.DiamondMultiTool, 3, -3F, new Item.Settings().group(DashMod.DashItemsTab));
        NetheriteMultitool = new MultiToolItem(DashToolMaterials.NetheriteMultitool, 3, -3F, new Item.Settings().group(DashMod.DashItemsTab));
    }

    public static void init(String modId) {
        DashMod.MainLogger.debug("Entering to item registry");

        Registry.register(Registry.ITEM, new Identifier(modId, "paper_apple"), PaperApple);
        Registry.register(Registry.ITEM, new Identifier(modId, "paper_crystal"), PaperCrystal);
        Registry.register(Registry.ITEM, new Identifier(modId, "golden_multitool"), GoldenMultitool);
        Registry.register(Registry.ITEM, new Identifier(modId, "iron_multitool"), IronMultitool);
        Registry.register(Registry.ITEM, new Identifier(modId, "diamond_multitool"), DiamondMultitool);
        Registry.register(Registry.ITEM, new Identifier(modId, "netherite_multitool"), NetheriteMultitool);
    }
}
