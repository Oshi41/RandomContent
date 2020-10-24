package dash.dashmode.feature;

import dash.dashmode.utils.DynamicResourceUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class JsonConfiguredFeature extends SaplingGenerator {
    private Lazy<ConfiguredFeature<TreeFeatureConfig, ?>> lazy;
    private Lazy<ConfiguredFeature<TreeFeatureConfig, ?>> lazyWithBee;
    private ServerWorld serverWorld;

    public JsonConfiguredFeature(Identifier tree) {
        this(tree, null);
    }

    public JsonConfiguredFeature(Identifier tree, Identifier withBee) {
        lazy = new Lazy<>(() -> (ConfiguredFeature<TreeFeatureConfig, ?>) DynamicResourceUtils.find(Registry.CONFIGURED_FEATURE_WORLDGEN, tree, serverWorld.getRegistryManager()));

        if (withBee != null) {
            lazyWithBee = new Lazy<>(() -> (ConfiguredFeature<TreeFeatureConfig, ?>) DynamicResourceUtils.find(Registry.CONFIGURED_FEATURE_WORLDGEN, withBee, serverWorld.getRegistryManager()));
        }
    }

    @Override
    protected @Nullable ConfiguredFeature<TreeFeatureConfig, ?> createTreeFeature(Random random, boolean bl) {
        if (bl && lazyWithBee != null) {
            return lazyWithBee.get();
        }

        return lazy.get();
    }

    @Override
    public boolean generate(ServerWorld serverWorld, ChunkGenerator chunkGenerator, BlockPos blockPos, BlockState blockState, Random random) {
        this.serverWorld = serverWorld;
        return super.generate(serverWorld, chunkGenerator, blockPos, blockState, random);
    }
}
