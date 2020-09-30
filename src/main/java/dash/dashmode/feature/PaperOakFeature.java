package dash.dashmode.feature;

import dash.dashmode.registry.DashFeatures;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class PaperOakFeature extends SaplingGenerator {
    @Nullable
    protected ConfiguredFeature<TreeFeatureConfig, ?> createTreeFeature(Random random, boolean bl) {
        return bl
                ? DashFeatures.FancyPaperOakBee
                : DashFeatures.FancyPaperOak;
    }
}
