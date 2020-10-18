package dash.dashmode.portal;

import dash.dashmode.registry.DashBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.class_5459;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.util.function.MaterialPredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class PaperPortalDesciption implements IPortalDesciption {
    private final Set<Block> possibleCorners = new HashSet<>();
    private final Set<Direction.Axis> axises = new HashSet<>();
    private final BlockPattern workingPattern;
    private final BlockPattern pattern;

    public PaperPortalDesciption() {
        BlockPatternBuilder builder = BlockPatternBuilder.start()
                .aisle("BBBB",
                        "C  C",
                        "C  C",
                        "C  C",
                        "CWWC")
                .where('B', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.BOOKSHELF)))
                .where('C', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(DashBlocks.PortalCane)))
                .where('W', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.WATER)))
                .where(' ', CachedBlockPosition.matchesBlockState(MaterialPredicate.create(Material.AIR)));

        workingPattern = builder.build();
        pattern = builder.where('C', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.SUGAR_CANE))).build();
        axises.add(Direction.Axis.X);
        axises.add(Direction.Axis.Z);

        possibleCorners.add(Blocks.BOOKSHELF);
        possibleCorners.add(DashBlocks.PortalCane);
        possibleCorners.add(Blocks.WATER);
        possibleCorners.add(Blocks.SUGAR_CANE);
    }

    @Override
    public boolean isFrame(BlockState state) {
        return possibleCorners.contains(state.getBlock());
    }

    @Override
    public @Nullable BlockPattern.Result test(WorldView world, BlockPos pos, Direction.Axis axis) {
        class_5459.class_5460 frame = checkFrame(world, pos, axis);

        if (frame.field_25937 > 0 && frame.field_25938 > 0) {
            return pattern.searchAround(world, frame.field_25936);
        }

        return null;
    }

    @Override
    public BlockPattern.Result testWorkingPortal(WorldView world, BlockPos pos, Direction.Axis axis) {
        class_5459.class_5460 frame = checkFrame(world, pos, axis);

        if (frame.field_25937 > 0 && frame.field_25938 > 0) {
            return workingPattern.searchAround(world, frame.field_25936);
        }

        return null;
    }

    @Override
    public void placePortal(World world, BlockPos corner, Direction forward, Direction right) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        BlockState obsidian = Blocks.MELON.getDefaultState();
        BlockState portal = Blocks.AIR.getDefaultState();

        for (int r = -1; r < 3; ++r) {
            for (int o = -1; o < 4; ++o) {
                if (r == -1 || r == 2 || o == -1 || o == 3) {
                    mutable.set(corner, r * forward.getOffsetX(), o, r * forward.getOffsetZ());
                    world.setBlockState(mutable, obsidian, 3);
                }
            }
        }


        for (int o = 0; o < 2; ++o) {
            for (int p = 0; p < 3; ++p) {
                mutable.set(corner, o * forward.getOffsetX(), p, o * forward.getOffsetZ());
                world.setBlockState(mutable, portal, 18);
            }
        }
    }
}
