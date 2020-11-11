package dash.dashmode;

import com.mojang.serialization.Lifecycle;
import dash.dashmode.armor.ArmorDescription;
import dash.dashmode.config.Config;
import dash.dashmode.config.DashConfig;
import dash.dashmode.config.JsonEngine;
import dash.dashmode.event.BlockBreakEvent;
import dash.dashmode.registry.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class DashMod implements ModInitializer {
    public static final String ModId = "random_content";
    public static final Config<DashConfig> MainConfig = new Config<>(DashConfig.class, ModId, new JsonEngine());
    public static final Logger MainLogger = LogManager.getLogger(ModId);
    public static final ItemGroup DashItemsTab = FabricItemGroupBuilder
            .build(new Identifier(ModId, "general"),
                    () -> new ItemStack(DashItems.IronBoer));

    public static final RegistryKey<Registry<ArmorDescription>> ArmorSetRegistryKey = RegistryKey.ofRegistry(new Identifier(ModId, "armor_registry"));
    public static final Registry<ArmorDescription> ArmorSetRegistry = new SimpleRegistry<>(ArmorSetRegistryKey, Lifecycle.experimental());

    @Override
    public void onInitialize() {
        MainLogger.info("Random Content is loading...");
        MainConfig.read();

        DashBlocks.init(ModId);
        DashItems.init(ModId);

        DashBlockEntities.init(ModId);
        DashEntities.init(ModId);

        DashArmor.init(ModId);
        DashRecipes.init(ModId);

        DashDimensions.init();
        DashFeatures.init(ModId);
        DashBiomes.init(ModId);
        DashScreens.init(ModId);

        PlayerBlockBreakEvents.AFTER.register(new BlockBreakEvent());
    }
}
