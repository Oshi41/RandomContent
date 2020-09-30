package dash.dashmode.registry;

import com.google.common.collect.ImmutableList;
import dash.dashmode.DashMod;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.LargeOakFoliagePlacer;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.trunk.LargeOakTrunkPlacer;

import java.util.OptionalInt;

public class DashFeatures {
    public static final ConfiguredFeature<TreeFeatureConfig, ?> FancyPaperOak;
    public static final ConfiguredFeature<TreeFeatureConfig, ?> FancyPaperOakBee;

    static {
        FancyPaperOak = Feature.TREE.configure(
                (new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(DashBlocks.PaperOakLog.getDefaultState()),
                        new SimpleBlockStateProvider(DashBlocks.PaperLeaves.getDefaultState()),
                        new LargeOakFoliagePlacer(UniformIntDistribution.of(2), UniformIntDistribution.of(4), 4),
                        new LargeOakTrunkPlacer(3, 11, 0),
                        new TwoLayersFeatureSize(0, 0, 0,
                                OptionalInt.of(4))))
                        .ignoreVines()
                        .heightmap(Heightmap.Type.MOTION_BLOCKING)
                        .build());

        FancyPaperOakBee = Feature.TREE.configure(FancyPaperOak.getConfig()
                .setTreeDecorators(ImmutableList.of(ConfiguredFeatures.Decorators.MORE_BEEHIVES_TREES)));
    }

    public static void init() {
        DashMod.MainLogger.debug("Entering to feature registry");

        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(DashMod.ModId, "paper_fancy_oak"), FancyPaperOak);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(DashMod.ModId, "paper_fancy_oak_bee"), FancyPaperOakBee);
    }

}
