package dash.dashmode;

import dash.dashmode.registry.DashBlocks;
import dash.dashmode.registry.DashFeatures;
import dash.dashmode.registry.DashItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class DashMod implements ModInitializer {
    public static final String ModId = "rc";
    public static final Logger MainLogger = LogManager.getLogger(ModId);
    public static final ItemGroup DashItemsTab = FabricItemGroupBuilder
            .build(new Identifier(ModId, "general"),
                    () -> new ItemStack(DashBlocks.PaperStone.asItem()));

    @Override
    public void onInitialize() {
        MainLogger.info("Random Content is loading...");

        DashBlocks.init(ModId);
        DashItems.init(ModId);

        DashFeatures.init();
    }
}
