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

    static {
        PaperApple = new Item((new Item.Settings()).group(DashMod.DashItemsTab).food(FoodComponents.APPLE));
        PaperCrystal = new Item(new Item.Settings().group(DashMod.DashItemsTab));
        GoldenMultitool = new MultiToolItem(DashToolMaterials.GoldenMultiTool, 3, -3F, new Item.Settings().group(DashMod.DashItemsTab));
    }

    public static void init(String modId) {
        DashMod.MainLogger.debug("Entering to item registry");

        Registry.register(Registry.ITEM, new Identifier(modId, "paper_apple"), PaperApple);
        Registry.register(Registry.ITEM, new Identifier(modId, "paper_crystal"), PaperCrystal);
        Registry.register(Registry.ITEM, new Identifier(modId, "golden_multitool"), GoldenMultitool);
    }
}
