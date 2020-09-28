package dash.dashmode.block;

import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FlowerFeature;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Custom grass block, can spread on own dirt
 */
public class DashGrassBlock extends GrassBlock {
    /**
     * Unique list of moddable grass for fast digging
     */
    private static final Set<DashGrassBlock> ModGrass = new HashSet<>();
    /**
     * Unique list of moddable dirt for fast digging
     */
    private static final Set<Block> ModDirt = new HashSet<>();
    /**
     * Supplier with possible dirt for spreading
     */
    private final Supplier<Block> dirt;

    public DashGrassBlock(Settings settings, Supplier<Block> dirt) {
        super(settings);
        this.dirt = dirt;
        ModGrass.add(this);
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        BlockPos blockPos = pos.up();
        BlockState blockState = this.getDefaultState();

        label48:
        for (int i = 0; i < 128; ++i) {
            BlockPos blockPos2 = blockPos;

            for (int j = 0; j < i / 16; ++j) {
                blockPos2 = blockPos2.add(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                if (!world.getBlockState(blockPos2.down()).isOf(this) || world.getBlockState(blockPos2).isFullCube(world, blockPos2)) {
                    continue label48;
                }
            }

            BlockState blockState2 = world.getBlockState(blockPos2);
            if (blockState2.isOf(blockState.getBlock()) && random.nextInt(10) == 0) {
                ((Fertilizable) blockState.getBlock()).grow(world, random, blockPos2, blockState2);
            }

            if (blockState2.isAir()) {
                BlockState blockState4;
                if (random.nextInt(8) == 0) {
                    List<ConfiguredFeature<?, ?>> list = world.getBiome(blockPos2).getGenerationSettings().getFlowerFeatures();
                    if (list.isEmpty()) {
                        continue;
                    }

                    ConfiguredFeature<?, ?> configuredFeature = list.get(0);
                    FlowerFeature flowerFeature = (FlowerFeature) configuredFeature.feature;
                    blockState4 = flowerFeature.getFlowerState(random, blockPos2, configuredFeature.getConfig());
                } else {
                    blockState4 = blockState;
                }

                if (blockState4.canPlaceAt(world, blockPos2)) {
                    world.setBlockState(blockPos2, blockState4, 3);
                }
            }
        }
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!canSurvive(state, world, pos)) {
            world.setBlockState(pos, dirt.get().getDefaultState());
        } else {
            if (world.getLightLevel(pos.up()) >= 9) {
                BlockState blockState = this.getDefaultState();

                for (int i = 0; i < 4; ++i) {
                    BlockPos blockPos = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                    if (world.getBlockState(blockPos).isOf(dirt.get()) && canSpread(blockState, world, blockPos)) {
                        world.setBlockState(blockPos, blockState.with(SNOWY, world.getBlockState(blockPos.up()).isOf(Blocks.SNOW)));
                    }
                }
            }

        }
    }

    private static boolean canSurvive(BlockState state, WorldView worldView, BlockPos pos) {
        BlockPos blockPos = pos.up();
        BlockState blockState = worldView.getBlockState(blockPos);
        if (blockState.isOf(Blocks.SNOW) && blockState.get(SnowBlock.LAYERS) == 1) {
            return true;
        } else if (blockState.getFluidState().getLevel() == 8) {
            return false;
        } else {
            int i = ChunkLightProvider.getRealisticOpacity(worldView, state, pos, blockState, blockPos, Direction.UP, blockState.getOpacity(worldView, blockPos));
            return i < worldView.getMaxLightLevel();
        }
    }

    private static boolean canSpread(BlockState state, WorldView worldView, BlockPos pos) {
        BlockPos blockPos = pos.up();
        return canSurvive(state, worldView, pos) && !worldView.getFluidState(blockPos).isIn(FluidTags.WATER);
    }

    /**
     * Checks wherever block can be easily obtained with shovel
     *
     * @param state
     * @return
     */
    public static boolean isDashSoil(BlockState state) {
        if (state == null)
            return false;

        Block toFind = state.getBlock();
        if (ModGrass.contains(toFind))
            return true;

        if (ModDirt.isEmpty()) {
            ModGrass.forEach(x -> ModDirt.add(x.dirt.get()));
        }

        if (ModDirt.contains(toFind))
            return true;

        return false;
    }
}
