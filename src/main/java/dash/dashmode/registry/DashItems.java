package dash.dashmode.registry;

import dash.dashmode.DashMod;
import net.minecraft.item.FoodComponents;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class DashItems {
    public static final Item PaperApple;

    static {
        PaperApple = new Item((new Item.Settings()).group(DashMod.DashItemsTab).food(FoodComponents.APPLE));
    }

    public static void init(String modId) {
        DashMod.MainLogger.debug("Entering to item registry");

        Registry.register(Registry.ITEM, new Identifier(modId, "paper_apple"), PaperApple);
    }
}
