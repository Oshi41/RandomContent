package dash.dashmode.registry;

import dash.dashmode.DashMod;
import net.fabricmc.fabric.api.biome.v1.OverworldBiomes;
import net.fabricmc.fabric.api.biome.v1.OverworldClimate;
import net.fabricmc.fabric.mixin.biome.BuiltinBiomesAccessor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

import java.awt.*;

public class DashBiomes {
    public static final Biome PaperBiome;
    public static final RegistryKey<Biome> PaperBiomeKey;
    private static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> PaperSurfaceBuilder;


    static {
        PaperBiomeKey = RegistryKey.of(Registry.BIOME_KEY,
                new Identifier(DashMod.ModId, "paper_biome"));

        PaperSurfaceBuilder = SurfaceBuilder.DEFAULT.withConfig(new TernarySurfaceConfig(
                DashBlocks.PaperGrass.getDefaultState(),
                DashBlocks.PaperDirt.getDefaultState(),
                DashBlocks.PaperStone.getDefaultState()));

        SpawnSettings.Builder spawnSettings = new SpawnSettings.Builder();
        GenerationSettings.Builder generationSettings = new GenerationSettings.Builder();

        generationSettings.surfaceBuilder(PaperSurfaceBuilder);
        DefaultBiomeFeatures.addFarmAnimals(spawnSettings);
        DefaultBiomeFeatures.addMonsters(spawnSettings, 95, 5, 100);
        DefaultBiomeFeatures.addDefaultUndergroundStructures(generationSettings);
        DefaultBiomeFeatures.addLandCarvers(generationSettings);
        DefaultBiomeFeatures.addDefaultLakes(generationSettings);
        DefaultBiomeFeatures.addDungeons(generationSettings);
        DefaultBiomeFeatures.addFrozenTopLayer(generationSettings);

        generationSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, DashFeatures.PaperCoalOre);
        generationSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, DashFeatures.PaperIronOre);
        generationSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, DashFeatures.PaperGoldOre);
        generationSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, DashFeatures.PaperRedstoneOre);
        generationSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, DashFeatures.PaperDiamondOre);
        generationSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, DashFeatures.PaperLapisOre);
        generationSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, DashFeatures.PaperEmeraldOre);

        generationSettings.feature(GenerationStep.Feature.VEGETAL_DECORATION, DashFeatures.RandomPaperTree);
        generationSettings.feature(GenerationStep.Feature.VEGETAL_DECORATION, DashFeatures.PaperFlowers);

        int white = Color.WHITE.getRGB();

        PaperBiome = (new Biome.Builder())
                .precipitation(Biome.Precipitation.RAIN)
                .category(Biome.Category.PLAINS)
                .depth(0.125F)
                .scale(0.05F)
                .temperature(0.8F)
                .downfall(0.4F)
                .effects((new BiomeEffects.Builder())
                        .waterColor(white)
                        .waterFogColor(white)
                        .fogColor(white)
                        .skyColor(white)
                        .build())
                .spawnSettings(spawnSettings.build())
                .generationSettings(generationSettings.build())
                .build();
    }

    public static void init() {
        Registry.register(BuiltinRegistries.CONFIGURED_SURFACE_BUILDER,
                new Identifier(DashMod.ModId, "paper_surface"),
                PaperSurfaceBuilder);

        Registry.register(BuiltinRegistries.BIOME, PaperBiomeKey.getValue(), PaperBiome);
        BuiltinBiomesAccessor.getBY_RAW_ID().put(BuiltinRegistries.BIOME.getRawId(PaperBiome), PaperBiomeKey);

        OverworldBiomes.addContinentalBiome(PaperBiomeKey, OverworldClimate.TEMPERATE, DashMod.MainConfig.getConfig().PaperBiomeWeight);
    }
}
