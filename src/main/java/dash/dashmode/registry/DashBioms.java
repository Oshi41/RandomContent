package dash.dashmode.registry;

import dash.dashmode.DashMod;
import dash.dashmode.event.ResLoadedEvent;
import dash.dashmode.utils.DynamicResourceUtils;
import net.fabricmc.fabric.api.biome.v1.OverworldBiomes;
import net.fabricmc.fabric.api.biome.v1.OverworldClimate;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

public class DashBioms implements ResLoadedEvent {
    private String modid;
    private Identifier paperLands;


    public DashBioms(String modid) {
        this.modid = modid;
    }

    /**
     * Returns id of overworld biome if it was registered
     *
     * @param manager - dunamic manager
     * @param id
     * @param weight
     * @return
     */
    private static Identifier injectInOverworld(DynamicRegistryManager.Impl manager, Identifier id, float weight) {
        Biome biome = DynamicResourceUtils.find(Registry.BIOME_KEY, id, manager);

        if (biome == null) {
            DashMod.MainLogger.warn("Can't locate biome to inject in overworld " + id.toString());
            return null;
        }

        RegistryKey<Biome> biomeKey = BuiltinRegistries.BIOME.getKey(biome).orElseGet(() -> RegistryKey.of(Registry.BIOME_KEY, id));
        Registry.register(BuiltinRegistries.BIOME, id, biome);

        OverworldBiomes.addContinentalBiome(biomeKey, OverworldClimate.TEMPERATE, weight);
        return id;
    }

    @Override
    public void afterLoading(DynamicRegistryManager.Impl manager) {
        if (paperLands == null)
            paperLands = injectInOverworld(manager, new Identifier(modid, "paper_lands"), DashMod.MainConfig.getConfig().paperBiomeWeight);
    }
}
