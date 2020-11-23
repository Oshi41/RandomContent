package dash.dashmode.registry;

import com.mojang.serialization.Codec;
import dash.dashmode.DashMod;
import dash.dashmode.event.DataPackLoadedEvent;
import dash.dashmode.utils.DynamicResourceUtils;
import net.fabricmc.fabric.api.biome.v1.OverworldBiomes;
import net.fabricmc.fabric.api.biome.v1.OverworldClimate;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;

import java.util.*;
import java.util.stream.Collectors;

public class DashBiomes {
    public static RegistryKey<Biome> PaperLands;
    public static RegistryKey<Biome> PaperMountains;
    public static RegistryKey<Codec<? extends BiomeSource>> PaperBiomeSource;

    public static void init(String modId) {
        PaperLands = RegistryKey.of(Registry.BIOME_KEY, new Identifier(modId, "paper_lands"));
        PaperMountains = RegistryKey.of(Registry.BIOME_KEY, new Identifier(modId, "paper_mountains"));
        PaperBiomeSource = RegistryKey.of(Registry.BIOME_SOURCE_KEY, new Identifier(modId, "paper"));

        Registry.register(Registry.BIOME_SOURCE, PaperBiomeSource.getValue(), dash.dashmode.biome.PaperBiomeSource.CODEC);

        BiomeRegistry biomeRegistry = new BiomeRegistry(modId, PaperLands, PaperMountains);
        biomeRegistry.injectToOverworld(PaperLands, DashMod.MainConfig.paperBiomeWeight.getValue());

        DataPackLoadedEvent.EVENT.register(biomeRegistry);
    }

    public static class BiomeRegistry implements DataPackLoadedEvent {

        private final String modid;
        private final Map<RegistryKey<Biome>, Boolean> biomes;
        private final Map<RegistryKey<Biome>, Float> overworldBiomes = new HashMap<>();

        public BiomeRegistry(String modid, RegistryKey<Biome>... biomes) {
            this.modid = modid;
            this.biomes = Arrays.stream(biomes).collect(Collectors.toMap(x -> x, x -> false));
        }

        public BiomeRegistry injectToOverworld(RegistryKey<Biome> biome, Float weight) {
            overworldBiomes.put(biome, weight);
            return this;
        }

        @Override
        public void afterLoading(DynamicRegistryManager.Impl manager) {
            if (biomes.isEmpty()) {
                return;
            }

            List<RegistryKey<Biome>> biomeKeys = new ArrayList<>(biomes.keySet());

            for (RegistryKey<Biome> key : biomeKeys) {
                Biome biome = DynamicResourceUtils.find(Registry.BIOME_KEY, key.getValue(), manager);
                if (biome == null) {
                    DashMod.MainLogger.warn("Can't locate biome to register " + key.getValue().toString());
                    continue;
                }

                Registry.register(BuiltinRegistries.BIOME, key.getValue(), biome);
                if (overworldBiomes.containsKey(key)) {
                    Float weight = overworldBiomes.get(key);
                    OverworldBiomes.addContinentalBiome(key, OverworldClimate.TEMPERATE, weight);
                    overworldBiomes.remove(key);
                }

                biomes.remove(key);
            }
        }
    }
}
