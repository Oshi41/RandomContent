package dash.dashmode.portal;

import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.class_5459;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public interface IPortalDesciption {

    /**
     * Check if current block is frame
     *
     * @param state
     * @return
     */
    boolean isFrame(BlockState state);

    /**
     * Performing test for portal check
     *
     * @param world - current world
     * @param pos   - pos for portal
     * @param axis
     * @return
     */
    @Nullable BlockPattern.Result test(WorldView world, BlockPos pos, Direction.Axis axis);

    /**
     * @param world
     * @param pos
     * @return
     */
    default BlockPattern.Result testWorkingPortal(WorldView world, BlockPos pos) {
        for (Direction.Axis axis : Arrays.asList(Direction.Axis.X, Direction.Axis.Z, Direction.Axis.Y)) {
            BlockPattern.Result result = testWorkingPortal(world, pos, axis);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    /**
     * @param world
     * @param pos
     * @param axis
     * @return
     */
    @Nullable BlockPattern.Result testWorkingPortal(WorldView world, BlockPos pos, Direction.Axis axis);

    /**
     * Placing portal in current block
     *
     * @param world   - corld where portal should be created
     * @param corner  - nether portal corner
     * @param forward - forward dir
     * @param right   - right dir
     */
    void placePortal(World world, BlockPos corner, Direction forward, Direction right);

    /**
     * Fast check for portal frame
     *
     * @param world - current world
     * @param pos   - current pos
     * @param axis  - portal axis
     * @return
     */
    default class_5459.class_5460 checkFrame(WorldView world, BlockPos pos, Direction.Axis axis) {
        class_5459.class_5460 lv = class_5459.method_30574(pos, axis,
                21,
                Direction.Axis.Y,
                21,
                (blockPos) -> isFrame(world.getBlockState(blockPos)));

        return lv;
    }
}
